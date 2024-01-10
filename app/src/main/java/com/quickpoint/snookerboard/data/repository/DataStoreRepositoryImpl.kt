package com.quickpoint.snookerboard.data.repository

import com.quickpoint.snookerboard.data.*
import com.quickpoint.snookerboard.domain.models.ShotType
import com.quickpoint.snookerboard.domain.repository.DataStoreRepository
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(private val dataStore: DataStore) : DataStoreRepository {

    override val toggleAdvancedRules = dataStore.getBoolFlow(K_BOOL_TOGGLE_ADVANCED_RULES)
    override val toggleAdvancedStatistics = dataStore.getBoolFlow(K_BOOL_TOGGLE_ADVANCED_STATISTICS)
    override val toggleAdvancedBreaks = dataStore.getBoolFlow(K_BOOL_TOGGLE_ADVANCED_BREAKS)
    override val toggleLongShot = dataStore.getBoolFlow(K_BOOL_TOGGLE_LONG_SHOT)
    override val toggleRestShot = dataStore.getBoolFlow(K_BOOL_TOGGLE_REST_SHOT)
    override val toggleFreeball = dataStore.getBoolFlow(K_BOOL_TOGGLE_FREEBALL)

    override val availableFrames = dataStore.getIntFlow(K_INT_MATCH_AVAILABLE_FRAMES)
    override val availableReds = dataStore.getIntFlow(K_INT_MATCH_AVAILABLE_REDS)
    override val foulModifier = dataStore.getIntFlow(K_INT_MATCH_FOUL_MODIFIER)
    override val startingPlayer = dataStore.getIntFlow(K_INT_MATCH_STARTING_PLAYER)
    override val handicapFrame = dataStore.getIntFlow(K_INT_MATCH_HANDICAP_FRAME)
    override val handicapMatch = dataStore.getIntFlow(K_INT_MATCH_HANDICAP_MATCH)
    override val crtPlayer = dataStore.getIntFlow(K_INT_MATCH_CRT_PLAYER)
    override val maxFramePoints = dataStore.getIntFlow(K_INT_MATCH_AVAILABLE_POINTS)
    override val counterRetake = dataStore.getIntFlow(K_INT_MATCH_COUNTER_RETAKE)
    override val pointsWithoutReturn = dataStore.getIntFlow(K_INT_MATCH_POINTS_WITHOUT_RETURN)

    override val crtFrame = dataStore.getLongFlow(K_LONG_MATCH_CRT_FRAME)

    override fun savePrefs(key: String, value: Any) = dataStore.savePrefs(key, value)
    override fun savePrefAndSwitchBoolValue(key: String) = dataStore.savePrefAndSwitchBoolValue(key)

    override suspend fun getShotType() = dataStore.getShotType()
    override suspend fun getPreferences() = dataStore.getPreferences()
    override suspend fun loadPreferences() = dataStore.loadPreferences()


}