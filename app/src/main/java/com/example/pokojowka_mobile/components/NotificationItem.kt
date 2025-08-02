package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokojowka_mobile.ui.theme.*
import com.example.pokojowka_mobile.data.NotificationType

@Composable
fun NotificationItem(
    modifier: Modifier = Modifier,
    type: NotificationType,
    icon: ImageVector,
    title: String,
    description: String,
    timestamp: String,
    onClick: () -> Unit = {}
) {
    val barColor: Color
    val tintColor: Color
    val iconColor: Color

    when (type) {
        NotificationType.ERROR -> {
            barColor = NotificationErrorRed
            tintColor = NotificationErrorTint
            iconColor = NotificationErrorRed
        }
        NotificationType.INFO -> {
            barColor = NotificationInfoBlue
            tintColor = NotificationInfoTint
            iconColor = NotificationInfoBlue
        }
        NotificationType.SUCCESS -> {
            barColor = NotificationSuccessGreen
            tintColor = NotificationSuccessTint
            iconColor = NotificationSuccessGreen
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = tintColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(barColor)
            )


            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = type.name,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = NotificationTextPrimary
                        )
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            color = NotificationTextSecondary
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = timestamp,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = NotificationTextTertiary
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationItemErrorPreview() {
    PokojowkamobileTheme {
        NotificationItem(
            type = NotificationType.ERROR,
            icon = Icons.Filled.Error,
            title = "Niska wilgotność gleby",
            description = "Chaber w sypialni jest do podlania.",
            timestamp = "2 min temu"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationItemInfoPreview() {
    PokojowkamobileTheme {
        NotificationItem(
            type = NotificationType.INFO,
            icon = Icons.Filled.Info,
            title = "Spadek temperatury",
            description = "Temp. w salonie spadła o 2 stopnie.",
            timestamp = "20 min temu"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationItemSuccessPreview() {
    PokojowkamobileTheme {
        NotificationItem(
            type = NotificationType.SUCCESS,
            icon = Icons.Filled.CheckCircle,
            title = "System zaktualizowany",
            description = "Wszystkie urządzenia działają poprawnie.",
            timestamp = "1 godz. temu"
        )
    }
}

