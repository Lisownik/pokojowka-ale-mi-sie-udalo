package com.example.pokojowka_mobile.network.repos

import com.example.pokojowka_mobile.data.GlobalRoomsList
import com.example.pokojowka_mobile.data.RoomData
import com.example.pokojowka_mobile.data.toRoomData
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

object RoomsRepository: Repository<List<RoomData>> {
    private val api: RoomsApi = Network.roomsApi


    private val scope = CoroutineScope(SupervisorJob())

    private val _state: MutableStateFlow<List<RoomData>> = MutableStateFlow(GlobalRoomsList)
    override val state: StateFlow<List<RoomData>> = _state.asStateFlow()

    private var refreshingJob: Job? = null

    @Volatile private var lockedUntil = 0L;


    private fun lock() {
        lockedUntil = System.currentTimeMillis() + 3_000
    }

    private fun isLocked() = System.currentTimeMillis() < lockedUntil

    override fun refresh(){
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
        lock()
        _state.update { rooms ->
            rooms.map { room ->
                if (room.id == roomId)
                    room.copy(name = name)
                else
                    room
            }
        }

        scope.launch {
            safeApiCall { api.changeName(roomId, name) }
                .onSuccess {
                    Debug.logDebug(LogClasses.NETWORK,"Poprawnie zmieniono nazwę pokoju $roomId na $name")
                }
        }
    }
}