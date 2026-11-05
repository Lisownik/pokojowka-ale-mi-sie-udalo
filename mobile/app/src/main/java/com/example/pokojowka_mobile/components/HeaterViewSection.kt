package com.example.pokojowka_mobile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokojowka_mobile.data.HeaterData
import com.example.pokojowka_mobile.ui.theme.tlo
import kotlin.math.roundToInt

@Composable
fun HeaterViewSection(
    modifier: Modifier = Modifier,
    heaterData: HeaterData,
    onHeatingToggle: (isHeatingOn: Boolean) -> Unit,
    onTargetTempChange: (targetTemp: Float) -> Unit,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = tlo),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Stan Grzania",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = heaterData.statusText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = heaterData.statusColor
                    )
                }

                Switch(
                    checked = heaterData.isHeatingOn,
                    onCheckedChange = { onHeatingToggle(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFF97316)
                    )
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = tlo),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ustaw Temperaturę Docelową",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    FilledIconButton(
                        onClick = {
                            val newTemp = (heaterData.targetTemp - 0.5f).coerceAtLeast(16.0f)
                            onTargetTempChange((newTemp * 2).roundToInt() / 2.0f)
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Zmniejsz temperaturę"
                        )
                    }

                    Text(
                        text = "${"%.1f".format(heaterData.targetTemp)}°C",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                            color = if (heaterData.isHeatingOn) Color(0xFFF97316) else contentColor
                        )
                    )

                    FilledIconButton(
                        onClick = {
                            val newTemp = (heaterData.targetTemp + 0.5f).coerceAtMost(28.0f)
                            onTargetTempChange((newTemp * 2).roundToInt() / 2.0f)
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFFF97316),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Zwiększ temperaturę"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = heaterData.targetTemp,
                    onValueChange = { newValue ->
                        val roundedTemp = (newValue * 2).roundToInt() / 2.0f
                        onTargetTempChange(roundedTemp)
                    },
                    valueRange = 16.0f..28.0f,
                    steps = 23,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFF97316),
                        activeTrackColor = Color(0xFFF97316),
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("16.0°C", style = MaterialTheme.typography.labelMedium, color = contentColor.copy(alpha = 0.6f))
                    Text("28.0°C", style = MaterialTheme.typography.labelMedium, color = contentColor.copy(alpha = 0.6f))
                }
            }
        }
    }
}
