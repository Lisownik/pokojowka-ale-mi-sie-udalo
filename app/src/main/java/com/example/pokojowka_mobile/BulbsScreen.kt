
package com.example.pokojowka_mobile.screens

import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pokojowka_mobile.AppDestinations
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination

import com.example.pokojowka_mobile.ui.components.AppBottomNavigationBar
import com.example.pokojowka_mobile.ui.components.BulbsListSection
import com.example.pokojowka_mobile.ui.components.SharedHeader


import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme


import com.example.pokojowka_mobile.data.BulbData
import com.example.pokojowka_mobile.data.sampleBulbsGlobalList

import com.example.pokojowka_mobile.data.UserData
import com.example.pokojowka_mobile.data.EnvironmentData
import com.example.pokojowka_mobile.data.SampleUserData

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokojowka_mobile.data.UserSettingsManager
import com.example.pokojowka_mobile.network.AuthViewModel
import com.example.pokojowka_mobile.network.RetrofitClient
import kotlinx.coroutines.delay
import kotlin.getValue

@Composable
fun BulbsScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val authViewModel: AuthViewModel = viewModel()

//    val bulbsListState = remember(sampleBulbsGlobalList) {
//        mutableStateListOf<BulbData>().apply {
//            addAll(sampleBulbsGlobalList.map { it.copy() })
//        }
//    }
    val bulbsListState by authViewModel.bulbsFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        authViewModel.loadBulbs()
    }

    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }


    val currentUserData by userSettingsManager.userPreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserData(userName = "", lastName = "", selectedHub = "", connectedDevices = emptyList())
    )

    val currentEnvironmentData = remember { SampleUserData.defaultEnvironment }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color.Transparent,
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

            BulbsListSection(
                bulbs = bulbsListState,
                onSwitchToggle = { bulbId, newState ->

                    val index = bulbsListState.indexOfFirst { it.id == bulbId }
                    if (index != -1) {
//                        val oldBulb = bulbsListState[index]
//                        bulbsListState[index] = oldBulb.copy(
//                            isSwitchedOn = newState
//                        )
                        Log.d("Logging bulbsListState", bulbsListState.toString())
                        authViewModel.changePowerState(bulbId, if(newState) "on" else "off")
                    }
                    Log.d("BulbsScreen", "Żarówka $bulbId przełączona na: $newState")
                },
                onBrightnessChange = { bulbId, newBrightness ->
                    val index = bulbsListState.indexOfFirst { it.id == bulbId }
                    if (index != -1) {
//                        bulbsListState[index] = bulbsListState[index].copy(brightnessPercentage = newBrightness)
                        authViewModel.changeBrightness(bulbId, newBrightness, RetrofitClient.BULB_CHANGE_DURATION)
                    }
                    Log.d("BulbsScreen", "Jasność żarówki $bulbId zmieniona na: $newBrightness%")
                },

                onItemClick = { bulbId ->
                    Log.d("BulbsScreen", "Nawigacja do BulbView dla żarówki: $bulbId")
                    navController.navigate("${AppDestinations.BULB_VIEW_SCREEN_ROUTE}/$bulbId")
                },

                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun BulbsScreenPreview() {
    val navController = rememberNavController()
    PokojowkamobileTheme {
        BulbsScreen(navController = navController)
    }
}

