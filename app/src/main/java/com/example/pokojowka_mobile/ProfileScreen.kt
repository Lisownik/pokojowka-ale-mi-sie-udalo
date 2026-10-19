package com.example.pokojowka_mobile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.outlined.Window
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pokojowka_mobile.components.ProfileInfoItem
import com.example.pokojowka_mobile.components.ProfileSection
import com.example.pokojowka_mobile.components.SharedHeader
import com.example.pokojowka_mobile.data.SampleUserData
import com.example.pokojowka_mobile.data.UserData
import com.example.pokojowka_mobile.data.UserSettingsManager
import com.example.pokojowka_mobile.data.availableDeviceModels
import com.example.pokojowka_mobile.network.HealthState
import com.example.pokojowka_mobile.network.ServerHealth
import com.example.pokojowka_mobile.network.repos.BulbsRepository
import com.example.pokojowka_mobile.network.repos.RoomsRepository
import com.example.pokojowka_mobile.network.repos.WindowsRepository
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val connectedToServer by HealthState.healthState.collectAsStateWithLifecycle()
    val bulbs by BulbsRepository.state.collectAsStateWithLifecycle()
    val rooms by RoomsRepository.state.collectAsStateWithLifecycle()
    val windows by WindowsRepository.state.collectAsStateWithLifecycle()

    val currentUserData by userSettingsManager.userPreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserData("", "", emptyList())
    )
    val currentEnvironmentData = remember { SampleUserData.defaultEnvironment }
    val scrollState = rememberScrollState()

    val connectedDevicesList = remember { availableDeviceModels }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding() + 80.dp)
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
                            rooms.forEach { room ->
                                ProfileInfoItem(
                                    label = room.name,
                                    value = room.id,
                                    icon = Icons.Filled.Sensors
                                )
                            }
                            bulbs.forEach { bulb ->
                                ProfileInfoItem(
                                    label = bulb.name,
                                    value = bulb.id,
                                    icon = Icons.Filled.Lightbulb
                                )
                            }
                            windows.forEach { window ->
                                ProfileInfoItem(
                                    label = window.name,
                                    value = window.id,
                                    icon = Icons.Outlined.Window
                                )
                            }
                        }
                    }

                    ProfileSection(title = "Dane Dewelopera") {
                        ProfileInfoItem(
                            label = "Stan połączenia z Serwerem",
                            icon = if (connectedToServer == ServerHealth.HEALTHY) Icons.Filled.Wifi else Icons.Filled.WifiOff,
                            value = if (connectedToServer == ServerHealth.HEALTHY) "Git" else if (connectedToServer == ServerHealth.UNHEALTHY) "Źle" else "unknown"
                        )
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
        val sampleUserData = SampleUserData.defaultUser.copy(
            userName = "Anna",
            lastName = "Projektantka"
        )
        val sampleEnvData = SampleUserData.defaultEnvironment

        Scaffold { innerPadding ->
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
