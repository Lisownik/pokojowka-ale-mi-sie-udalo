
package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokojowka_mobile.data.BulbData
import com.example.pokojowka_mobile.data.sampleBulbsGlobalList
import com.example.pokojowka_mobile.ui.theme.*

@Composable
fun BulbsSection(
    modifier: Modifier = Modifier,
    activeBulbsCount: Int,
    bulbs: List<BulbData>,
    onBulbSwitchToggle: (bulbId: String, newState: Boolean) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Żarówki",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "$activeBulbsCount aktywnych",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = RoomsGradientStart,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (bulbs.isEmpty()) {
                Text(
                    text = "Brak skonfigurowanych żarówek.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                bulbs.forEachIndexed { index, bulbData ->
                    BulbItem(
                        roomName = bulbData.roomName,
                        brightness = bulbData.brightnessDisplay,
                        isSwitchedOn = bulbData.isSwitchedOn,
                        iconBackgroundColor = bulbData.currentIconColor,
                        onSwitchToggle = { newState ->
                            onBulbSwitchToggle(bulbData.id, newState)
                        }
                    )
                    if (index < bulbs.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BulbsSectionPreview() {
    PokojowkamobileTheme {
        val sampleBulbsForPreview = remember {

            mutableStateListOf<BulbData>().apply {
                addAll(sampleBulbsGlobalList.take(3).map { it.copy() })
            }
        }

        val activeCount = sampleBulbsForPreview.count { it.isSwitchedOn }

        BulbsSection(
            activeBulbsCount = activeCount,
            bulbs = sampleBulbsForPreview,
            onBulbSwitchToggle = { id, newState ->
                val bulb = sampleBulbsForPreview.find { it.id == id }
                bulb?.let {

                    it.isSwitchedOn = newState
                    if (!newState) {
                        it.brightnessPercentage = 0
                    } else if (it.brightnessPercentage == 0) {
                        it.brightnessPercentage = 50
                    }
                }
            }
        )
    }
}
