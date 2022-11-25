package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.FRAME_RERACK
import com.quickpoint.snookerboard.utils.MatchRules.RULES
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
    fun isEmpty() = framePoints + matchPoints + successShots + missedShots + safetyMissedShots + safetyMissedShots + snookers + fouls == 0

    fun resetFrame(index: Int, matchAction: MatchAction) {
        if (matchAction != FRAME_RERACK)  scoreId = RULES.assignUniqueId()
        playerId = index
        framePoints = 0
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
fun MutableList<DomainScore>.isMatchEnding() = this[frameWinner()].matchPoints + 1 == RULES.frames
fun MutableList<DomainScore>.isMatchInProgress() = !(this[0].isEmpty() && this[1].isEmpty())

// Helper methods
fun MutableList<DomainScore>.resetFrame(matchAction: MatchAction) {
    this.forEachIndexed { index, domainScore -> domainScore.resetFrame(index, matchAction) }
}

fun MutableList<DomainScore>.resetMatch() {
    this.clear()
    repeat(2) { this.add(DomainScore(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0)) }
}

fun MutableList<DomainScore>.addMatchPointAndAssignFrameId() {
    this[frameWinner()].matchPoints += 1
    for (score in this) score.frameId = RULES.frameCount // TEMP - Assign a frameId to later use to add frame info to DATABASE
}

fun MutableList<DomainScore>.calculatePoints(pot: DomainPot, pol: Int, lastFoulSize: Int, frameStack: MutableList<DomainBreak>) {
    val points: Int
    when (pot.potType) {
        TYPE_HIT, TYPE_FREE, TYPE_ADDRED -> {
            points = pot.ball.points
            this[RULES.crtPlayer].framePoints += pol * points // Polarity is used to reverse score on undo
            pot.ball.points = points
            this[RULES.crtPlayer].successShots += pol
        }
        TYPE_FOUL -> {
            points = RULES.foul + if (pot.ball.ballType == BallType.TYPE_WHITE) max(lastFoulSize, 4) else pot.ball.foul
            pot.ball.foul = points
            this[RULES.getOtherPlayer()].framePoints += pol * points
            this[RULES.crtPlayer].missedShots += pol
            this[RULES.crtPlayer].fouls += pol
        }
        TYPE_MISS -> this[RULES.crtPlayer].missedShots += pol
        TYPE_SAFE -> this[RULES.crtPlayer].safetySuccessShots += pol
        TYPE_SAFE_MISS -> this[RULES.crtPlayer].safetyMissedShots += pol
        TYPE_SNOOKER -> this[RULES.crtPlayer].snookers += pol
        else -> {}
    }
    this[RULES.crtPlayer].highestBreak = frameStack.findMaxBreak(this[RULES.crtPlayer].highestBreak)
}