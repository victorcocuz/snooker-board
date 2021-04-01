package com.example.snookerscore.fragments.game

import androidx.lifecycle.MutableLiveData
import com.example.snookerscore.fragments.game.Balls.BLACK
import com.example.snookerscore.fragments.game.Balls.BLUE
import com.example.snookerscore.fragments.game.Balls.BROWN
import com.example.snookerscore.fragments.game.Balls.COLOR
import com.example.snookerscore.fragments.game.Balls.END
import com.example.snookerscore.fragments.game.Balls.GREEN
import com.example.snookerscore.fragments.game.Balls.PINK
import com.example.snookerscore.fragments.game.Balls.RED
import com.example.snookerscore.fragments.game.Balls.YELLOW
import timber.log.Timber
import java.util.*
import kotlin.math.abs


class Frame {
    var frameState = MutableLiveData<BallType>()
    var playerA = Player()
        private set
    var playerB = Player()
        private set
    private var crtPlayer = playerA
    var pointsDiff = MutableLiveData(0)
    val pointsRemaining = MutableLiveData(0)
    var ballStack = ArrayDeque<Ball>()
    var frameStack = ArrayDeque<Shot>()

    init {
        resetFrame()
    }

    fun resetFrame() {
        rerack()
        playerA.frameScore.value = 0
        playerB.frameScore.value = 0
        pointsRemaining.value = 147
        frameState.value = BallType.RED
    }

    private fun rerack() {
        ballStack.push(END)
        ballStack.push(BLACK)
        ballStack.push(PINK)
        ballStack.push(BLUE)
        ballStack.push(BROWN)
        ballStack.push(GREEN)
        ballStack.push(YELLOW)
        for (i in 0..14) {
            ballStack.push(COLOR)
            ballStack.push(RED)
        }
    }

    fun switchPlayer() {
        crtPlayer = when (crtPlayer) {
            playerA -> playerB
            else -> playerA
        }
        if (frameState.value == BallType.COLOR) {
            frameState.value = BallType.RED
            removeBall()
        }
    }

    fun addScore(ballPotted: Ball) {
        // Record shot in frame stack and change frame state
        removeBall()
        frameStack.push(Shot(crtPlayer, ballPotted, ShotStatus.HIT))
        frameState.value = ballStack.peek()!!.ballType

        // Add points and calculate difference
        calcPlayerPoints(ballPotted, 1)
        calcPointsDiffAndRemain(ballPotted, 1)
    }

    private fun calcPlayerPoints(ballPotted: Ball, polarity: Int) {
        crtPlayer.frameScore.value = crtPlayer.frameScore.value?.plus(polarity * ballPotted.points)
    }

    private fun calcPointsDiffAndRemain(ballPotted: Ball, polarity: Int) {
        pointsDiff.value = abs(playerA.frameScore.value!! - playerB.frameScore.value!!)
        pointsRemaining.value = when (ballPotted.ballType) {
            BallType.RED -> pointsRemaining.value?.minus(polarity * RED.points)?.minus(polarity * BLACK.points)
            else -> {
                if (frameState.value != BallType.COLOR) pointsRemaining.value?.minus(polarity * ballPotted.points)
                return
            }
        }
    }

    fun removeBall() = ballStack.pop()

    fun undo() {
        val lastShot = frameStack.pop()
        calcPlayerPoints(lastShot.ball, -1)
        calcPointsDiffAndRemain(lastShot.ball, -1)
        Timber.e("size ${frameStack.size}")
        ballStack.push(
            when (ballStack.size) {
                in arrayOf(7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35) -> COLOR
                else -> lastShot.ball
            }
        )
        frameState.value = ballStack.peek()!!.ballType
        crtPlayer = lastShot.player
    }
}

class Match {
    var matchScore: Int = 0
}