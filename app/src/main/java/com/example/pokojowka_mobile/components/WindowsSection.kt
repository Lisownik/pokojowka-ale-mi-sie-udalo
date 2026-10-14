package com.example.pokojowka_mobile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokojowka_mobile.data.GlobalWindowsList
import com.example.pokojowka_mobile.data.WindowData
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme

@Composable
fun WindowsSection(
    modifier: Modifier = Modifier,
    windows: List<WindowData>,
    onWindowClick: (windowId: String) -> Unit,
    onWindowToggle: (windowId: String, isOpen: Boolean) -> Unit,
    title: String = "Twoje Smart Okna"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
            )
        }

        if (windows.isEmpty()) {
            Text(
                text = "Nie masz jeszcze inteligentych okien",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            )
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                windows.forEach { windowData ->
                    WindowItem(
                        windowData = windowData,
                        onWindowClick = { onWindowClick(windowData.id) },
                        onWindowToggle = onWindowToggle
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun WindowsSectionPreview() {
    PokojowkamobileTheme {
        WindowsSection(
            windows = GlobalWindowsList,
            onWindowClick = {},
            onWindowToggle = { _, _ -> }
        )
    }
}
