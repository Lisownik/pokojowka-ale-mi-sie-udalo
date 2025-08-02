package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import com.example.pokojowka_mobile.ui.theme.tlo


val SafeColor = Color(0xFF4CAF50)
val DangerColor = Color(0xFFF44336)

@Composable
fun ThreatItem(
    icon: ImageVector,
    label: String,
    isDetected: Boolean,
    modifier: Modifier = Modifier
) {
    val statusColor = if (isDetected) DangerColor else SafeColor
    val statusText = if (isDetected) "Wykryto" else "Brak"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = statusColor
        )
    }
}

@Composable
fun ThreatsTile(
    coDetected: Boolean,
    otherGasesDetected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),

        colors = CardDefaults.cardColors(containerColor = tlo),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp

        )

    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Zagrożenia",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = "Zagrożenia",
                    modifier = Modifier.size(34.dp),
                    tint = contentColor.copy(alpha = 0.65f)
                )


            }

            Divider(modifier = Modifier.padding(horizontal = 16.dp))


            ThreatItem(
                icon = Icons.Filled.LocalFireDepartment,
                label = "Gaz CO",
                isDetected = coDetected
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))


            ThreatItem(
                icon = Icons.Filled.Science,
                label = "Inne Gazy",
                isDetected = otherGasesDetected
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThreatsTilePreview_NoThreats() {
    PokojowkamobileTheme {
        Surface {
            ThreatsTile(coDetected = false, otherGasesDetected = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThreatsTilePreview_CODetected() {
    PokojowkamobileTheme {
        Surface {
            ThreatsTile(coDetected = true, otherGasesDetected = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThreatsTilePreview_OtherGasesDetected() {
    PokojowkamobileTheme {
        Surface {
            ThreatsTile(coDetected = false, otherGasesDetected = true)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThreatsTilePreview_AllThreatsDetected() {
    PokojowkamobileTheme {
        Surface {
            ThreatsTile(coDetected = true, otherGasesDetected = true)
        }
    }
}
