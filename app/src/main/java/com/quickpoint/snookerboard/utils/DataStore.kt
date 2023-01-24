package com.quickpoint.snookerboard.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.quickpoint.snookerboard.BuildConfig
import com.quickpoint.snookerboard.domain.objects.DomainPlayer.Player01
import com.quickpoint.snookerboard.domain.objects.DomainPlayer.Player02
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.domain.objects.getMatchStateFromOrdinal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

const val K_PLAYER01_FIRST_NAME = "ds_key_player01_first_name"
const val K_PLAYER01_LAST_NAME = "ds_key_player01_last_name"
const val K_PLAYER02_FIRST_NAME = "ds_key_player02_first_name"
const val K_PLAYER02_LAST_NAME = "ds_key_player02_last_name"

const val K_INT_MATCH_UNIQUE_ID = "ds_key_match_unique_id"
const val K_INT_MATCH_AVAILABLE_FRAMES = "ds_key_match_available_frames"
const val K_INT_MATCH_AVAILABLE_REDS = "ds_key_match_available_reds"
const val K_INT_MATCH_FOUL_MODIFIER = "ds_key_match_foul_modifier"
const val K_INT_MATCH_STARTING_PLAYER = "ds_key_match_starting_player"
const val K_INT_MATCH_HANDICAP_FRAME = "ds_key_match_handicap_frame"
const val K_INT_MATCH_HANDICAP_MATCH = "ds_key_match_handicap_match"

const val K_LONG_MATCH_STATE = "ds_key_match_state"
const val K_LONG_MATCH_CRT_FRAME = "ds_key_match_crt_frame"
const val K_INT_MATCH_CRT_PLAYER = "ds_key_match_crt_player"
const val K_INT_MATCH_AVAILABLE_POINTS = "ds_key_match_available_points"
const val K_INT_MATCH_COUNTER_RETAKE = "ds_key_match_counter_retake"
const val K_INT_MATCH_POINTS_WITHOUT_RETURN = "ds_key_match_points_without_return"

const val K_BOOL_TOGGLE_ADVANCED_RULES = "ds_key_toggle_advanced_rules"
const val K_BOOL_TOGGLE_ADVANCED_STATISTICS = "ds_key_toggle_advanced_statistics"
const val K_BOOL_TOGGLE_ADVANCED_BREAKS = "ds_key_toggle_advanced_breaks"
const val K_BOOL_TOGGLE_FREEBALL = "ds_key_match_toggle_freeball"
const val K_BOOL_TOGGLE_LONG_SHOT = "ds_key_match_toggle_long"
const val K_BOOL_TOGGLE_REST_SHOT = "ds_key_match_toggle_rest"

class DataStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("UserEmail")
    }

    fun savePreferences(key: String, value: Any) = CoroutineScope(Dispatchers.IO).launch {
        context.dataStore.edit { preferences ->
            Timber.i("Saved: $key = $value")
            when (value::class.simpleName) {
                "String" -> preferences[stringPreferencesKey(key)] = value as String
                "Int" -> preferences[intPreferencesKey(key)] = value as Int
                "Long" -> preferences[longPreferencesKey(key)] = value as Long
                "Boolean" -> preferences[booleanPreferencesKey(key)] = value as Boolean
                else -> Timber.e("DataStore saving functionality for class ${value::class.simpleName} not implemented")
            }
        }
    }

    fun loadPreferences() = CoroutineScope(Dispatchers.IO).launch {
        val preferences = context.dataStore.data.first()

        Toggle.AdvancedRules.isEnabled = preferences[booleanPreferencesKey(K_BOOL_TOGGLE_ADVANCED_RULES)] ?: true
        Toggle.AdvancedStatistics.isEnabled = preferences[booleanPreferencesKey(K_BOOL_TOGGLE_ADVANCED_STATISTICS)] ?: true
        Toggle.AdvancedBreaks.isEnabled = preferences[booleanPreferencesKey(K_BOOL_TOGGLE_ADVANCED_BREAKS)] ?: true
        Toggle.FreeBall.isEnabled = preferences[booleanPreferencesKey(K_BOOL_TOGGLE_FREEBALL)] ?: true
        Toggle.LongShot.isEnabled = preferences[booleanPreferencesKey(K_BOOL_TOGGLE_LONG_SHOT)] ?: true
        Toggle.RestShot.isEnabled = preferences[booleanPreferencesKey(K_BOOL_TOGGLE_REST_SHOT)] ?: true

        Player01.loadPreferences(
            firstName = preferences[stringPreferencesKey(K_PLAYER01_FIRST_NAME)] ?: if (BuildConfig.DEBUG_TOGGLE) "Ronnie" else "",
            lastName = preferences[stringPreferencesKey(K_PLAYER01_LAST_NAME)] ?: if (BuildConfig.DEBUG_TOGGLE) "O'Sullivan" else ""
        )
        Player02.loadPreferences(
            firstName = preferences[stringPreferencesKey(K_PLAYER02_FIRST_NAME)] ?: if (BuildConfig.DEBUG_TOGGLE) "John" else "",
            lastName = preferences[stringPreferencesKey(K_PLAYER02_LAST_NAME)] ?: if (BuildConfig.DEBUG_TOGGLE) "Higgins" else ""
        )

        Settings.loadPreferences(
            matchState = getMatchStateFromOrdinal(preferences[intPreferencesKey(K_LONG_MATCH_STATE)] ?: 0),
            uniqueId = preferences[longPreferencesKey(K_INT_MATCH_UNIQUE_ID)] ?: 0,
            availableFrames = preferences[intPreferencesKey(K_INT_MATCH_AVAILABLE_FRAMES)] ?: 0,
            availableReds = preferences[intPreferencesKey(K_INT_MATCH_AVAILABLE_REDS)] ?: 0,
            foulModifier = preferences[intPreferencesKey(K_INT_MATCH_FOUL_MODIFIER)] ?: 0,
            startingPlayer = preferences[intPreferencesKey(K_INT_MATCH_STARTING_PLAYER)] ?: 0,
            handicapFrame = preferences[intPreferencesKey(K_INT_MATCH_HANDICAP_FRAME)] ?: 0,
            handicapMatch = preferences[intPreferencesKey(K_INT_MATCH_HANDICAP_MATCH)] ?: 0,
            crtFrame = preferences[longPreferencesKey(K_LONG_MATCH_CRT_FRAME)] ?: 0,
            crtPlayer = preferences[intPreferencesKey(K_INT_MATCH_CRT_PLAYER)] ?: 0,
            availablePoints = preferences[intPreferencesKey(K_INT_MATCH_AVAILABLE_POINTS)] ?: 0,
            counterRetake = preferences[intPreferencesKey(K_INT_MATCH_COUNTER_RETAKE)] ?: 0,
            pointsWithoutReturn = preferences[intPreferencesKey(K_INT_MATCH_POINTS_WITHOUT_RETURN)] ?: 0
        )
    }
}