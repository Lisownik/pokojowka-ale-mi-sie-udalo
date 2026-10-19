package com.example.pokojowka_mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pokojowka_mobile.components.AppBottomNavigationBar
import com.example.pokojowka_mobile.components.BulbsSection
import com.example.pokojowka_mobile.components.InfoTile
import com.example.pokojowka_mobile.components.NotificationsSection
import com.example.pokojowka_mobile.components.SharedHeader
import com.example.pokojowka_mobile.data.EnvironmentData
import com.example.pokojowka_mobile.data.GlobalPlantsList
import com.example.pokojowka_mobile.data.NotificationData
import com.example.pokojowka_mobile.data.NotificationType
import com.example.pokojowka_mobile.data.UserData
import com.example.pokojowka_mobile.data.UserSettingsManager
import com.example.pokojowka_mobile.data.sampleGlobalNotificationsList
import com.example.pokojowka_mobile.network.repos.BulbsRepository
import com.example.pokojowka_mobile.network.repos.HeatersRepository
import com.example.pokojowka_mobile.network.repos.RoomsRepository
import com.example.pokojowka_mobile.network.repos.WindowsRepository
import com.example.pokojowka_mobile.ui.theme.*

@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val roomsList by RoomsRepository.state.collectAsStateWithLifecycle()
    val bulbsList by BulbsRepository.state.collectAsStateWithLifecycle()
    val windowsList by WindowsRepository.state.collectAsStateWithLifecycle()
    val heatersList by HeatersRepository.state.collectAsStateWithLifecycle()

    val notificationsList = remember {
        mutableStateListOf<NotificationData>().apply {
            addAll(sampleGlobalNotificationsList.map { it.copy() })
        }
    }

    val newNotificationsCount = notificationsList.count { it.type == NotificationType.ERROR || it.type == NotificationType.INFO }

    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }

    val currentUserData by userSettingsManager.userPreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserData(userName = "", lastName = "", connectedDevices = emptyList())
    )

    val currentEnvironmentData = EnvironmentData(
        "TODO",
        "TODO"
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            AppBottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = { selectedRoute ->
                    val popped = navController.popBackStack(selectedRoute, inclusive = false)
                    if (!popped) {
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
                    println("Powiadomienia kliknięte z HomeScreen!")
                }
            )

            // Pierwszy rząd kafelków: Pokojówki & Rośliny
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoTile(
                    modifier = Modifier.weight(1f),
                    gradientColors = listOf(RoomsGradientStart, RoomsGradientEnd),
                    icon = Icons.Filled.Home,
                    iconContentDescription = "Pokoje",
                    chipText = "${roomsList.count()} pokoje",
                    title = "Pokojówki",
                    subtitle = "Parametry w normie",
                    onTileClick = {
                        val popped = navController.popBackStack(AppDestinations.ROOMS_SCREEN, inclusive = false)
                        if (!popped) {
                            navController.navigate(AppDestinations.ROOMS_SCREEN) {
                                popUpTo(AppDestinations.HOME_SCREEN) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
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
                        val popped = navController.popBackStack(AppDestinations.PLANTS_SCREEN, inclusive = false)
                        if (!popped) {
                            navController.navigate(AppDestinations.PLANTS_SCREEN) {
                                popUpTo(AppDestinations.HOME_SCREEN) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            // Drugi rząd kafelków: Smart Okna & Grzejniki
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val openWindowsCount = windowsList.count { it.isOpen }
                val windowSubtitle = if (openWindowsCount == 0) "Wszystkie zamknięte" else "$openWindowsCount otwarte"

                InfoTile(
                    modifier = Modifier.weight(1f),
                    gradientColors = listOf(WindowsGradientStart, WindowsGradientEnd),
                    icon = Icons.Filled.Window,
                    iconContentDescription = "Smart Okna",
                    chipText = "${windowsList.count()} okna",
                    title = "Smart Okna",
                    subtitle = windowSubtitle,
                    onTileClick = {
                        val popped = navController.popBackStack(AppDestinations.WINDOWS_SCREEN, inclusive = false)
                        if (!popped) {
                            navController.navigate(AppDestinations.WINDOWS_SCREEN) {
                                popUpTo(AppDestinations.HOME_SCREEN) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                )

                val activeHeatersCount = heatersList.count { it.isHeatingOn }
                val heaterSubtitle = if (activeHeatersCount > 0) "Grzanie aktywne" else "Wyłączone"

                InfoTile(
                    modifier = Modifier.weight(1f),
                    gradientColors = listOf(HeaterGradientStart, HeaterGradientEnd),
                    icon = Icons.Filled.Whatshot,
                    iconContentDescription = "Grzejniki",
                    chipText = "${heatersList.count()} grzejniki",
                    title = "Grzejniki",
                    subtitle = heaterSubtitle,
                    onTileClick = {
                        val popped = navController.popBackStack(AppDestinations.HEATERS_SCREEN, inclusive = false)
                        if (!popped) {
                            navController.navigate(AppDestinations.HEATERS_SCREEN) {
                                popUpTo(AppDestinations.HOME_SCREEN) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
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
                        BulbsRepository.setBulbState(bulbId, newState)
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
        HomeScreen(navController = navController)
    }
}
