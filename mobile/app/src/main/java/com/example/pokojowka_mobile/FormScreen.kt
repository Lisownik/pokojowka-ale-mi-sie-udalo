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

                // Usunięto cały komponent ExposedDropdownMenuBox

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        // Sprawdzamy tylko imię i nazwisko
                        if (firstName.isNotBlank() && lastName.isNotBlank()) {
                            val userData = UserData(
                                userName = firstName,
                                lastName = lastName,
                                connectedDevices = emptyList()
                            )
                            coroutineScope.launch {
                                userSettingsManager.saveUserSettings(
                                    firstName = userData.userName,
                                    lastName = userData.lastName,
                                    devices = userData.connectedDevices
                                )
                                println("Dane zapisane przez DeviceForm: $userData")
                                onFormSubmit(userData)
                            }
                        } else {
                            println("Błąd: Imię i nazwisko muszą być wypełnione.")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    // Przycisk jest aktywny, gdy imię i nazwisko nie są puste
                    enabled = firstName.isNotBlank() && lastName.isNotBlank(),
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
