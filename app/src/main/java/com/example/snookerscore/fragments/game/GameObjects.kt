package com.example.snookerscore.fragments.game

enum class MatchAction {
    CANCEL_MATCH, END_FRAME, FRAME_ENDED, END_MATCH, MATCH_ENDED
}

sealed class CurrentPlayer(var framePoints: Int, var matchPoints: Int) {
    object PlayerA : CurrentPlayer(0, 0)
    object PlayerB : CurrentPlayer(0, 0)

    fun otherPlayer() = when (this) {
        PlayerA -> PlayerB
        PlayerB -> PlayerA
    }

    fun getFirst() = PlayerA
    fun getSecond() = PlayerB

    fun addFramePoints(points: Int) {
        this.framePoints += points
    }

    fun incrementMatchPoint() {
        this.matchPoints += 1
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
    object FREE: BallType()
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

data class Shot(
    val player: CurrentPlayer,
    val frameState: BallType,
    val pot: Pot
)