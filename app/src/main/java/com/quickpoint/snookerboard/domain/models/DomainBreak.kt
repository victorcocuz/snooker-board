package com.quickpoint.snookerboard.domain.models

import com.quickpoint.snookerboard.data.database.models.DbPot
import com.quickpoint.snookerboard.domain.models.PotAction.CONTINUE
import com.quickpoint.snookerboard.domain.models.PotAction.RETAKE
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_FOUL
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_FREE_ACTIVE
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_HIT
import com.quickpoint.snookerboard.domain.utils.MatchSettings

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
fun List<DomainBreak>.isFrameInProgress() = isNotEmpty()
fun List<DomainBreak>.lastPot() = lastOrNull()?.lastPot()
fun List<DomainBreak>.lastPotType() = lastOrNull()?.lastPotType()
fun List<DomainBreak>.lastBall() = lastPot()?.ball
fun List<DomainBreak>.lastBallType() = lastBall()?.ballType
fun List<DomainBreak>.lastBallTypeBeforeRemoveBall(): BallType? {
    reversed().forEach { crtBreak ->
        crtBreak.pots.reversed().forEach { crtPot ->
            if (crtPot.potType == TYPE_HIT) return crtPot.ball.ballType
        }
    }
    return null
}
fun DomainBreak.isLastBallFoul() = pots.lastOrNull()?.potType == TYPE_FOUL

// Helper methods
fun MutableList<DomainBreak>.findMaxBreak(): Int {
    var newBreak = 0
    forEach { crtBreak ->
        if (crtBreak.player == MatchSettings.crtPlayer && crtBreak.breakSize > newBreak) newBreak = crtBreak.breakSize
    }
    return newBreak
}


fun List<DomainBreak>.displayShots(isAdvancedBreaksActive: Boolean): List<DomainBreak> {
    val list = mutableListOf<DomainBreak>()
    forEach {
        val listOfPotTypes: List<PotType> = if (isAdvancedBreaksActive) listOfAdvancedShowablePotTypes else listOfStandardShowablePotTypes
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
    score[MatchSettings.crtPlayer].highestBreak = findMaxBreak()
}

fun MutableList<DomainBreak>.addToFrameStack(pot: DomainPot, pointsWithoutReturn: Int, score: MutableList<DomainScore>) {
    if (size == 0 // Add a new current break if there are no breaks
        || pot.potType !in listOfPotTypesPointsAdding // or if the current pot is not generating points
        || lastPotType() !in listOfPotTypesPointsAdding // or if previous pot was not generating points
        || last().player != MatchSettings.crtPlayer // or if player has changed
    ) add(DomainBreak(MatchSettings.uniqueId, MatchSettings.crtPlayer, MatchSettings.crtFrame, mutableListOf(), 0, pointsWithoutReturn))
    last().pots.add(pot) // Add the current pot to the current break

    if (pot.potAction == RETAKE || (pot.potType == TYPE_FOUL && pot.potAction == CONTINUE && MatchSettings.counterRetake == 2)) // Update counter retake
        MatchSettings.counterRetake += 1 else MatchSettings.counterRetake = 0 // Check for frame forfeit option
    if (pot.potType in listOfPotTypesPointsAdding) {
        last().breakSize += pot.ball.points // Update the current break size
        last().pointsWithoutReturn += pot.ball.points // Update points without return
        score[MatchSettings.crtPlayer].pointsWithoutReturn = last().pointsWithoutReturn
        score[MatchSettings.getOtherPlayer()].pointsWithoutReturn = 0
    }
}

fun MutableList<DomainBreak>.removeLastPotFromFrameStack(score: MutableList<DomainScore>): DomainPot {
    val pot = last().pots.removeLast() // Get the last pot
    if (MatchSettings.counterRetake >= 0) MatchSettings.counterRetake-- // Update counter retake
    if (pot.potType in listOfPotTypesPointsAdding) {
        last().breakSize -= pot.ball.points // Update current break size
        last().pointsWithoutReturn -= pot.ball.points // Update points without return
        score[MatchSettings.crtPlayer].pointsWithoutReturn = last().pointsWithoutReturn
    }
    while (size > 0 && last().pots.size == 0) removeLast() // Remove all empty breaks, except initial one

    if (size == 0) { // Update points without return after removing empty pots and breaks
        if (MatchSettings.pointsWithoutReturn < 0) score[0].pointsWithoutReturn = MatchSettings.pointsWithoutReturn * -1
        else score[1].pointsWithoutReturn = MatchSettings.pointsWithoutReturn
    } else if (score[MatchSettings.crtPlayer].pointsWithoutReturn == 0)
        score[MatchSettings.getOtherPlayer()].pointsWithoutReturn = last().pointsWithoutReturn
    return pot
}

fun DomainBreak.ballsList(crtPlayer: Int): List<DomainBall> {
    if (player != crtPlayer) return emptyList()
    val balls = mutableListOf<DomainBall>()
    pots.forEach { if (it.potType in listOfPotTypesPointGenerating) balls.add(it.ball) }
    return balls
}