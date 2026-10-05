package com.example.pokojowka_mobile.repos

import com.example.pokojowka_mobile.data.GlobalRoomsList
import com.example.pokojowka_mobile.data.RoomData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object RoomsRepository: Repository<List<RoomData>> {
    private val scope = CoroutineScope(SupervisorJob())

    private val _state: MutableStateFlow<List<RoomData>> = MutableStateFlow(GlobalRoomsList)
    override val state: StateFlow<List<RoomData>> = _state.asStateFlow()

    private var refreshingJob: Job? = null

    override suspend fun refresh(){
        if(refreshingJob?.isActive == true)
            return
        refreshingJob = scope.launch {
            //todo: implement fetching data
        }
    }

    fun setName(roomId: String, name: String){
        // todo: add network change

        _state.update { rooms ->
            rooms.map { room ->
                if (room.id == roomId)
                    room.copy(name = name)
                else
                    room
            }
        }
    }
}