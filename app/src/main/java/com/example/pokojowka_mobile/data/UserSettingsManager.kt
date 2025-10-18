package com.example.pokojowka_mobile.data

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

// NOWOŚĆ: Klasa do przechowywania customizacji jednej rośliny
@Serializable
data class PlantCustomization(
    val customName: String,
    val iconName: String
)

// NOWOŚĆ: Mapa dostępnych ikon, aby można było je zapisywać i odczytywać po nazwie
object PlantIconMap {
    val icons = mapOf(
        "Eco" to Icons.Default.Eco,
        "Grass" to Icons.Default.Grass,
        "LocalFlorist" to Icons.Default.LocalFlorist,
        "FilterVintage" to Icons.Default.FilterVintage,
        "Yard" to Icons.Default.Yard,
        "Spa" to Icons.Default.Spa,
        "EnergySavingsLeaf" to Icons.Default.EnergySavingsLeaf,
        "Forest" to Icons.Default.Forest
    )

    fun getIconByName(name: String?): ImageVector? {
        return icons[name]
    }
}

class UserSettingsManager(private val context: Context) {

    companion object {
        val USER_FIRST_NAME_KEY = stringPreferencesKey("user_first_name")
        val USER_LAST_NAME_KEY = stringPreferencesKey("user_last_name")
        val SELECTED_HUB_KEY = stringPreferencesKey("selected_hub")
        val CONNECTED_DEVICES_KEY = stringSetPreferencesKey("connected_devices")

        // NOWOŚĆ: Klucz do przechowywania wszystkich customizacji roślin w jednym stringu JSON
        val PLANT_CUSTOMIZATIONS_KEY = stringPreferencesKey("plant_customizations")
    }

    suspend fun saveUserSettings(
        firstName: String,
        lastName: String,
        hub: String,
        devices: List<String>
    ) {
        context.dataStore.edit { settings ->
            settings[USER_FIRST_NAME_KEY] = firstName
            settings[USER_LAST_NAME_KEY] = lastName
            settings[SELECTED_HUB_KEY] = hub
            settings[CONNECTED_DEVICES_KEY] = devices.toSet()
        }
    }

    val userPreferencesFlow: Flow<UserData> = context.dataStore.data
        .map { preferences ->
            val firstName = preferences[USER_FIRST_NAME_KEY] ?: ""
            val lastName = preferences[USER_LAST_NAME_KEY] ?: ""
            val hub = preferences[SELECTED_HUB_KEY] ?: ""
            val devices = preferences[CONNECTED_DEVICES_KEY]?.toList() ?: emptyList()

            UserData(
                userName = firstName,
                lastName = lastName,
                selectedHub = hub,
                connectedDevices = devices
            )
        }

    // NOWOŚĆ: Funkcja do zapisywania zmian dla konkretnej rośliny
    suspend fun savePlantCustomization(plantId: String, customName: String, iconName: String) {
        context.dataStore.edit { settings ->
            // 1. Odczytaj obecny JSON z customizacjami
            val currentJson = settings[PLANT_CUSTOMIZATIONS_KEY]
            val currentCustomizations: MutableMap<String, PlantCustomization> = if (currentJson != null) {
                try {
                    Json.decodeFromString(currentJson)
                } catch (e: Exception) {
                    mutableMapOf()
                }
            } else {
                mutableMapOf()
            }

            // 2. Dodaj lub zaktualizuj dane dla danego plantId
            currentCustomizations[plantId] = PlantCustomization(customName, iconName)

            // 3. Zapisz zaktualizowaną mapę z powrotem jako JSON
            settings[PLANT_CUSTOMIZATIONS_KEY] = Json.encodeToString(currentCustomizations)
        }
    }

    // NOWOŚĆ: Funkcja do odczytywania flow ze wszystkimi customizacjami
    val plantCustomizationsFlow: Flow<Map<String, PlantCustomization>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[PLANT_CUSTOMIZATIONS_KEY]
            if (json != null) {
                try {
                    Json.decodeFromString<Map<String, PlantCustomization>>(json)
                } catch (e: Exception) {
                    // W razie błędu parsowania JSON, zwróć pustą mapę
                    emptyMap()
                }
            } else {
                emptyMap()
            }
        }


    suspend fun addDevice(newDeviceName: String) {
        context.dataStore.edit { settings ->
            val currentDevices = settings[CONNECTED_DEVICES_KEY]?.toMutableSet() ?: mutableSetOf()
            currentDevices.add(newDeviceName)
            settings[CONNECTED_DEVICES_KEY] = currentDevices
        }
    }

    suspend fun clearUserSettings() {
        context.dataStore.edit { settings ->
            settings.clear()
        }
    }
}
