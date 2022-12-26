package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.domain.PotType.*

sealed class DomainFreeBallInfo(
    var isActive: Boolean
) {
    object FREEBALLINFO : DomainFreeBallInfo(false)

    fun assignFreeballInfo(
        isActive: Boolean,
    ) {
        this.isActive = isActive
    }

    fun toggleActive() {
        isActive = !isActive
    }

    fun setInactive() {
        isActive = false
    }

    fun handlePotFreeballInfo(pot: DomainPot) { // Control freeball visibility and selection
        when (pot.potType) {
            TYPE_FREE_ACTIVE -> toggleActive()
            TYPE_HIT, TYPE_FREE, TYPE_MISS, TYPE_SAFE, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> setInactive()
            TYPE_ADDRED, TYPE_REMOVE_RED, TYPE_REMOVE_COLOR, TYPE_RESPOT_BLACK, TYPE_FOUL_ATTEMPT -> {}
        }
    }

    fun handleUndoFreeballInfo(potType: PotType, lastPotType: PotType?) {
        when (potType) {
            TYPE_FREE -> toggleActive()
            TYPE_FREE_ACTIVE -> setInactive()
            TYPE_SAFE, TYPE_MISS, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> when (lastPotType) {
                TYPE_FREE_ACTIVE -> toggleActive()
                else -> setInactive()
            }
            TYPE_HIT, TYPE_ADDRED, TYPE_REMOVE_RED, TYPE_REMOVE_COLOR, TYPE_RESPOT_BLACK, TYPE_FOUL_ATTEMPT -> {}
        }
    }
}