package com.example.pokojowka_mobile.network.repos.notifications

import com.example.pokojowka_mobile.BuildConfig
import com.example.pokojowka_mobile.data.NotificationData
import com.example.pokojowka_mobile.data.NotificationType
import com.example.pokojowka_mobile.data.sampleGlobalNotificationsList
import com.example.pokojowka_mobile.debug.Debug
import com.example.pokojowka_mobile.debug.LogClasses
import com.example.pokojowka_mobile.network.Network
import com.example.pokojowka_mobile.notifications.Notifier
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object NotificationRepository {
    private const val STATIC_MODE = BuildConfig.STATIC_MODE

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val adapter: JsonAdapter<NotificationData> = Network.moshi.adapter(NotificationData::class.java)

    private val _state: MutableStateFlow<List<NotificationData>> = MutableStateFlow(sampleGlobalNotificationsList)
    val state: StateFlow<List<NotificationData>> = _state.asStateFlow()


    private var streamJob: Job? = null

    fun startStream() {
        if (STATIC_MODE || streamJob?.isActive == true){
            Debug.logDebug(LogClasses.NETWORK, "Wywołano startStream() pomimo działającego streamu")
            return
        }

        streamJob = scope.launch {
            Network.stream("stream/notifications", adapter)
                .collect { data ->
                    Debug.logDebug(LogClasses.NETWORK, "Odebrano pakiet ? z streamu: stream/notifications")

                    _state.tryEmit(
                        _state.value + data
                    )

                    if(data.type === NotificationType.SILENT){
                        Notifier.silentNotification(data.title, data.description, data.notifIcon);
                        return@collect
                    }

                    Notifier.safeNotification(data.title, data.description, data.notifIcon);

                }
        }
    }

    fun stopStream(){
        streamJob?.cancel()
        streamJob = null
    }

}