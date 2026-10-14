package com.example.pokojowka_mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
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
import com.example.pokojowka_mobile.components.RoomViewSection
import com.example.pokojowka_mobile.components.SectionItemUIData
import com.example.pokojowka_mobile.components.SharedHeader
import com.example.pokojowka_mobile.components.WindowViewSection
import com.example.pokojowka_mobile.data.SampleUserData
import com.example.pokojowka_mobile.data.UserData
import com.example.pokojowka_mobile.data.UserSettingsManager
import com.example.pokojowka_mobile.repos.WindowsRepository
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme

@Composable
fun WindowViewInternalHeader(
    windowName: String,
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
            text = windowName,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun WindowView(
    navController: NavHostController,
    windowId: String?,
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
    var newWindowName by remember { mutableStateOf("") }

    val windows by WindowsRepository.state.collectAsStateWithLifecycle()
    val windowData = windows.firstOrNull { it.id == windowId }

    if (showRenameDialog && windowId != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Zmień nazwę okna") },
            text = {
                OutlinedTextField(
                    value = newWindowName,
                    onValueChange = { newWindowName = it },
                    label = { Text("Nowa nazwa okna") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newWindowName.isNotBlank()) {
                        showRenameDialog = false
                        WindowsRepository.setName(windowId, newWindowName)
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
                    println("Powiadomienia kliknięte z WindowView!")
                }
            )

            WindowViewInternalHeader(
                windowName = windowData?.name ?: "Szczegóły Okna",
                onNavigateUp = { navController.popBackStack() },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable {
                        newWindowName = windowData?.name ?: ""
                        showRenameDialog = true
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (windowData == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Nie odnaleziono danych dla wybranego okna.")
                }
            } else {
                val currentWindowData = windowData

                // Budowanie listy parametrów zewnętrznych za pomocą buildList
                val windowParameters = buildList {
                    add(
                        SectionItemUIData(
                            label = "Temperatura Zewnętrzna",
                            value = currentWindowData.outdoorTemp,
                            icon = Icons.Filled.Thermostat
                        )
                    )
                    add(
                        SectionItemUIData(
                            label = "Czujnik Opadow",
                            value = if (currentWindowData.rainDetected) "Wykryto deszcz" else "Brak opadow",
                            icon = Icons.Filled.WaterDrop
                        )
                    )
                }

                WindowViewSection(
                    windowData = currentWindowData,
                    onWindowToggle = { isOpen ->
                        WindowsRepository.setWindowState(currentWindowData.id, isOpen)
                    }
                )

                RoomViewSection(
                    title = "Parametry Zewnętrzne",
                    items = windowParameters,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun WindowViewPreview() {
    val navController = rememberNavController()
    PokojowkamobileTheme {
        WindowView(navController = navController, windowId = "w1")
    }
}
