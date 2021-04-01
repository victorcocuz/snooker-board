package com.example.snookerscore.fragments.game

sealed class CurrentPlayer {

    object PlayerA : CurrentPlayer()
    object PlayerB : CurrentPlayer()

    fun switchPlayer(): CurrentPlayer = when (this) {
        PlayerA -> PlayerB
        PlayerB -> PlayerA
    }
}

sealed class BallType {
    object NONE : BallType()
    object WHITE : BallType()
    object RED : BallType()
    object COLOR : BallType()
    object YELLOW : BallType()
    object GREEN : BallType()
    object BROWN : BallType()
    object BLUE : BallType()
    object PINK : BallType()
    object BLACK : BallType()
    object END : BallType()

    fun nextType(): BallType = when (this) {
        NONE -> RED
        WHITE -> RED
        RED -> COLOR
        COLOR -> YELLOW
        YELLOW -> GREEN
        GREEN -> BROWN
        BROWN -> BLUE
        BLUE -> PINK
        PINK -> BLACK
        BLACK -> END
        END -> RED
    }

    fun alternate(): BallType = when (this) {
        RED -> COLOR
        else -> RED
    }
}

sealed class ShotStatus {
    object HIT : ShotStatus()
    object MISS : ShotStatus()
    object FOUL : ShotStatus()
    object FREEBALL : ShotStatus()
}

object Balls {
    val END = Ball( 0, BallType.END)
    val RED = Ball(1, BallType.RED)
    val COLOR = Ball(0, BallType.COLOR)
    val YELLOW = Ball(2, BallType.YELLOW)
    val GREEN = Ball(3, BallType.GREEN)
    val BROWN = Ball(4, BallType.BROWN)
    val BLUE = Ball(5, BallType.BLUE)
    val PINK = Ball(6, BallType.PINK)
    val BLACK = Ball(7, BallType.BLACK)
}

data class Ball (
    val points: Int,
    val ballType: BallType
)
