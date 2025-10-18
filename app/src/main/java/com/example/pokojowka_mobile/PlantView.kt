package com.example.pokojowka_mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pokojowka_mobile.data.*
import com.example.pokojowka_mobile.network.AuthViewModel
import com.example.pokojowka_mobile.ui.components.AppBottomNavigationBar
import com.example.pokojowka_mobile.ui.components.PlantViewSection
import com.example.pokojowka_mobile.ui.components.SharedHeader
import kotlinx.coroutines.launch
import com.example.pokojowka_mobile.ui.components.PlantUIData

// Definicje ikon specyficznych dla roślin
private object PlantParameterIcons {
    val Light: ImageVector = Icons.Filled.WbSunny
    val Moisture: ImageVector = Icons.Filled.Opacity
    val Air: ImageVector = Icons.Filled.Air
    val Location: ImageVector = Icons.Filled.Place
    val Temperature: ImageVector = Icons.Filled.Thermostat
}

@Composable
fun PlantViewInternalHeader(
    plantName: String,
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
            text = plantName,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

// Komponent do wyświetlania AlertDialog z opcjami edycji
@Composable
fun EditPlantDialog(
    plant: PlantData,
    customizations: PlantCustomization?,
    onDismiss: () -> Unit,
    onConfirm: (newName: String, newIconName: String) -> Unit
) {
    var tempName by remember { mutableStateOf(customizations?.customName ?: plant.name) }
    val defaultIconName = customizations?.iconName ?: PlantIconMap.icons.entries.find { it.value == plant.icon }?.key
    var selectedIconName by remember { mutableStateOf(defaultIconName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edytuj Roślinę") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Nazwa rośliny") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Wybierz ikonę", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 56.dp),
                    modifier = Modifier.heightIn(max = 200.dp), // Ograniczenie wysokości siatki
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(PlantIconMap.icons.entries.toList()) { (name, icon) ->
                        val isSelected = selectedIconName == name
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedIconName = name },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = name,
                                modifier = Modifier.size(32.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tempName.isNotBlank() && selectedIconName != null) {
                        onConfirm(tempName, selectedIconName!!)
                    }
                },
                enabled = tempName.isNotBlank() && selectedIconName != null
            ) {
                Text("Zatwierdź")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}

@Composable
fun PlantView(
    navController: NavHostController,
    plantId: String?,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val currentUserData by userSettingsManager.userPreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserData("", "", "", emptyList())
    )
    val allCustomizations by userSettingsManager.plantCustomizationsFlow.collectAsStateWithLifecycle(initialValue = emptyMap())
    val defaultPlantData = samplePlantsGlobal.find { it.id == plantId }

    var showEditDialog by remember { mutableStateOf(false) }

    val customization = plantId?.let { allCustomizations[it] }
    val displayName = customization?.customName ?: defaultPlantData?.name
    val displayIcon = customization?.iconName?.let { PlantIconMap.getIconByName(it) } ?: defaultPlantData?.icon

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = { selectedRoute ->
                    if (currentRoute != selectedRoute) {
                        navController.navigate(selectedRoute) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        if (defaultPlantData == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Błąd: Nie znaleziono rośliny.")
                    if (plantId != null) {
                        Text("ID: $plantId")
                    }
                }
            }
        } else {
            val currentPlant = defaultPlantData

            if (showEditDialog && plantId != null) {
                EditPlantDialog(
                    plant = currentPlant,
                    customizations = customization,
                    onDismiss = { showEditDialog = false },
                    onConfirm = { newName, newIconName ->
                        coroutineScope.launch {
                            userSettingsManager.savePlantCustomization(plantId, newName, newIconName)
                        }
                        showEditDialog = false
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .verticalScroll(scrollState)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Ta część wymaga ViewModel, na razie używamy pustych danych
                val authViewModel: AuthViewModel = viewModel()
                val currentEnvironmentData by authViewModel.getAvgRooms.collectAsStateWithLifecycle()
                SharedHeader(
                    userData = currentUserData,
                    environmentData = currentEnvironmentData,
                    onNotificationClick = {}
                )

                val headerText = "${displayName ?: "..."} (${currentPlant.species})"
                PlantViewInternalHeader(
                    plantName = headerText,
                    onNavigateUp = { navController.popBackStack() },
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(currentPlant.backgroundColor),
                            contentAlignment = Alignment.Center
                        ) {
                            if (displayIcon != null) {
                                Icon(
                                    imageVector = displayIcon,
                                    contentDescription = displayName,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(90.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edytuj roślinę",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Parametry Pielęgnacji",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp, top = 16.dp)
                    )
                    Text(
                        text = "Lokalizacja: ${currentPlant.roomLocation}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                val plantParameters = listOf(
                    PlantUIData("Temperatura", currentPlant.temperature, PlantParameterIcons.Temperature),
                    PlantUIData("Poziom światła", currentPlant.lightLevel, PlantParameterIcons.Light),
                    PlantUIData("Wilgotność gleby", currentPlant.soilMoisture, PlantParameterIcons.Moisture),
                    PlantUIData("Wilgotnośc powietrza", currentPlant.airHumidity, PlantParameterIcons.Air)
                )

                PlantViewSection(
                    title = "",
                    items = plantParameters,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
