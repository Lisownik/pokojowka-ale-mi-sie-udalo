package com.example.pokojowka_mobile.network

import com.example.pokojowka_mobile.BuildConfig
import com.example.pokojowka_mobile.debug.Debug
import com.example.pokojowka_mobile.debug.LogClasses
import com.example.pokojowka_mobile.network.repos.bot.BotApi
import com.example.pokojowka_mobile.network.repos.bulbs.BulbsApi
import com.example.pokojowka_mobile.network.repos.heaters.HeatersApi
import com.example.pokojowka_mobile.network.repos.program.ProgramApi
import com.example.pokojowka_mobile.network.repos.rooms.RoomsApi
import com.example.pokojowka_mobile.network.repos.windows.WindowsApi
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.Dispatcher
import okio.IOException
import kotlin.time.Duration.Companion.milliseconds


object Network{
    private const val API_URL = BuildConfig.API_URL


    val moshi: Moshi = Moshi.Builder()
        .build()

    private val client = OkHttpClient.Builder()
        .addInterceptor(HealthInterceptor())
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val sseHttpClient = client.newBuilder()
        .dispatcher(Dispatcher().apply {
            maxRequests = 64
            maxRequestsPerHost = 32
        })
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .writeTimeout(0, TimeUnit.MILLISECONDS)
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl(API_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val sse = SseClient(sseHttpClient)

    fun <T> stream(path: String, adapter: JsonAdapter<T>): Flow<T> = flow {
        val url = API_URL.trimEnd('/') + "/" + path.trimStart('/')
        var backoff = 1_000L
        while(true){
            sse.connect(url)
                .catch { e ->
                    //xd
                }
                .collect { e ->
                    when(e){
                        SseEvent.Open -> backoff = 1_000L
                        is SseEvent.Message -> {
                            try {
                                adapter.fromJson(e.data)?.let {
                                    emit(it)
                                }
                            } catch (ex: IOException){
                                Debug.logDebug(LogClasses.NETWORK_ERROR, "Bad SSE payload ${ex.message}")
                            } catch (ex: Exception){
                                Debug.logDebug(LogClasses.NETWORK_ERROR, "Error with adapter at: $path, msg: ${ex.message} \npayload=${e.data}")
                            }
                        }
                        else -> Unit
                    }
                }

            delay(backoff.milliseconds)
            backoff = (backoff * 2).coerceAtMost(1_000L)
        }
    }

    val programApi: ProgramApi = retrofit.create(ProgramApi::class.java)
    val roomsApi: RoomsApi = retrofit.create(RoomsApi::class.java)
    val bulbsApi: BulbsApi = retrofit.create(BulbsApi::class.java)
    val windowsApi: WindowsApi = retrofit.create(WindowsApi::class.java)
    val heatersApi: HeatersApi = retrofit.create(HeatersApi::class.java)
    val botApi: BotApi = retrofit.create(BotApi::class.java)
}