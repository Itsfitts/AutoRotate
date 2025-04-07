package com.eiyooooo.autorotate.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "screen_configs")
private val CONFIGS_KEY = stringPreferencesKey("all_screen_configs")

class ScreenConfigRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    val configs: Flow<List<ScreenConfig>> = context.dataStore.data
        .map { preferences ->
            preferences[CONFIGS_KEY]?.let {
                try {
                    json.decodeFromString<List<ScreenConfig>>(it)
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()
        }

    suspend fun saveConfig(config: ScreenConfig) {
        context.dataStore.edit { preferences ->
            val currentConfigs = preferences[CONFIGS_KEY]?.let {
                try {
                    json.decodeFromString<List<ScreenConfig>>(it)
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()

            val updatedConfigs = currentConfigs.toMutableList()
            val existingIndex = updatedConfigs.indexOfFirst { it.displayAddress == config.displayAddress }

            if (existingIndex >= 0) {
                updatedConfigs[existingIndex] = config
            } else {
                updatedConfigs.add(config)
            }

            preferences[CONFIGS_KEY] = json.encodeToString(updatedConfigs)
        }
    }

    suspend fun deleteConfig(displayAddress: String) {
        context.dataStore.edit { preferences ->
            val currentConfigs = preferences[CONFIGS_KEY]?.let {
                try {
                    json.decodeFromString<List<ScreenConfig>>(it)
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()

            val updatedConfigs = currentConfigs.filter { it.displayAddress != displayAddress }
            preferences[CONFIGS_KEY] = json.encodeToString(updatedConfigs)
        }
    }
}
