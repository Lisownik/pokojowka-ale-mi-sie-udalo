package com.example.pokojowka_mobile.network.repos.house

import retrofit2.Response
import retrofit2.http.PUT
import retrofit2.http.Path

interface HouseApi {
    @PUT("devices/home/state/{state}")
    suspend fun changeState(@Path("state") state: String): Response<Unit>
}