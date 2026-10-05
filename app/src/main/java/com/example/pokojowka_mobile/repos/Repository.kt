package com.example.pokojowka_mobile.repos

import kotlinx.coroutines.flow.StateFlow

interface Repository<T> {
    val state : StateFlow<T>

    suspend fun refresh()
}