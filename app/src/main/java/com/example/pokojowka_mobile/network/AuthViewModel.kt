package com.example.pokojowka_mobile.network

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokojowka_mobile.data.BulbData
import com.example.pokojowka_mobile.data.EnvironmentData
import com.example.pokojowka_mobile.data.NetworkRoomData
import com.example.pokojowka_mobile.data.PlantData
import com.example.pokojowka_mobile.data.RoomData
import com.example.pokojowka_mobile.data.RoomStatus
import com.example.pokojowka_mobile.data.SampleUserData
import com.example.pokojowka_mobile.data.GlobalBulbsList
import com.example.pokojowka_mobile.data.GlobalPlantsList
import com.example.pokojowka_mobile.data.GlobalRoomsList
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundBed
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.pokojowka_mobile.data.toBulbData
import com.example.pokojowka_mobile.data.toPlantData

class AuthViewModel : ViewModel() {
    private var pollingJob: Job? = null
    private val _bulbsFlow = MutableStateFlow<List<BulbData>>(emptyList())
    val bulbsFlow: StateFlow<List<BulbData>> = _bulbsFlow.asStateFlow()
    private val _roomsFlow = MutableStateFlow<List<RoomData>>(emptyList())
    val roomsFlow: StateFlow<List<RoomData>> = _roomsFlow.asStateFlow()
    private val _avgRooms = MutableStateFlow<EnvironmentData>(SampleUserData.defaultEnvironment)
    public val getAvgRooms: StateFlow<EnvironmentData> = _avgRooms
    private val _plantsFlow = MutableStateFlow<List<PlantData>>(GlobalPlantsList)
    val plantsFlow: StateFlow<List<PlantData>> = _plantsFlow.asStateFlow()

    public lateinit var publicOwner: LifecycleOwner

    init {
        startPolling()
        getBulbs()
        getRooms()
    }

//    fun init() {
////        getBulbsResult.observeForever { response ->
////            sampleBulbsGlobalList = mutableListOf()
////            response.forEach {bulb ->
////                Log.d("Init -> GetBulbs() -> Foreach -> Bulb ", bulb.name)
////                Log.d("API_rooms_observe", "46")
////                sampleBulbsGlobalList += BulbData(
////                    id = bulb.id,
////                    roomName = bulb.name,
////                    brightnessPercentage = bulb.data.brightness,
////                    isSwitchedOn = bulb.data.power == "on",
////                    colorTemperatureKelvin = 2700,
////                )
////            }
////            _bulbsFlow.value = sampleBulbsGlobalList
////        }
////        getRoomsResult.observeForever { response ->
////            sampleRoomsGlobal = mutableListOf()
////            response.forEach {room ->
////                Log.d("Init -> GetRooms() -> Foreach -> Room ", room.name)
////                Log.d("API_rooms_observe", "62")
////                sampleRoomsGlobal += RoomData(
////                    id = room.id,
////                    name = room.name,
////                    icon = Icons.Filled.Bed,
////                    backgroundColor = RoomIconBackgroundBed,
////                    status = RoomStatus.MEDIUM,
////                    temperature = "${room.data.temperature}°C",
////                    humidity = "${room.data.humidity}%",
////                    airQuality = "${room.data.quality}",
////                    pressure = "${room.data.pressure} hPa",
////                    coDetected = room.data.co,
////                    otherGasesDetected = room.data.gasses,
////                )
////            }
//////            _roomsFlow.value = sampleRoomsGlobal
////            _roomsFlow.tryEmit(sampleRoomsGlobal)
////        }
////        getBulbs()
////        getRooms()
//        startPolling()
//    }

    fun setRooms(newRooms: List<RoomData>) {
        _roomsFlow.value = newRooms.toMutableList()
    }

    private fun updateBulb(bulbId: String, transform: (BulbData) -> BulbData) {
        _bulbsFlow.update { currentBulbs ->
            currentBulbs.map { bulb ->
                if (bulb.id == bulbId) transform(bulb) else bulb
            }
        }
    }

    fun loadBulbs() {
        viewModelScope.launch {
            _bulbsFlow.tryEmit(RetrofitClient.apiService.getBulbs().map { it.toBulbData() })
//            _bulbsFlow.value = RetrofitClient.apiService.getBulbs().map { it.toBulbData() }
        }
    }

    private fun NetworkRoomData.toRoomData(): RoomData {
        return RoomData(
            id = this.id,
            name = this.name,
            icon = Icons.Filled.Bed,
            backgroundColor = RoomIconBackgroundBed,
            status = RoomStatus.MEDIUM,
            temperature = "${this.data.temperature}°C",
            humidity = "${this.data.humidity}%",
            airQuality = "${this.data.quality}",
            pressure = "${this.data.pressure} hPa",
            coDetected = this.data.co,
            otherGasesDetected = this.data.gasses,
            lastUpdated = System.currentTimeMillis()
        )
    }

    public fun startPolling() {
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(3000)
                try {
                    Log.d("API_Polling", "Fetching bulbs and rooms...")
                    getRooms()
                    getBulbs()
                    getPlants()
                } catch (e: Exception) {
                    Log.e("API_BulbPolling", "Error in polling", e)
                    pollingJob?.cancel()
                }
            }
        }
    }

    fun upsertRoom(room: RoomData) {
//        _roomsFlow.update { current ->
//            val idx = current.indexOfFirst { it.id == room.id }
//            if (idx >= 0) {
//                current.also { it[idx] = room }
//            } else {
//                current + room
//            }
//        }
    }

    fun getBulbs() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getBulbs()
                Log.i("API_RESULT /bulbs:", response.toString())
                GlobalBulbsList = mutableListOf()
                response.forEach {bulb ->
                    Log.d("Init -> GetBulbs() -> Foreach -> Bulb ", bulb.name)
                    Log.d("API_bulbs_observe", "Added/Changed Bulbs")
                    GlobalBulbsList += bulb.toBulbData()
                }
                _bulbsFlow.value = GlobalBulbsList
            } catch (e: Exception) {
                Log.e("API_ERROR /bulbs:", e.message ?: "Unknown error")
            }
        }
    }

//    fun calcTempTrend(room: NetworkRoomData): TrendData {
//        val diff = _avgRooms.value.temperature - room.data.temperature
//    }

    fun getRooms() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getRooms()
                Log.i("API_RESULT /rooms:", response.toString())
                _avgRooms.value = averageOfRooms(response)

                val newList = response.map { room ->
                    RoomData(
                        id = room.id,
                        name = room.name,
                        icon = Icons.Filled.Bed,
                        backgroundColor = RoomIconBackgroundBed,
                        status = RoomStatus.MEDIUM,
                        temperature = "${room.data.temperature}°C",
                        humidity = "${room.data.humidity}%",
                        airQuality = "${room.data.quality}",
                        pressure = "${room.data.pressure} hPa",
                        coDetected = room.data.co,
                        otherGasesDetected = room.data.gasses,
                        lastUpdated = System.currentTimeMillis(),
//                        temperatureTrend = calcTempTrend(room)
                    )
                }
                GlobalRoomsList = newList.toMutableList()
                _roomsFlow.value = newList
                Log.d("API_get_room_end", "new List: ${newList.count()}")

            } catch (e: Exception) {
                Log.e("API_ERROR /rooms:", e.message ?: "Unknown error")
            }
        }
    }

    fun averageOfRooms(list: Array<NetworkRoomData>): EnvironmentData {
        if(list.count() < 1)
            return EnvironmentData(temperature = "NAN°C", humidity = "NAN%")
        var temperature: Double = 0.0
        var humidity: Double = 0.0
        list.forEach { it ->
            temperature += it.data.temperature
            humidity += it.data.humidity
        }
        temperature /= list.count()
        humidity /= list.count()
        return EnvironmentData(temperature = "${temperature}°C", humidity = "$humidity%")
    }


    fun changeRoomName(id: String, newName: String) {
        Log.v("API_REQUEST_NAME", "Start")
        Log.d("API_CALL_ID",  id)
        Log.d("API_CALL_NEW_NAME",  newName)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.changeRoomName(id, newName)

                _roomsFlow.update { currentRooms ->
                    currentRooms.map { room ->
                        if (room.id == id) room.copy(name = newName)
                        else room
                    } as MutableList<RoomData>
                }

                Log.i("API_RESULT /room/{id}/name:", response.toString())
                Log.v("API_REQUEST_NAME", "End")
            } catch (e: Exception) {
                Log.e("API_ERROR /room/{id}/name:", e.message ?: "Unknown error")
            }
        }
    }

    fun changeBulbName(id: String, newName: String) {
        Log.v("API_REQUEST_NAME", "Start")
        Log.d("API_CALL_ID",  id)
        Log.d("API_CALL_NEW_NAME",  newName)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.changeBulbName(id, newName)

                _bulbsFlow.update { currentBulbs ->
                    currentBulbs.map { bulb ->
                        if (bulb.id == id) bulb.copy(name = newName)
                        else bulb
                    }
                }

                Log.i("API_RESULT /bulb/{id}/name:", response.toString())
                Log.v("API_REQUEST_NAME", "End")
            } catch (e: Exception) {
                Log.e("API_ERROR /bulb/{id}/name:", e.message ?: "Unknown error")
            }
        }
    }

    fun changePowerState( id: String, powerState: String) {
        Log.v("API_REQUEST_POWER_STATE", "Start")
        Log.d("API_CALL_ID",  id)
        Log.d("API_CALL_STATE",  powerState)
        pollingJob?.cancel()
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.changeBulbPowerState(id, powerState)

                _bulbsFlow.update { currentBulbs ->
                    currentBulbs.map { bulb ->
                        if (bulb.id == id) bulb.copy(isSwitchedOn = powerState == "on")
                        else bulb
                    }
                }

                Log.v("API_REQUEST_POWER_STATE", "End")
            } catch (e: Exception) {
                updateBulb(id) { it.copy(isSwitchedOn = !powerState.toBoolean()) }
            }
        }
    }
    fun changeBrightness(id: String, brightness: Int, duration: Int) {
        Log.v("API_REQUEST_BULB_BRIGHTNESS", "Start")
        Log.d("API_CALL_ID",  id)
        Log.d("API_CALL_BRIGHTNESS", "$brightness %")
        Log.d("API_CALL_DURATION", "${RetrofitClient.BULB_CHANGE_DURATION} ms")
        pollingJob?.cancel()
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.changeBulbBrightness(id, brightness, duration)
//                startBulbPolling()

                _bulbsFlow.update { currentBulbs ->
                    currentBulbs.map { bulb ->
                        if (bulb.id == id) bulb.copy(brightnessPercentage = brightness)
                        else bulb
                    }
                }

                Log.i("API_RESULT /bulb/{id}/brightness:", response.toString())
                Log.v("API_REQUEST_BULB_BRIGHTNESS", "End")
            } catch (e: Exception) {
                Log.e("API_ERROR /bulb/{id}/brightness:", e.message ?: "Unknown error")
            }
        }
    }

    fun getPlants() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getPlants()
                Log.i("API_RESULT /pot:", response.toString())

                val newList = response.map { room ->
                    room.toPlantData()
                }
                
                GlobalPlantsList = newList.toMutableList()
                _plantsFlow.value = newList.toMutableList()
                Log.d("API_get_plant_end", "new List: ${newList.count()}")

            } catch (e: Exception) {
                Log.e("API_ERROR /pot:", e.message ?: "Unknown error")
            }
        }
    }

    fun changePotName(id: String, name: String) {
        Log.v("API_REQUEST_NAME", "Start")
        Log.d("API_CALL_ID",  id)
        Log.d("API_CALL_NEW_NAME",  name)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.changePotName(id, name)

                Log.i("API_RESULT /pot/{id}/{name}:", response.toString())
                Log.v("API_REQUEST_NAME", "End")
            } catch (e: Exception) {
                Log.e("API_ERROR /bulb/{id}/{name}:", e.message ?: "Unknown error")
            }
        }
    }
}