package com.example.pokojowka_mobile.network.repos.bot

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BotApi {
    @GET("gpt/commands/{msg}")
    suspend fun sendMessage(@Path("msg") message: String): Response<String>
}