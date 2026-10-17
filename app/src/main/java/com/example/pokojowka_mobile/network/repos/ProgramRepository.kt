package com.example.pokojowka_mobile.network.repos

import android.util.Log
import com.example.pokojowka_mobile.debug.Debug
import com.example.pokojowka_mobile.debug.LogClasses
import com.example.pokojowka_mobile.network.Network
import com.example.pokojowka_mobile.network.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object ProgramRepository {
    private val scope = CoroutineScope(SupervisorJob())

    private val api: ProgramApi = Network.programApi
    fun emitProgram() {
        Log.d("ProgramRepository", "Emitting push programmed button")
        scope.launch {
            safeApiCall { api.emitProgram() }
                .onSuccess {
                    Debug.logDebug(LogClasses.NETWORK, "pomyślnie wywołano programowalny przycisk")
                }
        }
    }


}