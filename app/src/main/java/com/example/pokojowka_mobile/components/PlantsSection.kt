package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.pokojowka_mobile.data.PlantData
import com.example.pokojowka_mobile.data.PlantHealthStatus

import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.RoomIconBackgroundBathroom



@Composable
fun PlantsSection(
    modifier: Modifier = Modifier,
    plants: List<PlantData>,
    onPlantClick: (plantId: String) -> Unit,
    title: String = "Twoje Rośliny",
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                modifier = Modifier.padding(
                    start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
                    top = 16.dp,
                    end = contentPadding.calculateRightPadding(LayoutDirection.Ltr),
                    bottom = 12.dp
                )
            )
        }

        if (plants.isEmpty()) {
            Text(
                text = "Nie masz jeszcze żadnych roślin.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(vertical = 24.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {

            Column(
                modifier = Modifier.padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                plants.forEach { plantData ->
                    PlantItem(
                        plantData = plantData,
                        onPlantClick = { onPlantClick(plantData.id) }
                    )
                }
            }
        }
    }
}

