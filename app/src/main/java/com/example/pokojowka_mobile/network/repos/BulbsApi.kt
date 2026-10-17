package com.example.pokojowka_mobile.network.repos

import com.example.pokojowka_mobile.data.NetworkBulbData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface BulbsApi {
    @GET("devices/bulb")
    suspend fun getBulbs(): Response<List<NetworkBulbData>>

    @PUT("devices/{id}/{name}")
    suspend fun changeName(@Path("id") id: String, @Path("name") name: String): Response<Unit>

    @PUT("devices/bulb/{id}/state/{state}")
    suspend fun changeState(@Path("id") id: String, @Path("state") state: String): Response<Unit>


    @PUT("devices/bulb/{id}/brightness/{brightness}")
    suspend fun changeBrightness(@Path("id") id: String, @Path("brightness") brightness: Int): Response<Unit>

}