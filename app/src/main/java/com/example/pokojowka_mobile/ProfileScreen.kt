package com.example.pokojowka_mobile.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pokojowka_mobile.AppDestinations
import com.example.pokojowka_mobile.data.UserData
import com.example.pokojowka_mobile.data.UserSettingsManager
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import kotlinx.coroutines.launch
import com.example.pokojowka_mobile.ui.components.SharedHeader
import com.example.pokojowka_mobile.ui.components.AppBottomNavigationBar
import com.example.pokojowka_mobile.data.SampleUserData
import com.example.pokojowka_mobile.data.availableDeviceModels
import com.example.pokojowka_mobile.ui.components.ProfileInfoItem
import com.example.pokojowka_mobile.ui.components.ProfileSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val currentUserData by userSettingsManager.userPreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserData("", "", emptyList())
    )
    val currentEnvironmentData = remember { SampleUserData.defaultEnvironment }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scrollState = rememberScrollState()

    val connectedDevicesList = remember { availableDeviceModels }

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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(scrollState)

        ) {
            SharedHeader(
                userData = currentUserData,
                environmentData = currentEnvironmentData,
                onNotificationClick = {
                    println("Powiadomienia kliknięte z ProfileScreen!")
                }
            )


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                if (currentUserData.userName.isNotBlank() || currentUserData.lastName.isNotBlank()) {
                    ProfileSection(title = "Dane Osobowe") {
                        ProfileInfoItem(
                            label = "Imię",
                            value = currentUserData.userName,
                            icon = Icons.Filled.Person
                        )
                        ProfileInfoItem(
                            label = "Nazwisko",
                            value = currentUserData.lastName,
                            icon = Icons.Filled.Badge
                        )
                    }

                    ProfileSection(title = "Połączone Urządzenia") {
                        if (connectedDevicesList.isEmpty()) {
                            ProfileInfoItem(
                                label = "Urządzenia",
                                value = "Brak podłączonych urządzeń",
                                icon = Icons.Filled.Sensors
                            )
                        } else {
                            connectedDevicesList.forEach { deviceName ->
                                ProfileInfoItem(
                                    label = deviceName,
                                    value = "Online",
                                    icon = Icons.Filled.Sensors
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                userSettingsManager.clearUserSettings()
                                navController.navigate(AppDestinations.FORM_SCREEN) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text("Wyloguj")
                    }

                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Ładowanie danych profilu...", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                navController.navigate(AppDestinations.FORM_SCREEN) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }) {
                                Text("Uzupełnij Dane")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, name = "Profile Screen with SharedHeader in content")
@Composable
fun ProfileScreenPreview() {
    PokojowkamobileTheme {
        val navController = rememberNavController()
        // Zaktualizowano dane podglądu, usuwając selectedHub
        val sampleUserData = SampleUserData.defaultUser.copy(
            userName = "Anna",
            lastName = "Projektantka"
        )
        val sampleEnvData = SampleUserData.defaultEnvironment
        val previewConnectedDevices = listOf("Tablet Graficzny", "Lampa Studyjna LED", "Głośnik Smart")

        Scaffold(
            bottomBar = {
                AppBottomNavigationBar(currentRoute = AppDestinations.PROFILE_SCREEN, onItemSelected = {})
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                    .verticalScroll(rememberScrollState())
            ) {
                SharedHeader(
                    userData = sampleUserData,
                    environmentData = sampleEnvData,
                    onNotificationClick = {}
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileSection(title = "Dane Osobowe") {
                        ProfileInfoItem(label = "Imię", value = sampleUserData.userName, icon = Icons.Filled.Person)
                        ProfileInfoItem(label = "Nazwisko", value = sampleUserData.lastName, icon = Icons.Filled.Badge)
                    }
                    ProfileSection(title = "Połączone Urządzenia") {
                        if (previewConnectedDevices.isEmpty()) {
                            ProfileInfoItem(label = "Urządzenia", value = "Brak", icon = Icons.Filled.Sensors)
                        } else {
                            previewConnectedDevices.forEach { deviceName ->
                                ProfileInfoItem(label = deviceName, value = "Aktywne", icon = Icons.Filled.Sensors)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) { Text("Wyloguj (Preview)") }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
