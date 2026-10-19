package com.example.pokojowka_mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pokojowka_mobile.components.AppBottomNavigationBar
import com.example.pokojowka_mobile.components.HeaterViewSection
import com.example.pokojowka_mobile.components.RoomViewSection
import com.example.pokojowka_mobile.components.SectionItemUIData
import com.example.pokojowka_mobile.components.SharedHeader
import com.example.pokojowka_mobile.data.SampleUserData
import com.example.pokojowka_mobile.data.UserData
import com.example.pokojowka_mobile.data.UserSettingsManager
import com.example.pokojowka_mobile.network.repos.HeatersRepository
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme

@Composable
fun HeaterViewInternalHeader(
    heaterName: String,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateUp) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Wróć"
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = heaterName,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun HeaterView(
    navController: NavHostController,
    heaterId: String?,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }

    val currentUserData by userSettingsManager.userPreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserData(userName = "", lastName = "", connectedDevices = emptyList())
    )

    val currentEnvironmentData = remember { SampleUserData.defaultEnvironment }

    var showRenameDialog by remember { mutableStateOf(false) }
    var newHeaterName by remember { mutableStateOf("") }

    val heaters by HeatersRepository.state.collectAsStateWithLifecycle()
    val heaterData = heaters.firstOrNull { it.id == heaterId }

    if (showRenameDialog && heaterId != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Zmień nazwę grzejnika") },
            text = {
                OutlinedTextField(
                    value = newHeaterName,
                    onValueChange = { newHeaterName = it },
                    label = { Text("Nowa nazwa grzejnika") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newHeaterName.isNotBlank()) {
                        showRenameDialog = false
                        HeatersRepository.setName(heaterId, newHeaterName)
                    }
                }) {
                    Text("Zatwierdź")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }

    Scaffold(
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
                    println("Powiadomienia kliknięte z HeaterView!")
                }
            )

            HeaterViewInternalHeader(
                heaterName = heaterData?.name ?: "Szczegóły Grzejnika",
                onNavigateUp = { navController.popBackStack() },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable {
                        newHeaterName = heaterData?.name ?: ""
                        showRenameDialog = true
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (heaterData == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Nie odnaleziono danych dla wybranego grzejnika.")
                }
            } else {
                val currentHeaterData = heaterData

                val heaterParameters = buildList {
                    add(
                        SectionItemUIData(
                            label = "Obecna Temperatura",
                            value = currentHeaterData.currentTemp,
                            icon = Icons.Filled.Thermostat
                        )
                    )
                    add(
                        SectionItemUIData(
                            label = "Docelowa Temperatura",
                            value = "${"%.1f".format(currentHeaterData.targetTemp)}°C",
                            icon = Icons.Filled.Whatshot
                        )
                    )
                }

                HeaterViewSection(
                    heaterData = currentHeaterData,
                    onHeatingToggle = { isHeatingOn ->
                        HeatersRepository.setHeatingState(currentHeaterData.id, isHeatingOn)
                    },
                    onTargetTempChange = { newTargetTemp ->
                        HeatersRepository.setTargetTemperature(currentHeaterData.id, newTargetTemp)
                    }
                )

                RoomViewSection(
                    title = "Parametry Grzejnika",
                    items = heaterParameters,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun HeaterViewPreview() {
    val navController = rememberNavController()
    PokojowkamobileTheme {
        HeaterView(navController = navController, heaterId = "h1")
    }
}
