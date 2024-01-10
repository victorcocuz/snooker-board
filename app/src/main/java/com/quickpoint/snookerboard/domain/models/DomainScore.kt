package com.quickpoint.snookerboard.domain.models

import com.quickpoint.snookerboard.core.utils.MatchAction
import com.quickpoint.snookerboard.core.utils.MatchAction.FRAME_RERACK
import com.quickpoint.snookerboard.domain.models.BallType.TYPE_WHITE
import com.quickpoint.snookerboard.domain.models.PotType.*
import com.quickpoint.snookerboard.domain.models.ShotType.*
import com.quickpoint.snookerboard.domain.utils.MatchSettings
import com.quickpoint.snookerboard.domain.utils.getHandicap
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
    var longShotsSuccess: Int,
    var longShotsMissed: Int,
    var restShotsSuccess: Int,
    var restShotsMissed: Int,
    var pointsWithoutReturn: Int,
) {
    fun cumulatedValues() =
        framePoints + matchPoints + successShots + missedShots + safetyMissedShots + safetyMissedShots + snookers + fouls + highestBreak

    fun resetFrame(index: Int, matchAction: MatchAction) {
        if (matchAction != FRAME_RERACK) scoreId = MatchSettings.uniqueId
        playerId = index
        framePoints = getHandicap(MatchSettings.handicapFrame, if (index == 0) -1 else 1)
        successShots = 0
        missedShots = 0
        safetySuccessShots = 0
        safetyMissedShots = 0
        snookers = 0
        fouls = 0
        highestBreak = 0
        longShotsSuccess = 0
        longShotsMissed = 0
        restShotsSuccess = 0
        restShotsMissed = 0
    }

}

val emptyDomainScore = DomainScore(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1)

// Checker Methods
fun List<DomainScore>.frameScoreDiff() = if (isEmpty()) 0 else abs((this[0].framePoints) - (this[1].framePoints))
fun List<DomainScore>.isFrameEqual() = if (isEmpty()) false else this[0].framePoints == this[1].framePoints
fun List<DomainScore>.isMatchEqual() = if (isEmpty()) false else this[0].matchPoints == this[1].matchPoints
fun List<DomainScore>.isFrameAndMatchEqual() = isFrameEqual() && isMatchEqual()
fun List<DomainScore>.isNoFrameFinished() = if (isEmpty()) false else this[0].matchPoints + this[1].matchPoints == 0
fun List<DomainScore>.frameWinner() = if (isEmpty()) 0 else if (this[0].framePoints > this[1].framePoints) 0 else 1
fun List<DomainScore>.isFrameWinResultingMatchTie() = if (isEmpty()) false else this[frameWinner()].matchPoints + 1 == this[1 - frameWinner()].matchPoints
fun List<DomainScore>.isMatchEnding() = if (isEmpty()) false else this[frameWinner()].matchPoints + 1 == MatchSettings.availableFrames
fun List<DomainScore>.isMatchInProgress() = if (isEmpty()) false else (this[0].cumulatedValues() + this[1].cumulatedValues()) > 0

// Helper methods
fun MutableList<DomainScore>.resetFrame(matchAction: MatchAction) {
    this.forEachIndexed { index, domainScore -> domainScore.resetFrame(index, matchAction) }
}

fun MutableList<DomainScore>.resetMatch() {
    this.clear()
    (0 until 2).forEach {
        this.add(DomainScore(0, 0, 0, 0, getHandicap(MatchSettings.handicapMatch, if (it == 0) -1 else 1), 0, 0, 0, 0, 0, 0, 0,0,0,0,0, 0))
    }
}

fun MutableList<DomainScore>.endFrame() {
    if (MatchSettings.counterRetake == 3) this[MatchSettings.getOtherPlayer()].matchPoints += 1 // If a non-snooker shot was retaken 3 times game is lost by the crt player
    else this[frameWinner()].matchPoints += 1
    for (score in this) score.frameId = MatchSettings.crtFrame // TEMP - Assign a frameId to later use to add frame info to DATABASE
    MatchSettings.pointsWithoutReturn =
        if (this[0].pointsWithoutReturn > 0) this[0].pointsWithoutReturn * -1
        else this[1].pointsWithoutReturn
}

fun MutableList<DomainScore>.calculatePoints(pot: DomainPot, pol: Int, lastFoulSize: Int) {
    val points: Int
    when (pot.potType) { // Generic shots score
        TYPE_HIT, TYPE_FREE, TYPE_ADDRED -> {
            points = pot.ball.points
            this[MatchSettings.crtPlayer].framePoints += pol * points // Polarity is used to reverse score on undo
            pot.ball.points = points
            this[MatchSettings.crtPlayer].successShots += pol
        }
        TYPE_FOUL -> {
            points = if (pot.ball.ballType == TYPE_WHITE) max(lastFoulSize, 4) else pot.ball.foul
            pot.ball.foul = points
            this[MatchSettings.getOtherPlayer()].framePoints += pol * points
            this[MatchSettings.crtPlayer].missedShots += pol
            this[MatchSettings.crtPlayer].fouls += pol
        }
        TYPE_MISS -> this[MatchSettings.crtPlayer].missedShots += pol
        TYPE_SAFE -> this[MatchSettings.crtPlayer].safetySuccessShots += pol
        TYPE_SAFE_MISS -> this[MatchSettings.crtPlayer].safetyMissedShots += pol
        TYPE_SNOOKER -> this[MatchSettings.crtPlayer].snookers += pol
        else -> {}
    }
    when (pot.potType) { // Long shots and rest shots score
        TYPE_HIT, TYPE_FREE, TYPE_SAFE, TYPE_SNOOKER -> {
            if (pot.shotType in listOf(LONG_AND_REST, LONG)) this[MatchSettings.crtPlayer].longShotsSuccess += pol
            if (pot.shotType in listOf(LONG_AND_REST, REST)) this[MatchSettings.crtPlayer].restShotsSuccess += pol
        }
        TYPE_FOUL, TYPE_MISS, TYPE_SAFE_MISS -> {
            if (pot.shotType in listOf(LONG_AND_REST, LONG)) this[MatchSettings.crtPlayer].longShotsMissed += pol
            if (pot.shotType in listOf(LONG_AND_REST, REST)) this[MatchSettings.crtPlayer].restShotsMissed += pol
        }
        else -> {}
    }
}