package com.example.pokojowka_mobile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pokojowka_mobile.ui.components.AppBottomNavigationBar
import com.example.pokojowka_mobile.ui.components.RoomsSection
import com.example.pokojowka_mobile.ui.components.SharedHeader
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.data.sampleRoomsGlobal
import com.example.pokojowka_mobile.data.UserData
import com.example.pokojowka_mobile.data.EnvironmentData
import com.example.pokojowka_mobile.data.SampleUserData

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokojowka_mobile.data.RoomData
import com.example.pokojowka_mobile.data.UserSettingsManager
import com.example.pokojowka_mobile.network.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun RoomsScreen(navController: NavHostController, modifier: Modifier = Modifier) {

    val authViewModel: AuthViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }


    val currentUserData by userSettingsManager.userPreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserData(userName = "", lastName = "", connectedDevices = emptyList())
    )

    val currentEnvironmentData = remember { SampleUserData.defaultEnvironment }
    val rooms by authViewModel.roomsFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        authViewModel.getRooms()
    }

    LaunchedEffect(rooms) {
        Log.d("API_RoomsScreen", "Rooms updated: ${rooms.size}")
    }


    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = { selectedRoute ->
                    if (currentRoute != selectedRoute) {
                        navController.navigate(selectedRoute) {

                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }

                            launchSingleTop = true

                            restoreState = true
                        }
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
        ) {
            SharedHeader(
                userData = currentUserData,
                environmentData = currentEnvironmentData,
                onNotificationClick = {
                    println("Powiadomienia kliknięte z [NazwaTwojegoEkranu]!")

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
