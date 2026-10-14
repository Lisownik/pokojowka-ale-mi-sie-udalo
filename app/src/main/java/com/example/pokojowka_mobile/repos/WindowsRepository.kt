package com.example.pokojowka_mobile.repos

import com.example.pokojowka_mobile.data.GlobalWindowsList
import com.example.pokojowka_mobile.data.WindowData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object WindowsRepository : Repository<List<WindowData>> {
    private val scope = CoroutineScope(SupervisorJob())

    private val _state: MutableStateFlow<List<WindowData>> = MutableStateFlow(GlobalWindowsList)
    override val state: StateFlow<List<WindowData>> = _state.asStateFlow()

    private var refreshingJob: Job? = null

    override suspend fun refresh() {
        if (refreshingJob?.isActive == true)
            return
        refreshingJob = scope.launch {
            // todo: implement fetching data from backend
        }
    }

    fun setWindowState(windowId: String, isOpen: Boolean) {
        // todo: add network change

        _state.update { windows ->
            windows.map { window ->
                if (window.id == windowId)
                    window.copy(isOpen = isOpen)
                else
                    window
            }
        }
    }

    fun setName(windowId: String, name: String) {
        // todo: add network change

        _state.update { windows ->
            windows.map { window ->
                if (window.id == windowId)
                    window.copy(name = name)
                else
                    window
            }
        }
    }
}
