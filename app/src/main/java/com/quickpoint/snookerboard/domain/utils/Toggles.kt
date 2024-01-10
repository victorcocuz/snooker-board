package com.quickpoint.snookerboard.domain.utils

import com.quickpoint.snookerboard.data.DataStore
import com.quickpoint.snookerboard.data.K_BOOL_TOGGLE_FREEBALL
import com.quickpoint.snookerboard.domain.models.PotType
import com.quickpoint.snookerboard.domain.models.PotType.*
import javax.inject.Singleton

sealed class Toggle(
    var isEnabled: Boolean
) {
    object FreeBall : Toggle(false)
}

// Helpers
fun handlePotFreeballToggle(potType: PotType, dataStore: DataStore) { // Control freeball visibility and selection
    when (potType) {
        TYPE_FREE_ACTIVE -> dataStore.saveAndSwitchValue(K_BOOL_TOGGLE_FREEBALL)
        TYPE_HIT, TYPE_FREE, TYPE_MISS, TYPE_SAFE, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> dataStore.savePreferences(
            K_BOOL_TOGGLE_FREEBALL, false)
        TYPE_ADDRED, TYPE_REMOVE_RED, TYPE_REMOVE_COLOR, TYPE_LAST_BLACK_FOULED, TYPE_RESPOT_BLACK, TYPE_FOUL_ATTEMPT -> {}
    }
}

fun handleUndoFreeballToggle(potType: PotType, lastPotType: PotType?, dataStore: DataStore) {
    when (potType) {
        TYPE_FREE -> dataStore.saveAndSwitchValue(K_BOOL_TOGGLE_FREEBALL)
        TYPE_FREE_ACTIVE -> dataStore.savePreferences(K_BOOL_TOGGLE_FREEBALL, false)
        TYPE_SAFE, TYPE_MISS, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> when (lastPotType) {
            TYPE_FREE_ACTIVE -> dataStore.saveAndSwitchValue(K_BOOL_TOGGLE_FREEBALL)
            else -> dataStore.savePreferences(K_BOOL_TOGGLE_FREEBALL, false)
        }
        TYPE_HIT, TYPE_ADDRED, TYPE_REMOVE_RED, TYPE_REMOVE_COLOR, TYPE_LAST_BLACK_FOULED, TYPE_RESPOT_BLACK, TYPE_FOUL_ATTEMPT -> {}
    }
}