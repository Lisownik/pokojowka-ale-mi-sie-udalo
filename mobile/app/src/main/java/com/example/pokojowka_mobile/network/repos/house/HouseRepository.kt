package com.example.pokojowka_mobile.network.repos.house

import com.example.pokojowka_mobile.data.HouseData
import com.example.pokojowka_mobile.data.SampleHouseData
import com.example.pokojowka_mobile.network.repos.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object HouseRepository : Repository<HouseData> {
    private val _state: MutableStateFlow<HouseData> = MutableStateFlow(SampleHouseData)
    override val state: StateFlow<HouseData> = _state.asStateFlow()

    override fun refresh() {
        // todo: implement fetching data
    }

    fun setDoorLockState(isLocked: Boolean) {
        _state.update { house ->
            house.copy(
                isDoorLocked = isLocked,
                isDoorOpen = !isLocked
            )
        }
    }

    fun setWeatherState(weatherState: String) {
        _state.update { house ->
            val isRain = weatherState == "sunny-rain" || weatherState == "dark-rain"
            house.copy(
                weatherState = weatherState,
                rainDetected = isRain
            )
        }
    }
}
