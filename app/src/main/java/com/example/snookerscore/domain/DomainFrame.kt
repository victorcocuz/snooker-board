package com.example.snookerscore.domain

import com.example.snookerscore.database.*
import kotlin.math.abs

data class DomainFrame(
    val frameId: Int,
    val frameScore: MutableList<DomainPlayerScore>,
    val frameStack: MutableList<DomainBreak>,
    val ballStack: MutableList<DomainBall>,
    val frameMax: Int
) {
    fun getFrameScoreDiff() = abs(frameScore[0].framePoints - frameScore[1].framePoints)
    fun getMatchScoreDiff() = abs(frameScore[0].matchPoints - frameScore[1].matchPoints)
    fun getFrameScoreRemaining() = ballStack.size.apply {
        return if (this <= 7) (-(8 - this) * (8 - this) - (8 - this) + 56) / 2
        else 27 + ((this - 7) / 2) * 8
    }
    fun isFrameInProgress() = getFrameScoreRemaining() > getFrameScoreDiff()
}

fun DomainFrame.asDbFrame(): DbFrame {
    return DbFrame(
        frameId = frameId,
        frameMax = frameMax
    )
}

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

fun DomainFrame.asDbBreak(): List<DbBreak> {
    return frameStack.map {
        DbBreak(
            player = it.player,
            frameId = it.frameId,
//            breakId = it.breakId,
            breakSize = it.breakSize
        )
    }
}

fun DomainBreak.asDbPot(breakId: Long): List<DbPot> {
    return pots.map { pot ->
        DbPot(
            breakId = breakId,
            ball = pot.ball.getBallOrdinal(),
            ballPoints = pot.ball.points,
            ballFoul = pot.ball.foul,
            potType = pot.potType.ordinal,
            potAction = pot.potAction.ordinal
        )
    }
}