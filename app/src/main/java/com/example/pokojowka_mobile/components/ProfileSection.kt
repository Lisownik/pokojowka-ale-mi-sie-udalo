package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme


@Composable
fun ProfileSection(
    modifier: Modifier = Modifier,
    title: String,
    contentPaddingForTitle: PaddingValues = PaddingValues(bottom = 12.dp, top = 8.dp),
    spacingBetweenItems: Dp = 4.dp,
    sectionContent: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(contentPaddingForTitle)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(spacingBetweenItems)) {
            this.sectionContent()
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ProfileSectionPreview() {
    PokojowkamobileTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileSection(title = "Przykładowa Sekcja Danych") {
                ProfileInfoItem(label = "Item 1", value = "Wartość 1", icon = Icons.Default.Info)
                ProfileInfoItem(label = "Item 2", value = "Wartość 2", icon = Icons.Default.Info)
            }
            ProfileSection(title = "Inna Sekcja") {
                Text("Prosty tekst w sekcji.")
            }
        }
    }
}
