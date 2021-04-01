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

data class Ball(
    val points: Int,
    val ballType: BallType
)

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

sealed class FoulAction {
    object CONTINUE: FoulAction()
    object FREEBALL: FoulAction()
    object FORCE_CONTINUE: FoulAction()
    object FORCE_RETAKE: FoulAction()
}

object FoulActions {
    val CONTINUE = FoulAction.CONTINUE
    val CONTINUE_WITH_FREEBALL = FoulAction.FREEBALL
    val FORCE_CONTINUE = FoulAction.FORCE_CONTINUE
    val FORCE_RETAKE = FoulAction.FORCE_RETAKE
}