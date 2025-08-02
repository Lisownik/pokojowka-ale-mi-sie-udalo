package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pokojowka_mobile.data.TrendData
import com.example.pokojowka_mobile.data.ValueTrend
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme

data class SectionItemUIData(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val trend: TrendData? = null
)

@Composable
fun RoomViewSection(
    modifier: Modifier = Modifier,
    title: String,
    items: List<SectionItemUIData>,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (items.isEmpty()) {
            Text(
                text = "Brak szczegółowych danych.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { uiItem ->

                    RoomViewItem(
                        itemName = uiItem.label,
                        itemValue = uiItem.value,
                        itemIcon = uiItem.icon,
                        trendData = uiItem.trend
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun RoomViewSectionPreview_WithData() {
    PokojowkamobileTheme {
        val sampleUIItems = listOf(
            SectionItemUIData(
                label = "Temperatura",
                value = "23.5°C",
                icon = Icons.Filled.Thermostat,
                trend = TrendData(ValueTrend.UP, "+1.5°C")
            ),
            SectionItemUIData(
                label = "Wilgotność",
                value = "55%",
                icon = Icons.Filled.Opacity,
                trend = TrendData(ValueTrend.DOWN, "-5%")
            )
        )
        RoomViewSection(
            title = "Parametry Pomieszczenia",
            items = sampleUIItems
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun RoomViewSectionPreview_Empty() {
    PokojowkamobileTheme {
        RoomViewSection(
            title = "Czujniki",
            items = emptyList()
        )
    }
}
