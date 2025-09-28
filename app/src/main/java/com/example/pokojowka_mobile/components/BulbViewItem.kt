package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokojowka_mobile.data.BulbData
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.tlo
import kotlin.math.roundToInt

enum class BulbViewItemType {
    INFO,
    SWITCH,
    SLIDER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulbViewItem(
    modifier: Modifier = Modifier,
    itemName: String,
    itemIcon: ImageVector,
    itemType: BulbViewItemType = BulbViewItemType.INFO,
    itemValueString: String? = null,
    isChecked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    sliderValue: Float = 0f,
    sliderValueRange: ClosedFloatingPointRange<Float> = 1f..100f,
    sliderSteps: Int = 0,
    sliderValueRepresentation: (Float) -> String = { it.roundToInt().toString() },
    onSliderValueChange: ((Float) -> Unit)? = null,
    onSliderValueChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = tlo
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = itemIcon,
                        contentDescription = "$itemName icon",
                        modifier = Modifier.size(28.dp),
                        tint = contentColor.copy(alpha = if (enabled) 0.9f else 0.4f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = itemName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = contentColor.copy(alpha = if (enabled) 1f else 0.6f)
                    )
                }

                when (itemType) {
                    BulbViewItemType.INFO -> {
                        if (itemValueString != null) {
                            if (onClick != null) {
                                Text(
                                    text = itemValueString,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = contentColor.copy(alpha = if (enabled) 1f else 0.6f),
                                    modifier = Modifier.clickable(
                                        enabled = enabled,
                                        onClick = onClick
                                    )
                                )
                            } else {
                                Text(
                                    text = itemValueString,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = contentColor.copy(alpha = if (enabled) 1f else 0.6f)
                                )
                            }
                        }
                    }
                    BulbViewItemType.SWITCH -> {
                        if (onCheckedChange != null) {
                            val disabledAlpha = 0.38f
                            Switch(
                                checked = isChecked,
                                onCheckedChange = onCheckedChange,
                                enabled = enabled,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                    disabledCheckedThumbColor = MaterialTheme.colorScheme.primary.copy(alpha = disabledAlpha),
                                    disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = disabledAlpha)
                                )
                            )
                        }
                    }
                    BulbViewItemType.SLIDER -> {}
                }
            }


            if (itemType == BulbViewItemType.SLIDER && onSliderValueChange != null) {
                Spacer(modifier = Modifier.height(if (itemValueString == null && onCheckedChange == null) 4.dp else 10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val thumbRadiusBulbView = 8.dp
                    Slider(
                        value = sliderValue,
                        onValueChange = { onSliderValueChange(it) },
                        onValueChangeFinished = onSliderValueChangeFinished,
                        valueRange = sliderValueRange,
                        steps = sliderSteps,
                        enabled = enabled,
                        modifier = Modifier.weight(1f),
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(thumbRadiusBulbView * 2)
                                    .background(
                                        if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.38f
                                        ),
                                        CircleShape
                                    )
                            )
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Transparent,
                            activeTrackColor = if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            inactiveTrackColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent,
                            disabledActiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            disabledInactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            disabledThumbColor = Color.Transparent
                        )
                    )
                    Text(
                        text = sliderValueRepresentation(sliderValue),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = if (enabled) MaterialTheme.colorScheme.primary else contentColor.copy(alpha = 0.6f),
                        modifier = Modifier.widthIn(min = 55.dp)
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true, widthDp = 360)
@Composable
fun BulbViewItemInfoPreview() {
    PokojowkamobileTheme {
        BulbViewItem(itemName = "Nazwa żarówki", itemValueString = "Lampa Salon", itemIcon = Icons.Filled.Info)
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun BulbViewItemSwitchPreview() {
    PokojowkamobileTheme {
        var checked by remember { mutableStateOf(true) }
        BulbViewItem(
            itemName = "Stan",
            itemIcon = Icons.Filled.PowerSettingsNew,
            itemType = BulbViewItemType.SWITCH,
            isChecked = checked,
            onCheckedChange = { checked = it }
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun BulbViewItemBrightnessSliderPreview() {
    PokojowkamobileTheme {
        var sliderVal by remember { mutableStateOf(50f) }
        BulbViewItem(
            itemName = "Jasność",
            itemIcon = Icons.Filled.Brightness7,
            itemType = BulbViewItemType.SLIDER,
            sliderValue = sliderVal,
            onSliderValueChange = { sliderVal = it },
            sliderValueRange = 0f..100f,
            sliderSteps = 99,
            sliderValueRepresentation = { "${it.roundToInt()}%" }
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun BulbViewItemKelvinSliderPreview() {
    PokojowkamobileTheme {
        var kelvinVal by remember { mutableStateOf(3500f) }
        val minKelvin = BulbData.MIN_KELVIN.toFloat()
        val maxKelvin = BulbData.MAX_KELVIN.toFloat()
        val stepKelvin = 50f
        BulbViewItem(
            itemName = "Temperatura Barwowa",
            itemIcon = Icons.Filled.Thermostat,
            itemType = BulbViewItemType.SLIDER,
            sliderValue = kelvinVal,
            onSliderValueChange = { kelvinVal = it },
            sliderValueRange = minKelvin..maxKelvin,
            sliderSteps = ((maxKelvin - minKelvin) / stepKelvin).toInt() - 1,
            sliderValueRepresentation = { "${it.roundToInt()}K" }
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun BulbViewItemDisabledSliderPreview() {
    PokojowkamobileTheme {
        BulbViewItem(
            itemName = "Jasność (Wyłączona)",
            itemIcon = Icons.Filled.Brightness7,
            itemType = BulbViewItemType.SLIDER,
            sliderValue = 0f,
            onSliderValueChange = { },
            sliderValueRange = 0f..100f,
            sliderSteps = 99,
            sliderValueRepresentation = { "${it.roundToInt()}%" },
            enabled = false
        )
    }
}

