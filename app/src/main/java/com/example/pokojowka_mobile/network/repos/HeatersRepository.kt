package com.example.pokojowka_mobile.network.repos

import com.example.pokojowka_mobile.data.GlobalHeatersList
import com.example.pokojowka_mobile.data.HeaterData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object HeatersRepository : Repository<List<HeaterData>> {
    private val scope = CoroutineScope(SupervisorJob())

    private val _state: MutableStateFlow<List<HeaterData>> = MutableStateFlow(GlobalHeatersList)
    override val state: StateFlow<List<HeaterData>> = _state.asStateFlow()

    private var refreshingJob: Job? = null

    override fun refresh() {
        if (refreshingJob?.isActive == true) return
        refreshingJob = scope.launch {
            // todo: implement fetching data
        }
    }

    fun setHeatingState(heaterId: String, isHeatingOn: Boolean) {
        _state.update { heaters ->
            heaters.map { heater ->
                if (heater.id == heaterId)
                    heater.copy(isHeatingOn = isHeatingOn)
                else
                    heater
            }
        }
    }

    fun setTargetTemperature(heaterId: String, targetTemp: Float) {
        _state.update { heaters ->
            heaters.map { heater ->
                if (heater.id == heaterId)
                    heater.copy(targetTemp = targetTemp)
                else
                    heater
            }
        }
    }

    fun setName(heaterId: String, name: String) {
        _state.update { heaters ->
            heaters.map { heater ->
                if (heater.id == heaterId)
                    heater.copy(name = name)
                else
                    heater
            }
        }
    }
}
