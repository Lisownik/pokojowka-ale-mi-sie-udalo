package com.example.pokojowka_mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Whatshot
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
import com.example.pokojowka_mobile.data.HeaterData
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundLiving
import com.example.pokojowka_mobile.ui.theme.tlo

@Composable
fun HeaterItem(
    modifier: Modifier = Modifier,
    heaterData: HeaterData,
    onHeaterClick: (heaterId: String) -> Unit,
    onHeaterToggle: (heaterId: String, isHeatingOn: Boolean) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onHeaterClick(heaterData.id) },
        shape = RoundedCornerShape(16.dp),
        color = tlo,
        tonalElevation = 6.dp
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
                            .background(if (heaterData.isHeatingOn) Color(0xFFF97316) else heaterData.backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = heaterData.icon,
                            contentDescription = heaterData.name,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = heaterData.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = heaterData.roomName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Switch(
                    checked = heaterData.isHeatingOn,
                    onCheckedChange = { newState ->
                        onHeaterToggle(heaterData.id, newState)
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
                    value = heaterData.currentTemp,
                    label = "Obecna Temp."
                )
                DataItem(
                    icon = Icons.Filled.Whatshot,
                    value = "${"%.1f".format(heaterData.targetTemp)}°C",
                    label = "Cel"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeaterItemPreview() {
    PokojowkamobileTheme {
        val sampleHeater = HeaterData(
            id = "h1",
            name = "Grzejnik Salon",
            roomName = "Salon",
            backgroundColor = RoomIconBackgroundLiving,
            isHeatingOn = true,
            currentTemp = "21.5°C",
            targetTemp = 23.0f,
        )
        HeaterItem(
            heaterData = sampleHeater,
            onHeaterClick = {},
            onHeaterToggle = { _, _ -> }
        )
    }
}
