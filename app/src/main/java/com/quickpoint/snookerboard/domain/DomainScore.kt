package com.quickpoint.snookerboard.domain

import kotlin.math.max

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

    fun getPlayerAsInt(): Int = when (this) {
        PLAYER01 -> 0
        PLAYER02 -> 1
    }

    fun getPlayerFromInt(player: Int) = if (player == 0) PLAYER01 else PLAYER02

    fun getWinner() = if (this.framePoints > this.getOther().framePoints) this else this.getOther()

    fun isMatchEnding(matchFrames: Int) = this.getWinner().matchPoints + 1 == matchFrames

    fun isFrameEqual() = this.framePoints == this.getOther().framePoints

    fun isMatchEqual() = this.matchPoints == this.getOther().matchPoints

    fun isFrameInProgress() = (this.framePoints + this.getOther().framePoints > 0)

    fun isMatchInProgress() = this.framePoints + this.matchPoints + this.getOther().framePoints + this.getOther().matchPoints > 0

    fun addFramePoints(points: Int) {
        this.framePoints += points
    }

    fun addSuccessShots(pol: Int) {
        this.successShots += pol
    }

    fun addMissedShots(pol: Int) {
        this.missedShots += pol
    }

    fun addFouls(pol: Int) {
        this.fouls += pol
    }

    fun addMatchPoint() {
        this.matchPoints += 1
    }

    fun findMaxBreak(frameStack: MutableList<DomainBreak>) {
        var highestBreak = 0
        frameStack.forEach { crtBreak ->
            if (this.getPlayerAsInt() == crtBreak.player && crtBreak.breakSize > highestBreak) {
                highestBreak = crtBreak.breakSize
            }
        }
        this.highestBreak = highestBreak
    }

    fun resetFrameScore() {
        this.framePoints = 0
        this.highestBreak = 0
        this.missedShots = 0
        this.successShots = 0
    }

    fun resetMatchScore() {
        this.matchPoints = 0
        this.frameId = 0
    }

    // Polarity is used to reverse score on undo
    fun calculatePoints(pot: DomainPot, pol: Int, lastBall: DomainBall, matchFoul: Int, frameStack: MutableList<DomainBreak>) {
        val points: Int
        when (pot.potType) {
            in listOf(PotType.HIT, PotType.FREE, PotType.ADDRED) -> {
                points = if (pot.potType == PotType.FREE) lastBall.points else pot.ball.points
                addFramePoints(pol * points)
                pot.ball.setCustomPointValue(points)
                addSuccessShots(pol)
            }
            PotType.FOUL -> {
                points = matchFoul + if (pot.ball is DomainBall.WHITE) max(lastBall.foul, 4) else pot.ball.foul
                pot.ball.setCustomFoulValue(points)
                getOther().addFramePoints(pol * points)
                addMissedShots(pol)
                addFouls(pol)
            }
            PotType.MISS -> addMissedShots(pol)
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