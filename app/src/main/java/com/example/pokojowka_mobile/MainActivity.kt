package com.example.pokojowka_mobile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokojowka_mobile.data.NotificationHelper
import com.example.pokojowka_mobile.data.UserSettingsManager
import com.example.pokojowka_mobile.screens.BulbView
import com.example.pokojowka_mobile.screens.BulbsScreen
import com.example.pokojowka_mobile.screens.ProfileScreen
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannels(this)
        setContent {
            PokojowkamobileTheme {
                AppNavigation()
            }
        }
        Log.d("MainActivity -> OnCreate()", "OnCreate")
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
    val navController = rememberNavController()
    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }

    // Obserwujemy, czy imię użytkownika jest zapisane.
    // Dajemy wartość początkową `null`, żeby odróżnić stan "nie wiem jeszcze" od "wiem, że jest pusty".
    val isLoggedIn by userSettingsManager.userPreferencesFlow
        .map { it.userName.isNotBlank() } // Konwertujemy UserData na prostą wartość true/false
        .collectAsState(initial = null)

    // Jeśli `isLoggedIn` nie jest już `null` (czyli mamy odpowiedź z DataStore),
    // możemy zbudować interfejs.
    if (isLoggedIn != null) {
        // Wybieramy ekran startowy na podstawie tego, czy użytkownik jest zalogowany
        val startDestination = if (isLoggedIn == true) {
            AppDestinations.HOME_SCREEN
        } else {
            AppDestinations.FORM_SCREEN
        }

        NavHost(
            navController = navController,
            startDestination = startDestination // Używamy dynamicznie wybranej trasy startowej
        ) {
            composable(AppDestinations.FORM_SCREEN) {
                DeviceForm(
                    navController = navController,
                    onFormSubmit = {
                        // Po zalogowaniu przechodzimy do ekranu głównego i czyścimy historię
                        navController.navigate(AppDestinations.HOME_SCREEN) {
                            popUpTo(AppDestinations.FORM_SCREEN) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Reszta definicji ekranów pozostaje bez zmian
            composable(AppDestinations.HOME_SCREEN) {
                HomeScreen(navController = navController)
            }

            composable(AppDestinations.ROOMS_SCREEN) {
                RoomsScreen(navController = navController)
            }

            composable(AppDestinations.PLANTS_SCREEN) {
                PlantsScreen(navController = navController)
            }

            composable(AppDestinations.BULBS_SCREEN) {
                BulbsScreen(navController = navController)
            }

            composable(AppDestinations.PROFILE_SCREEN) {
                ProfileScreen(navController = navController)
            }

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
    } else {
        // Opcjonalnie: Pokaż wskaźnik ładowania, dopóki `isLoggedIn` jest `null`.
        // To będzie widoczne tylko przez ułamek sekundy przy pierwszym starcie.
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
