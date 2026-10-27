package com.example.pokojowka_mobile.network.repos.heaters

import com.example.pokojowka_mobile.data.NetworkHeaterData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface HeatersApi {

    @GET("devices/heater")
    suspend fun getHeaters(): Response<List<NetworkHeaterData>>

    @PUT("devices/{id}/{name}")
    suspend fun changeName(@Path("id") id: String, @Path("name") name: String): Response<Unit>

    @PUT("devices/heater/{id}/state/{state}")
    suspend fun changeState(@Path("id") id: String, @Path("state") state: String): Response<Unit>

    @PUT("devices/heater/{id}/temperature_goal/{temperature}")
    suspend fun changeTemperatureGoal(@Path("id") id: String, @Path("temperature") temperature: Float): Response<Unit>
}