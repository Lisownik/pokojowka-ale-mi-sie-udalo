
package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.background
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
import com.example.pokojowka_mobile.ui.theme.*

@Composable
fun BulbItem(
    modifier: Modifier = Modifier,
    roomName: String,
    brightness: String,
    isSwitchedOn: Boolean,
    iconBackgroundColor: Color,
    onSwitchToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = LightBulbItemBackground
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconBackgroundColor),
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
                        text = roomName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = brightness,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextGray,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            Switch(
                checked = isSwitchedOn,
                onCheckedChange = onSwitchToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun BulbItemActivePreview() {
    PokojowkamobileTheme {
        var isChecked by remember { mutableStateOf(true) }
        BulbItem(
            roomName = "Salon",
            brightness = "65% jasności",
            isSwitchedOn = isChecked,
            iconBackgroundColor = BulbActiveColor,
            onSwitchToggle = { isChecked = it }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun BulbItemInactivePreview() {
    PokojowkamobileTheme {
        var isChecked by remember { mutableStateOf(false) }
        BulbItem(
            roomName = "Sypialnia",
            brightness = "Wyłączona",
            isSwitchedOn = isChecked,
            iconBackgroundColor = BulbInactiveColor,
            onSwitchToggle = { isChecked = it }
        )
    }
}
