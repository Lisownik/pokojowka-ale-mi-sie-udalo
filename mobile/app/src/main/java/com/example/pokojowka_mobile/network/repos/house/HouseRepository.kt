package com.example.pokojowka_mobile.network.repos.house

import com.example.pokojowka_mobile.BuildConfig
import com.example.pokojowka_mobile.data.HouseData
import com.example.pokojowka_mobile.data.NetworkHomeData
import com.example.pokojowka_mobile.data.SampleHouseData
import com.example.pokojowka_mobile.data.WeatherState
import com.example.pokojowka_mobile.data.toHouseData
import com.example.pokojowka_mobile.debug.Debug
import com.example.pokojowka_mobile.debug.LogClasses
import com.example.pokojowka_mobile.network.Network
import com.example.pokojowka_mobile.network.repos.Repository
import com.example.pokojowka_mobile.network.safeApiCall
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object HouseRepository {
    private const val STATIC_MODE = BuildConfig.STATIC_MODE
    private val api: HouseApi = Network.houseApi
    private val scope = CoroutineScope(SupervisorJob())

    private val _state: MutableStateFlow<HouseData> = MutableStateFlow(SampleHouseData)
    val state: StateFlow<HouseData> = _state.asStateFlow()

    private val listAdapter: JsonAdapter<NetworkHomeData> = Network.moshi.adapter(NetworkHomeData::class.java)
    private var streamJob: Job? = null
    @Volatile private var lockedUntil = 0L;
    private fun lock() {
        lockedUntil = System.currentTimeMillis() + 3_000
    }

    private fun isLocked() = System.currentTimeMillis() < lockedUntil

    fun startStream() {
        if (STATIC_MODE || streamJob?.isActive == true) return

        streamJob = scope.launch {
            Network.stream("stream/home", listAdapter)
                .collect { data ->
                    Debug.logDebug(LogClasses.NETWORK, "Poprawnie odebrano pakiet od /stream/home")
                    _state.tryEmit(
                        data.toHouseData()
                    )
                }
        }
    }

    fun stopStream(){
        streamJob?.cancel()
        streamJob = null
    }

    fun setDoorState(isOpened: Boolean) {
        _state.update { home ->
            home.copy(isDoorOpen = isOpened, isDoorLocked = !isOpened)
        }

        if(STATIC_MODE) return
        lock()
        scope.launch {
            Debug.logDebug(LogClasses.NETWORK, "Trying to set front door state")
            safeApiCall { api.changeState(if (isOpened) "true" else "false") }
                .onSuccess {
                    Debug.logDebug(LogClasses.NETWORK, "Pomyślnie zmieniono stan drzwi na $isOpened")
                }
                .onFailure {
                    lockedUntil = 0L;
                }
        }
    }
}
