package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Klasa pomocnicza do przekazywania danych do sekcji.
 */
data class PlantUIData(
    val label: String,
    val value: String,
    val icon: ImageVector,
)

/**
 * Komponent grupujący wiele kafelków `PlantViewItem` w jedną sekcję z tytułem.
 */
@Composable
fun PlantViewSection(
    modifier: Modifier = Modifier,
    title: String,
    items: List<PlantUIData>,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        // Tytuł sekcji
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Komunikat, jeśli brak danych
        if (items.isEmpty()) {
            Text(
                text = "Brak szczegółowych danych.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            )
        } else {
            // Lista kafelków z parametrami
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { uiItem ->
                    PlantViewItem(
                        itemName = uiItem.label,
                        itemValue = uiItem.value,
                        itemIcon = uiItem.icon
                    )
                }
            }
        }
    }
}
