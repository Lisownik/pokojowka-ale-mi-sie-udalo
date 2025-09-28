package com.example.pokojowka_mobile.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundBathroom
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundBed
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundKitchen
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundLiving
import com.example.pokojowka_mobile.ui.theme.RoomStatusBad
import com.example.pokojowka_mobile.ui.theme.RoomStatusGood
import com.example.pokojowka_mobile.ui.theme.RoomStatusMedium
import com.example.pokojowka_mobile.ui.theme.RoomStatusUnknown

enum class RoomStatus {
    GOOD, MEDIUM, BAD, UNKNOWN
}

enum class ValueTrend {
    UP, DOWN, SAME, UNKNOWN
}

data class TrendData (
    val trend: ValueTrend,
    val difference: String
)

data class RoomData(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val status: RoomStatus,
    val temperature: String,
    val humidity: String,
    val airQuality: String,
    val pressure: String,

    val temperatureTrend: TrendData? = null,
    val humidityTrend: TrendData? = null,
    val pressureTrend: TrendData? = null,
    val airQualityTrend: TrendData? = null,

    val coDetected: Boolean = false,
    val otherGasesDetected: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
) {

    val statusColor: Color
        get() = when (status) {
            RoomStatus.GOOD -> RoomStatusGood
            RoomStatus.MEDIUM -> RoomStatusMedium
            RoomStatus.BAD -> RoomStatusBad
            RoomStatus.UNKNOWN -> RoomStatusUnknown
        }
}

var sampleRoomsGlobal: MutableList<RoomData> = mutableListOf(
    RoomData(
        id = "r1", name = "Sypialnia", icon = Icons.Filled.Bed, backgroundColor = RoomIconBackgroundBed,
        status = RoomStatus.GOOD, temperature = "22°C", humidity = "45%", airQuality = "Dobra", pressure = "1012 hPa",
        temperatureTrend = TrendData(ValueTrend.UP, "+1°C"),
        humidityTrend = TrendData(ValueTrend.SAME, "Bez zmian"),
        pressureTrend = TrendData(ValueTrend.DOWN, "-2 hPa"),
        coDetected = true,
        otherGasesDetected = false
    ),
    RoomData(
        id = "r2", name = "Salon", icon = Icons.Filled.Weekend, backgroundColor = RoomIconBackgroundLiving,
        status = RoomStatus.MEDIUM, temperature = "24°C", humidity = "60%", airQuality = "Średnia", pressure = "1010 hPa",
        temperatureTrend = TrendData(ValueTrend.DOWN, "-0.5°C"),
        humidityTrend = TrendData(ValueTrend.UP, "+5%"),
        coDetected = false,
        otherGasesDetected = false
    ),
    RoomData(
        id = "r3", name = "Kuchnia", icon = Icons.Filled.Kitchen, backgroundColor = RoomIconBackgroundKitchen,
        status = RoomStatus.BAD, temperature = "26°C", humidity = "70%", airQuality = "Słaba", pressure = "1008 hPa",
        temperatureTrend = TrendData(ValueTrend.UP, "+2°C"),
        coDetected = false,
        otherGasesDetected = true

    ),
    RoomData(
        id = "r4", name = "Łazienka", icon = Icons.Filled.Bathtub, backgroundColor = RoomIconBackgroundBathroom,
        status = RoomStatus.UNKNOWN, temperature = "21°C", humidity = "55%", airQuality = "Średnia", pressure = "1011 hPa",
        coDetected = true,
        otherGasesDetected = false


    )
)