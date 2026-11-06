package com.example.pokojowka_mobile.notifications


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.core.app.NotificationManagerCompat
import com.example.pokojowka_mobile.R
import androidx.core.net.toUri

object Notifier{
    private const val CHANNEL_ID = "my_pokojowka_albert_black"
    private const val SILENT_ID = "my_pokojowka_silent"
    var initialized: Boolean = false
    private lateinit var appContext: Context


    @RequiresApi(Build.VERSION_CODES.O)
    fun init(context: Context) {
        Log.d("Notifier", "Initializing notifier")
        this.appContext = context.applicationContext

        val soundUri = "android.resource://${context.packageName}/${R.raw.notification}".toUri()

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Normal Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply{
            setSound(soundUri, audioAttributes)
        }

        NotificationManagerCompat.from(appContext).createNotificationChannel(channel)

        val silentChannel = NotificationChannel(
            SILENT_ID,
            "Silent notifications",
            NotificationManager.IMPORTANCE_MIN
        ).apply{
            description = "Notifications without sound or pop"
            setSound(null, null)
            enableVibration(false)
        }
        NotificationManagerCompat.from(appContext).createNotificationChannel(silentChannel)

        initialized = true
    }


    fun safeNotification(title: String, message: String, icon: Int = android.R.drawable.ic_dialog_info) {
        if (!initialized) {
            Log.w("Notifier", "Notifier is not initialized. Call Notifier.init(context) first.")
            throw IllegalStateException("Notifier is not initialized. Call Notifier.init(context) first.")
        }
        showNotification(title, message, CHANNEL_ID, icon)
    }


    fun silentNotification(title: String, message: String, icon: Int = android.R.drawable.ic_dialog_info){
        if(!initialized){
            Log.w("Notifier", "Notifier is not initialized. Call Notifier.init(context) first.")
        }

        showNotification(title, message, SILENT_ID,icon)
    }


    @SuppressLint("MissingPermission")
    fun showNotification(title: String, message: String, channelId: String, icon: Int = android.R.drawable.ic_dialog_info) {
        val builder = androidx.core.app.NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat.from(appContext).notify(1, builder.build())
    }
}