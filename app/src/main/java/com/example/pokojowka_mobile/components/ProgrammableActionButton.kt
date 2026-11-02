package com.example.pokojowka_mobile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pokojowka_mobile.network.repos.ProgramRepository
import com.example.pokojowka_mobile.notifications.Notifier
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.RoomsGradientEnd
import com.example.pokojowka_mobile.ui.theme.RoomsGradientStart

@Composable
fun ProgrammableActionButton(
    modifier: Modifier = Modifier,
    onChatClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Akcja Programowalna",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Teścik",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Zamknij")
                }
            }
        )
    }

    Surface(
        modifier = modifier
            .height(52.dp)
            .width(104.dp)
            .clip(RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        shadowElevation = 8.dp,
        tonalElevation = 6.dp,
        border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(RoomsGradientStart, RoomsGradientEnd)
                    )
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        showDialog = true
                        Notifier.safeNotification("Programmable button", "Testowa wiadomość")
                        ProgramRepository.emitProgram()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Akcja",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(26.dp)
                    .background(Color.White.copy(alpha = 0.35f))
            )


            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        onChatClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SmartToy,
                    contentDescription = "Chatbot AI",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ProgrammableActionButtonPreview() {
    PokojowkamobileTheme {
        ProgrammableActionButton(onChatClick = {})
    }
}
