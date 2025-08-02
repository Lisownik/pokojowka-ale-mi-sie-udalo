package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    enabled: Boolean = true,
    defaultContentColor: Color = MaterialTheme.colorScheme.onSurface,
    disabledContentAlpha: Float = 0.38f
) {
    val currentContentColor = if (enabled) defaultContentColor else defaultContentColor.copy(alpha = disabledContentAlpha)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(28.dp),
                    tint = currentContentColor
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = currentContentColor,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value.ifBlank { "Nie podano" },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = currentContentColor,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileInfoItemPreview() {
    PokojowkamobileTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ProfileInfoItem(
                label = "Aktywna Etykieta",
                value = "Aktywna Wartość",
                icon = Icons.Filled.Info
            )
            ProfileInfoItem(
                label = "Dłuższa Etykieta Profilu Użytkownika",
                value = "Krótka Wartość",
                icon = Icons.Filled.Info
            )
            ProfileInfoItem(
                label = "Etykieta z Pustą Wartością",
                value = "",
                icon = Icons.Filled.Info
            )
            ProfileInfoItem(
                label = "Nieaktywna Etykieta",
                value = "Nieaktywna Wartość",
                icon = Icons.Filled.Info,
                enabled = false
            )
        }
    }
}
