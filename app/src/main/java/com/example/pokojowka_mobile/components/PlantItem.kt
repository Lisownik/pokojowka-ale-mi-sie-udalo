package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.pokojowka_mobile.data.PlantData
import com.example.pokojowka_mobile.data.PlantHealthStatus
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundBathroom
import com.example.pokojowka_mobile.ui.theme.tlo



@Composable
fun PlantItem(
    modifier: Modifier = Modifier,
    plantData: PlantData,
    onPlantClick: (plantId: String) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onPlantClick(plantData.id) },
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
                            .background(plantData.backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = plantData.icon,
                            contentDescription = plantData.name,

                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = plantData.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = plantData.species,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(plantData.statusColor, CircleShape)
                        .padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DataItem(
                    icon = Icons.Filled.Lightbulb,
                    value = plantData.lightLevel,
                    label = "Światło"

                )
                DataItem(
                    icon = Icons.Filled.Opacity,
                    value = plantData.soilMoisture,
                    label = "Wilgotność"
                )
                DataItem(
                    icon = Icons.Filled.Spa,
                    value = plantData.fertilizerLevel,
                    label = "Nawóz"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (plantData.roomLocation != null) {
                Text(
                    text = "Lokalizacja: ${plantData.roomLocation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}




@Preview(showBackground = true, backgroundColor = 0xFFF7F7F7)
@Composable
fun PlantItemPreview() {
    PokojowkamobileTheme {
        val samplePlant = PlantData(
            id = "p_prev1",
            name = "Monstera Okazała",
            species = "Deliciosa",
            icon = Icons.Filled.Eco,
            healthStatus = PlantHealthStatus.HEALTHY,
            lightLevel = "Jasne",
            soilMoisture = "65%",
            fertilizerLevel = "W normie",
            roomLocation = "Salon Kasi",
            backgroundColor = RoomIconBackgroundBathroom
        )
        PlantItem(plantData = samplePlant, onPlantClick = {})
    }
}



