package com.example.pokojowka_mobile.data

data class UserData(
    val userName: String,
    val lastName: String,
    val selectedHub: String,           
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
        selectedHub = "Centrala Domowa XYZ", 
        connectedDevices = listOf("Światło Salon", "Termostat Sypialnia", "Czujnik Drzwi")
    )
    val defaultEnvironment = EnvironmentData(temperature = "22°C", humidity = "65%")
}

