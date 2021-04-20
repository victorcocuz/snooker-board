package com.example.snookerscore.fragments.game

import com.example.snookerscore.database.DatabaseFrameScore
import java.util.*

enum class MatchAction {
    CANCEL_MATCH, END_FRAME, FRAME_ENDED, END_MATCH, MATCH_ENDED
}

data class FrameScore(
    val frameCount: Int,
    val playerId: Int,
    val framePoints: Int,
    val matchPoints: Int,
    val successShots: Int,
    val missedShots: Int,
    val fouls: Int,
    val highestBreak: Int
)

sealed class CurrentScore(
    var frameCount: Int,
    var framePoints: Int,
    var matchPoints: Int,
    var successShots: Int,
    var missedShots: Int,
    var fouls: Int,
    var highestBreak: Int
) {
    object PlayerA : CurrentScore(0, 0, 0, 0, 0, 0, 0)
    object PlayerB : CurrentScore(0, 0, 0, 0, 0, 0, 0)

    fun getOther() = when (this) {
        PlayerA -> PlayerB
        PlayerB -> PlayerA
    }

    fun getFirst() = PlayerA
    fun getSecond() = PlayerB

    fun getPlayerAsInt(): Int = when (this) {
        PlayerA -> 0
        PlayerB -> 1
    }

    fun getPlayerFromInt(player: Int) = if (player == 0) PlayerA else PlayerB

    fun incrementFramePoints(points: Int) {
        this.framePoints += points
    }

    fun incrementSuccessShots(pol: Int) {
        this.successShots += pol
    }

    fun incrementMissedShots(pol: Int) {
        this.missedShots += pol
    }

    fun incrementFouls(pol: Int) {
        this.fouls += pol
    }

    fun incrementFrameCount() {
        this.frameCount += 1
    }

    fun incrementMatchPoint() {
        this.matchPoints += 1
    }

    fun findMaxBreak(frameStack: ArrayDeque<Break>) {
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
        this.frameCount = 0
        resetFrameScore()
    }
}

fun CurrentScore.asDatabaseFrameScore(): DatabaseFrameScore {
    return DatabaseFrameScore(
        frameCount = this.frameCount,
        playerId = when (this) {
            CurrentScore.PlayerA -> 0
            CurrentScore.PlayerB -> 1
        },
        framePoints = this.framePoints,
        matchPoints = this.matchPoints,
        successShots = this.successShots,
        missedShots = this.missedShots,
        fouls = this.fouls,
        highestBreak = this.highestBreak
    )
}

enum class Ball(
    val points: Int,
    val foulPoints: Int
) {
    NOBALL(0, 0),
    WHITE(4, 4),
    RED(1, 4),
    YELLOW(2, 4),
    GREEN(3, 4),
    BROWN(4, 4),
    BLUE(5, 5),
    PINK(6, 6),
    BLACK(7, 7),
    COLOR(7, 7),
    FREE(1, 4)
}

enum class PotType { HIT, FREE, SAFE, MISS, FOUL, REMOVERED, ADDRED }
enum class PotAction { CONTINUE, SWITCH }

sealed class Pot(
    val ball: Ball,
    val potType: PotType,
    val potAction: PotAction
) {
    class HIT(ball: Ball) : Pot(ball, PotType.HIT, PotAction.CONTINUE)
    object SAFE : Pot(Ball.NOBALL, PotType.SAFE, PotAction.SWITCH)
    object MISS : Pot(Ball.NOBALL, PotType.MISS, PotAction.SWITCH)
    object FREEMISS: Pot(Ball.NOBALL, PotType.FREE, PotAction.CONTINUE)
    class FOUL(ball: Ball, action: PotAction): Pot(ball, PotType.FOUL, action)
    class REMOVERED(ball: Ball): Pot(ball, PotType.REMOVERED, PotAction.CONTINUE)
    object ADDRED: Pot(Ball.RED, PotType.ADDRED, PotAction.CONTINUE)
}

data class Break(
    val player: Int,
    val pots: ArrayDeque<Pot>,
    var breakSize: Int
)