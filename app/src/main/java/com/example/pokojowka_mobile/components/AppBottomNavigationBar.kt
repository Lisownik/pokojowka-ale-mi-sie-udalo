package com.example.pokojowka_mobile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.pokojowka_mobile.AppDestinations
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.RoomsGradientStart
import com.example.pokojowka_mobile.ui.theme.tlo

data class SectionInfo(
    val icon: ImageVector,
    val label: String,
    val route: String
)

private var globalLastSelectedSection by mutableStateOf(SectionInfo(Icons.Filled.Home, "Dom", AppDestinations.HOME_SCREEN))

private fun getActiveSectionInfo(currentRoute: String?, previousSection: SectionInfo): SectionInfo {
    if (currentRoute == null) return previousSection
    return when {
        currentRoute == AppDestinations.HOME_SCREEN -> SectionInfo(Icons.Filled.Home, "Dom", AppDestinations.HOME_SCREEN)
        currentRoute == AppDestinations.ROOMS_SCREEN || currentRoute.startsWith(AppDestinations.ROOM_VIEW_SCREEN_ROUTE) -> SectionInfo(Icons.Filled.Weekend, "Pokoje", AppDestinations.ROOMS_SCREEN)
        currentRoute == AppDestinations.WINDOWS_SCREEN || currentRoute.startsWith(AppDestinations.WINDOW_VIEW_SCREEN_ROUTE) -> SectionInfo(Icons.Filled.Window, "Okna", AppDestinations.WINDOWS_SCREEN)
        currentRoute == AppDestinations.HEATERS_SCREEN || currentRoute.startsWith(AppDestinations.HEATER_VIEW_SCREEN_ROUTE) -> SectionInfo(Icons.Filled.Whatshot, "Grzejniki", AppDestinations.HEATERS_SCREEN)
        currentRoute == AppDestinations.PLANTS_SCREEN || currentRoute.startsWith(AppDestinations.PLANT_VIEW_SCREEN_ROUTE) -> SectionInfo(Icons.Filled.Grass, "Rośliny", AppDestinations.PLANTS_SCREEN)
        currentRoute == AppDestinations.BULBS_SCREEN || currentRoute.startsWith(AppDestinations.BULB_VIEW_SCREEN_ROUTE) -> SectionInfo(Icons.Filled.Lightbulb, "Żarówki", AppDestinations.BULBS_SCREEN)
        currentRoute == AppDestinations.PROFILE_SCREEN -> previousSection
        else -> previousSection
    }
}

private val menuSections = listOf(
    SectionInfo(Icons.Filled.Home, "Strona Główna", AppDestinations.HOME_SCREEN),
    SectionInfo(Icons.Filled.Weekend, "Pokojówki", AppDestinations.ROOMS_SCREEN),
    SectionInfo(Icons.Filled.Window, "Smart Okna", AppDestinations.WINDOWS_SCREEN),
    SectionInfo(Icons.Filled.Whatshot, "Grzejniki", AppDestinations.HEATERS_SCREEN),
    SectionInfo(Icons.Filled.Grass, "Rośliny", AppDestinations.PLANTS_SCREEN),
    SectionInfo(Icons.Filled.Lightbulb, "Żarówki", AppDestinations.BULBS_SCREEN)
)

@Composable
fun AppBottomNavigationBar(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onItemSelected: (String) -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    val activeSection = remember(currentRoute) {
        val calculated = getActiveSectionInfo(currentRoute, globalLastSelectedSection)
        if (currentRoute != AppDestinations.PROFILE_SCREEN && currentRoute != null) {
            globalLastSelectedSection = calculated
        }
        calculated
    }

    val isProfileSelected = currentRoute == AppDestinations.PROFILE_SCREEN

    val visibleState = remember { MutableTransitionState(false) }
    LaunchedEffect(isMenuExpanded) {
        visibleState.targetState = isMenuExpanded
    }

    val density = LocalDensity.current
    val popupOffsetX = with(density) { 16.dp.roundToPx() }
    val popupOffsetY = with(density) { (-84).dp.roundToPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 6.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (visibleState.targetState || visibleState.currentState) {
            Popup(
                alignment = Alignment.BottomStart,
                offset = IntOffset(x = popupOffsetX, y = popupOffsetY),
                onDismissRequest = { isMenuExpanded = false },
                properties = PopupProperties(focusable = true, dismissOnClickOutside = true)
            ) {
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = fadeIn(animationSpec = tween(220)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(220)
                    ),
                    exit = fadeOut(animationSpec = tween(180)) + slideOutVertically(
                        targetOffsetY = { it / 2 },
                        animationSpec = tween(180)
                    )
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = tlo,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier
                                .width(200.dp)
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            menuSections.forEach { section ->
                                val isCurrentSection = activeSection.route == section.route
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            isMenuExpanded = false
                                            onItemSelected(section.route)
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isCurrentSection && !isProfileSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = section.icon,
                                            contentDescription = section.label,
                                            tint = if (isCurrentSection && !isProfileSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = section.label,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isCurrentSection && !isProfileSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isCurrentSection && !isProfileSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3-POZYCYJNY GŁÓWNY DOCK
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = tlo,
            shadowElevation = 8.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEWY PRZYCISK: DYNAMICZNY WYBÓR EKRANU (STAŁA SZEROKOŚĆ 84.dp)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .width(84.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            if (isProfileSelected) {
                                isMenuExpanded = false
                                onItemSelected(activeSection.route)
                            } else {
                                isMenuExpanded = !isMenuExpanded
                            }
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = activeSection.icon,
                        contentDescription = "Menu Sekcji",
                        tint = if (!isProfileSelected) RoomsGradientStart else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = activeSection.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (!isProfileSelected) RoomsGradientStart else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1
                    )
                }

                // ŚRODKOWY PRZYCISK: ZINTEGROWANY PRZYCISK AKCJI (FAB)
                ProgrammableActionButton()

                // PRAWY PRZYCISK: PROFIL (STAŁA SZEROKOŚĆ 84.dp)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .width(84.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            isMenuExpanded = false
                            onItemSelected(AppDestinations.PROFILE_SCREEN)
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profil",
                        tint = if (isProfileSelected) RoomsGradientStart else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Profil",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isProfileSelected) RoomsGradientStart else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomNavigationBarPreviewHomeSelected() {
    PokojowkamobileTheme {
        AppBottomNavigationBar(
            currentRoute = AppDestinations.HOME_SCREEN,
            onItemSelected = { println("Wybrano: $it") }
        )
    }
}
