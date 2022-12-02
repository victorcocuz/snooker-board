package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.database.DbPot
import com.quickpoint.snookerboard.domain.PotAction.CONTINUE
import com.quickpoint.snookerboard.domain.PotAction.RETAKE
import com.quickpoint.snookerboard.domain.PotType.TYPE_FOUL
import com.quickpoint.snookerboard.domain.PotType.TYPE_FREE_TOGGLE
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS

// The DOMAIN Break class is a list of balls potted in one visit (consecutive balls by one player until the other player takes over or the frame ends)
data class DomainBreak(
    val breakId: Long,
    val player: Int,
    val frameId: Long,
    val pots: MutableList<DomainPot>,
    var breakSize: Int,
) {
    fun lastPot() = pots.lastOrNull()
    fun lastPotType() = lastPot()?.potType
    fun lastBall() = lastPot()?.ball

    // CONVERTER method from DOMAIN Break to a list of DATABASE Pots
    fun asDbPots(breakId: Long): List<DbPot> {
        return pots.map { pot ->
            DbPot(
                potId = pot.potId,
                breakId = breakId,
                ballId = pot.ball.ballId,
                ballOrdinal = pot.ball.getBallOrdinal(),
                ballPoints = pot.ball.points,
                ballFoul = pot.ball.foul,
                potType = pot.potType.ordinal,
                potAction = pot.potAction.ordinal
            )
        }
    }
}

// Checker methods
fun MutableList<DomainBreak>.isFrameInProgress() = size > 0
fun MutableList<DomainBreak>.lastPot() = lastOrNull()?.lastPot()
fun MutableList<DomainBreak>.lastPotType() = lastOrNull()?.lastPotType()
fun MutableList<DomainBreak>.lastBall() = lastPot()?.ball

// Helper methods
fun MutableList<DomainBreak>.findMaxBreak(): Int {
    var newBreak = 0
    forEach { crtBreak ->
        if (crtBreak.player == SETTINGS.crtPlayer && crtBreak.breakSize > newBreak) newBreak = crtBreak.breakSize
    }
    return newBreak
}


fun MutableList<DomainBreak>.displayShots(): MutableList<DomainBreak> {
    val list = mutableListOf<DomainBreak>() // Create a list of pots show within the break rv (SAFE, MISS, REMOVERED are not shown)
    forEach { if (it.pots.last().potType !in listOfPotTypesHelpers) list.add(it.copy()) }
    return list
}

// Pot and undo methods
fun MutableList<DomainBreak>.onPot(pot: DomainPot) { // Add to frameStack all pots, but remove repeated freeball toggles
    if (pot.potType == TYPE_FREE_TOGGLE && lastPotType() == TYPE_FREE_TOGGLE) removeLastPotFromFrameStack()
    else addToFrameStack(pot)
}

fun MutableList<DomainBreak>.addToFrameStack(pot: DomainPot) {
    if (size == 0 // or if there are no breaks
        || pot.potType !in listOfPotTypesPointsAdding // If the current pot is not generating points
        || lastPotType() !in listOfPotTypesPointsAdding // or if previous pot was not generating points
        || last().player != SETTINGS.crtPlayer // or if player has changed
    ) add(DomainBreak(SETTINGS.assignUniqueId(), SETTINGS.crtPlayer, SETTINGS.crtFrame, mutableListOf(), 0)) // Add a new current break
    last().pots.add(pot) // Add the current pot to the current break
    if (pot.potType in listOfPotTypesPointsAdding) last().breakSize += pot.ball.points // Update the current break size
    if (pot.potAction == RETAKE || (pot.potType == TYPE_FOUL && pot.potAction == CONTINUE && SETTINGS.counterRetake == 2))
        SETTINGS.counterRetake += 1 else SETTINGS.counterRetake = 0 // Check for frame forfeit option
}

fun MutableList<DomainBreak>.removeLastPotFromFrameStack(): DomainPot {
    if (SETTINGS.counterRetake >= 0) SETTINGS.counterRetake--
    val pot = last().pots.removeLast() // Get the last pot
    if (pot.potType in listOfPotTypesPointsAdding) last().breakSize -= pot.ball.points // Update current break size
    while (size > 0 && last().pots.size == 0) removeLast() // Remove all empty breaks, except initial one
    return pot
}