package com.example.pokojowka_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokojowka_mobile.ui.theme.BottomRoundedArcShape
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.pokojowka_mobile.ui.theme.purpleleft
import com.example.pokojowka_mobile.ui.theme.purpleright

import com.example.pokojowka_mobile.data.EnvironmentData
import com.example.pokojowka_mobile.data.UserData
import com.example.pokojowka_mobile.data.SampleUserData

@Composable
fun SharedHeader(
    modifier: Modifier = Modifier,
    userData: UserData,
    environmentData: EnvironmentData,
    onNotificationClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(BottomRoundedArcShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        purpleleft,
                        purpleright
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Witaj, ${userData.userName}!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Miłego dnia, twój inteligenty dom!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Powiadomienia",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            onNotificationClick()
                        }
                        .padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Temperatura",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = environmentData.temperature,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = Color.White
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Wilgotność",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = environmentData.humidity,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SharedHeaderPreview() {
    PokojowkamobileTheme {
        SharedHeader(
            userData = SampleUserData.defaultUser.copy(userName = "Podgląd Użytkownika"),
            environmentData = SampleUserData.defaultEnvironment.copy(temperature = "24°C", humidity = "60%"),
            onNotificationClick = { println("Preview: Kliknięto powiadomienia") }
        )
    }
}