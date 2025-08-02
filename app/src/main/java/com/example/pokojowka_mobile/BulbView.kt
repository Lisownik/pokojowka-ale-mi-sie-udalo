package com.example.pokojowka_mobile.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pokojowka_mobile.AppDestinations
import com.example.pokojowka_mobile.data.BulbData
import com.example.pokojowka_mobile.data.sampleBulbsGlobalList

import com.example.pokojowka_mobile.ui.components.AppBottomNavigationBar
import com.example.pokojowka_mobile.ui.components.BulbViewItem
import com.example.pokojowka_mobile.ui.components.BulbViewItemType
import com.example.pokojowka_mobile.ui.components.BulbViewSection
import com.example.pokojowka_mobile.ui.components.SharedHeader
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import kotlin.math.roundToInt
import com.example.pokojowka_mobile.data.UserData
import com.example.pokojowka_mobile.data.EnvironmentData
import com.example.pokojowka_mobile.data.SampleUserData

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokojowka_mobile.data.UserSettingsManager

class BulbViewModel : ViewModel() {
    private val _bulbState = mutableStateOf<BulbData?>(null)
    val bulbState: State<BulbData?> = _bulbState
    private var originalBulbId: String? = null

    fun loadBulb(bulbId: String) {
        originalBulbId = bulbId
        val bulb = sampleBulbsGlobalList.find { it.id == bulbId }?.copy()
        _bulbState.value = bulb
        if (bulb == null) {
            Log.e("BulbViewModel", "Nie znaleziono żarówki o ID: $bulbId")
        }
    }

    fun updateBulbSwitched(newState: Boolean) {
        _bulbState.value = _bulbState.value?.copy(isSwitchedOn = newState)
        saveCurrentBulbState()
    }

    fun updateBulbBrightness(newBrightness: Int) {
        val clampedBrightness = newBrightness.coerceIn(0, 100)
        _bulbState.value = _bulbState.value?.copy(brightnessPercentage = clampedBrightness)
        saveCurrentBulbState()
    }

    fun updateBulbColorTemperature(newTemperature: Int) {
        val clampedTemp = newTemperature.coerceIn(BulbData.MIN_KELVIN, BulbData.MAX_KELVIN)
        _bulbState.value = _bulbState.value?.copy(colorTemperatureKelvin = clampedTemp)
        saveCurrentBulbState()
    }

    private fun saveCurrentBulbState() {
        val currentBulb = _bulbState.value
        val id = originalBulbId
        if (currentBulb != null && id != null) {
            val index = sampleBulbsGlobalList.indexOfFirst { it.id == id }
            if (index != -1) {
                (sampleBulbsGlobalList as? MutableList<BulbData>)?.set(index, currentBulb.copy())
                Log.d("BulbViewModel", "Zaktualizowano żarówkę (symulacja zapisu) ID: $id -> $currentBulb")
            }
        }
    }
}

@Composable
fun BulbView(
    navController: NavHostController,
    bulbId: String,
    viewModel: BulbViewModel = viewModel(key = bulbId)
) {
    LaunchedEffect(bulbId) {
        viewModel.loadBulb(bulbId)
    }

    val bulb by viewModel.bulbState
    val scrollState = rememberScrollState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }


    val currentUserData by userSettingsManager.userPreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserData(userName = "", lastName = "", selectedHub = "", connectedDevices = emptyList())
    )

    val currentEnvironmentData = remember { SampleUserData.defaultEnvironment }

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
        containerColor = MaterialTheme.colorScheme.background
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

            if (bulb == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Ładowanie danych żarówki...")
                }
            } else {
                val currentBulb = bulb!!


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp, bottom = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { navController.popBackStack() },

                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Wróć"
                                )
                            }

                            Text(
                                text = "Ustawienia Ogólne",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(12.dp))


                    BulbViewItem(
                        itemName = "Nazwa",
                        itemValueString = currentBulb.roomName,
                        itemIcon = Icons.Filled.Label
                    )
                    BulbViewItem(
                        itemName = "Stan",
                        itemIcon = Icons.Filled.PowerSettingsNew,
                        itemType = BulbViewItemType.SWITCH,
                        isChecked = currentBulb.isSwitchedOn,
                        onCheckedChange = { viewModel.updateBulbSwitched(it) }
                    )
                }



                BulbViewSection(
                    title = "Parametry Światła",

                ) {
                    BulbViewItem(
                        itemName = "Jasność",
                        itemIcon = Icons.Filled.BrightnessHigh,
                        itemType = BulbViewItemType.SLIDER,
                        sliderValue = currentBulb.brightnessPercentage.toFloat(),
                        onSliderValueChange = { viewModel.updateBulbBrightness(it.roundToInt()) },
                        sliderValueRange = 0f..100f,
                        sliderSteps = 99,
                        sliderValueRepresentation = { "${it.roundToInt()}%" },
                        enabled = currentBulb.isSwitchedOn
                    )
                    BulbViewItem(
                        itemName = "Temperatura Barwowa",
                        itemIcon = Icons.Filled.Thermostat,
                        itemType = BulbViewItemType.SLIDER,
                        sliderValue = currentBulb.colorTemperatureKelvin.toFloat(),
                        onSliderValueChange = { viewModel.updateBulbColorTemperature(it.roundToInt()) },
                        sliderValueRange = BulbData.MIN_KELVIN.toFloat()..BulbData.MAX_KELVIN.toFloat(),
                        sliderSteps = ((BulbData.MAX_KELVIN - BulbData.MIN_KELVIN) / 50) - 1,
                        sliderValueRepresentation = { "${it.roundToInt()}K" },
                        enabled = currentBulb.isSwitchedOn
                    )
                }


                BulbViewSection(
                    title = "Informacje Dodatkowe",

                ) {
                    BulbViewItem(
                        itemName = "ID Urządzenia",
                        itemValueString = currentBulb.id,
                        itemIcon = Icons.Filled.Fingerprint
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun BulbViewPreview_Loaded() {
    PokojowkamobileTheme {
        val previewViewModel = viewModel<BulbViewModel>(key = "preview_salon_main_loaded_v5")
        LaunchedEffect(Unit) {
            previewViewModel.loadBulb("salon_main")
        }
        BulbView(navController = rememberNavController(), bulbId = "salon_main", viewModel = previewViewModel)
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun BulbViewPreview_Loading() {
    PokojowkamobileTheme {
        val previewViewModel = viewModel<BulbViewModel>(key = "preview_loading_v5")
        LaunchedEffect(Unit) {
            previewViewModel.loadBulb("non_existent_id_for_loading_preview_v5")
        }
        BulbView(navController = rememberNavController(), bulbId = "non_existent_id_for_loading_preview_v5", viewModel = previewViewModel)
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun BulbViewPreview_BulbOff() {
    PokojowkamobileTheme {
        val previewViewModel = viewModel<BulbViewModel>(key = "preview_bedroom_night_off_v5")
        LaunchedEffect(Unit) {
            previewViewModel.loadBulb("bedroom_night")
        }
        BulbView(navController = rememberNavController(), bulbId = "bedroom_night", viewModel = previewViewModel)
    }
}

