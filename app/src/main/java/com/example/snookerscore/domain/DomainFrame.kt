package com.example.snookerscore.domain

import com.example.snookerscore.database.*

data class DomainFrame(
    val frameId: Int,
    val frameScore: MutableList<DomainPlayerScore>,
    val frameStack: MutableList<DomainBreak>,
    val ballStack: MutableList<DomainBall>,
)

fun DomainFrame.asDbFrame(): DbFrame {
    return DbFrame(
        frameId = frameId
    )
}

fun DomainFrame.asDbCrtScore(): List<DbCrtScore> {
    return frameScore.map { playerScore ->
        DbCrtScore(
            frameId = this.frameId,
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
            breakId = it.breakId,
            breakSize = it.breakSize
        )
    }
}

fun DomainBreak.asDbPot(): List<DbPot> {
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