package com.example.pokojowka_mobile.repos

import android.health.connect.datatypes.units.Percentage
import android.health.connect.datatypes.units.Temperature
import com.example.pokojowka_mobile.data.BulbData
import com.example.pokojowka_mobile.data.GlobalBulbsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


object BulbsRepository: Repository<List<BulbData>> {
    private val scope = CoroutineScope(SupervisorJob())

    private val _state: MutableStateFlow<List<BulbData>> = MutableStateFlow(GlobalBulbsList)
    override val state: StateFlow<List<BulbData>> = _state.asStateFlow()

    private var refreshingJob: Job? = null

    override suspend fun refresh(){
        if(refreshingJob?.isActive == true)
            return
        refreshingJob = scope.launch {
            //todo: implement fetching data
        }
    }

    fun setBulbState(bulbId: String, isSwitchedOn: Boolean){
        // todo: add network change

        _state.update { bulbs ->
            bulbs.map { bulb ->
                if (bulb.id == bulbId)
                    bulb.copy(isSwitchedOn = isSwitchedOn)
                else
                    bulb
            }
        }
    }

    fun setBulbBrightness(bulbId: String, brightnessPercentage: Int){
        // todo: add network change

        _state.update { bulbs ->
            bulbs.map { bulb ->
                if (bulb.id == bulbId)
                    bulb.copy(brightnessPercentage = brightnessPercentage)
                else
                    bulb
            }
        }
    }

    fun setBulbColor(bulbId: String, colorTemperatureinKelvin: Int){
        // todo: add network change

        _state.update { bulbs ->
            bulbs.map { bulb ->
                if (bulb.id == bulbId)
                    bulb.copy(colorTemperatureKelvin = colorTemperatureinKelvin)
                else
                    bulb
            }
        }
    }
}