package com.example.snookerscore.fragments.game

import androidx.lifecycle.MutableLiveData

sealed class CurrentPlayer {

    object PlayerA : CurrentPlayer()
    object PlayerB : CurrentPlayer()

    fun switchPlayer(): CurrentPlayer = when (this) {
        PlayerA -> PlayerB
        PlayerB -> PlayerA
    }
}

sealed class BallType {
    object MISS : BallType()
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
}

object Balls {
    val END = Ball(0, BallType.END)
    val MISS = Ball(0, BallType.MISS)
    val WHITE = Ball(4, BallType.WHITE)
    val RED = Ball(1, BallType.RED)
    val COLOR = Ball(0, BallType.COLOR)
    val YELLOW = Ball(2, BallType.YELLOW)
    val GREEN = Ball(3, BallType.GREEN)
    val BROWN = Ball(4, BallType.BROWN)
    val BLUE = Ball(5, BallType.BLUE)
    val PINK = Ball(6, BallType.PINK)
    val BLACK = Ball(7, BallType.BLACK)
}

data class Ball(
    val points: Int,
    val ballType: BallType
)

class Player(
    var frameScore: MutableLiveData<Int> = MutableLiveData<Int>(0),
    var matchScore: MutableLiveData<Int> = MutableLiveData<Int>(0)
)

sealed class ShotStatus {
    object HIT : ShotStatus()
    object MISS : ShotStatus()
    object FOUL : ShotStatus()
    object FREEBALL : ShotStatus()
}

data class Shot(
    val player: Player,
    val ball: Ball,
    val shotStatus: ShotStatus
)



//fun nextType(): BallType = when (this) {
//    BallType.NONE -> BallType.RED
//    BallType.WHITE -> BallType.RED
//    BallType.RED -> BallType.COLOR
//    BallType.COLOR -> BallType.YELLOW
//    BallType.YELLOW -> BallType.GREEN
//    BallType.GREEN -> BallType.BROWN
//    BallType.BROWN -> BallType.BLUE
//    BallType.BLUE -> BallType.PINK
//    BallType.PINK -> BallType.BLACK
//    BallType.BLACK -> BallType.END
//    BallType.END -> BallType.RED
//}
//
//fun alternate(): BallType = when (this) {
//    BallType.RED -> BallType.COLOR
//    else -> BallType.RED
//}