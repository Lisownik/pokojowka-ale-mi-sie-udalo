package com.example.pokojowka_mobile.data

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.pokojowka_mobile.MainActivity

enum class NotificationType {
    ERROR, INFO, SUCCESS
}

data class NotificationData(
    val id: String,
    val type: NotificationType,
    val title: String,
    val description: String,
    val timestamp: String
) {

    val icon: ImageVector
        get() = when (type) {
            NotificationType.ERROR -> Icons.Filled.Error
            NotificationType.INFO -> Icons.Filled.Info
            NotificationType.SUCCESS -> Icons.Filled.CheckCircle
        }
}


val sampleGlobalNotificationsList = listOf(
    NotificationData("n1", NotificationType.ERROR, "Niska wilgotność gleby", "Chaber w sypialni jest do podlania.", "2 min temu"),
    NotificationData("n2", NotificationType.INFO, "Spadek temperatury", "Temp. w salonie spadła o 2 stopnie.", "20 min temu"),
    NotificationData("n3", NotificationType.SUCCESS, "Podlewanie zakończone", "Rośliny w salonie zostały podlane.", "1 godz. temu"),
    NotificationData("n4", NotificationType.ERROR, "Czujnik offline", "Czujnik dymu w garażu nie odpowiada.", "5 godz. temu"),
    NotificationData("n5", NotificationType.INFO, "Nowe urządzenie", "Wykryto nową żarówkę w kuchni.", "3 godz. temu")
)

object NotificationHelper {
    private const val CHANNEL_ID = "app_notifications"
    private const val URGENT_CHANNEL_ID = "urgent_alerts"

    @SuppressLint("ObsoleteSdkInt")
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val urgentChannel = NotificationChannel(
                URGENT_CHANNEL_ID,
                "Pilne Alerty",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pilne powiadomienia pojawiające się na ekranie"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(urgentChannel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showHeadsUpNotification(context: Context, title: String, message: String, notificationId: Int = System.currentTimeMillis().toInt()) {
        if (!hasNotificationPermission(context)) {
            Log.w("Notification", "Notification permission not granted")
            return
        }

        val notificationManager = NotificationManagerCompat.from(context)


        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("from_notification", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val builder = NotificationCompat.Builder(context, URGENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setTimeoutAfter(10000)


        builder.addAction(
            R.drawable.ic_menu_view,
            "Otwórz",
            pendingIntent
        )

        try {
            notificationManager.notify(notificationId, builder.build())
            Log.d("Notification", "Heads-up notification shown: $title")
        } catch (e: Exception) {
            Log.e("Notification", "Failed to show notification: ${e.message}")
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // Specific notification methods
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showBulbAlert(context: Context, bulbName: String, message: String) {
        showHeadsUpNotification(context, "⚡ $bulbName", message, 1001)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showRoomAlert(context: Context, roomName: String, message: String) {
        showHeadsUpNotification(context, "🏠 $roomName", message, 1002)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showPlantAlert(context: Context, plantName: String, message: String) {
        showHeadsUpNotification(context, "🌿 $plantName", message, 1003)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showImmediateAlert(context: Context, title: String, message: String) {
        showHeadsUpNotification(context, title, message)

        Toast.makeText(context, "$title: $message", Toast.LENGTH_LONG).show()

        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        vibrator?.vibrate(500)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showCriticalAlert(
        context: Context,
        alertType: String,
        location: String,
        message: String,
        immediateAction: String = "EWAKUACJA"
    ) {
        if (!hasNotificationPermission(context)) {
            Log.w("Notification", "Notification permission not granted")
            showEmergencyToast(context, alertType, location, message)
            return
        }

        val notificationManager = NotificationManagerCompat.from(context)


        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("emergency_type", alertType)
            putExtra("emergency_location", location)
            action = "EMERGENCY_ACTION"
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )




        val builder = NotificationCompat.Builder(context, URGENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle("$alertType - $location")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setTimeoutAfter(30000)
            .setOngoing(true)
            .setColor(Color.RED)
            .setColorized(true)
            .setLights(Color.RED, 1000, 1000)
            .setStyle(NotificationCompat.BigTextStyle().bigText("\uD83D\uDCCD $location\n\n\uD83D\uDEA8 $message\n\n"))

        builder.setFullScreenIntent(pendingIntent, true)

        try {
            notificationManager.notify(0x0, builder.build())
            triggerEmergencyEffects(context)
            Log.d("NOTIFY_CriticalAlert", "Critical alert shown: $alertType")
        } catch (e: Exception) {
            Log.e("NOTIFY_CriticalAlert", "Failed to show critical alert: ${e.message}")
            showEmergencyToast(context, alertType, location, message)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun triggerEmergencyEffects(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 1000, 500, 1000, 500, 1000),
                        0
                    )
                )
            } else {
                vibrator.vibrate(longArrayOf(0, 1000, 500, 1000, 500, 1000), -1)
            }
        }

        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            Log.e("CriticalAlert", "Could not play alarm sound: ${e.message}")
        }
    }

    private fun showEmergencyToast(context: Context, alertType: String, location: String, message: String) {
        Toast.makeText(
            context,
            "🚨 $alertType - $location: $message",
            Toast.LENGTH_LONG
        ).show()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showGasLeakAlert(context: Context, location: String, concentration: String = "wysokie") {
        showCriticalAlert(
            context = context,
            alertType = "WYKRYTO TLENEK WĘGLA",
            location = location,
            message = "Natychmiastowa ewakuacja!",
            immediateAction = "EWAKUUJ SIĘ NATYCHMIAST! OTWÓRZ OKNA."
        )
    }
}