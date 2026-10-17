package com.example.pokojowka_mobile.network


import android.util.Log
import com.example.pokojowka_mobile.debug.Debug
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import retrofit2.Response

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(HttpException(response))
        }
    } catch (e: CancellationException) {
        Debug.descError {
            Log.e("SafeApiCall", "API call cancelled: ${e.message}", e)
        }
        throw e
    } catch (e: Exception) {
        Debug.descError {
            Log.e("SafeApiCall", "API call failed: ${e.message}", e)
        }
        Result.failure(e)
    }
}