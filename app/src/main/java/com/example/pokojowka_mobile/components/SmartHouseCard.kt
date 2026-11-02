package com.example.pokojowka_mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
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
import com.example.pokojowka_mobile.data.HouseData
import com.example.pokojowka_mobile.data.SampleHouseData
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.RoomStatusGood
import com.example.pokojowka_mobile.ui.theme.tlo

@Composable
fun SmartHouseCard(
    modifier: Modifier = Modifier,
    houseData: HouseData
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = tlo,
        shadowElevation = 4.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (weatherIcon, weatherText, weatherColor) = when (houseData.weatherState) {
                "sunny" -> Triple(Icons.Filled.WbSunny, "Słonecznie", Color(0xFFFBBF24))
                "dark" -> Triple(Icons.Filled.NightsStay, "Pochmurno", Color(0xFF94A3B8))
                "sunny-rain" -> Triple(Icons.Filled.Grain, "Przelotne", Color(0xFF38BDF8))
                "dark-rain" -> Triple(Icons.Filled.WaterDrop, "Leje", Color(0xFFEF4444))
                else -> Triple(Icons.Filled.WbSunny, houseData.weatherCondition, Color(0xFFFBBF24))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(weatherColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = weatherIcon,
                        contentDescription = "Pogoda",
                        tint = weatherColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Pogoda",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = weatherText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .height(36.dp)
                    .width(1.5.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                val doorColor = if (houseData.isDoorLocked) RoomStatusGood else Color(0xFFF97316)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Drzwi",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = if (houseData.isDoorLocked) "Zamknięte" else "Otwarte",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = doorColor
                        )
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(doorColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (houseData.isDoorLocked) Icons.Filled.Lock else Icons.Filled.LockOpen,
                        contentDescription = "Stan Drzwi",
                        tint = doorColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SmartHouseCardPreview() {
    PokojowkamobileTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
            SmartHouseCard(
                houseData = SampleHouseData.copy(weatherState = "sunny", isDoorLocked = true)
            )
            SmartHouseCard(
                houseData = SampleHouseData.copy(weatherState = "dark-rain", isDoorLocked = false)
            )
        }
    }
}
