package com.example.pokojowka_mobile.network.repos.rooms

import com.example.pokojowka_mobile.BuildConfig
import com.example.pokojowka_mobile.data.GlobalRoomsList
import com.example.pokojowka_mobile.data.NetworkRoomData
import com.example.pokojowka_mobile.data.RoomData
import com.example.pokojowka_mobile.data.toRoomData
import com.example.pokojowka_mobile.debug.Debug
import com.example.pokojowka_mobile.debug.LogClasses
import com.example.pokojowka_mobile.network.Network
import com.example.pokojowka_mobile.network.repos.Repository
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
import com.example.pokojowka_mobile.data.AvgRoomData
import com.example.pokojowka_mobile.data.NullAvgRoom


object RoomsRepository: Repository<List<RoomData>> {
    private val STATIC_MODE = BuildConfig.STATIC_MODE
    private val api: RoomsApi = Network.roomsApi


    private val scope = CoroutineScope(SupervisorJob())

    private val _state: MutableStateFlow<List<RoomData>> = MutableStateFlow(GlobalRoomsList)
    override val state: StateFlow<List<RoomData>> = _state.asStateFlow()


    private val _avg_state: MutableStateFlow<AvgRoomData> = MutableStateFlow(
        value = NullAvgRoom
    )
    val avgState: StateFlow<AvgRoomData> = _avg_state.asStateFlow()


    private val listAdapter: JsonAdapter<List<NetworkRoomData>> = Network.moshi.adapter(
        Types.newParameterizedType(List::class.java, NetworkRoomData::class.java)
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
            Network.stream("stream/rooms", listAdapter)
                .collect { data ->
                    Debug.logDebug(LogClasses.NETWORK, "Poprawnie odebrano pakiet od /stream/Rooms")

                    _state.tryEmit(
                        data.map {
                            it.toRoomData()
                        }
                    )

                    val size = data.size

                    if (size == 0){
                        _avg_state.tryEmit(NullAvgRoom)
                        return@collect
                    }

                    var temperatureSum: Double = 0.0
                    var humiditySum: Double = 0.0
                    var pressureSum: Double = 0.0
                    var qualitySum: Double = 0.0

                    data.forEach { it ->
                        temperatureSum += it.state.temperature
                        humiditySum += it.state.humidity
                        pressureSum += it.state.pressure
                        qualitySum += it.state.quality
                    }

                    val avgRoom: AvgRoomData = AvgRoomData(
                        temperature = (temperatureSum/size).toLong().toString(),
                        humidity = (humiditySum/size).toLong().toString(),
                        pressure = (pressureSum/size).toLong().toString(),
                        airQuality = (qualitySum/size).toLong().toString()
                    )

                    _avg_state.tryEmit(
                        avgRoom
                    )

                }
        }
    }

    fun stopStream(){
        streamJob?.cancel()
        streamJob = null
    }

    override fun refresh(){
        if(STATIC_MODE) return

        if (isLocked() || refreshingJob?.isActive == true) return

        refreshingJob = scope.launch {
            safeApiCall { api.getRooms() }
                .onSuccess { response ->
                    _state.tryEmit(
                        response.map { it ->
                            it.toRoomData()
                        }
                    )
                }
                .onFailure {
                    lockedUntil = 0L;
                }
        }
    }

    fun setName(roomId: String, name: String){
        _state.update { rooms ->
            rooms.map { room ->
                if (room.id == roomId)
                    room.copy(name = name)
                else
                    room
            }
        }

        if(STATIC_MODE) return
//        lock()
        scope.launch {
            safeApiCall { api.changeName(roomId, name) }
                .onSuccess {
                    Debug.logDebug(LogClasses.NETWORK,"Poprawnie zmieniono nazwę pokoju $roomId na $name")
                }
        }
    }
}