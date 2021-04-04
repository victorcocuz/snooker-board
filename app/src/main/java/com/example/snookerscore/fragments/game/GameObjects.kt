package com.example.snookerscore.fragments.game

import androidx.lifecycle.MutableLiveData

class Player(
    var frameScore: MutableLiveData<Int> = MutableLiveData<Int>(0),
    var matchScore: MutableLiveData<Int> = MutableLiveData<Int>(0)
)

data class NewPlayer(
    var frameScore: Int,
    var matchScore: Int
)

sealed class CurrentPlayer {
    object PlayerA : CurrentPlayer()
    object PlayerB : CurrentPlayer()

    fun switchPlayers() = when (this) {
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

    fun resetToRed() = if (this == COLOR) RED else this
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

sealed class ShotType {
    object HIT : ShotType()
    object MISS : ShotType()
    object SAFE: ShotType()
    object FOUL : ShotType()
    object FREEBALL : ShotType()
}

object ShotTypes {
    val HIT = ShotType.HIT
    val SAFE = ShotType.SAFE
    val MISS = ShotType.MISS
    val FOUL = ShotType.FOUL
    val FREEBALL = ShotType.FREEBALL
}

sealed class Action {
    object Continue: Action()
    object Switch: Action()
}

object Actions {
    val CONTINUE = Action.Continue
    val SWITCH = Action.Switch
}

data class Shot(
    val player: CurrentPlayer,
    val ball: Ball,
    val shotType: ShotType,
    val action: Action
)