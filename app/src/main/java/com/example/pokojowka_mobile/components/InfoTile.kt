package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home

@Composable
fun InfoTile(
    modifier: Modifier = Modifier,
    gradientColors: List<Color>,
    icon: ImageVector,
    iconContentDescription: String,
    chipText: String,
    title: String,
    subtitle: String,
    onTileClick: () -> Unit = {}
) {
    var isClickEnabled by remember { mutableStateOf(true) }
    val clickCooldownMillis = 150L
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(colors = gradientColors)
            )
            .clickable(
                enabled = isClickEnabled,
                onClick = {
                    if (isClickEnabled) {
                        isClickEnabled = false
                        onTileClick()
                        scope.launch {
                            delay(clickCooldownMillis)
                            isClickEnabled = true
                        }
                    }
                }
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )

                androidx.compose.material3.Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.20f),
                ) {
                    Text(
                        text = chipText,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}


@Preview(showBackground = false, widthDp = 180, heightDp = 180)
@Composable
fun InfoTilePreviewRooms() {
    PokojowkamobileTheme {
        InfoTile(
            gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB)),
            icon = Icons.Filled.Home,
            iconContentDescription = "Pokoje",
            chipText = "3 pokoje",
            title = "Pokojówki",
            subtitle = "Parametry w normie",
            onTileClick = { println("Rooms tile clicked!") }
        )
    }
}

@Preview(showBackground = false, widthDp = 180, heightDp = 180)
@Composable
fun InfoTilePreviewPlants() {
    PokojowkamobileTheme {
        val plantIcon = Icons.Filled.Home
        InfoTile(
            gradientColors = listOf(Color(0xFF22C55E), Color(0xFF16A34A)),
            icon = plantIcon,
            iconContentDescription = "Rośliny",
            chipText = "5 roślin",
            title = "Rośliny",
            subtitle = "Wszystkie zdrowe",
            onTileClick = { println("Plants tile clicked!") }
        )
    }
}
