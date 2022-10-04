package com.quickpoint.snookerboard.domain

import kotlin.math.max
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.domain.DomainMatchInfo.RULES

// Keeps a dynamic live score for both players
sealed class CurrentPlayer(
    var frameId: Int,
    var framePoints: Int,
    var matchPoints: Int,
    var successShots: Int,
    var missedShots: Int,
    var fouls: Int,
    var highestBreak: Int
) {
    object PLAYER01 : CurrentPlayer(0, 0, 0, 0, 0, 0, 0)
    object PLAYER02 : CurrentPlayer(0, 0, 0, 0, 0, 0, 0)

    fun initPlayer(
        frameId: Int,
        framePoints: Int,
        matchPoints: Int,
        successShots: Int,
        missedShots: Int,
        fouls: Int,
        highestBreak: Int
    ) {
        this.frameId = frameId
        this.framePoints = framePoints
        this.matchPoints = matchPoints
        this.successShots = successShots
        this.missedShots = missedShots
        this.fouls = fouls
        this.highestBreak = highestBreak
    }

    fun getFirst() = PLAYER01
    fun getSecond() = PLAYER02

    fun getOther() = when (this) {
        PLAYER01 -> PLAYER02
        PLAYER02 -> PLAYER01
    }

    fun setOther(): CurrentPlayer {
        RULES.switchCrtPlayer()
        return getOther()
    }

    fun getPlayerAsInt(): Int = when (this) {
        PLAYER01 -> 0
        PLAYER02 -> 1
    }

    fun getFirstPlayerFromRules() = if (RULES.first == 0) PLAYER01 else PLAYER02
    fun getCrtPlayerFromRules() = if (RULES.crtPlayer == 0) PLAYER01 else PLAYER02

    fun getWinner() = if (framePoints > getOther().framePoints) this else getOther()

    fun isMatchEnding(matchFrames: Int) = getWinner().matchPoints + 1 == matchFrames

    fun isFrameEqual() = framePoints == getOther().framePoints

    fun isMatchEqual() = matchPoints == getOther().matchPoints

    fun isFrameInProgress() = (framePoints + getOther().framePoints > 0)

    fun hasMatchStarted() = framePoints + matchPoints + getOther().framePoints + getOther().matchPoints > 0

    fun addFramePoints(points: Int) {
        framePoints += points
    }

    fun addSuccessShots(pol: Int) {
        successShots += pol
    }

    fun addMissedShots(pol: Int) {
        missedShots += pol
    }

    fun addFouls(pol: Int) {
        fouls += pol
    }

    fun addMatchPoint() {
        matchPoints += 1
    }

    fun findMaxBreak(frameStack: MutableList<DomainBreak>) {
        var highestBreak = 0
        frameStack.forEach { crtBreak ->
            if (getPlayerAsInt() == crtBreak.player && crtBreak.breakSize > highestBreak) {
                highestBreak = crtBreak.breakSize
            }
        }
        this.highestBreak = highestBreak
    }

    fun resetFrameScore() {
        framePoints = 0
        highestBreak = 0
        missedShots = 0
        successShots = 0
    }

    fun resetMatchScore() {
        matchPoints = 0
        frameId = 0
    }

    // Polarity is used to reverse score on undo
    fun calculatePoints(pot: DomainPot, pol: Int, lastBall: DomainBall, frameStack: MutableList<DomainBreak>) {
        val points: Int
        when (pot.potType) {
            TYPE_HIT, TYPE_FREE, TYPE_ADDRED -> {
                points = if (pot.potType == TYPE_FREE) lastBall.points else pot.ball.points
                addFramePoints(pol * points)
                pot.ball.setCustomPointValue(points)
                addSuccessShots(pol)
            }
            TYPE_FOUL -> {
                points = RULES.foul + if (pot.ball is DomainBall.WHITE) max(lastBall.foul, 4) else pot.ball.foul
                pot.ball.setCustomFoulValue(points)
                getOther().addFramePoints(pol * points)
                addMissedShots(pol)
                addFouls(pol)
            }
            TYPE_MISS -> addMissedShots(pol)
            else -> {
            }
        }
        findMaxBreak(frameStack)
    }
}

// DOMAIN Player Score
data class DomainPlayerScore(
    val frameId: Int,
    val playerId: Int,
    val framePoints: Int,
    val matchPoints: Int,
    val successShots: Int,
    val missedShots: Int,
    val fouls: Int,
    val highestBreak: Int
)

fun CurrentPlayer.asDomainPlayerScore(): DomainPlayerScore { // Converts the current score into a domain score
    return DomainPlayerScore(
        frameId = this.frameId,
        playerId = this.getPlayerAsInt(),
        framePoints = this.framePoints,
        matchPoints = this.matchPoints,
        successShots = this.successShots,
        missedShots = this.missedShots,
        fouls = this.fouls,
        highestBreak = this.highestBreak
    )
}

fun MutableList<DomainPlayerScore>.asCurrentScore(): CurrentPlayer? { // Converts the domain player score into the current score
    if (this.size > 1) {
        val currentScore: CurrentPlayer = CurrentPlayer.PLAYER01
        val dbPlayerA = this[this.lastIndex - 1]
        val dbPlayerB = this[this.lastIndex]
        currentScore.getFirst().initPlayer(
            dbPlayerA.frameId,
            dbPlayerA.framePoints,
            dbPlayerA.matchPoints,
            dbPlayerA.successShots,
            dbPlayerA.missedShots,
            dbPlayerA.fouls,
            dbPlayerA.highestBreak
        )
        currentScore.getSecond().initPlayer(
            dbPlayerB.frameId,
            dbPlayerB.framePoints,
            dbPlayerB.matchPoints,
            dbPlayerB.successShots,
            dbPlayerB.missedShots,
            dbPlayerB.fouls,
            dbPlayerB.highestBreak
        )
        return currentScore
    }
    return null
}