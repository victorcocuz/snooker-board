package com.example.snookerscore.fragments.game

import androidx.lifecycle.MutableLiveData
import com.example.snookerscore.fragments.game.Balls.BLACK
import com.example.snookerscore.fragments.game.Balls.BLUE
import com.example.snookerscore.fragments.game.Balls.BROWN
import com.example.snookerscore.fragments.game.Balls.COLOR
import com.example.snookerscore.fragments.game.Balls.END
import com.example.snookerscore.fragments.game.Balls.GREEN
import com.example.snookerscore.fragments.game.Balls.MISS
import com.example.snookerscore.fragments.game.Balls.PINK
import com.example.snookerscore.fragments.game.Balls.RED
import com.example.snookerscore.fragments.game.Balls.YELLOW
import timber.log.Timber
import java.util.*
import kotlin.math.abs


class Frame {

    // Observables
    var frameState = MutableLiveData<BallType>()
    val pointsPlayerB = MutableLiveData(0)
    val pointsPlayerA = MutableLiveData(0)
    val pointsDiff = MutableLiveData(0)
    val pointsRemaining = MutableLiveData(0)

    // Variables
    private var crtPlayer: CurrentPlayer = CurrentPlayer.PlayerA
    var crtPoints = pointsPlayerA
    var ballStack = ArrayDeque<Ball>()
    var frameStack = ArrayDeque<Shot>()

    init {
        resetFrame()
    }

    fun resetFrame() {
        rerack()
        pointsPlayerA.value = 0
        pointsPlayerB.value = 0
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
        crtPlayer = crtPlayer.switchPlayers()
        crtPoints = when (crtPlayer) {
            CurrentPlayer.PlayerA -> pointsPlayerA
            CurrentPlayer.PlayerB -> pointsPlayerB
        }
        frameState.value = frameState.value!!.resetToRed()
        if (ballStack.peek() == COLOR) removeBall()
    }

    fun addScore(ballPotted: Ball, shotType: ShotType) {
        Timber.e("shot type $shotType")
        // Record shot in frame stack and change frame state
        removeBall()
        frameStack.push(Shot(crtPlayer, ballPotted, ShotType.HIT, Action.Continue))
        frameState.value = ballStack.peek()!!.ballType

        // Add points and calculate difference
        calcPlayerPoints(ballPotted, 1)
        calcPointsDiffAndRemain(ballPotted, 1)
    }

    fun onMiss() {
        frameStack.push(Shot(crtPlayer, MISS, ShotType.MISS, Action.Switch))
        switchPlayer()
    }

    private fun calcPlayerPoints(ballPotted: Ball, polarity: Int) {
        crtPoints.value = crtPoints.value?.plus(polarity * ballPotted.points)
    }

    private fun calcPointsDiffAndRemain(ballPotted: Ball, polarity: Int) {
        pointsDiff.value = abs(pointsPlayerA.value!! - pointsPlayerB.value!!)
        pointsRemaining.value = when (ballPotted) {
            RED -> pointsRemaining.value?.minus(polarity * RED.points)?.minus(polarity * BLACK.points)
            else -> {
                if (frameState.value != BallType.COLOR) pointsRemaining.value?.minus(polarity * ballPotted.points)
                return
            }
        }
    }

    fun removeBall() = ballStack.pop()

    fun undo() {
        val lastShot = frameStack.pop()
        when (lastShot.shotType) {
            ShotType.HIT -> {
                calcPlayerPoints(lastShot.ball, -1)
                calcPointsDiffAndRemain(lastShot.ball, -1)
                ballStack.push(
                    when (ballStack.size) {
                        in arrayOf(7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35) -> COLOR
                        else -> lastShot.ball
                    }
                )
                frameState.value = ballStack.peek()!!.ballType
                crtPlayer = lastShot.player
            }
            ShotType.MISS -> switchPlayer()
        }
    }
}

class Match {
    var matchScore: Int = 0
}