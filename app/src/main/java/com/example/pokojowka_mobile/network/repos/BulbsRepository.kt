package com.example.pokojowka_mobile.network.repos

import com.example.pokojowka_mobile.BuildConfig
import com.example.pokojowka_mobile.data.BulbData
import com.example.pokojowka_mobile.data.GlobalBulbsList
import com.example.pokojowka_mobile.data.toBulbData
import com.example.pokojowka_mobile.debug.Debug
import com.example.pokojowka_mobile.debug.LogClasses
import com.example.pokojowka_mobile.network.Network
import com.example.pokojowka_mobile.network.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


object BulbsRepository: Repository<List<BulbData>> {
    private val STATIC_MODE = BuildConfig.STATIC_MODE
    private val api: BulbsApi = Network.bulbsApi

    private val scope = CoroutineScope(SupervisorJob())

    private val _state: MutableStateFlow<List<BulbData>> = MutableStateFlow(GlobalBulbsList)
    override val state: StateFlow<List<BulbData>> = _state.asStateFlow()

    private var refreshingJob: Job? = null
    @Volatile private var lockedUntil = 0L;


    private fun lock() {
        lockedUntil = System.currentTimeMillis() + 3_000
    }

    private fun isLocked() = System.currentTimeMillis() < lockedUntil

    override fun refresh(){
        if(STATIC_MODE) return

        if (isLocked() || refreshingJob?.isActive == true) return

        refreshingJob = scope.launch {
            safeApiCall { api.getBulbs() }
                .onSuccess { result ->
                    if(isLocked()) return@onSuccess
                    _state.tryEmit(
                        result.map { it ->
                            it.toBulbData()
                        }
                    )
                }
                .onFailure {
                    lockedUntil = 0L;
                }
        }
    }

    fun setBulbState(bulbId: String, isSwitchedOn: Boolean){
        _state.update { bulbs ->
            bulbs.map { bulb ->
                if (bulb.id == bulbId)
                    bulb.copy(isSwitchedOn = isSwitchedOn)
                else
                    bulb
            }
        }

        if(STATIC_MODE) return
        lock()
        scope.launch {
            safeApiCall { api.changeState(bulbId, if (isSwitchedOn) "true" else "false") }
                .onSuccess {
                    Debug.logDebug(LogClasses.NETWORK,"Poprawnie zmieniono stan żarówki $bulbId na $isSwitchedOn")
                }
                .onFailure {
                    lockedUntil = 0L;
                }
        }
    }

    fun setBulbBrightness(bulbId: String, brightnessPercentage: Int){

        _state.update { bulbs ->
            bulbs.map { bulb ->
                if (bulb.id == bulbId)
                    bulb.copy(brightnessPercentage = brightnessPercentage)
                else
                    bulb
            }
        }

        if(STATIC_MODE) return
        lock()
        scope.launch {
            safeApiCall { api.changeBrightness(bulbId, brightnessPercentage) }
                .onSuccess {
                    Debug.logDebug(LogClasses.NETWORK,"Poprawnie zmieniono jasność $bulbId na $brightnessPercentage")
                }
                .onFailure {
                    lockedUntil = 0L;
                }
        }
    }

    fun setBulbColor(bulbId: String, colorTemperatureinKelvin: Int){
        // todo: add network change xd xd

        _state.update { bulbs ->
            bulbs.map { bulb ->
                if (bulb.id == bulbId)
                    bulb.copy(colorTemperatureKelvin = colorTemperatureinKelvin)
                else
                    bulb
            }
        }
    }

    fun setName(bulbId: String, name: String){
        _state.update { rooms ->
            rooms.map { room ->
                if (room.id == bulbId)
                    room.copy(name = name)
                else
                    room
            }
        }

        if(STATIC_MODE) return
        lock()
        scope.launch {
            safeApiCall { api.changeName(bulbId, name) }
                .onSuccess {
                    Debug.logDebug(LogClasses.NETWORK,"Poprawnie zmieniono nazwę żarówki $bulbId na $name")
                }
                .onFailure {
                    lockedUntil = 0L;
                }
        }
    }
}