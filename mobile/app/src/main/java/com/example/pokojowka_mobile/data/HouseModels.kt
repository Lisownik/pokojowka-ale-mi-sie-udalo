package com.example.pokojowka_mobile.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.ui.graphics.vector.ImageVector


enum class WeatherState(val text: String){
    SUNNY("sunny"),
    DARK("dark"),
    SUNNY_RAIN("sunny-rain"),
    DARK_RAIN("dark-rain")
}

fun parseWeather(rainDetected: Boolean, light: Int): WeatherState{
    if(light > 50){
        return if(rainDetected) WeatherState.SUNNY_RAIN else WeatherState.SUNNY
    }
    return if(rainDetected) WeatherState.DARK_RAIN else WeatherState.DARK
}

data class HouseData(
    val id: String = "house_main",
    val name: String = "Mój Inteligentny Dom",
    val isDoorLocked: Boolean = true,
    val isDoorOpen: Boolean = false,
    val rainDetected: Boolean = false,
    val outdoorTemp: String = "19.0°C",
    val weatherCondition: String = "Słonecznie",
    val weatherState: WeatherState = WeatherState.DARK_RAIN,
    val icon: ImageVector = Icons.Filled.HomeWork,
    val lastUpdated: Long = System.currentTimeMillis()
)

val SampleHouseData = HouseData()
