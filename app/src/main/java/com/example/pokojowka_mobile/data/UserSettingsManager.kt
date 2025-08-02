package com.example.pokojowka_mobile.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserSettingsManager(private val context: Context) {

    companion object {
        val USER_FIRST_NAME_KEY = stringPreferencesKey("user_first_name")
        val USER_LAST_NAME_KEY = stringPreferencesKey("user_last_name")
        val SELECTED_HUB_KEY = stringPreferencesKey("selected_hub")
        val CONNECTED_DEVICES_KEY = stringSetPreferencesKey("connected_devices")
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

