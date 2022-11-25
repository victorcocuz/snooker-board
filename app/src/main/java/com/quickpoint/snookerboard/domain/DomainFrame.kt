package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.database.*

// The DOMAIN Frame is a class containing all frame information
data class DomainFrame(
    val frameId: Long,
    val ballStack: MutableList<DomainBall>, // Keep track of all DOMAIN Balls (i.e. a list of all balls potted, in order)
    val score: MutableList<DomainScore>, // Two element array to keep track of DOMAIN Score (i.e. overrides latest score)
    val frameStack: MutableList<DomainBreak>, // Keep track of all DOMAIN Breaks (i.e. a list of all breaks)
    val actionLogs: MutableList<DomainActionLog>, // Keep track of all actions for debug purposes
    val frameMax: Int, // Keep track of maximum remaining points
) {

    // Checkers
    fun isFoulAndAMiss() = ballStack.availablePoints() > score.frameScoreDiff()

    // Text methods
    fun getTextInfo(): String {
        var text = "Id: $frameId "
        frameStack.forEach { breaks -> breaks.pots.forEach { pot -> text = text + pot.ball.ballType.toString() + ", " } }
        return text
    }

    // CONVERTER method from DOMAIN frame to a list of DATABASE Frame
    fun asDbFrame(): DbFrame {
        return DbFrame(
            frameId = frameId,
            frameMax = frameMax
        )
    }

    // CONVERTER method from DOMAIN Frame a list of DATABASE Balls
    fun asDbBallStack(): List<DbBall> {
        return ballStack.map { ball ->
            DbBall(
                ballId = ball.ballId,
                frameId = frameId,
                ballValue = ball.getBallOrdinal(),
                ballPoints = ball.points,
                ballFoul = ball.foul
            )
        }
    }

    // CONVERTER method from DOMAIN frame to a list of DATABASE Score
    fun asDbCrtScore(): List<DbScore> {
        return score.map { score ->
            DbScore(
                scoreId = score.scoreId,
                frameId = frameId,
                playerId = score.playerId,
                framePoints = score.framePoints,
                matchPoints = score.matchPoints,
                successShots = score.successShots,
                missedShots = score.missedShots,
                safetySuccessShots = score.safetySuccessShots,
                safetyMissedShots = score.safetyMissedShots,
                snookers = score.snookers,
                fouls = score.fouls,
                highestBreak = score.highestBreak
            )
        }
    }

    // CONVERTER method from DOMAIN Frame a list of DATABASE Breaks
    fun asDbBreaks(): List<DbBreak> {
        return frameStack.map {
            DbBreak(
                breakId = it.breakId,
                player = it.player,
                frameId = it.frameId,
                breakSize = it.breakSize
            )
        }
    }

    // CONVERTER method from DOMAIN Frame a list of DATABASE DbDebugFrameAction list
    fun asDbDebugFrameActions(): List<DbActionLog> {
        return actionLogs.map { actionLog ->
            DbActionLog(
                frameId = frameId,
                description = actionLog.description,
                potType = actionLog.potType,
                ballType = actionLog.ballType,
                ballPoints = actionLog.ballPoints,
                potAction = actionLog.potAction,
                player = actionLog.player,
                breakCount = actionLog.breakCount,
                ballStackLast = actionLog.ballStackLast,
                frameCount = actionLog.frameCount
            )
        }
    }
}