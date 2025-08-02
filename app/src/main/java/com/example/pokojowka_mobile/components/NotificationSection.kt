package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokojowka_mobile.ui.theme.*
import com.example.pokojowka_mobile.data.NotificationData
import com.example.pokojowka_mobile.data.NotificationType
import com.example.pokojowka_mobile.data.sampleGlobalNotificationsList

@Composable
fun NotificationsSection(
    modifier: Modifier = Modifier,
    newNotificationsCount: Int,
    notifications: List<NotificationData>,
    onNotificationClick: (notificationId: String) -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Powiadomienia",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                if (newNotificationsCount > 0) {
                    Text(
                        text = "$newNotificationsCount ${if (newNotificationsCount == 1) "nowe" else if (newNotificationsCount < 5) "nowe" else "nowych"}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = NotificationErrorRed,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            if (notifications.isEmpty()) {
                Text(
                    text = "Brak nowych powiadomień.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextGray),
                    modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally)
                )
            } else {
                notifications.forEachIndexed { index, notificationData ->
                    NotificationItem(
                        type = notificationData.type,
                        icon = notificationData.icon,
                        title = notificationData.title,
                        description = notificationData.description,
                        timestamp = notificationData.timestamp,
                        onClick = { onNotificationClick(notificationData.id) }
                    )
                    if (index < notifications.size - 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsSectionPreview() {
    PokojowkamobileTheme {
        val sampleNotifications = sampleGlobalNotificationsList.take(4)
        NotificationsSection(
            newNotificationsCount = 2,
            notifications = sampleNotifications,
            onNotificationClick = { id -> println("Notification $id clicked") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsSectionEmptyPreview() {
    PokojowkamobileTheme {
        NotificationsSection(
            newNotificationsCount = 0,
            notifications = emptyList()
        )
    }
}
