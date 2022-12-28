package com.quickpoint.snookerboard.utils

import com.quickpoint.snookerboard.domain.DomainPot
import com.quickpoint.snookerboard.domain.PotType
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.domain.ShotType
import com.quickpoint.snookerboard.domain.ShotType.*

sealed class MatchToggle (
    var toggleAdvancedRules: Int
){
    object MATCHTOGGLES: MatchToggle(0)

    fun toggleAdvancedRulesOn() = toggleAdvancedRules == 1

    fun switchToggleAdvancedRules(){
        toggleAdvancedRules = 1 - toggleAdvancedRules
    }
}

sealed class FrameToggles(
    var isFreeball: Boolean,
    var isLongShot: Boolean,
    var isRestShot: Boolean
) {
    object FRAMETOGGLES : FrameToggles(false, false, false)

    fun assignToggles(
        isFreeball: Boolean,
        isLongShot: Boolean,
        isRestShot: Boolean
    ) {
        this.isFreeball = isFreeball
        this.isLongShot = isLongShot
        this.isRestShot = isRestShot
    }

    // Freeball
    fun toggleFreeball() {
        isFreeball = !isFreeball
    }

    fun setFreeballInactive() {
        isFreeball = false
    }

    fun handlePotFreeballToggle(pot: DomainPot) { // Control freeball visibility and selection
        when (pot.potType) {
            TYPE_FREE_ACTIVE -> toggleFreeball()
            TYPE_HIT, TYPE_FREE, TYPE_MISS, TYPE_SAFE, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> setFreeballInactive()
            TYPE_ADDRED, TYPE_REMOVE_RED, TYPE_REMOVE_COLOR, TYPE_RESPOT_BLACK, TYPE_FOUL_ATTEMPT -> {}
        }
    }

    fun handleUndoFreeballToggle(potType: PotType, lastPotType: PotType?) {
        when (potType) {
            TYPE_FREE -> toggleFreeball()
            TYPE_FREE_ACTIVE -> setFreeballInactive()
            TYPE_SAFE, TYPE_MISS, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> when (lastPotType) {
                TYPE_FREE_ACTIVE -> toggleFreeball()
                else -> setFreeballInactive()
            }
            TYPE_HIT, TYPE_ADDRED, TYPE_REMOVE_RED, TYPE_REMOVE_COLOR, TYPE_RESPOT_BLACK, TYPE_FOUL_ATTEMPT -> {}
        }
    }

    // Long and rest
    fun toggleLongShot() {
        isLongShot = !isLongShot
    }

    fun toggleRestShot() {
        isRestShot = !isRestShot
    }

    fun resetToggleLongAndRest() {
        isLongShot = false
        isRestShot = false
    }

    fun getShotType(): ShotType {
        val shotType = when {
            isLongShot && isRestShot -> LONG_AND_REST
            isLongShot -> LONG
            isRestShot -> REST
            else -> STANDARD
        }
        return shotType
    }
}