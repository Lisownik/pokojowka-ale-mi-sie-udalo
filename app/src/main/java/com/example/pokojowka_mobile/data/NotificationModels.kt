package com.example.pokojowka_mobile.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

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
