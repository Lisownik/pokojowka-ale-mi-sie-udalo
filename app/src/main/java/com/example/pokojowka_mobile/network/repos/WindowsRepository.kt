package com.example.pokojowka_mobile.network.repos

import com.example.pokojowka_mobile.BuildConfig
import com.example.pokojowka_mobile.data.GlobalWindowsList
import com.example.pokojowka_mobile.data.WindowData
import com.example.pokojowka_mobile.data.toWindowData
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

object WindowsRepository : Repository<List<WindowData>> {
    private val STATIC_MODE = BuildConfig.STATIC_MODE
    private val api: WindowsApi = Network.windowsApi

    private val scope = CoroutineScope(SupervisorJob())

    private val _state: MutableStateFlow<List<WindowData>> = MutableStateFlow(GlobalWindowsList)
    override val state: StateFlow<List<WindowData>> = _state.asStateFlow()

    @Volatile private var lockedUntil = 0L;


    private fun lock() {
        lockedUntil = System.currentTimeMillis() + 3_000
    }

    private fun isLocked() = System.currentTimeMillis() < lockedUntil

    private var refreshingJob: Job? = null

    override fun refresh() {
        if(STATIC_MODE) return

        if (refreshingJob?.isActive == true)
            return
        refreshingJob = scope.launch {
            safeApiCall { api.getWindows() }
                .onSuccess { result ->
                    if(isLocked()) return@onSuccess
                    _state.tryEmit(
                        result.map { it ->
                            it.toWindowData()
                        }
                    )
                }
                .onFailure {
                    lockedUntil = 0L;
                }
            }
        }

    fun setWindowState(windowId: String, isOpen: Boolean) {
        _state.update { windows ->
            windows.map { window ->
                if (window.id == windowId)
                    window.copy(isOpen = isOpen)
                else
                    window
            }
        }

        if(STATIC_MODE) return
        lock()
        scope.launch {
            Debug.logDebug(LogClasses.NETWORK, "Trying to set window state")
            safeApiCall { api.changeState(windowId, if (isOpen) "open" else "close") }
                .onSuccess {
                    Debug.logDebug(LogClasses.NETWORK, "Pomyślnie zmieniono stan okna $windowId na $isOpen")
                }
                .onFailure {
                    lockedUntil = 0L;
                }
        }
    }

    fun setName(windowId: String, name: String) {
        _state.update { windows ->
            windows.map { window ->
                if (window.id == windowId)
                    window.copy(name = name)
                else
                    window
            }
        }

        if(STATIC_MODE) return
        lock()
        scope.launch {
            safeApiCall { api.changeName(windowId, name) }
                .onSuccess {
                    Debug.logDebug(LogClasses.NETWORK, "pomyślnie zmieniono nazwę dla $windowId na $name")
                }
                .onFailure {
                    lockedUntil = 0L;
                }
        }
    }
}
