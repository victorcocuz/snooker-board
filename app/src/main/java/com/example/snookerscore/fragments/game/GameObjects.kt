package com.example.snookerscore.fragments.game

sealed class CurrentPlayer(var framePoints: Int, var matchPoints: Int) {
    object PlayerA : CurrentPlayer(0, 0)
    object PlayerB : CurrentPlayer(0, 0)

    fun switchPlayers() = when (this) {
        PlayerA -> PlayerB
        PlayerB -> PlayerA
    }

    fun getFirstPlayer() = PlayerA
    fun getSecondPlayer() = PlayerB

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

    fun resetToRed() = if (this == COLOR) RED else this
}

data class Ball(
    val points: Int,
    val ballType: BallType
)

object Balls {
    val NOBALL = Ball(0, BallType.NOBALL)
    val WHITE = Ball(4, BallType.WHITE)
    val RED = Ball(1, BallType.RED)
    val YELLOW = Ball(2, BallType.YELLOW)
    val GREEN = Ball(3, BallType.GREEN)
    val BROWN = Ball(4, BallType.BROWN)
    val BLUE = Ball(5, BallType.BLUE)
    val PINK = Ball(6, BallType.PINK)
    val BLACK = Ball(7, BallType.BLACK)
    val COLOR = Ball(7, BallType.COLOR)
}

sealed class PotType {
    object HIT : PotType()
    object MISS : PotType()
    object SAFE : PotType()
    object FOUL : PotType()
    object FREEBALL : PotType()
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