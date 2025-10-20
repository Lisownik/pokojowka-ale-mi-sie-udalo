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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokojowka_mobile.network.AuthViewModel
import com.example.pokojowka_mobile.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")


@Serializable
data class PlantCustomization(
    val customName: String,
    val iconName: String
)


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
        val CONNECTED_DEVICES_KEY = stringSetPreferencesKey("connected_devices")
        val PLANT_CUSTOMIZATIONS_KEY = stringPreferencesKey("plant_customizations")
    }

    suspend fun saveUserSettings(
        firstName: String,
        lastName: String,
        devices: List<String>
    ) {
        context.dataStore.edit { settings ->
            settings[USER_FIRST_NAME_KEY] = firstName
            settings[USER_LAST_NAME_KEY] = lastName
            settings[CONNECTED_DEVICES_KEY] = devices.toSet()
        }
    }

    val userPreferencesFlow: Flow<UserData> = context.dataStore.data
        .map { preferences ->
            val firstName = preferences[USER_FIRST_NAME_KEY] ?: ""
            val lastName = preferences[USER_LAST_NAME_KEY] ?: ""
            val devices = preferences[CONNECTED_DEVICES_KEY]?.toList() ?: emptyList()

            UserData(
                userName = firstName,
                lastName = lastName,
                connectedDevices = devices
            )
        }

    suspend fun savePlantCustomization(plantId: String, customName: String, iconName: String) {
        RetrofitClient.apiService.changePotName(plantId, customName)

        context.dataStore.edit { settings ->

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

            currentCustomizations[plantId] = PlantCustomization(customName, iconName)

            settings[PLANT_CUSTOMIZATIONS_KEY] = Json.encodeToString(currentCustomizations)
        }
    }


    val plantCustomizationsFlow: Flow<Map<String, PlantCustomization>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[PLANT_CUSTOMIZATIONS_KEY]
            if (json != null) {
                try {
                    Json.decodeFromString<Map<String, PlantCustomization>>(json)
                } catch (e: Exception) {
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
