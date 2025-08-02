package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokojowka_mobile.data.BulbData
import com.example.pokojowka_mobile.ui.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulbControlItem(
    modifier: Modifier = Modifier,
    bulb: BulbData,
    onSwitchToggle: (newState: Boolean) -> Unit,
    onBrightnessChange: (newBrightness: Int) -> Unit,
    onItemClick: (bulbId: String) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onItemClick(bulb.id) },
        shape = RoundedCornerShape(12.dp),
        color = LightBulbItemBackground
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(bulb.currentIconColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lightbulb,
                            contentDescription = "Żarówka",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = bulb.roomName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp
                            )
                        )
                        Text(
                            text = bulb.brightnessDisplay,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextGray,
                                fontSize = 13.sp
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                Switch(
                    checked = bulb.isSwitchedOn,
                    onCheckedChange = onSwitchToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                )
            }

            if (bulb.isSwitchedOn) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val thumbRadius = 6.dp
                    Slider(
                        value = bulb.brightnessPercentage.toFloat(),
                        onValueChange = { newValue ->
                            onBrightnessChange(newValue.roundToInt())
                        },
                        valueRange = 0f..100f,
                        steps = 99,
                        modifier = Modifier.weight(1f),
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(thumbRadius * 2)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            )
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Transparent,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent
                        )
                    )
                    Text(
                        text = "${bulb.brightnessPercentage}%",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.width(50.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "BulbControlItem - ON")
@Composable
fun BulbControlItemOnPreview() {
    PokojowkamobileTheme {
        var bulbState by remember {
            mutableStateOf(
                BulbData(
                    id = "preview_on",
                    roomName = "Salon (Podgląd)",
                    brightnessPercentage = 75,
                    isSwitchedOn = true,
                    colorTemperatureKelvin = 3500
                )
            )
        }
        BulbControlItem(
            bulb = bulbState,
            onSwitchToggle = { newState ->
                bulbState = bulbState.copy(
                    isSwitchedOn = newState,
                    brightnessPercentage = if (!newState) 0 else if (bulbState.brightnessPercentage == 0) 50 else bulbState.brightnessPercentage
                )
            },
            onBrightnessChange = { newBrightness ->
                bulbState = bulbState.copy(brightnessPercentage = newBrightness)
            },
            onItemClick = { bulbId -> println("Clicked bulb: $bulbId") }
        )
    }
}

@Preview(showBackground = true, name = "BulbControlItem - OFF")
@Composable
fun BulbControlItemOffPreview() {
    PokojowkamobileTheme {
        var bulbState by remember {
            mutableStateOf(
                BulbData(
                    id = "preview_off",
                    roomName = "Sypialnia (Podgląd)",
                    brightnessPercentage = 0,
                    isSwitchedOn = false,
                    colorTemperatureKelvin = 2700
                )
            )
        }
        BulbControlItem(
            bulb = bulbState,
            onSwitchToggle = { newState ->
                bulbState = bulbState.copy(
                    isSwitchedOn = newState,
                    brightnessPercentage = if (!newState) 0 else if (bulbState.brightnessPercentage == 0) 50 else bulbState.brightnessPercentage
                )
            },
            onBrightnessChange = { newBrightness ->
                bulbState = bulbState.copy(brightnessPercentage = newBrightness)
            },
            onItemClick = { bulbId -> println("Clicked bulb: $bulbId") }
        )
    }
}
