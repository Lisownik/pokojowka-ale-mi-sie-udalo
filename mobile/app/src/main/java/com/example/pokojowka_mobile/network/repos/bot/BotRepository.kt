package com.example.pokojowka_mobile.network.repos.bot

import com.example.pokojowka_mobile.BuildConfig
import com.example.pokojowka_mobile.ChatMessage
import com.example.pokojowka_mobile.debug.Debug
import com.example.pokojowka_mobile.debug.LogClasses
import com.example.pokojowka_mobile.network.Network
import com.example.pokojowka_mobile.network.repos.Repository
import com.example.pokojowka_mobile.network.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object BotRepository: Repository<List<ChatMessage>> {
    private val STATIC_MODE = BuildConfig.STATIC_MODE
    private val api: BotApi = Network.botApi

    private val scope = CoroutineScope(SupervisorJob())

    private val _state: MutableStateFlow<List<ChatMessage>> = MutableStateFlow(
        listOf(ChatMessage("W czym mogę Ci dzisiaj pomóc w zarządzaniu inteligentnym domem?", false))
    )
    override val state: StateFlow<List<ChatMessage>> = _state.asStateFlow()
    override fun refresh() {}

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    public val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private fun addMessage(message: String, fromUser: Boolean){
        _state.tryEmit(
            _state.value + ChatMessage(message, fromUser)
        )
    }

    fun sendMessage(message: String){
        if(isLoading.value) return

        addMessage(message, true)

        if(STATIC_MODE) {
            addMessage("Otrzymałem wiadomość, dziękuje 67", false)
            return
        }

        _isLoading.tryEmit(true)
        scope.launch {
            safeApiCall { api.sendMessage(message) }
                .onSuccess { result ->
                    Debug.logDebug(LogClasses.NETWORK, "Pomyślnie bot odpowiedział na zapytanie $message -> $result")
                    _isLoading.tryEmit(false)
                    addMessage(result, false)
                }
                .onFailure { error ->
                    _isLoading.tryEmit(false)
                    Debug.logDebug(LogClasses.NETWORK_ERROR, "error: $error")
                    addMessage("ERROR", false)
                }
        }




    }



}