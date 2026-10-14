package com.example.pokojowka_mobile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.RoomsGradientEnd
import com.example.pokojowka_mobile.ui.theme.RoomsGradientStart

@Composable
fun ProgrammableActionButton(
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "",
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
            .size(56.dp)
            .clip(CircleShape)
            .clickable { showDialog = true },
        shape = CircleShape,
        shadowElevation = 10.dp,
        tonalElevation = 6.dp,
        border = BorderStroke(2.dp, Color.White)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(RoomsGradientStart, RoomsGradientEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Zaprogramowalny Przycisk Akcji",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Preview
@Composable
fun ProgrammableActionButtonPreview() {
    PokojowkamobileTheme {
        ProgrammableActionButton()
    }
}
