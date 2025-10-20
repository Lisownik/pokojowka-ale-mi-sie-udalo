package com.example.pokojowka_mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pokojowka_mobile.ui.components.SharedHeader
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.components.InfoTile
import androidx.compose.material3.Scaffold
import com.example.pokojowka_mobile.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Eco
import androidx.compose.runtime.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pokojowka_mobile.ui.components.BulbsSection
import com.example.pokojowka_mobile.ui.components.NotificationsSection
import com.example.pokojowka_mobile.ui.components.AppBottomNavigationBar
import com.example.pokojowka_mobile.data.NotificationData
import com.example.pokojowka_mobile.data.NotificationType
import com.example.pokojowka_mobile.data.sampleGlobalNotificationsList
import com.example.pokojowka_mobile.data.UserData

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokojowka_mobile.data.UserSettingsManager
import com.example.pokojowka_mobile.data.GlobalPlantsList
import com.example.pokojowka_mobile.data.GlobalRoomsList
import com.example.pokojowka_mobile.network.AuthViewModel


@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val authViewModel: AuthViewModel = viewModel()

//    val bulbsList = remember {
//        mutableStateListOf<BulbData>().apply {
//            addAll(sampleBulbsGlobalList.map { it.copy() })
//        }
//    }
    val bulbsList by authViewModel.bulbsFlow.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        authViewModel.loadBulbs()
    }

    val notificationsList = remember {
        mutableStateListOf<NotificationData>().apply {
            addAll(sampleGlobalNotificationsList.map { it.copy() })
        }
    }

    val newNotificationsCount = notificationsList.count { it.type == NotificationType.ERROR || it.type == NotificationType.INFO }

    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }


    val currentUserData by userSettingsManager.userPreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserData(userName = "", lastName = "",  connectedDevices = emptyList())
    )

    val currentEnvironmentData by authViewModel.getAvgRooms.collectAsStateWithLifecycle()


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    Scaffold(
        modifier = modifier.fillMaxSize(),
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            SharedHeader(
                userData = currentUserData,
                environmentData = currentEnvironmentData,
                onNotificationClick = {
                    println("Powiadomienia kliknięte z [NazwaTwojegoEkranu]!")
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoTile(
                    modifier = Modifier.weight(1f),
                    gradientColors = listOf(RoomsGradientStart, RoomsGradientEnd),
                    icon = Icons.Filled.Home,
                    iconContentDescription = "Pokoje",
                    chipText = "${GlobalRoomsList.count()} pokoje",
                    title = "Pokojówki",
                    subtitle = "Parametry w normie",
                    onTileClick = {
                        println("Kafelek Pokoje kliknięty!")
                        navController.navigate(AppDestinations.ROOMS_SCREEN) {

                            popUpTo(AppDestinations.HOME_SCREEN) {
                                saveState = true

                            }
                            launchSingleTop = true

                        }
                    }
                )
                InfoTile(
                    modifier = Modifier.weight(1f),
                    gradientColors = listOf(PlantsGradientStart, PlantsGradientEnd),
                    icon = Icons.Filled.Eco,
                    iconContentDescription = "Rośliny",
                    chipText = "${GlobalPlantsList.count()} roślin",
                    title = "Rośliny",
                    subtitle = "Wszystkie zdrowe",
                    onTileClick = {
                        println("Kafelek Rośliny kliknięty!")
                        navController.navigate(AppDestinations.PLANTS_SCREEN) {

                            popUpTo(AppDestinations.HOME_SCREEN) {
                                saveState = true

                            }
                            launchSingleTop = true

                        }
                    }
                )
            }


            BulbsSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                activeBulbsCount = bulbsList.count { it.isSwitchedOn },
                bulbs = bulbsList,
                onBulbSwitchToggle = { bulbId, newState ->
                    val index = bulbsList.indexOfFirst { it.id == bulbId }
                    if (index != -1) {
                        val oldBulb = bulbsList[index]

                        val updatedBulb = oldBulb.copy(
                            isSwitchedOn = newState
                        )
                        authViewModel.changePowerState(bulbId, if(newState) "on" else "off")

                    }
                }
            )
            NotificationsSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                newNotificationsCount = newNotificationsCount,
                notifications = notificationsList,
                onNotificationClick = { notificationId ->
                    println("Kliknięto powiadomienie ID: $notificationId")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeScreenPreview() {
    PokojowkamobileTheme {

        val navController = rememberNavController()
        HomeScreen(navController = navController as NavHostController)
    }
}

