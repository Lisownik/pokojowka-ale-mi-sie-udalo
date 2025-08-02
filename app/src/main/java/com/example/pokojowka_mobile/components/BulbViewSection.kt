package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme


@Composable
fun BulbViewSection(
    modifier: Modifier = Modifier,
    title: String,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    sectionContent: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
            )
        }
        this.sectionContent()
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun BulbViewSectionPreview_WithItems() {
    PokojowkamobileTheme {
        Column {
            BulbViewSection(title = "Ustawienia Ogólne") {
                BulbViewItemInfoPreview()
                BulbViewItemSwitchPreview()
            }
            BulbViewSection(title = "Parametry Światła") {
                BulbViewItemBrightnessSliderPreview()
                BulbViewItemKelvinSliderPreview()
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun BulbViewSectionPreview_EmptyContent() {
    PokojowkamobileTheme {
        BulbViewSection(title = "Pusta Sekcja") {

        }
    }
}

