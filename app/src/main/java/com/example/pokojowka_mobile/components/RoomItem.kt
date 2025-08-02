package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.pokojowka_mobile.data.RoomData
import com.example.pokojowka_mobile.data.RoomStatus


import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundBed
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundLiving
import com.example.pokojowka_mobile.ui.theme.tlo


@Composable
fun RoomItem(
    modifier: Modifier = Modifier,
    roomData: RoomData,
    onRoomClick: (roomId: String) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onRoomClick(roomData.id) },
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
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(roomData.backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = roomData.icon,
                            contentDescription = roomData.name,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = roomData.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(roomData.statusColor, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DataItem(icon = Icons.Filled.Thermostat, value = roomData.temperature, label = "Temperatura")
                DataItem(icon = Icons.Filled.Opacity, value = roomData.humidity, label = "Wilgotność")
                DataItem(icon = Icons.Filled.Air, value = roomData.airQuality, label = "Jakość Pow.")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoomItemPreview() {
    PokojowkamobileTheme {

        val sampleRoom = RoomData(
            id = "1",
            name = "Sypialnia",
            icon = Icons.Filled.Bed,
            backgroundColor = RoomIconBackgroundBed,
            status = RoomStatus.GOOD,
            temperature = "22°C",
            humidity = "45%",
            airQuality = "Dobra",
            pressure = "1010 hPa"
        )
        RoomItem(roomData = sampleRoom, onRoomClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun RoomItemMediumStatusPreview() {
    PokojowkamobileTheme {
        val sampleRoom = RoomData(
            id = "2",
            name = "Salon",
            icon = Icons.Filled.Bed,
            backgroundColor = RoomIconBackgroundLiving,
            status = RoomStatus.MEDIUM,
            temperature = "24°C",
            humidity = "60%",
            airQuality = "Średnia",
            pressure = "1010 hPa"
        )
        RoomItem(roomData = sampleRoom, onRoomClick = {})
    }
}
