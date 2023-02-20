package com.quickpoint.snookerboard.domain.objects

import com.quickpoint.snookerboard.domain.DomainPot
import com.quickpoint.snookerboard.domain.PotType
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.domain.ShotType
import com.quickpoint.snookerboard.domain.ShotType.*
import com.quickpoint.snookerboard.domain.objects.Toggle.*
import com.quickpoint.snookerboard.utils.K_BOOL_TOGGLE_ADVANCED_BREAKS
import com.quickpoint.snookerboard.utils.K_BOOL_TOGGLE_ADVANCED_RULES
import com.quickpoint.snookerboard.utils.K_BOOL_TOGGLE_ADVANCED_STATISTICS


sealed class Toggle(
    var isEnabled: Boolean,
) {
    object AdvancedRules : Toggle(true)
    object AdvancedStatistics : Toggle(true)
    object AdvancedBreaks : Toggle(true)
    object FreeBall : Toggle(false)
    object LongShot : Toggle(false)
    object RestShot : Toggle(false)

    fun toggleEnabled(): Boolean {
        isEnabled = !isEnabled
        return isEnabled
    }

    fun setDisabled(): Boolean {
        isEnabled = false
        return false
    }
}

fun getToggleByKey(key: String): Toggle? = when (key) {
    K_BOOL_TOGGLE_ADVANCED_RULES -> AdvancedRules
    K_BOOL_TOGGLE_ADVANCED_STATISTICS -> AdvancedStatistics
    K_BOOL_TOGGLE_ADVANCED_BREAKS -> AdvancedBreaks
    else -> null
}

// Helpers
fun FreeBall.handlePotFreeballToggle(pot: DomainPot) { // Control freeball visibility and selection
    when (pot.potType) {
        TYPE_FREE_ACTIVE -> toggleEnabled()
        TYPE_HIT, TYPE_FREE, TYPE_MISS, TYPE_SAFE, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> isEnabled = false
        TYPE_ADDRED, TYPE_REMOVE_RED, TYPE_REMOVE_COLOR, TYPE_LAST_BLACK_FOULED, TYPE_RESPOT_BLACK, TYPE_FOUL_ATTEMPT -> {}
    }
}

fun FreeBall.handleUndoFreeballToggle(potType: PotType, lastPotType: PotType?) {
    when (potType) {
        TYPE_FREE -> toggleEnabled()
        TYPE_FREE_ACTIVE -> isEnabled = false
        TYPE_SAFE, TYPE_MISS, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> when (lastPotType) {
            TYPE_FREE_ACTIVE -> toggleEnabled()
            else -> isEnabled = false
        }
        TYPE_HIT, TYPE_ADDRED, TYPE_REMOVE_RED, TYPE_REMOVE_COLOR, TYPE_LAST_BLACK_FOULED, TYPE_RESPOT_BLACK, TYPE_FOUL_ATTEMPT -> {}
    }
}

fun resetToggleLongAndRest() {
    LongShot.isEnabled = false
    RestShot.isEnabled = false
}

fun getShotType(): ShotType = when {
        LongShot.isEnabled && RestShot.isEnabled -> LONG_AND_REST
        LongShot.isEnabled -> LONG
        RestShot.isEnabled -> REST
        else -> STANDARD
}