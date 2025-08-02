package com.example.pokojowka_mobile.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Weekend

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.pokojowka_mobile.AppDestinations
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.RoomsGradientStart



sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(AppDestinations.HOME_SCREEN, Icons.Filled.Home, "Dom")
    object Rooms : BottomNavItem(AppDestinations.ROOMS_SCREEN, Icons.Filled.Weekend, "Pokoje")
    object Plants : BottomNavItem(AppDestinations.PLANTS_SCREEN, Icons.Filled.Grass, "Rośliny")
    object Bulbs : BottomNavItem(AppDestinations.BULBS_SCREEN, Icons.Filled.Lightbulb, "Żarówki")
    object Profile : BottomNavItem(AppDestinations.PROFILE_SCREEN, Icons.Filled.Person, "Profil")
}

@Composable
fun AppBottomNavigationBar(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onItemSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Rooms,
        BottomNavItem.Plants,
        BottomNavItem.Bulbs,
        BottomNavItem.Profile
    )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        onItemSelected(screen.route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RoomsGradientStart,
                    selectedTextColor = RoomsGradientStart,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(LocalAbsoluteTonalElevation.current + NavigationBarDefaults.Elevation)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomNavigationBarPreviewHomeSelected() {
    PokojowkamobileTheme {
        AppBottomNavigationBar(
            currentRoute = BottomNavItem.Home.route,
            onItemSelected = { println("Wybrano: $it") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomNavigationBarPreviewRoomsSelected() {
    PokojowkamobileTheme {
        AppBottomNavigationBar(
            currentRoute = BottomNavItem.Rooms.route,
            onItemSelected = { println("Wybrano: $it") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomNavigationBarPreviewPlantsSelected() {
    PokojowkamobileTheme {
        AppBottomNavigationBar(
            currentRoute = BottomNavItem.Plants.route,
            onItemSelected = { println("Wybrano: $it") }
        )
    }
}