package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.domain.DomainMatchInfo.RULES
import com.quickpoint.snookerboard.domain.PotType.*
import kotlin.math.max

// Keeps a dynamic live score for both players
sealed class CurrentScore(
    var frameId: Int,
    var framePoints: Int,
    var matchPoints: Int,
    var successShots: Int,
    var missedShots: Int,
    var fouls: Int,
    var highestBreak: Int
) {
    object SCORE01 : CurrentScore(0, 0, 0, 0, 0, 0, 0)
    object SCORE02 : CurrentScore(0, 0, 0, 0, 0, 0, 0)

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

    fun isMatchEnding(matchFrames: Int) = getWinner().matchPoints + 1 == matchFrames
    fun isFrameEqual() = framePoints == getOther().framePoints
    fun isMatchEqual() = matchPoints == getOther().matchPoints
    fun isFrameInProgress() = (framePoints + getOther().framePoints > 0)
    fun isMatchInProgress() = matchPoints + getOther().matchPoints > 0

    fun getFirst() = SCORE01
    fun getSecond() = SCORE02
    fun getWinner() = if (framePoints > getOther().framePoints) this else getOther()

    private fun getOther() = when (this) {
        SCORE01 -> SCORE02
        SCORE02 -> SCORE01
    }

    fun getPlayerAsInt(): Int = when (this) {
        SCORE01 -> 0
        SCORE02 -> 1
    }

    fun getCrtPlayerFromRules(): CurrentScore = if (RULES.crtPlayer == 0) SCORE01 else SCORE02

    fun resetFrame() {
        this.resetFrameScoreEach()
        this.getOther().resetFrameScoreEach()
    }

    private fun resetFrameScoreEach() {
        framePoints = 0
        highestBreak = 0
        successShots = 0
        missedShots = 0
        fouls = 0
    }

    fun resetMatch() {
        this.resetMatchScoreEach()
        this.getOther().resetMatchScoreEach()
    }

    private fun resetMatchScoreEach() {
        matchPoints = 0
        frameId = 0
    }

    private fun addFramePoints(points: Int) {
        framePoints += points
    }

    private fun addMissedShots(pol: Int) {
        missedShots += pol
    }

    private fun addFouls(pol: Int) {
        fouls += pol
    }

    fun addMatchPointAndAssignFrameId() {
        getWinner().matchPoints += 1
        this.frameId = RULES.frameCount // TEMP - Assign a frameId to later use to add frame info to DATABASE
        this.getOther().frameId = RULES.frameCount // TEMP - Assign a frameId to later use to add frame info to DATABASE
    }

    private fun findMaxBreak(frameStack: MutableList<DomainBreak>) {
        var highestBreak = 0
        frameStack.forEach { crtBreak ->
            if (getPlayerAsInt() == crtBreak.player && crtBreak.breakSize > highestBreak) {
                highestBreak = crtBreak.breakSize
            }
        }
        this.highestBreak = highestBreak
    }

    // Polarity is used to reverse score on undo
    fun calculatePoints(pot: DomainPot, pol: Int, lastBall: DomainBall) {
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
    }
}

fun CurrentScore.addSuccessShots(pol: Int) {
    successShots += pol
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

fun CurrentScore.asDomainPlayerScore(): DomainPlayerScore { // Converts the current score into a domain score
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

fun MutableList<DomainPlayerScore>.asCurrentScore(): CurrentScore? { // Converts the domain player score into the current score
    if (this.size > 1) {
        val currentScore: CurrentScore = CurrentScore.SCORE01
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