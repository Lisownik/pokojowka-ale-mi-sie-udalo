package com.example.pokojowka_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.RoomView
import com.example.pokojowka_mobile.PlantView
import com.example.pokojowka_mobile.screens.BulbsScreen
import com.example.pokojowka_mobile.screens.BulbView
import com.example.pokojowka_mobile.screens.ProfileScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokojowkamobileTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController() as NavHostController

    NavHost(
        navController = navController,
        startDestination = AppDestinations.FORM_SCREEN
    ) {

        composable(AppDestinations.FORM_SCREEN) {

            DeviceForm(
                navController = navController,
                onFormSubmit = {

                    navController.navigate(AppDestinations.HOME_SCREEN) {
                        popUpTo(AppDestinations.FORM_SCREEN) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

            )
        }

        // Ekran Hoe
        composable(AppDestinations.HOME_SCREEN) {
            HomeScreen(navController = navController)
        }

        // Ekran Pokoików
        composable(AppDestinations.ROOMS_SCREEN) {
            RoomsScreen(navController = navController)
        }

        // Ekran Roślinek
        composable(AppDestinations.PLANTS_SCREEN) {
            PlantsScreen(navController = navController)
        }

        // Ekran Bulbów
        composable(AppDestinations.BULBS_SCREEN) {
            BulbsScreen(navController = navController)
        }

        // Ekran Profily7wuaighdwlyuiavfdtkghawgwda
        composable(AppDestinations.PROFILE_SCREEN) {
            ProfileScreen(navController = navController)
        }

        // View Pokoiku
        composable(
            route = AppDestinations.ROOM_VIEW_SCREEN,
            arguments = listOf(navArgument(AppDestinations.ROOM_ID_ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString(AppDestinations.ROOM_ID_ARG)
            if (roomId != null) {
                RoomView(navController = navController, roomId = roomId)
            }
        }

        composable(
            route = AppDestinations.PLANT_VIEW_SCREEN,
            arguments = listOf(navArgument(AppDestinations.PLANT_ID_ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getString(AppDestinations.PLANT_ID_ARG)
            if (plantId != null) {
                PlantView(navController = navController, plantId = plantId)
            }
        }

        composable(
            route = AppDestinations.BULB_VIEW_SCREEN,
            arguments = listOf(navArgument(AppDestinations.BULB_ID_ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val bulbId = backStackEntry.arguments?.getString(AppDestinations.BULB_ID_ARG)
            if (bulbId != null) {
                BulbView(navController = navController, bulbId = bulbId)
            } else {
                androidx.compose.material3.Text("Błąd: Brak ID żarówki")
            }
        }


    }
}


