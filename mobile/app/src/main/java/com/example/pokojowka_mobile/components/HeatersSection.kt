package com.example.pokojowka_mobile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokojowka_mobile.data.GlobalHeatersList
import com.example.pokojowka_mobile.data.HeaterData
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme

@Composable
fun HeatersSection(
    modifier: Modifier = Modifier,
    heaters: List<HeaterData>,
    onHeaterClick: (heaterId: String) -> Unit,
    onHeaterToggle: (heaterId: String, isHeatingOn: Boolean) -> Unit,
    title: String = "Twoje Grzejniki"
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

        if (heaters.isEmpty()) {
            Text(
                text = "Nie masz jeszcze grzejników.",
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
                heaters.forEach { heaterData ->
                    HeaterItem(
                        heaterData = heaterData,
                        onHeaterClick = { onHeaterClick(heaterData.id) },
                        onHeaterToggle = onHeaterToggle
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun HeatersSectionPreview() {
    PokojowkamobileTheme {
        HeatersSection(
            heaters = GlobalHeatersList,
            onHeaterClick = {},
            onHeaterToggle = { _, _ -> }
        )
    }
}
