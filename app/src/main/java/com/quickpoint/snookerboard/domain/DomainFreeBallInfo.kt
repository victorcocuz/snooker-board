package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.domain.PotType.*

sealed class DomainFreeBallInfo(
    var isVisible: Boolean,
    var isSelected: Boolean
) {
    object FREEBALLINFO : DomainFreeBallInfo(false, false)

    fun assignFreeballInfo(
        isVisible: Boolean,
        isSelected: Boolean
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
            TYPE_FREEAVAILABLE -> toggleVisible()
            TYPE_FREETOGGLE -> toggleSelected()
            TYPE_HIT, TYPE_FREE, TYPE_MISS, TYPE_SAFE, TYPE_FOUL -> resetFreeball()
            else -> {}
        }
    }

    fun handleUndoFreeballInfo(pot: DomainPot, frameStack: MutableList<DomainBreak>) {
        when (pot.potType) {
            TYPE_FREE -> {
                toggleVisible()
                toggleSelected()
            }
            TYPE_SAFE, TYPE_MISS, TYPE_FOUL -> frameStack.apply {
                when (lastPotType()) {
                    TYPE_FREEAVAILABLE -> toggleVisible()
                    TYPE_FREETOGGLE -> {
                        toggleVisible()
                        toggleSelected()
                    }
                    else -> setInvisible()
                }
            }
            TYPE_FREETOGGLE -> toggleSelected()
            TYPE_FREEAVAILABLE -> resetFreeball()
            else -> {}
        }
    }
}