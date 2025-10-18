package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.tlo

@Composable
fun PlantViewItem(
    modifier: Modifier = Modifier,
    itemName: String,
    itemValue: String,
    itemIcon: ImageVector,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = tlo),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
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
fun PlantViewItemSyncedPreview() {
    PokojowkamobileTheme {
        PlantViewItem(
            itemName = "Poziom światła",
            itemValue = "750 lux",
            itemIcon = Icons.Default.WbSunny
        )
    }
}
