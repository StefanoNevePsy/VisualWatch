package com.visualwatch.timer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timer_presets")

class PresetRepository(private val context: Context) {

    private val presetsKey = stringPreferencesKey("presets")

    val presets: Flow<List<TimerPreset>> = context.dataStore.data.map { prefs ->
        val json = prefs[presetsKey] ?: return@map defaultPresets()
        parsePresets(json)
    }

    suspend fun savePresets(presets: List<TimerPreset>) {
        context.dataStore.edit { prefs ->
            prefs[presetsKey] = serializePresets(presets)
        }
    }

    suspend fun addPreset(preset: TimerPreset) {
        context.dataStore.edit { prefs ->
            val current = prefs[presetsKey]?.let { parsePresets(it) } ?: defaultPresets()
            prefs[presetsKey] = serializePresets(current + preset)
        }
    }

    suspend fun updatePreset(preset: TimerPreset) {
        context.dataStore.edit { prefs ->
            val current = prefs[presetsKey]?.let { parsePresets(it) } ?: defaultPresets()
            val updated = current.map { if (it.id == preset.id) preset else it }
            prefs[presetsKey] = serializePresets(updated)
        }
    }

    suspend fun deletePreset(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[presetsKey]?.let { parsePresets(it) } ?: defaultPresets()
            prefs[presetsKey] = serializePresets(current.filter { it.id != id })
        }
    }

    private fun defaultPresets(): List<TimerPreset> = listOf(
        TimerPreset(
            id = "default_50",
            name = "Seduta 50min",
            totalSeconds = 50 * 60L,
            finalSectorSeconds = 10 * 60L,
            animationType = AnimationType.CIRCLE_SWEEP
        ),
        TimerPreset(
            id = "default_60",
            name = "Seduta 60min",
            totalSeconds = 60 * 60L,
            finalSectorSeconds = 15 * 60L,
            animationType = AnimationType.CIRCLE_SWEEP
        ),
        TimerPreset(
            id = "default_45",
            name = "Seduta 45min",
            totalSeconds = 45 * 60L,
            finalSectorSeconds = 10 * 60L,
            animationType = AnimationType.LIQUID_FILL
        ),
        TimerPreset(
            id = "default_30",
            name = "Seduta 30min",
            totalSeconds = 30 * 60L,
            finalSectorSeconds = 5 * 60L,
            animationType = AnimationType.CIRCLE_SWEEP
        )
    )

    private fun serializePresets(presets: List<TimerPreset>): String {
        val array = JSONArray()
        presets.forEach { preset ->
            array.put(JSONObject().apply {
                put("id", preset.id)
                put("name", preset.name)
                put("totalSeconds", preset.totalSeconds)
                put("finalSectorSeconds", preset.finalSectorSeconds)
                put("animationType", preset.animationType.name)
            })
        }
        return array.toString()
    }

    private fun parsePresets(json: String): List<TimerPreset> {
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                TimerPreset(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    totalSeconds = obj.getLong("totalSeconds"),
                    finalSectorSeconds = obj.optLong("finalSectorSeconds", 0L),
                    animationType = try {
                        AnimationType.valueOf(obj.optString("animationType", "CIRCLE_SWEEP"))
                    } catch (_: Exception) {
                        AnimationType.CIRCLE_SWEEP
                    }
                )
            }
        } catch (_: Exception) {
            defaultPresets()
        }
    }
}
