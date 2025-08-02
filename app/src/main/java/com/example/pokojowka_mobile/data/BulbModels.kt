
package com.example.pokojowka_mobile.data

import androidx.compose.ui.graphics.Color
import com.example.pokojowka_mobile.ui.theme.BulbActiveColor
import com.example.pokojowka_mobile.ui.theme.BulbInactiveColor

data class BulbData(
    val id: String,
    val roomName: String,
    var brightnessPercentage: Int,
    var isSwitchedOn: Boolean,
    var colorTemperatureKelvin: Int,
    val iconColorActive: Color = BulbActiveColor,
    val iconColorInactive: Color = BulbInactiveColor
) {
    val brightnessDisplay: String
        get() = if (isSwitchedOn) "$brightnessPercentage% jasności" else "Wyłączona"

    val currentIconColor: Color
        get() = if (isSwitchedOn) iconColorActive else iconColorInactive


    companion object {
        const val MIN_KELVIN = 2000
        const val MAX_KELVIN = 6500
    }
}

val sampleBulbsGlobalList: List<BulbData> = listOf(
    BulbData(
        id = "salon_main",
        roomName = "Salon",
        brightnessPercentage = 70,
        isSwitchedOn = true,
        colorTemperatureKelvin = 3500
    ),
    BulbData(
        id = "bedroom_night",
        roomName = "Sypialnia",
        brightnessPercentage = 0,
        isSwitchedOn = false,
        colorTemperatureKelvin = 2700
    ),
    BulbData(
        id = "kitchen_spot",
        roomName = "Kuchnia",
        brightnessPercentage = 90,
        isSwitchedOn = true,
        colorTemperatureKelvin = 4500
    ),
    BulbData(
        id = "office_desk",
        roomName = "Biuro",
        brightnessPercentage = 50,
        isSwitchedOn = true,
        iconColorActive = Color(0xFF3B82F6),
        colorTemperatureKelvin = 5000
    ),
    BulbData(
        id = "hallway_led",
        roomName = "Przedpokój",
        brightnessPercentage = 0,
        isSwitchedOn = false,
        colorTemperatureKelvin = 3000
    )
)

