package com.example.pokojowka_mobile.network

import android.util.Log
import com.example.pokojowka_mobile.debug.Debug
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HealthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val response = chain.proceed(chain.request())
            if (response.isSuccessful) {
                HealthState.setHealthState(ServerHealth.HEALTHY)
            } else {
                Log.d("HealthInterceptor", "API call failed, response code: ${response.code}, message: ${response.message}")
                HealthState.setHealthState(ServerHealth.UNHEALTHY)
            }
            response
        } catch (e: Exception) {
            HealthState.setHealthState(ServerHealth.UNHEALTHY)
            Debug.descError {
                Log.e("HealthInterceptor", "Network error: ${e.message}", e)
            }
            throw e as? IOException ?: IOException("Network error", e)
        }
    }
}