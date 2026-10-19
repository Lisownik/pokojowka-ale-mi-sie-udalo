package com.example.pokojowka_mobile.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RoomService
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundBed
import com.squareup.moshi.JsonClass

sealed interface ModuleData {
    val id: String
    val type: String
    val online: Boolean
    val lastSeen: Long
    val name: String
}

@JsonClass(generateAdapter = true)
data class InnerRoomData(
    val temperature: Double,
    val humidity: Double,
    val pressure: Double,
    val quality: Int,
)

@JsonClass(generateAdapter = true)
data class NetworkRoomData(
    override val id: String,
    override val type: String,
    override val online: Boolean,
    override val lastSeen: Long,
    val state: InnerRoomData,
    override val name: String,
) : ModuleData

fun NetworkRoomData.toRoomData(): RoomData {
    return RoomData(
        id = this.id,
        name = this.name,
        icon = Icons.Outlined.RoomService,
        backgroundColor = RoomIconBackgroundBed,
        status = RoomStatus.MEDIUM,
        temperature = this.state.temperature.toString(),
        humidity = this.state.humidity.toString(),
        airQuality = this.state.quality.toString(),
        pressure = this.state.pressure.toString(),
    )
}

@JsonClass(generateAdapter = true)
data class InnerBulbData(
    val power: String,
    val brightness: Int,
    val temperature: Int
)

@JsonClass(generateAdapter = true)
data class NetworkBulbData(
    override val id: String,
    override val type: String,
    override val online: Boolean,
    override val lastSeen: Long,
    val state: InnerBulbData,
    override val name: String,
) : ModuleData

fun NetworkBulbData.toBulbData(): BulbData {
    return BulbData(
        id = this.id,
        name = this.name,
        brightnessPercentage = this.state.brightness,
        isSwitchedOn = this.state.power == "true",
        colorTemperatureKelvin = this.state.temperature,
    )
}

@JsonClass(generateAdapter = true)
data class InnerWindowData(
    val state: Boolean
)

@JsonClass(generateAdapter = true)
data class NetworkWindowData(
    override val id: String,
    override val type: String,
    override val online: Boolean,
    override val lastSeen: Long,
    val state: InnerWindowData,
    override val name: String,
) : ModuleData

fun NetworkWindowData.toWindowData(): WindowData {
    return WindowData(
        id = this.id,
        name = this.name,
        roomName = "idk",
        isOpen = this.state.state,
    )
}
