package com.example.pokojowka_mobile.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pokojowka_mobile.ui.theme.*


enum class PlantHealthStatus {
    HEALTHY, NEEDS_WATER, NEEDS_FERTILIZER, LOW_LIGHT, UNKNOWN
}


data class PlantData(
    val id: String,
    val name: String,
    val species: String,
    val icon: ImageVector,
    val healthStatus: PlantHealthStatus,
    val lightLevel: String,
    val soilMoisture: String,
    val airHumidity: String,
    val roomLocation: String,
    val backgroundColor: Color,
    val temperature: String
) {
    val statusColor: Color
        get() = when (healthStatus) {
            PlantHealthStatus.HEALTHY -> PlantStatusHealthy
            PlantHealthStatus.NEEDS_WATER -> PlantStatusNeedsWater
            PlantHealthStatus.NEEDS_FERTILIZER -> PlantStatusNeedsFertilizer
            PlantHealthStatus.LOW_LIGHT -> PlantStatusLowLight
            PlantHealthStatus.UNKNOWN -> PlantStatusUnknown
        }
}

public fun NetworkPlantData.toPlantData(): PlantData {
    return PlantData(
        id = this.id,
        name = this.name,
        species = "nwm",
        icon = Icons.Filled.Eco,
        healthStatus = PlantHealthStatus.HEALTHY,
        lightLevel = "${this.data.sun} lux",
        soilMoisture = "${this.data.wet}%",
        airHumidity = "${this.data.humidity}%",
        roomLocation = "nwm",
        backgroundColor = RoomIconBackgroundBed,
        temperature = "${this.data.temperature}°C",
    )
}
var GlobalPlantsList: MutableList<PlantData> = mutableListOf(
    PlantData(
        id = "p1",
        name = "Monstera",
        species = "Deliciosa",
        icon = Icons.Filled.Eco,
        healthStatus = PlantHealthStatus.HEALTHY,
        lightLevel = "750 lux",
        soilMoisture = "60%",
        airHumidity = "55%",
        roomLocation = "Salon",
        backgroundColor = RoomIconBackgroundBed,
        temperature = "22.5°C"
    ),
    PlantData(
        id = "p2",
        name = "Fikus",
        species = "Benjamina",
        icon = Icons.Filled.Grass,
        healthStatus = PlantHealthStatus.NEEDS_WATER,
        lightLevel = "500 lux",
        soilMoisture = "25%",
        airHumidity = "50%",
        roomLocation = "Sypialnia",
        backgroundColor = RoomIconBackgroundLiving,
        temperature = "21.0°C"
    ),
    PlantData(
        id = "p3",
        name = "Storczyk",
        species = "Phalaenopsis",
        icon = Icons.Filled.LocalFlorist,
        healthStatus = PlantHealthStatus.NEEDS_FERTILIZER,
        lightLevel = "400 lux",
        soilMoisture = "50%",
        airHumidity = "65%",
        roomLocation = "Parapet",
        backgroundColor = RoomIconBackgroundKitchen,
        temperature = "23.1°C"
    ),
    PlantData(
        id = "p4",
        name = "Zamiokulkas",
        species = "Zamiifolia",
        icon = Icons.Filled.FilterVintage,
        healthStatus = PlantHealthStatus.LOW_LIGHT,
        lightLevel = "200 lux",
        soilMoisture = "45%",
        airHumidity = "45%",
        roomLocation = "Biuro",
        backgroundColor = RoomIconBackgroundBathroom,
        temperature = "20.5°C"
    )
)
