package com.example.pokojowka_mobile

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokojowka_mobile.data.NotificationHelper
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.screens.BulbsScreen
import com.example.pokojowka_mobile.screens.BulbView
import com.example.pokojowka_mobile.screens.ProfileScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout


class MainActivity : ComponentActivity() {
    private val channelId = "sample_channel"
    private val notificationId = 101
    private lateinit var notificationManager: NotificationManagerCompat

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannels(this)
//        NotificationHelper.showHeadsUpNotification(this, "Test", "Bardzo długi tekst")
 setContent {
            PokojowkamobileTheme {
                AppNavigation()
            }
        }


        Log.d("MainActivity -> OnCreate()", "OnCreate")
//        Handler().postDelayed({
//            NotificationHelper.showGasLeakAlert(this, "Kuchnia", "wysokie")
//        }, 10000)
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkNotificationPermission()
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
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
                Text("Błąd: Brak ID żarówki")
            }
        }


    }
}


