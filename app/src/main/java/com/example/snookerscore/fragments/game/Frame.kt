package com.example.snookerscore.fragments.game

import androidx.lifecycle.MutableLiveData
import java.util.*


class Frame {
    private var currentPlayer: CurrentPlayer = CurrentPlayer.PlayerA
    var frameState: BallType = BallType.RED
    var playerA = Player()
        private set
    var playerB = Player()
        private set
    var pointsDiff = MutableLiveData(0)
    val pointsLeft = MutableLiveData(0)
    var ballStack = ArrayDeque<Ball>()
    var frameStack = ArrayDeque<Ball>()

    init {
        resetFrame()
    }

    private fun rerack() {
        ballStack.push(Balls.END)
        ballStack.push(Balls.BLACK)
        ballStack.push(Balls.PINK)
        ballStack.push(Balls.BLUE)
        ballStack.push(Balls.BROWN)
        ballStack.push(Balls.GREEN)
        ballStack.push(Balls.YELLOW)
        for (i in 0..14) {
            ballStack.push(Balls.COLOR)
            ballStack.push(Balls.RED)
        }
    }

    fun switchPlayer() {
        currentPlayer = currentPlayer.switchPlayer()
        if (frameState == BallType.COLOR) frameState = BallType.RED
    }

    fun addScore(ballPotted: Ball) {
        val points = ballPotted.points
        val crtBall = removeBall()
        frameStack.push(crtBall)
        frameState = ballStack.peek()!!.ballType
        when (currentPlayer) {
            CurrentPlayer.PlayerA -> playerA.frameScore.value = playerA.frameScore.value?.plus(points)
            CurrentPlayer.PlayerB -> playerB.frameScore.value = playerB.frameScore.value?.plus(points)
        }

        // Calculate diff and maximum points
        pointsDiff.value = kotlin.math.abs(playerA.frameScore.value!! - playerB.frameScore.value!!)
        pointsLeft.value = when(ballPotted.ballType) {
            BallType.RED -> pointsLeft.value?.minus(8)
            BallType.COLOR -> {return}
            else -> pointsLeft.value?.minus(points)
        }
    }

    fun removeBall() = ballStack.pop()

    fun resetFrame() {
        rerack()
        playerA.frameScore.value = 0
        playerB.frameScore.value = 0
        pointsLeft.value = 147
    }

}

class Match {
    var matchScore: Int = 0
}