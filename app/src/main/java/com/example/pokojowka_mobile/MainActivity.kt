package com.example.pokojowka_mobile

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokojowka_mobile.components.ProgrammableActionButton
import com.example.pokojowka_mobile.data.NotificationHelper
import com.example.pokojowka_mobile.data.UserSettingsManager
import com.example.pokojowka_mobile.network.repos.BulbsRepository
import com.example.pokojowka_mobile.network.repos.RoomsRepository
import com.example.pokojowka_mobile.network.repos.WindowsRepository
import com.example.pokojowka_mobile.notifications.Notifier
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Notifier.init(this)

        MainScope().launch {
            while(true) {
                RoomsRepository.refresh()
                BulbsRepository.refresh()
                delay(3000.milliseconds)
            }
        }

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

@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val userSettingsManager = remember { UserSettingsManager(context) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Obserwujemy, czy imię użytkownika jest zapisane.
    val isLoggedIn by userSettingsManager.userPreferencesFlow
        .map { it.userName.isNotBlank() }
        .collectAsState(initial = null)

    if (isLoggedIn != null) {
        val startDestination = if (isLoggedIn == true) {
            AppDestinations.HOME_SCREEN
        } else {
            AppDestinations.FORM_SCREEN
        }

        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = startDestination
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

                composable(AppDestinations.HOME_SCREEN) {
                    RoomsRepository.refresh()
                    BulbsRepository.refresh()
                    WindowsRepository.refresh()
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

                composable(AppDestinations.WINDOWS_SCREEN) {
                    WindowsScreen(navController = navController)
                }

                composable(
                    route = AppDestinations.WINDOW_VIEW_SCREEN,
                    arguments = listOf(navArgument(AppDestinations.WINDOW_ID_ARG) {
                        type = NavType.StringType
                    })
                ) { backStackEntry ->
                    val windowId = backStackEntry.arguments?.getString(AppDestinations.WINDOW_ID_ARG)
                    if (windowId != null) {
                        WindowView(navController = navController, windowId = windowId)
                    } else {
                        Text("Błąd: Brak ID okna")
                    }
                }
            }

//         Nad wszytsim
            if (currentRoute != AppDestinations.FORM_SCREEN && isLoggedIn == true) {
                ProgrammableActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 104.dp, end = 10.dp)
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
