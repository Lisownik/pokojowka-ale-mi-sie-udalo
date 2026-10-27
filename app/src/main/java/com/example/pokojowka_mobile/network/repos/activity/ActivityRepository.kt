package com.example.pokojowka_mobile.network.repos.activity

import com.example.pokojowka_mobile.BuildConfig
import com.example.pokojowka_mobile.debug.Debug
import com.example.pokojowka_mobile.debug.LogClasses
import com.example.pokojowka_mobile.network.Network
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object ActivityRepository {
    private const val STATIC_MODE = BuildConfig.STATIC_MODE

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val listAdapter: JsonAdapter<List<String>> = Network.moshi.adapter(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    private var streamJob: Job? = null

    fun startStream() {
        if (STATIC_MODE || streamJob?.isActive == true){
            Debug.logDebug(LogClasses.NETWORK, "Wywołano startStream() pomimo działającego streamu")
            return
        }

        streamJob = scope.launch {
            Network.stream("stream/activity", listAdapter)
                .collect { data ->
                    Debug.logDebug(LogClasses.NETWORK, "Odebrano pakiet ? z streamu: stream/activity")
                }
        }
    }

    fun stopStream(){
        streamJob?.cancel()
        streamJob = null
    }

}