package com.quickpoint.snookerboard.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.quickpoint.snookerboard.core.utils.Constants.EMPTY_STRING
import com.quickpoint.snookerboard.domain.models.ShotType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Singleton

const val K_INT_MATCH_UNIQUE_ID = "ds_key_match_unique_id"
const val K_INT_MATCH_AVAILABLE_FRAMES = "ds_key_match_available_frames"
const val K_INT_MATCH_AVAILABLE_REDS = "ds_key_match_available_reds"
const val K_INT_MATCH_FOUL_MODIFIER = "ds_key_match_foul_modifier"
const val K_INT_MATCH_STARTING_PLAYER = "ds_key_match_starting_player"
const val K_INT_MATCH_HANDICAP_FRAME = "ds_key_match_handicap_frame"
const val K_INT_MATCH_HANDICAP_MATCH = "ds_key_match_handicap_match"
val listOfKeysInt = listOf(
    K_INT_MATCH_UNIQUE_ID, K_INT_MATCH_AVAILABLE_FRAMES, K_INT_MATCH_AVAILABLE_REDS,
    K_INT_MATCH_FOUL_MODIFIER, K_INT_MATCH_STARTING_PLAYER, K_INT_MATCH_HANDICAP_FRAME, K_INT_MATCH_HANDICAP_MATCH
)

const val K_LONG_MATCH_STATE = "ds_key_match_state"
const val K_LONG_MATCH_CRT_FRAME = "ds_key_match_crt_frame"
const val K_INT_MATCH_CRT_PLAYER = "ds_key_match_crt_player"
const val K_INT_MATCH_AVAILABLE_POINTS = "ds_key_match_available_points"
const val K_INT_MATCH_COUNTER_RETAKE = "ds_key_match_counter_retake"
const val K_INT_MATCH_POINTS_WITHOUT_RETURN = "ds_key_match_points_without_return"
val listOfKeysLong = listOf(
    K_LONG_MATCH_STATE, K_LONG_MATCH_CRT_FRAME, K_INT_MATCH_CRT_PLAYER,
    K_INT_MATCH_AVAILABLE_POINTS, K_INT_MATCH_COUNTER_RETAKE, K_INT_MATCH_POINTS_WITHOUT_RETURN
)

const val K_BOOL_TOGGLE_ADVANCED_RULES = "ds_key_toggle_advanced_rules"
const val K_BOOL_TOGGLE_ADVANCED_STATISTICS = "ds_key_toggle_advanced_statistics"
const val K_BOOL_TOGGLE_ADVANCED_BREAKS = "ds_key_toggle_advanced_breaks"
const val K_BOOL_TOGGLE_FREEBALL = "ds_key_match_toggle_freeball"
const val K_BOOL_TOGGLE_LONG_SHOT = "ds_key_match_toggle_long"
const val K_BOOL_TOGGLE_REST_SHOT = "ds_key_match_toggle_rest"
val listOfKeysBool = listOf(
    K_BOOL_TOGGLE_ADVANCED_RULES, K_BOOL_TOGGLE_ADVANCED_STATISTICS, K_BOOL_TOGGLE_ADVANCED_BREAKS,
    K_BOOL_TOGGLE_FREEBALL, K_BOOL_TOGGLE_LONG_SHOT, K_BOOL_TOGGLE_REST_SHOT
)

fun getPrefKey(key: String) = when (key) {
    in listOfKeysInt -> intPreferencesKey(key)
    in listOfKeysLong -> longPreferencesKey(key)
    else -> booleanPreferencesKey(key) // boolean
}

@Singleton
class DataStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("UserEmail")
    }

    fun savePrefs(key: String, value: Any) = CoroutineScope(Dispatchers.IO).launch {
        context.dataStore.edit { preferences ->
            when (value::class.simpleName) {
                "String" -> preferences[stringPreferencesKey(key)] = value as String
                "Int" -> preferences[intPreferencesKey(key)] = value as Int
                "Long" -> preferences[longPreferencesKey(key)] = value as Long
                "Boolean" -> preferences[booleanPreferencesKey(key)] = value as Boolean
                else -> Timber.e("DataStore saving functionality for class ${value::class.simpleName} not implemented")
            }
        }
    }

    fun savePrefAndSwitchBoolValue(key: String) = CoroutineScope(Dispatchers.IO).launch {
        val value = context.dataStore.data.first()[booleanPreferencesKey(key)] ?: false
        savePrefs(key, !value)
    }

    fun getStringFlow(key: String) = context.dataStore.data.map { it[getPrefKey(key)] as? String ?: EMPTY_STRING }
    fun getIntFlow(key: String) = context.dataStore.data.map { it[getPrefKey(key)] as? Int ?: 0 }
    fun getLongFlow(key: String) = context.dataStore.data.map { it[getPrefKey(key)] as? Long ?: 0 }
    fun getBoolFlow(key: String) = context.dataStore.data.map { it[getPrefKey(key)] as? Boolean ?: false }

    suspend fun getShotType(): ShotType {
        val preferences = context.dataStore.data.first()
        val longShot = preferences[booleanPreferencesKey(K_BOOL_TOGGLE_LONG_SHOT)] ?: false
        val restShot = preferences[booleanPreferencesKey(K_BOOL_TOGGLE_REST_SHOT)] ?: false
        return when {
            longShot && restShot -> ShotType.LONG_AND_REST
            longShot -> ShotType.LONG
            restShot -> ShotType.REST
            else -> ShotType.STANDARD
        }
    }

    suspend fun getPreferences(): Preferences {
        return withContext(Dispatchers.IO) {
            val preferencesFlow = context.dataStore.data.catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            preferencesFlow.first()
        }
    }

    fun loadPreferences() = CoroutineScope(Dispatchers.IO).launch {
        val preferencesFlow = context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        val preferences = preferencesFlow.first()
    }
}