package com.quickpoint.snookerboard.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.quickpoint.snookerboard.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

const val USER_PLAYER01_FIRST_NAME_KEY = "ds_key_player01_first_name"
const val USER_PLAYER01_LAST_NAME_KEY = "ds_key_player01_last_name"
const val USER_PLAYER02_FIRST_NAME_KEY = "ds_key_player02_first_name"
const val USER_PLAYER02_LAST_NAME_KEY = "ds_key_player02_last_name"

const val KEY_INT_MATCH_UNIQUE_ID = "ds_key_match_unique_id"
const val KEY_INT_MATCH_AVAILABLE_FRAMES = "ds_key_match_available_frames"
const val KEY_INT_MATCH_AVAILABLE_REDS = "ds_key_match_available_reds"
const val KEY_INT_MATCH_FOUL_MODIFIER = "ds_key_match_foul_modifier"
const val KEY_INT_MATCH_STARTING_PLAYER = "ds_key_match_starting_player"
const val KEY_INT_MATCH_HANDICAP_FRAME = "ds_key_match_handicap_frame"
const val KEY_INT_MATCH_HANDICAP_MATCH = "ds_key_match_handicap_match"

const val KEY_LONG_MATCH_STATE = "ds_key_match_state"
const val KEY_LONG_MATCH_CRT_FRAME = "ds_key_match_crt_frame"
const val KEY_INT_MATCH_CRT_PLAYER = "ds_key_match_crt_player"
const val KEY_INT_MATCH_AVAILABLE_POINTS = "ds_key_match_available_points"
const val KEY_INT_MATCH_COUNTER_RETAKE = "ds_key_match_counter_retake"
const val KEY_INT_MATCH_POINTS_WITHOUT_RETURN  = "ds_key_match_points_without_return"

const val KEY_INT_MATCH_TOGGLE_FREEBALL = "ds_key_match_toggle_freeball"
const val KEY_INT_MATCH_TOGGLE_LONG = "ds_key_match_toggle_long"
const val KEY_INT_MATCH_TOGGLE_REST = "ds_key_match_toggle_rest"

const val KEY_INT_MATCH_TOGGLE_ADVANCED_RULES = "ds_key_match_toggle_advanced_rules"
const val KEY_INT_MATCH_TOGGLE_ADVANCED_STATISTICS = "ds_key_match_toggle_advanced_statistics"
const val KEY_INT_MATCH_TOGGLE_ADVANCE_BREAKS = "ds_key_match_toggle_advanced_breaks"

class DataStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("UserEmail")
    }

    fun getNameByKey(key: String): Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey(key)]
        }

    suspend fun readAllNames(): Map<String, String> {
        val preferences = context.dataStore.data.first()
        return mapOf(
            USER_PLAYER01_FIRST_NAME_KEY to (preferences[stringPreferencesKey(USER_PLAYER01_FIRST_NAME_KEY)] ?: if (BuildConfig.DEBUG_TOGGLE) "Ronnie" else ""),
            USER_PLAYER01_LAST_NAME_KEY to (preferences[stringPreferencesKey(USER_PLAYER01_LAST_NAME_KEY)] ?: if (BuildConfig.DEBUG_TOGGLE) "O'Sullivan" else ""),
            USER_PLAYER02_FIRST_NAME_KEY to (preferences[stringPreferencesKey(USER_PLAYER02_FIRST_NAME_KEY)] ?: if (BuildConfig.DEBUG_TOGGLE) "John" else ""),
            USER_PLAYER02_LAST_NAME_KEY to (preferences[stringPreferencesKey(USER_PLAYER02_LAST_NAME_KEY)] ?: if (BuildConfig.DEBUG_TOGGLE) "Higgins" else ""),
        )
    }

    suspend fun readAllRules(): Map<String, Any> {
        val preferences = context.dataStore.data.first()
        return mapOf(
            KEY_INT_MATCH_AVAILABLE_FRAMES to (preferences[intPreferencesKey(KEY_INT_MATCH_AVAILABLE_FRAMES)] ?: 0),
            KEY_INT_MATCH_AVAILABLE_REDS to (preferences[intPreferencesKey(KEY_INT_MATCH_AVAILABLE_REDS)] ?: 0),
            KEY_INT_MATCH_FOUL_MODIFIER to (preferences[intPreferencesKey(KEY_INT_MATCH_FOUL_MODIFIER)] ?: 0),
            KEY_INT_MATCH_STARTING_PLAYER to (preferences[intPreferencesKey(KEY_INT_MATCH_STARTING_PLAYER)] ?: 0),
            KEY_INT_MATCH_HANDICAP_FRAME to (preferences[intPreferencesKey(KEY_INT_MATCH_HANDICAP_FRAME)] ?: 0),
            KEY_INT_MATCH_HANDICAP_MATCH to (preferences[intPreferencesKey(KEY_INT_MATCH_HANDICAP_MATCH)] ?: 0),
        )
    }

    suspend fun readString(key: String): String {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[dataStoreKey] ?: ""
    }

    fun save(settings: Map<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        context.dataStore.edit { preferences ->
            settings.forEach {
                Timber.e("class ${it.value::class.simpleName}")
                when(it.value::class.simpleName) {
                    "String" -> preferences[stringPreferencesKey(it.key)] = it.value as String
                    "Int" -> preferences[intPreferencesKey(it.key)] = it.value as Int
                    "Long" -> preferences[longPreferencesKey(it.key)] = it.value as Long
                    "Boolean" -> preferences[booleanPreferencesKey(it.key)] = it.value as Boolean
                    else -> Timber.e("DataStore saving functionality for class ${it.value::class.simpleName} not implemented")
                }
            }
        }
    }
}