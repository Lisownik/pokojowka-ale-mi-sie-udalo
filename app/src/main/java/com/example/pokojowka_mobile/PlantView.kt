package com.example.pokojowka_mobile

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

import com.example.pokojowka_mobile.data.PlantData
import com.example.pokojowka_mobile.data.samplePlantsGlobal

import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlantView(
    navController: NavHostController,
    plantId: String,
    modifier: Modifier = Modifier
) {
    val plantDataState = remember { mutableStateOf<PlantData?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(plantId) {
        Log.d("PlantView", "Ładowanie danych dla rośliny o ID: $plantId")
        val foundPlant = samplePlantsGlobal.find { it.id == plantId }
        if (foundPlant == null) {
            Log.w("PlantView", "Nie znaleziono rośliny o ID: $plantId w samplePlantsGlobal")
        }
        plantDataState.value = foundPlant
    }

    val currentPlant = plantDataState.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = currentPlant?.name ?: "Ładowanie rośliny...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wróć"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        if (currentPlant != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = currentPlant.icon,
                            contentDescription = currentPlant.name,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = currentPlant.name,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Text(
                            text = currentPlant.species,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        "Status: ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = currentPlant.healthStatus.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(currentPlant.statusColor, CircleShape)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))


                Text("Informacje o pielęgnacji", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                PlantDetailItem("Lokalizacja:", currentPlant.roomLocation.toString())
                PlantDetailItem("Poziom światła:", currentPlant.lightLevel)
                PlantDetailItem("Wilgotność gleby:", currentPlant.soilMoisture)
                PlantDetailItem("Poziom nawozu:", currentPlant.fertilizerLevel)

                Spacer(modifier = Modifier.height(16.dp))
                Text("ID Rośliny (deweloperskie): $plantId", style = MaterialTheme.typography.labelSmall)


            }
        } else {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (plantDataState.value == null && plantId.isNotEmpty()) {
                        Text(text = "Ładowanie danych rośliny o ID: $plantId...")
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                    } else if (plantId.isEmpty()){
                        Text(text = "Nieprawidłowe ID rośliny.", style = MaterialTheme.typography.titleMedium)
                    }

                }
            }
        }
    }
}

@Composable
private fun PlantDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
fun PlantViewPreview() {
    PokojowkamobileTheme {
        val navController = rememberNavController()
        PlantView(navController = navController, plantId = "p1")
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
fun PlantViewNotFoundPreview() {
    PokojowkamobileTheme {
        val navController = rememberNavController()
        PlantView(navController = navController, plantId = "nie_istnieje_to_id")
    }
}

