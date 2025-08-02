package com.example.pokojowka_mobile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pokojowka_mobile.data.UserData
import com.example.pokojowka_mobile.data.UserSettingsManager
import com.example.pokojowka_mobile.ui.theme.PokojowkamobileTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceForm(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onFormSubmit: (UserData) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    var selectedHubState by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val hubs = listOf("Centrala 1", "Centrala 2", "Pokojówka", "Lodóweczka")

    val context = LocalContext.current

    val userSettingsManager = remember { UserSettingsManager(context) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Rozpocznij korzystanie z aplikacji") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Imię") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Nazwisko") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )


                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedHubState ?: "Wybierz centralę",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Centrala") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        hubs.forEach { hub ->
                            DropdownMenuItem(
                                text = { Text(hub) },
                                onClick = {
                                    selectedHubState = hub
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val finalSelectedHub = selectedHubState ?: ""

                        if (firstName.isNotBlank() && lastName.isNotBlank() && finalSelectedHub.isNotBlank()) {
                            val initialConnectedDevices = emptyList<String>()

                            val userData = UserData(
                                userName = firstName,
                                lastName = lastName,
                                selectedHub = finalSelectedHub,
                                connectedDevices = initialConnectedDevices
                            )
                            coroutineScope.launch {
                                userSettingsManager.saveUserSettings(
                                    firstName = userData.userName,
                                    lastName = userData.lastName,
                                    hub = userData.selectedHub,
                                    devices = userData.connectedDevices
                                )
                                println("Dane (z pustą listą urządzeń) zapisane przez DeviceForm: $userData")

                                onFormSubmit(userData)
                            }
                        } else {

                            println("Błąd: Wszystkie pola muszą być wypełnione.")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = firstName.isNotBlank() && lastName.isNotBlank() && selectedHubState != null,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Zatwierdź", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun DeviceFormPreview() {
    PokojowkamobileTheme {
        DeviceForm(
            navController = rememberNavController(),
            onFormSubmit = { userData -> println("Preview Submit: $userData") }
        )
    }
}
