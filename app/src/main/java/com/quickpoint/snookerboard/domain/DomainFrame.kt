package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.database.DbBall
import com.quickpoint.snookerboard.database.DbBreak
import com.quickpoint.snookerboard.database.DbFrame
import com.quickpoint.snookerboard.database.DbScore
import kotlin.math.abs

// The DOMAIN Frame is a class containing all frame information
data class DomainFrame(
    val frameId: Int,
    val ballStack: MutableList<DomainBall>, // Keep track of all DOMAIN Balls (i.e. a list of all balls potted, in order)
    val frameScore: MutableList<DomainPlayerScore>, // Two element array to keep track of DOMAIN Player Score and not the Current Score (i.e. overrides latest score)
    val frameStack: MutableList<DomainBreak>, // Keep track of all DOMAIN Breaks (i.e. a list of all breaks)
    val frameMax: Int // Keep track of maximum remaining points
) {
    fun getFrameScoreDiff() = abs(frameScore[0].framePoints - frameScore[1].framePoints)
    fun getMatchScoreDiff() = abs(frameScore[0].matchPoints - frameScore[1].matchPoints)
    fun getFrameScoreRemaining() = ballStack.size.apply { // Formula to calculate remaining available points
        return if (this <= 7) (-(8 - this) * (8 - this) - (8 - this) + 56) / 2
        else 27 + ((this - 7) / 2) * 8
    }
    fun isFrameOver() = getFrameScoreRemaining() < getFrameScoreDiff()
    fun isLastBall() = ballStack.size == 1
}

// CONVERTER method from DOMAIN frame to a list of DATABASE Frame
fun DomainFrame.asDbFrame(): DbFrame {
    return DbFrame(
        frameId = frameId,
        frameMax = frameMax
    )
}

// CONVERTER method from DOMAIN Frame a list of DATABASE Balls
fun DomainFrame.asDbBallStack(): List<DbBall> {
    return ballStack.map { ball ->
        DbBall(
            frameId = frameId,
            ballValue = ball.getBallOrdinal(),
            ballPoints = ball.points,
            ballFoul = ball.foul
        )
    }
}

// CONVERTER method from DOMAIN frame to a list of DATABASE Score
fun DomainFrame.asDbCrtScore(): List<DbScore> {
    return frameScore.map { playerScore ->
        DbScore(
            frameId = frameId,
            playerId = playerScore.playerId,
            framePoints = playerScore.framePoints,
            matchPoints = playerScore.matchPoints,
            successShots = playerScore.successShots,
            missedShots = playerScore.missedShots,
            fouls = playerScore.fouls,
            highestBreak = playerScore.highestBreak
        )
    }
}

// CONVERTER method from DOMAIN Frame a list of DATABASE Breaks
fun DomainFrame.asDbBreak(): List<DbBreak> {
    return frameStack.map {
        DbBreak(
            player = it.player,
            frameId = it.frameId,
            breakSize = it.breakSize
        )
    }
}