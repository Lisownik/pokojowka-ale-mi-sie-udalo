package com.example.pokojowka_mobile.network.repos

import retrofit2.Response
import retrofit2.http.POST

interface ProgramApi {
    @POST("event/program")
    suspend fun emitProgram(): Response<Unit>
}