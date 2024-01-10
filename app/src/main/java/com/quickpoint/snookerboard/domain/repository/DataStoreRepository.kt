package com.quickpoint.snookerboard.domain.repository

import androidx.datastore.preferences.core.Preferences
import com.quickpoint.snookerboard.domain.models.ShotType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val toggleAdvancedRules: Flow<Boolean>
    val toggleAdvancedStatistics: Flow<Boolean>
    val toggleAdvancedBreaks: Flow<Boolean>
    val toggleLongShot: Flow<Boolean>
    val toggleRestShot: Flow<Boolean>
    val toggleFreeball: Flow<Boolean>

    val availableFrames: Flow<Int>
    val availableReds: Flow<Int>
    val foulModifier: Flow<Int>
    val startingPlayer: Flow<Int>
    val handicapFrame: Flow<Int>
    val handicapMatch: Flow<Int>
    val crtPlayer: Flow<Int>
    val maxFramePoints: Flow<Int>
    val counterRetake: Flow<Int>
    val pointsWithoutReturn: Flow<Int>

    val crtFrame: Flow<Long>

    fun savePrefs(key: String, value: Any): Job
    fun savePrefAndSwitchBoolValue(key: String): Job

    suspend fun getShotType(): ShotType
    suspend fun getPreferences(): Preferences
    suspend fun loadPreferences(): Job
}