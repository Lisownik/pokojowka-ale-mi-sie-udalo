package com.example.pokojowka_mobile.data

import androidx.lifecycle.LiveData

data class UserData(
    val userName: String,
    val lastName: String,
    val connectedDevices: List<String> 
)

data class EnvironmentData(
    val temperature: String,
    val humidity: String
)

object SampleUserData {
    val defaultUser = UserData(
        userName = "Wojtek",
        lastName = "Kowalski",
        connectedDevices = listOf("Światło Salon", "Termostat Sypialnia", "Czujnik Drzwi")
    )
    val defaultEnvironment = EnvironmentData(temperature = "22°C", humidity = "65%")
}

