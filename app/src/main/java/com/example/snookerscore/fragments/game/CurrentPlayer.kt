package com.example.snookerscore.fragments.game

sealed class CurrentPlayer {

    object PlayerA : CurrentPlayer()
    object PlayerB : CurrentPlayer()

    fun switchPlayer() : CurrentPlayer = when (this) {
        PlayerA -> PlayerB
        PlayerB -> PlayerA
    }
}

sealed class BallType {
    object RED: BallType()
    object COLOR: BallType()
    object YELLOW: BallType()
    object GREEN: BallType()
    object BROWN: BallType()
    object BLUE: BallType()
    object PINK: BallType()
    object BLACK: BallType()
    object END: BallType()

    fun nextState() : BallType = when (this) {
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

    fun alternate() : BallType = when (this) {
        RED -> COLOR
        else -> RED
    }
}

object Balls {
    val RED = Pair(1, BallType.RED)
    val COLOR = Pair(0, BallType.COLOR)
    val YELLOW = Pair(2, BallType.YELLOW)
    val GREEN = Pair(3, BallType.GREEN)
    val BROWN = Pair(4, BallType.BROWN)
    val BLUE = Pair(5, BallType.BLUE)
    val PINK = Pair(6, BallType.PINK)
    val BLACK = Pair(7, BallType.BLACK)
}
