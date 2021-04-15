package com.example.snookerscore.fragments.game

import com.example.snookerscore.database.DatabaseFrame
import java.util.*

enum class MatchAction {
    CANCEL_MATCH, END_FRAME, FRAME_ENDED, END_MATCH, MATCH_ENDED
}

data class Frame(
    val frameCount: Int,
    val frameScore: FrameScore
)

fun Frame.asDatabaseFrame() : DatabaseFrame {
    return DatabaseFrame(
        frameCount = this.frameCount,
        frameScore = this.frameScore
    )
}

data class FrameScore(
    val framePoints: Int,
    val matchPoints: Int,
    val successShots: Int,
    val missedShots: Int,
    val fouls: Int,
    val highestBreak: Int
)

fun CurrentFrame.asFrameScore() : FrameScore {
    return FrameScore(
        framePoints = this.framePoints,
        matchPoints = this.matchPoints,
        successShots = this.successShots,
        missedShots = this.missedShots,
        fouls = this.fouls,
        highestBreak = this.highestBreak
    )
}

sealed class CurrentFrame(
    var framePoints: Int,
    var matchPoints: Int,
    var successShots: Int,
    var missedShots: Int,
    var fouls: Int,
    var highestBreak: Int
) {
    object PlayerA : CurrentFrame(0, 0, 0, 0, 0, 0)
    object PlayerB : CurrentFrame(0, 0, 0, 0, 0, 0)

    fun otherPlayer() = when (this) {
        PlayerA -> PlayerB
        PlayerB -> PlayerA
    }

    fun getFirstPlayer() = PlayerA
    fun getSecondPlayer() = PlayerB

    fun addFramePoints(points: Int) {
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

    fun incrementMatchPoint() {
        this.matchPoints += 1
    }

    fun findMaxBreak(frameStack: ArrayDeque<Break>) {
        var highestBreak = 0
        frameStack.forEach{  crtBreak ->
            if (this == crtBreak.player && crtBreak.breakSize > highestBreak) highestBreak = crtBreak.breakSize
        }
        this.highestBreak = highestBreak
    }
}

sealed class BallType {
    object NOBALL : BallType()
    object WHITE : BallType()
    object RED : BallType()
    object YELLOW : BallType()
    object GREEN : BallType()
    object BROWN : BallType()
    object BLUE : BallType()
    object PINK : BallType()
    object BLACK : BallType()
    object COLOR : BallType()
    object FREE : BallType()
}

data class Ball(
    val points: Int,
    val foulPoints: Int,
    val ballType: BallType
)

object Balls {
    val NOBALL = Ball(0, 0, BallType.NOBALL)
    val WHITE = Ball(4, 4, BallType.WHITE)
    val RED = Ball(1, 4, BallType.RED)
    val YELLOW = Ball(2, 4, BallType.YELLOW)
    val GREEN = Ball(3, 4, BallType.GREEN)
    val BROWN = Ball(4, 4, BallType.BROWN)
    val BLUE = Ball(5, 5, BallType.BLUE)
    val PINK = Ball(6, 6, BallType.PINK)
    val BLACK = Ball(7, 7, BallType.BLACK)
    val COLOR = Ball(7, 7, BallType.COLOR)
    val FREE = Ball(1, 4, BallType.FREE)
}

sealed class PotType {
    object HIT : PotType()
    object FREE : PotType()
    object SAFE : PotType()
    object MISS : PotType()
    object FOUL : PotType()
    object REMOVE_RED : PotType()
    object ADD_RED : PotType()
}

sealed class PotAction {
    object Continue : PotAction()
    object Switch : PotAction()
}

object ShotActions {
    val CONTINUE = PotAction.Continue
    val SWITCH = PotAction.Switch
}

data class Pot(
    val ball: Ball,
    val potType: PotType,
    val potAction: PotAction
)

data class Break(
    val player: CurrentFrame,
    val pots: ArrayDeque<Pot>,
    var breakSize: Int
)
