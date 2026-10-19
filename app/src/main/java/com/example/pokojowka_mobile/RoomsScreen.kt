package com.example.pokojowka_mobile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pokojowka_mobile.components.RoomsSection
import com.example.pokojowka_mobile.components.SharedHeader
import com.example.pokojowka_mobile.data.SampleUserData
import com.example.pokojowka_mobile.data.UserData
import com.example.pokojowka_mobile.data.UserSettingsManager
import com.example.pokojowka_mobile.network.repos.RoomsRepository
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme

@Composable
fun RoomsScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }

    val currentUserData by userSettingsManager.userPreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserData(userName = "", lastName = "", connectedDevices = emptyList())
    )

    val currentEnvironmentData = remember { SampleUserData.defaultEnvironment }
    val rooms by RoomsRepository.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding() + 80.dp)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
        ) {
            SharedHeader(
                userData = currentUserData,
                environmentData = currentEnvironmentData,
                onNotificationClick = {
                    println("Powiadomienia kliknięte z RoomsScreen!")
                }
            )

            RoomsSection(
                rooms = rooms.toMutableList(),
                onRoomClick = { roomId ->
                    Log.d("RoomsScreen", "Kliknięto pokój o ID: $roomId. Nawigacja do RoomView.")
                    navController.navigate("${AppDestinations.ROOM_VIEW_SCREEN_ROUTE}/$roomId")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun RoomsScreenPreview() {
    val navController = rememberNavController()
    PokojowkamobileTheme {
        RoomsScreen(navController = navController)
    }
}
