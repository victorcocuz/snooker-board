package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.domain.PotType.*

sealed class DomainFreeBallInfo(
    var isVisible: Boolean,
    var isSelected: Boolean,
) {
    object FREEBALLINFO : DomainFreeBallInfo(false, false)

    fun assignFreeballInfo(
        isVisible: Boolean,
        isSelected: Boolean,
    ) {
        this.isVisible = isVisible
        this.isSelected = isSelected

    }

    private fun setInvisible() {
        isVisible = false
    }

    private fun toggleVisible() {
        isVisible = !isVisible
    }

    private fun setUnselected() {
        isSelected = false
    }

    private fun toggleSelected() {
        isSelected = !isSelected
    }

    fun resetFreeball() {
        setInvisible()
        setUnselected()
    }

    fun handlePotFreeballInfo(pot: DomainPot) { // Control freeball visibility and selection
        when (pot.potType) {
            TYPE_FREE_AVAILABLE -> toggleVisible()
            TYPE_FREE_TOGGLE -> toggleSelected()
            TYPE_HIT, TYPE_FREE, TYPE_MISS, TYPE_SAFE, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> resetFreeball()
            TYPE_ADDRED, TYPE_REMOVE_RED, TYPE_REMOVE_COLOR, TYPE_RESPOT_BLACK, TYPE_FOUL_ATTEMPT -> {}
        }
    }

    fun handleUndoFreeballInfo(potType: PotType, lastPotType: PotType?) {
        when (potType) {
            TYPE_FREE -> {
                toggleVisible()
                toggleSelected()
            }
            TYPE_SAFE, TYPE_MISS, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> when (lastPotType) {
                TYPE_FREE_AVAILABLE -> toggleVisible()
                TYPE_FREE_TOGGLE -> {
                    toggleVisible()
                    toggleSelected()
                }
                else -> setInvisible()
            }
            TYPE_FREE_TOGGLE -> toggleSelected()
            TYPE_FREE_AVAILABLE -> resetFreeball()
            TYPE_HIT, TYPE_ADDRED, TYPE_REMOVE_RED, TYPE_REMOVE_COLOR, TYPE_RESPOT_BLACK, TYPE_FOUL_ATTEMPT -> {}
        }
    }
}