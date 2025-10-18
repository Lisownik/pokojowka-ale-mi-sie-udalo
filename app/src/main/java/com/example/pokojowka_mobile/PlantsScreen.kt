package com.example.pokojowka_mobile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import com.example.pokojowka_mobile.data.*
import com.example.pokojowka_mobile.ui.components.AppBottomNavigationBar
import com.example.pokojowka_mobile.ui.components.PlantsSection
import com.example.pokojowka_mobile.ui.components.SharedHeader
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantsScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }


    val currentUserData by userSettingsManager.userPreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserData(userName = "", lastName = "",  connectedDevices = emptyList())
    )
    val allCustomizations by userSettingsManager.plantCustomizationsFlow.collectAsStateWithLifecycle(
        initialValue = emptyMap()
    )


    val displayPlants = remember(allCustomizations) {
        samplePlantsGlobal.map { defaultPlant ->
            val customization = allCustomizations[defaultPlant.id]
            if (customization != null) {

                defaultPlant.copy(
                    name = customization.customName,
                    icon = PlantIconMap.getIconByName(customization.iconName) ?: defaultPlant.icon
                )
            } else {

                defaultPlant
            }
        }
    }



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
        modifier = modifier.fillMaxSize()
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
                    println("Powiadomienia kliknięte z PlantsScreen!")
                }
            )

            PlantsSection(

                plants = displayPlants,
                onPlantClick = { plantId ->
                    Log.d("PlantsScreen", "Kliknięto roślinę o ID: $plantId. Nawigacja do PlantView.")
                    navController.navigate(
                        AppDestinations.PLANT_VIEW_SCREEN.replace(
                            oldValue = "{${AppDestinations.PLANT_ID_ARG}}",
                            newValue = plantId
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun PlantsScreenPreview() {
    val navController = rememberNavController()
    PokojowkamobileTheme {
        PlantsScreen(navController = navController)
    }
}
