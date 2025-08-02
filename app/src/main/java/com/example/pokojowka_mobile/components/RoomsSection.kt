package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.pokojowka_mobile.data.RoomData
import com.example.pokojowka_mobile.data.RoomStatus

import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundBed
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundKitchen
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundLiving


@Composable
fun RoomsSection(
    modifier: Modifier = Modifier,
    rooms: List<RoomData>,
    onRoomClick: (roomId: String) -> Unit,
    title: String = "Twoje Pokoje"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
            )
        }

        if (rooms.isEmpty()) {
            Text(
                text = "Nie masz jeszcze żadnych pokoi.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            )
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rooms.forEach { roomData ->
                    RoomItem(
                        roomData = roomData,
                        onRoomClick = { onRoomClick(roomData.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun RoomsSectionPreview() {
    PokojowkamobileTheme {

        val sampleRooms = remember {
            listOf(
                RoomData(
                    id = "1",
                    name = "Sypialnia",
                    icon = Icons.Filled.Bed,
                    backgroundColor = RoomIconBackgroundBed,
                    status = RoomStatus.GOOD,
                    temperature = "21°C",
                    humidity = "50%",
                    airQuality = "Dobra",
                    pressure = "1010 hPa"
                ),
                RoomData(
                    id = "2",
                    name = "Salon",
                    icon = Icons.Default.Weekend,
                    backgroundColor = RoomIconBackgroundLiving,
                    status = RoomStatus.MEDIUM,
                    temperature = "23°C",
                    humidity = "55%",
                    airQuality = "Średnia",
                    pressure = "1010 hPa"
                ),
                RoomData(
                    id = "3",
                    name = "Kuchnia",
                    icon = Icons.Filled.Kitchen,
                    backgroundColor = RoomIconBackgroundKitchen,
                    status = RoomStatus.BAD,
                    temperature = "25°C",
                    humidity = "65%",
                    airQuality = "Zła",
                    pressure = "1010 hPa"
                )
            )
        }
        RoomsSection(
            rooms = sampleRooms,
            onRoomClick = { roomId ->
                println("Clicked room: $roomId")
            },
            title = "Pokoje (Preview)"
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun EmptyRoomsSectionPreview() {
    PokojowkamobileTheme {
        RoomsSection(
            rooms = emptyList(),
            onRoomClick = {},
            title = "Brak Pokoi (Preview)"
        )
    }
}
