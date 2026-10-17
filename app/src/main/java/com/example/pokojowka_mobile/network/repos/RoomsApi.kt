package com.example.pokojowka_mobile.network.repos

import com.example.pokojowka_mobile.data.NetworkRoomData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoomsApi {
    @GET("devices/room")
    suspend fun getRooms(): Response<List<NetworkRoomData>>

    @PUT("devices/{id}/{name}")
    suspend fun changeName(@Path("id") id: String, @Path("name") name: String): Response<Unit>
}