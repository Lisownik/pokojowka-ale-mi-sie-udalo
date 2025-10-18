package com.example.pokojowka_mobile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import com.example.pokojowka_mobile.ui.components.ThreatsTile
import com.example.pokojowka_mobile.ui.components.AppBottomNavigationBar
import com.example.pokojowka_mobile.ui.components.RoomViewSection
import com.example.pokojowka_mobile.ui.components.SectionItemUIData
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.data.RoomData
import com.example.pokojowka_mobile.data.sampleRoomsGlobal
import com.example.pokojowka_mobile.ui.components.SharedHeader

import com.example.pokojowka_mobile.data.UserData
import com.example.pokojowka_mobile.data.EnvironmentData
import com.example.pokojowka_mobile.data.SampleUserData

import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokojowka_mobile.data.UserSettingsManager
import com.example.pokojowka_mobile.network.AuthViewModel
import kotlinx.coroutines.delay


@Composable
fun RoomViewInternalHeader(
    roomName: String,
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
            text = roomName,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            modifier = Modifier.weight(1f)
        )

    }
}



private object RoomParameterIcons {
    val Temperature: ImageVector = Icons.Filled.Thermostat
    val Humidity: ImageVector = Icons.Filled.Opacity
    val Pressure: ImageVector = Icons.Filled.Speed
    val AirQuality: ImageVector = Icons.Filled.Air
}

private fun getRoomById(roomId: String?): RoomData? {
    if (roomId == null) return null
    return sampleRoomsGlobal.find { it.id == roomId }
}

@Composable
fun RoomView(
    navController: NavHostController,
    roomId: String?,
    modifier: Modifier = Modifier
) {
//    var roomData by remember { mutableStateOf<RoomData?>(null) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scrollState = rememberScrollState()


    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }


    val currentUserData by userSettingsManager.userPreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserData(userName = "", lastName = "", connectedDevices = emptyList())
    )

    val currentEnvironmentData = remember { SampleUserData.defaultEnvironment }


    var showRenameDialog by remember { mutableStateOf(false) }
    var newRoomName by remember { mutableStateOf("") }
    val rooms by authViewModel.roomsFlow.collectAsStateWithLifecycle()
//    val roomData = remember(rooms) {
//        rooms.firstOrNull { it.id == roomId }
//    }


    LaunchedEffect(rooms) {
        Log.d("API_RoomView_Debug", "Rooms list: ${rooms.joinToString { it.id + ":" + it.name }}")
        delay(500)
        rooms.forEach { room ->
            Log.d("API_RoomView_Debug", "Room ${room.id}: ${room.hashCode()}")
            delay(250)
        }
    }

    val roomData = rooms.firstOrNull { it.id == roomId }

    LaunchedEffect(roomData) {
        if (roomData != null) {
            delay(250)
            Log.d("API_RoomView", "Room data reference: ${roomData.hashCode()}")
            delay(250)
            Log.d("API_RoomView", "Room data: $roomData")
        }
    }

    if (showRenameDialog && roomId != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = {
                Text("Zmień nazwę pokoju")
            },
            text = {
                OutlinedTextField(
                    value = newRoomName,
                    onValueChange = { newRoomName = it },
                    label = { Text("Nowa nazwa") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newRoomName.isNotBlank()) {
                        authViewModel.changeRoomName(roomId, newRoomName)
                        showRenameDialog = false
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
            RoomViewInternalHeader(
                roomName = roomData?.name ?: "Szczegóły Pokoju",
                onNavigateUp = { navController.popBackStack() },
                modifier = Modifier.padding(top = 8.dp).clickable(onClick = {
                    newRoomName = roomData?.name ?: "Nie ma"
                    showRenameDialog = true
                }),

            )

            Spacer(modifier = Modifier.height(12.dp))

            if (roomData == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (roomId != null) {
                        Text("Ładowanie danych pokoju lub pokój o ID '$roomId' nie istnieje.")
                    } else {
                        Text("Nie określono ID pokoju.")
                    }
                }
            } else {
                val currentRoomData = roomData!!
                val roomParameters =
                    buildList<SectionItemUIData> {
                        add(
                            SectionItemUIData(
                                label = "Temperatura",
                                value = currentRoomData.temperature,
                                icon = RoomParameterIcons.Temperature,
                                trend = currentRoomData.temperatureTrend
                            )
                        )
                        add(
                            SectionItemUIData(
                                label = "Wilgotność",
                                value = currentRoomData.humidity,
                                icon = RoomParameterIcons.Humidity,
                                trend = currentRoomData.humidityTrend
                            )
                        )
                        add(
                            SectionItemUIData(
                                label = "Ciśnienie",
                                value = currentRoomData.pressure,
                                icon = RoomParameterIcons.Pressure,
                                trend = currentRoomData.pressureTrend
                            )
                        )
                        if (currentRoomData.airQuality.isNotBlank()) {
                            add(
                                SectionItemUIData(
                                    label = "Jakość Powietrza",
                                    value = currentRoomData.airQuality,
                                    icon = RoomParameterIcons.AirQuality,
                                    trend = currentRoomData.airQualityTrend
                                )
                            )
                        }
                    }


                RoomViewSection(
                    title = "Parametry Pomieszczenia",
                    items = roomParameters,
                    modifier = Modifier.fillMaxWidth()
                )


                ThreatsTile(
                    coDetected = currentRoomData.coDetected,
                    otherGasesDetected = currentRoomData.otherGasesDetected,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun RoomViewPreview_ExistingRoom_CorrectLayout() {
    val navController = rememberNavController()
    PokojowkamobileTheme {
        RoomView(navController = navController, roomId = "r1")
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun RoomViewPreview_NonExistingRoom_CorrectLayout() {
    val navController = rememberNavController()
    PokojowkamobileTheme {
        RoomView(navController = navController, roomId = "nonexistent")
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun RoomViewPreview_NullRoomId_CorrectLayout() {
    val navController = rememberNavController()
    PokojowkamobileTheme {
        RoomView(navController = navController, roomId = null)
    }
}

