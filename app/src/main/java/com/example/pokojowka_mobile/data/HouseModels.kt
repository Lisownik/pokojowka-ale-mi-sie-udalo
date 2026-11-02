package com.example.pokojowka_mobile.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.ui.graphics.vector.ImageVector

data class HouseData(
    val id: String = "house_main",
    val name: String = "Mój Inteligentny Dom",
    val isDoorLocked: Boolean = true,
    val isDoorOpen: Boolean = false,
    val rainDetected: Boolean = false,
    val outdoorTemp: String = "19.0°C",
    val weatherCondition: String = "Słonecznie",
    val weatherState: String = "dark-rain", // "sunny", "dark", "sunny-rain", "dark-rain"
    val icon: ImageVector = Icons.Filled.HomeWork,
    val lastUpdated: Long = System.currentTimeMillis()
)

val SampleHouseData = HouseData()
