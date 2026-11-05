package com.example.pokojowka_mobile.network.repos

import kotlinx.coroutines.flow.StateFlow

interface Repository<T> {
    val state : StateFlow<T>

    fun refresh()
}

