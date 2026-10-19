package com.example.pokojowka_mobile.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundBed
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundKitchen
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundLiving
import com.example.pokojowka_mobile.ui.theme.RoomStatusUnknown

enum class HeaterStatus {
    OFF,        // Wyłączony
    HEATING     // Aktywne grzanie
}

data class HeaterData(
    val id: String,
    val name: String,
    val roomName: String,
    val icon: ImageVector = Icons.Filled.Whatshot,
    val backgroundColor: Color = RoomIconBackgroundLiving,
    val isHeatingOn: Boolean = false,
    val currentTemp: String = "21.0°C",
    val targetTemp: Float = 22.0f,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val status: HeaterStatus
        get() = if (isHeatingOn) HeaterStatus.HEATING else HeaterStatus.OFF

    val statusText: String
        get() = when (status) {
            HeaterStatus.OFF -> "Wyłączony"
            HeaterStatus.HEATING -> "Aktywne grzanie"
        }

    val statusColor: Color
        get() = when (status) {
            HeaterStatus.OFF -> RoomStatusUnknown
            HeaterStatus.HEATING -> Color(0xFFF97316) // Warm Orange
        }
}

val GlobalHeatersList: MutableList<HeaterData> = mutableListOf(
    HeaterData(
        id = "h1",
        name = "Grzejnik Salon",
        roomName = "Salon",
        icon = Icons.Filled.Whatshot,
        backgroundColor = RoomIconBackgroundLiving,
        isHeatingOn = true,
        currentTemp = "21.5°C",
        targetTemp = 23.0f
    ),
    HeaterData(
        id = "h2",
        name = "Grzejnik Sypialnia",
        roomName = "Sypialnia",
        icon = Icons.Filled.Thermostat,
        backgroundColor = RoomIconBackgroundBed,
        isHeatingOn = false,
        currentTemp = "19.5°C",
        targetTemp = 21.0f
    ),
    HeaterData(
        id = "h3",
        name = "Grzejnik Kuchnia",
        roomName = "Kuchnia",
        icon = Icons.Filled.Whatshot,
        backgroundColor = RoomIconBackgroundKitchen,
        isHeatingOn = true,
        currentTemp = "22.0°C",
        targetTemp = 22.0f
    )
)
