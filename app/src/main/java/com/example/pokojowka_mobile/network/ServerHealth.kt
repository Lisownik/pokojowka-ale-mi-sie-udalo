package com.example.pokojowka_mobile.network

import com.example.pokojowka_mobile.debug.Debug
import com.example.pokojowka_mobile.notifications.Notifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ServerHealth{
    UNKNOWN,
    HEALTHY,
    UNHEALTHY,
}

object HealthState {
    private val _healthState = MutableStateFlow(ServerHealth.UNKNOWN)
    val healthState: StateFlow<ServerHealth> = _healthState.asStateFlow()

    fun setHealthState(state: ServerHealth) {
        _healthState.value = state
        if(state == ServerHealth.UNHEALTHY)
            Debug.debug {
                Notifier.safeNotification("NETWORK ERROR", "BLABLA", android.R.drawable.ic_dialog_alert)
            }
    }
}
