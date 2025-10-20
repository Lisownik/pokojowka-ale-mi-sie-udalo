package com.example.pokojowka_mobile.network
import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.Toast
import com.example.pokojowka_mobile.data.NetworkBulbData
import com.example.pokojowka_mobile.data.NetworkPlantData
import com.example.pokojowka_mobile.data.NetworkRoomData
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL =
    "http://10.0.2.2:3333"

interface ApiService {
    @GET("rooms")
    suspend fun getRooms(): Array<NetworkRoomData>

    @PUT("room/{id}/name")
    suspend fun changeRoomName(@Path("id") id: String, @Query("newName") newName: String)

    @GET("bulbs")
    suspend fun getBulbs(): Array<NetworkBulbData>

    @PUT("bulb/{id}/name")
    suspend fun changeBulbName(@Path("id") id: String, @Query("newName") newName: String)

    @PUT("bulb/{id}/power")
    suspend fun changeBulbPowerState(@Path("id") id: String, @Query("power_state") powerState: String)

    @PUT("bulb/{id}/brightness")
    suspend fun changeBulbBrightness(@Path("id") id: String, @Query("brightness") brightness: Int, @Query("duration") duration: Int)

    @GET("pot")
    suspend fun getPlants(): Array<NetworkPlantData>

    @PUT("pot/{id}/{name}")
    suspend fun changePotName(@Path("id") id: String, @Path("name") name: String)
}

object RetrofitClient {
    private const val BASE_URL = com.example.pokojowka_mobile.network.BASE_URL
    public const val BULB_CHANGE_DURATION = 1000
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

fun showRenameRoomDialog(context: Context, currentName: String, onRename: (String) -> Unit) {
    val input = EditText(context).apply {
        setText(currentName)
        hint = "Wprowadź nową nazwę"
    }

    AlertDialog.Builder(context)
        .setTitle("Zmień nazwę pokoju")
        .setView(input)
        .setNegativeButton("Anuluj") { dialog, _ ->
            dialog.dismiss()
        }
        .setPositiveButton("Zatwierdź") { dialog, _ ->
            val newName = input.text.toString().trim()
            if (newName.isNotEmpty()) {
                onRename(newName)
            } else {
                Toast.makeText(context, "Nazwa nie może być pusta", Toast.LENGTH_SHORT).show()
            }
        }
        .show()
}
