
package com.example.pokojowka_mobile.data

import androidx.compose.ui.graphics.Color
import com.example.pokojowka_mobile.ui.theme.BulbActiveColor
import com.example.pokojowka_mobile.ui.theme.BulbInactiveColor

data class BulbData(
    val id: String,
    val name: String,
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

public fun NetworkBulbData.toBulbData(): BulbData {
    return BulbData(
        id = this.id,
        name = this.name,
        brightnessPercentage = this.data.brightness,
        isSwitchedOn = this.data.power == "on",
        colorTemperatureKelvin = 2700,
    )
}

var GlobalBulbsList: MutableList<BulbData> = mutableListOf(
    BulbData(
        id = "salon_main",
        name = "Salon",
        brightnessPercentage = 70,
        isSwitchedOn = true,
        colorTemperatureKelvin = 3500
    ),
    BulbData(
        id = "bedroom_night",
        name = "Sypialnia",
        brightnessPercentage = 0,
        isSwitchedOn = false,
        colorTemperatureKelvin = 2700
    ),
    BulbData(
        id = "kitchen_spot",
        name = "Kuchnia",
        brightnessPercentage = 90,
        isSwitchedOn = true,
        colorTemperatureKelvin = 4500
    ),
    BulbData(
        id = "office_desk",
        name = "Biuro",
        brightnessPercentage = 50,
        isSwitchedOn = true,
        iconColorActive = Color(0xFF3B82F6),
        colorTemperatureKelvin = 5000
    ),
    BulbData(
        id = "hallway_led",
        name = "Przedpokój",
        brightnessPercentage = 0,
        isSwitchedOn = false,
        colorTemperatureKelvin = 3000
    )
)

