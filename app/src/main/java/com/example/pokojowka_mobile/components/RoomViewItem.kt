package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.difference
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokojowka_mobile.data.TrendData
import com.example.pokojowka_mobile.data.ValueTrend
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.RoomStatusGood
import com.example.pokojowka_mobile.ui.theme.RoomStatusBad
import com.example.pokojowka_mobile.ui.theme.RoomStatusMedium
import com.example.pokojowka_mobile.ui.theme.tlo


@Composable
fun RoomViewItem(
    modifier: Modifier = Modifier,
    itemName: String,
    itemValue: String,
    itemIcon: ImageVector,
    trendData: TrendData?,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = tlo),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp

        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = itemName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = itemValue,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))


                if (trendData != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val trendIcon: ImageVector
                        val trendColor: Color
                        when (trendData.trend) {
                            ValueTrend.UP -> {
                                trendIcon = Icons.Filled.ArrowUpward
                                trendColor = RoomStatusGood
                            }
                            ValueTrend.DOWN -> {
                                trendIcon = Icons.Filled.ArrowDownward
                                trendColor = RoomStatusBad
                            }
                            ValueTrend.SAME -> {
                                trendIcon = Icons.Filled.CompareArrows
                                trendColor = RoomStatusMedium
                            }
                            ValueTrend.UNKNOWN -> {
                                trendIcon = Icons.Filled.Remove
                                trendColor = contentColor.copy(alpha = 0.7f)
                            }
                        }
                        Icon(
                            imageVector = trendIcon,
                            contentDescription = "Trend: ${trendData.trend}",
                            tint = trendColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = trendData.difference,
                            style = MaterialTheme.typography.bodyMedium,
                            color = trendColor
                        )
                    }
                } else {

                    Text(
                        text = "Trend: b.d.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.5f)
                    )
                }
            }

            Icon(
                imageVector = itemIcon,
                contentDescription = "$itemName icon",
                modifier = Modifier.size(36.dp),
                tint = contentColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun RoomViewItemPreview_Up() {
    PokojowkamobileTheme {
        RoomViewItem(
            itemName = "Temperatura",
            itemValue = "23.5°C",
            itemIcon = Icons.Filled.Thermostat,
            trendData = TrendData(ValueTrend.UP, "+1.5°C")
        )
    }
}

