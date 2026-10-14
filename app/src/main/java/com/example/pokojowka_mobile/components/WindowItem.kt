package com.example.pokojowka_mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokojowka_mobile.data.WindowData
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundLiving
import com.example.pokojowka_mobile.ui.theme.tlo

@Composable
fun WindowItem(
    modifier: Modifier = Modifier,
    windowData: WindowData,
    onWindowClick: (windowId: String) -> Unit,
    onWindowToggle: (windowId: String, isOpen: Boolean) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onWindowClick(windowData.id) },
        shape = RoundedCornerShape(16.dp),
        color = tlo,
        tonalElevation = 6.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(windowData.backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = windowData.icon,
                            contentDescription = windowData.name,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = windowData.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = windowData.roomName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Switch(
                    checked = windowData.isOpen,
                    onCheckedChange = { newState ->
                        onWindowToggle(windowData.id, newState)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DataItem(
                    icon = Icons.Filled.Thermostat,
                    value = windowData.outdoorTemp,
                    label = "Temp. zewn."
                )
                DataItem(
                    icon = Icons.Filled.WaterDrop,
                    value = if (windowData.rainDetected) "Wykryty" else "Brak",
                    label = "Opady deszczu"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WindowItemPreview() {
    PokojowkamobileTheme {
        val sampleWindow = WindowData(
            id = "w1",
            name = "Okno Salon",
            roomName = "Salon",
            backgroundColor = RoomIconBackgroundLiving,
            isOpen = true,
            rainDetected = false,
            outdoorTemp = "21°C"
        )
        WindowItem(
            windowData = sampleWindow,
            onWindowClick = {},
            onWindowToggle = { _, _ -> }
        )
    }
}
