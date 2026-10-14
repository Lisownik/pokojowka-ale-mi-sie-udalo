package com.example.pokojowka_mobile.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Window
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundBed
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundKitchen
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundLiving
import com.example.pokojowka_mobile.ui.theme.RoomStatusBad
import com.example.pokojowka_mobile.ui.theme.RoomStatusGood

enum class WindowStatus {
    CLOSED,
    OPEN
}

data class WindowData(
    val id: String,
    val name: String,
    val roomName: String,
    val icon: ImageVector = Icons.Filled.Window,
    val backgroundColor: Color = RoomIconBackgroundLiving,
    val isOpen: Boolean = false,
    val rainDetected: Boolean = false,
    val outdoorTemp: String = "18°C",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val status: WindowStatus
        get() = if (isOpen) WindowStatus.OPEN else WindowStatus.CLOSED

    val statusText: String
        get() = if (isOpen) "Otwarte" else "Zamknięte"

    val statusColor: Color
        get() = if (isOpen) RoomStatusGood else RoomStatusBad
}

val GlobalWindowsList: MutableList<WindowData> = mutableListOf(
    WindowData(
        id = "w1",
        name = "Okno Salon",
        roomName = "Salon",
        icon = Icons.Filled.Window,
        backgroundColor = RoomIconBackgroundLiving,
        isOpen = true,
        rainDetected = false,
        outdoorTemp = "21°C"
    ),
    WindowData(
        id = "w2",
        name = "Okno Sypialnia",
        roomName = "Sypialnia",
        icon = Icons.Filled.Window,
        backgroundColor = RoomIconBackgroundBed,
        isOpen = false,
        rainDetected = false,
        outdoorTemp = "19°C"
    ),
    WindowData(
        id = "w3",
        name = "Okno Kuchnia",
        roomName = "Kuchnia",
        icon = Icons.Filled.Window,
        backgroundColor = RoomIconBackgroundKitchen,
        isOpen = false,
        rainDetected = true,
        outdoorTemp = "17°C"
    )
)
