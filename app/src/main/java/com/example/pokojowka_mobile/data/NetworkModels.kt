package com.example.pokojowka_mobile.data

data class InnerBulbData(
    val power: String,
    val brightness: Int
)

data class NetworkBulbData(
    val id: String,
    val name: String,
    val data: InnerBulbData
)

data class InnerRoomData(
    val date: String,
    val temperature: Double,
    val humidity: Double,
    val pressure: Double,
    val quality: Int,
    val co: Boolean,
    val gasses: Boolean
)

data class NetworkRoomData(
    val id: String,
    val name: String,
    val data: InnerRoomData
)


