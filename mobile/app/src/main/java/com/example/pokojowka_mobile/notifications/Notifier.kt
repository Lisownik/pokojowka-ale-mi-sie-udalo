package com.example.pokojowka_mobile.notifications


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat

object Notifier{
    private const val CHANNEL_ID = "my_pokojowka_albert_black"
    var initialized: Boolean = false
    private lateinit var appContext: Context

    @RequiresApi(Build.VERSION_CODES.O)
    fun init(context: Context) {
        Log.d("Notifier", "Initializing notifier")
        this.appContext = context.applicationContext
        val channel = NotificationChannel(
            CHANNEL_ID,
            "My Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        NotificationManagerCompat.from(appContext).createNotificationChannel(channel)
        initialized = true
    }


    fun safeNotification(title: String, message: String, icon: Int = android.R.drawable.ic_dialog_info) {
        if (!initialized) {
            Log.w("Notifier", "Notifier is not initialized. Call Notifier.init(context) first.")
            throw IllegalStateException("Notifier is not initialized. Call Notifier.init(context) first.")
        }
        showNotification(title, message, icon)
    }


    @SuppressLint("MissingPermission")
    fun showNotification(title: String, message: String, icon: Int = android.R.drawable.ic_dialog_info) {
        val builder = androidx.core.app.NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat.from(appContext).notify(1, builder.build())
    }
}