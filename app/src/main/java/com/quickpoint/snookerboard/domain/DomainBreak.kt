package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.database.models.DbPot
import com.quickpoint.snookerboard.domain.PotAction.CONTINUE
import com.quickpoint.snookerboard.domain.PotAction.RETAKE
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.domain.objects.getOtherPlayer

// The DOMAIN Break class is a list of balls potted in one visit (consecutive balls by one player until the other player takes over or the frame ends)
data class DomainBreak(
    val breakId: Long,
    val player: Int,
    val frameId: Long,
    val pots: MutableList<DomainPot>,
    var breakSize: Int,
    var pointsWithoutReturn: Int,
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
                potAction = pot.potAction.ordinal,
                shotType = pot.shotType.ordinal
            )
        }
    }
}

// Checker methods
fun MutableList<DomainBreak>.isFrameInProgress() = size > 0
fun MutableList<DomainBreak>.lastPot() = lastOrNull()?.lastPot()
fun MutableList<DomainBreak>.lastPotType() = lastOrNull()?.lastPotType()
fun MutableList<DomainBreak>.lastBall() = lastPot()?.ball
fun MutableList<DomainBreak>.lastBallType() = lastBall()?.ballType
fun MutableList<DomainBreak>.lastBallTypeBeforeRemoveBall(): BallType? {
    reversed().forEach { crtBreak ->
        crtBreak.pots.reversed().forEach { crtPot ->
            if (crtPot.potType == TYPE_HIT) return crtPot.ball.ballType
        }
    }
    return null
}
fun DomainBreak.isLastBallFoul() = this.pots.lastOrNull()?.potType == TYPE_FOUL

// Helper methods
fun MutableList<DomainBreak>.findMaxBreak(): Int {
    var newBreak = 0
    forEach { crtBreak ->
        if (crtBreak.player == Settings.crtPlayer && crtBreak.breakSize > newBreak) newBreak = crtBreak.breakSize
    }
    return newBreak
}


fun List<DomainBreak>.displayShots(): MutableList<DomainBreak> {
    val list = mutableListOf<DomainBreak>() // Create a list of pots show within the break rv (SAFE, MISS, REMOVERED are not shown)
    forEach {
        val listOfPotTypes: List<PotType> = if (Toggle.AdvancedBreaks.isEnabled) listOfAdvancedShowablePotTypes else listOfStandardShowablePotTypes
        if (it.pots.last().potType in listOfPotTypes) list.add(it.copy())
    }
    return list
}

// Pot and undo methods
fun MutableList<DomainBreak>.onPot(
    pot: DomainPot,
    pointsWithoutReturn: Int,
    score: MutableList<DomainScore>,
) { // Add to frameStack all pots, but remove repeated freeball toggles
    if (pot.potType == TYPE_FREE_ACTIVE && lastPotType() == TYPE_FREE_ACTIVE) removeLastPotFromFrameStack(score)
    else addToFrameStack(pot, pointsWithoutReturn, score)
    score[Settings.crtPlayer].highestBreak = findMaxBreak()
}

fun MutableList<DomainBreak>.addToFrameStack(pot: DomainPot, pointsWithoutReturn: Int, score: MutableList<DomainScore>) {
    if (size == 0 // Add a new current break if there are no breaks
        || pot.potType !in listOfPotTypesPointsAdding // or if the current pot is not generating points
        || lastPotType() !in listOfPotTypesPointsAdding // or if previous pot was not generating points
        || last().player != Settings.crtPlayer // or if player has changed
    ) add(DomainBreak(Settings.assignUniqueId(), Settings.crtPlayer, Settings.crtFrame, mutableListOf(), 0, pointsWithoutReturn))
    last().pots.add(pot) // Add the current pot to the current break

    if (pot.potAction == RETAKE || (pot.potType == TYPE_FOUL && pot.potAction == CONTINUE && Settings.counterRetake == 2)) // Update counter retake
        Settings.counterRetake += 1 else Settings.counterRetake = 0 // Check for frame forfeit option
    if (pot.potType in listOfPotTypesPointsAdding) {
        last().breakSize += pot.ball.points // Update the current break size
        last().pointsWithoutReturn += pot.ball.points // Update points without return
        score[Settings.crtPlayer].pointsWithoutReturn = last().pointsWithoutReturn
        score[Settings.getOtherPlayer()].pointsWithoutReturn = 0
    }
}

fun MutableList<DomainBreak>.removeLastPotFromFrameStack(score: MutableList<DomainScore>): DomainPot {
    val pot = last().pots.removeLast() // Get the last pot
    if (Settings.counterRetake >= 0) Settings.counterRetake-- // Update counter retake
    if (pot.potType in listOfPotTypesPointsAdding) {
        last().breakSize -= pot.ball.points // Update current break size
        last().pointsWithoutReturn -= pot.ball.points // Update points without return
        score[Settings.crtPlayer].pointsWithoutReturn = last().pointsWithoutReturn
    }
    while (size > 0 && last().pots.size == 0) removeLast() // Remove all empty breaks, except initial one

    if (size == 0) { // Update points without return after removing empty pots and breaks
        if (Settings.pointsWithoutReturn < 0) score[0].pointsWithoutReturn = Settings.pointsWithoutReturn * -1
        else score[1].pointsWithoutReturn = Settings.pointsWithoutReturn
    } else if (score[Settings.crtPlayer].pointsWithoutReturn == 0)
        score[Settings.getOtherPlayer()].pointsWithoutReturn = last().pointsWithoutReturn
    return pot
}

fun DomainBreak.ballsList(crtPlayer: Int): List<DomainBall> {
    if (player != crtPlayer) return emptyList()
    val balls = mutableListOf<DomainBall>()
    pots.forEach { if (it.potType in listOfPotTypesPointGenerating) balls.add(it.ball) }
    return balls
}