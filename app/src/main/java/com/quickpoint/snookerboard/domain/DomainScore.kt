package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.domain.BallType.TYPE_WHITE
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.FRAME_RERACK
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS
import kotlin.math.abs
import kotlin.math.max

// DOMAIN Player Score
data class DomainScore(
    var scoreId: Long,
    var frameId: Long,
    var playerId: Int,
    var framePoints: Int,
    var matchPoints: Int,
    var successShots: Int,
    var missedShots: Int,
    var safetySuccessShots: Int,
    var safetyMissedShots: Int,
    var snookers: Int,
    var fouls: Int,
    var highestBreak: Int,
) {
    fun cumulatedValues() =
        framePoints + matchPoints + successShots + missedShots + safetyMissedShots + safetyMissedShots + snookers + fouls + highestBreak

    fun resetFrame(index: Int, matchAction: MatchAction) {
        if (matchAction != FRAME_RERACK) scoreId = SETTINGS.assignUniqueId()
        playerId = index
        framePoints = SETTINGS.getHandicap(SETTINGS.handicapFrame, if (index == 0) -1 else 1)
        successShots = 0
        missedShots = 0
        safetySuccessShots = 0
        safetyMissedShots = 0
        snookers = 0
        fouls = 0
        highestBreak = 0
    }
}

// Checker Methods
fun MutableList<DomainScore>?.frameScoreDiff() =
    if (this == null || this.size == 0) 0 else abs((this[0].framePoints) - (this[1].framePoints))

fun MutableList<DomainScore>.isFrameEqual() = this[0].framePoints == this[1].framePoints
fun MutableList<DomainScore>.isMatchEqual() = this[0].matchPoints == this[1].matchPoints
fun MutableList<DomainScore>.isFrameAndMatchEqual() = isFrameEqual() && isMatchEqual()
fun MutableList<DomainScore>.isNoFrameFinished() = this[0].matchPoints + this[1].matchPoints == 0
fun MutableList<DomainScore>.frameWinner() = if (this[0].framePoints > this[1].framePoints) 0 else 1
fun MutableList<DomainScore>.isFrameWinResultingMatchTie() = this[frameWinner()].matchPoints + 1 == this[1 - frameWinner()].matchPoints
fun MutableList<DomainScore>.isMatchEnding() = this[frameWinner()].matchPoints + 1 == SETTINGS.maxFramesAvailable
fun MutableList<DomainScore>.isMatchInProgress() = (this[0].cumulatedValues() + this[1].cumulatedValues()) > 0

// Helper methods
fun MutableList<DomainScore>.resetFrame(matchAction: MatchAction) {
    this.forEachIndexed { index, domainScore -> domainScore.resetFrame(index, matchAction) }
}

fun MutableList<DomainScore>.resetMatch() {
    this.clear()
    (0 until 2).forEach {
        this.add(DomainScore(0, 0, 0, 0, SETTINGS.getHandicap(SETTINGS.handicapMatch, if (it == 0) -1 else 1), 0, 0, 0, 0, 0, 0, 0))
    }
}

fun MutableList<DomainScore>.addMatchPointAndAssignFrameId() {
    if (SETTINGS.counterRetake == 3) this[1 - SETTINGS.crtPlayer].matchPoints += 1 // If a non-snooker shot was retaken 3 times game is lost by the crt player
    else this[frameWinner()].matchPoints += 1
    for (score in this) score.frameId = SETTINGS.crtFrame // TEMP - Assign a frameId to later use to add frame info to DATABASE
}

fun MutableList<DomainScore>.calculatePoints(pot: DomainPot, pol: Int, lastFoulSize: Int, frameStack: MutableList<DomainBreak>) {
    val points: Int
    when (pot.potType) {
        TYPE_HIT, TYPE_FREE, TYPE_ADDRED -> {
            points = pot.ball.points
            this[SETTINGS.crtPlayer].framePoints += pol * points // Polarity is used to reverse score on undo
            pot.ball.points = points
            this[SETTINGS.crtPlayer].successShots += pol
        }
        TYPE_FOUL -> {
            points = if (pot.ball.ballType == TYPE_WHITE) max(lastFoulSize, 4) else pot.ball.foul
            pot.ball.foul = points
            this[SETTINGS.getOtherPlayer()].framePoints += pol * points
            this[SETTINGS.crtPlayer].missedShots += pol
            this[SETTINGS.crtPlayer].fouls += pol
        }
        TYPE_MISS -> this[SETTINGS.crtPlayer].missedShots += pol
        TYPE_SAFE -> this[SETTINGS.crtPlayer].safetySuccessShots += pol
        TYPE_SAFE_MISS -> this[SETTINGS.crtPlayer].safetyMissedShots += pol
        TYPE_SNOOKER -> this[SETTINGS.crtPlayer].snookers += pol
        else -> {}
    }
    this[SETTINGS.crtPlayer].highestBreak = frameStack.findMaxBreak()
}