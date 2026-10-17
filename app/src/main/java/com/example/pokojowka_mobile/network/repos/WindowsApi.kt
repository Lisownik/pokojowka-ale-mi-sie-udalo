package com.example.pokojowka_mobile.network.repos

import com.example.pokojowka_mobile.data.NetworkWindowData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface WindowsApi {
    @GET("devices/window")
    suspend fun getWindows(): Response<List<NetworkWindowData>>

    @PUT("devices/{id}/{name}")
    suspend fun changeName(@Path("id") id: String, @Path("name") name: String): Response<Unit>

    @PUT("devices/window/{id}/state/{state}")
    suspend fun changeState(@Path("id") id: String, @Path("state") state: String): Response<Unit>
}