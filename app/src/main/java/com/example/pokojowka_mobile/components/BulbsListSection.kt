package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pokojowka_mobile.data.BulbData
import com.example.pokojowka_mobile.data.sampleBulbsGlobalList
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme


@Composable
fun BulbsListSection(
    modifier: Modifier = Modifier,
    bulbs: List<BulbData>,
    onSwitchToggle: (bulbId: String, newState: Boolean) -> Unit,
    onBrightnessChange: (bulbId: String, newBrightness: Int) -> Unit,
    onItemClick: (bulbId: String) -> Unit,
    title: String = "Wszystkie Żarówki"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
            )
        }

        if (bulbs.isEmpty()) {
            Text(
                text = "Brak dostępnych żarówek.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                bulbs.forEach { bulbData ->
                    BulbControlItem(
                        bulb = bulbData,
                        onSwitchToggle = { newState ->
                            onSwitchToggle(bulbData.id, newState)
                        },
                        onBrightnessChange = { newBrightness ->
                            onBrightnessChange(bulbData.id, newBrightness)
                        },
                        onItemClick = { onItemClick(bulbData.id) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BulbsListSectionPreview() {
    PokojowkamobileTheme {
        BulbsListSection(
            bulbs = sampleBulbsGlobalList.take(3),
            onSwitchToggle = { _, _ -> },
            onBrightnessChange = { _, _ -> },
            onItemClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BulbsListSectionEmptyPreview() {
    PokojowkamobileTheme {
        BulbsListSection(
            bulbs = emptyList(),
            onSwitchToggle = { _, _ -> },
            onBrightnessChange = { _, _ -> },
            onItemClick = { }
        )
    }
}
