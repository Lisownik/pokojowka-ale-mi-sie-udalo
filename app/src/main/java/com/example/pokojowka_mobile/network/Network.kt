package com.example.pokojowka_mobile.network

import com.example.pokojowka_mobile.BuildConfig
import com.example.pokojowka_mobile.network.repos.BulbsApi
import com.example.pokojowka_mobile.network.repos.ProgramApi
import com.example.pokojowka_mobile.network.repos.RoomsApi
import com.example.pokojowka_mobile.network.repos.WindowsApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object Network{
    private const val API_URL = BuildConfig.API_URL

    private val client = OkHttpClient.Builder()
        .addInterceptor(HealthInterceptor())
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(API_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val programApi: ProgramApi = retrofit.create(ProgramApi::class.java)
    val roomsApi: RoomsApi = retrofit.create(RoomsApi::class.java)
    val bulbsApi: BulbsApi = retrofit.create(BulbsApi::class.java)
    val windowsApi: WindowsApi = retrofit.create(WindowsApi::class.java)
}