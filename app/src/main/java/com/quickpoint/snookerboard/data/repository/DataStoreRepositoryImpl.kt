package com.quickpoint.snookerboard.data.repository

import com.quickpoint.snookerboard.data.*
import com.quickpoint.snookerboard.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(private val dataStore: DataStore) : DataStoreRepository {

    override val toggleAdvancedRules = dataStore.getPrefFlow(K_BOOL_TOGGLE_ADVANCED_RULES) as Flow<Boolean>
    override val toggleAdvancedStatistics = dataStore.getPrefFlow(K_BOOL_TOGGLE_ADVANCED_STATISTICS) as Flow<Boolean>
    override val toggleAdvancedBreaks = dataStore.getPrefFlow(K_BOOL_TOGGLE_ADVANCED_BREAKS) as Flow<Boolean>
    override val toggleLongShot = dataStore.getPrefFlow(K_BOOL_TOGGLE_LONG_SHOT) as Flow<Boolean>
    override val toggleRestShot = dataStore.getPrefFlow(K_BOOL_TOGGLE_REST_SHOT) as Flow<Boolean>
    override val toggleFreeball = dataStore.getPrefFlow(K_BOOL_TOGGLE_FREEBALL) as Flow<Boolean>

    override val availableFrames = dataStore.getPrefFlow(K_INT_MATCH_AVAILABLE_FRAMES) as Flow<Int>
    override val availableReds = dataStore.getPrefFlow(K_INT_MATCH_AVAILABLE_REDS) as Flow<Int>
    override val foulModifier = dataStore.getPrefFlow(K_INT_MATCH_FOUL_MODIFIER) as Flow<Int>
    override val startingPlayer = dataStore.getPrefFlow(K_INT_MATCH_STARTING_PLAYER) as Flow<Int>
    override val handicapFrame = dataStore.getPrefFlow(K_INT_MATCH_HANDICAP_FRAME) as Flow<Int>
    override val handicapMatch = dataStore.getPrefFlow(K_INT_MATCH_HANDICAP_MATCH) as Flow<Int>
    override val crtPlayer = dataStore.getPrefFlow(K_INT_MATCH_CRT_PLAYER) as Flow<Int>
    override val maxFramePoints = dataStore.getPrefFlow(K_INT_MATCH_AVAILABLE_POINTS) as Flow<Int>
    override val counterRetake = dataStore.getPrefFlow(K_INT_MATCH_COUNTER_RETAKE) as Flow<Int>
    override val pointsWithoutReturn = dataStore.getPrefFlow(K_INT_MATCH_POINTS_WITHOUT_RETURN) as Flow<Int>

    override val crtFrame = dataStore.getPrefFlow(K_LONG_MATCH_CRT_FRAME) as Flow<Long>

    override fun savePref(key: String, value: Any) = dataStore.savePreferences(key, value)
    override fun switchBoolAndSavePref(key: String) = dataStore.saveAndSwitchValue(key)

}