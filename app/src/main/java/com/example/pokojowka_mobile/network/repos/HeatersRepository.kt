package com.example.pokojowka_mobile.network.repos

import com.example.pokojowka_mobile.BuildConfig
import com.example.pokojowka_mobile.data.GlobalHeatersList
import com.example.pokojowka_mobile.data.HeaterData
import com.example.pokojowka_mobile.data.NetworkBulbData
import com.example.pokojowka_mobile.data.NetworkHeaterData
import com.example.pokojowka_mobile.data.toBulbData
import com.example.pokojowka_mobile.data.toHeaterData
import com.example.pokojowka_mobile.data.toWindowData
import com.example.pokojowka_mobile.debug.Debug
import com.example.pokojowka_mobile.debug.LogClasses
import com.example.pokojowka_mobile.network.Network
import com.example.pokojowka_mobile.network.safeApiCall
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object HeatersRepository : Repository<List<HeaterData>> {
    private const val STATIC_MODE = BuildConfig.STATIC_MODE
    private val api: HeatersApi = Network.heatersApi
    private val scope = CoroutineScope(SupervisorJob())

    private val _state: MutableStateFlow<List<HeaterData>> = MutableStateFlow(GlobalHeatersList)
    override val state: StateFlow<List<HeaterData>> = _state.asStateFlow()

    private val listAdapter: JsonAdapter<ArrayList<NetworkHeaterData>> = Network.moshi.adapter(
        Types.newParameterizedType(List::class.java, NetworkHeaterData::class.java)
    )

    private var streamJob: Job? = null

    private var refreshingJob: Job? = null
    @Volatile private var lockedUntil = 0L;


    private fun lock() {
        lockedUntil = System.currentTimeMillis() + 3_000
    }

    private fun isLocked() = System.currentTimeMillis() < lockedUntil

    fun startStream() {
        if (STATIC_MODE || streamJob?.isActive == true) return

        streamJob = scope.launch {
            Network.stream("stream/heaters", listAdapter)
                .collect { data ->
                    Debug.logDebug(LogClasses.NETWORK, "Poprawnie odebrano pakiet od /stream/heaters")
                    _state.tryEmit(
                        data.map {
                            it.toHeaterData()
                        }
                    )
                }
        }
    }

    fun stopStream(){
        streamJob?.cancel()
        streamJob = null
    }


    override fun refresh() {
        if(STATIC_MODE) return

        if (isLocked() || refreshingJob?.isActive == true) return

        refreshingJob = scope.launch {
            safeApiCall { api.getHeaters() }
                .onSuccess { result ->
                    if(isLocked()) return@onSuccess
                    _state.tryEmit(
                        result.map { it ->
                            it.toHeaterData()
                        }
                    )
                }
                .onFailure {
                    lockedUntil = 0L;
                }
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

        if(STATIC_MODE) return
        lock()
        scope.launch {
            Debug.logDebug(LogClasses.NETWORK, "Trying to set window state")
            safeApiCall { api.changeState(heaterId, if (isHeatingOn) "true" else "false") }
                .onSuccess {
                    Debug.logDebug(LogClasses.NETWORK, "Pomyślnie zmieniono stan heatera $heaterId na $isHeatingOn")
                }
                .onFailure {
                    lockedUntil = 0L;
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

        if(STATIC_MODE) return
        lock()
        scope.launch {
            safeApiCall { api.changeTemperatureGoal(heaterId, targetTemp) }
                .onSuccess {
                    Debug.logDebug(LogClasses.NETWORK,"Poprawnie zmieniono temperature $heaterId na $targetTemp")
                }
                .onFailure {
                    lockedUntil = 0L;
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

        if(STATIC_MODE) return
        lock()
        scope.launch {
            safeApiCall { api.changeName(heaterId, name) }
                .onSuccess {
                    Debug.logDebug(LogClasses.NETWORK, "pomyślnie zmieniono nazwę dla $heaterId na $name")
                }
                .onFailure {
                    lockedUntil = 0L;
                }
        }
    }
}
