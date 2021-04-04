package com.example.snookerscore.fragments.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.snookerscore.fragments.game.Balls.BLACK
import com.example.snookerscore.fragments.game.Balls.BLUE
import com.example.snookerscore.fragments.game.Balls.BROWN
import com.example.snookerscore.fragments.game.Balls.COLOR
import com.example.snookerscore.fragments.game.Balls.END
import com.example.snookerscore.fragments.game.Balls.GREEN
import com.example.snookerscore.fragments.game.Balls.NOBALL
import com.example.snookerscore.fragments.game.Balls.PINK
import com.example.snookerscore.fragments.game.Balls.RED
import com.example.snookerscore.fragments.game.Balls.YELLOW
import java.util.*
import kotlin.math.abs


class Frame {

    // Observables
    private val _frameState = MutableLiveData<BallType>()
    val frameState: LiveData<BallType> = _frameState

    private val _pointsPlayerA = MutableLiveData<Int>()
    val pointsPlayerA: LiveData<Int> = _pointsPlayerA

    private val _pointsPlayerB = MutableLiveData<Int>()
    val pointsPlayerB: LiveData<Int> = _pointsPlayerB

    private val _pointsDiff = MutableLiveData<Int>()
    val pointsDiff: LiveData<Int> = _pointsDiff

    private val _pointsRemaining = MutableLiveData<Int>()
    val pointsRemaining: LiveData<Int> = _pointsRemaining

    // Variables
    private var crtPlayer: CurrentPlayer = CurrentPlayer.PlayerA
    var crtPoints = _pointsPlayerA
    var ballStack = ArrayDeque<Ball>()
    var frameStack = ArrayDeque<Shot>()

    init {
        resetFrame()
    }

    fun resetFrame() {
        rerack()
        _pointsPlayerA.value = 0
        _pointsPlayerB.value = 0
        _pointsRemaining.value = 147
        _frameState.value = BallType.RED
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
            CurrentPlayer.PlayerA -> _pointsPlayerA
            CurrentPlayer.PlayerB -> _pointsPlayerB
        }
        _frameState.value = frameState.value!!.resetToRed()
        if (ballStack.peek() == COLOR) removeBall()
    }

    fun addScore(ballPotted: Ball, shotType: ShotType) {
        // Record shot in frame stack and change frame state
        removeBall()
        frameStack.push(Shot(crtPlayer, ballPotted, shotType, Action.Continue))
        _frameState.value = ballStack.peek()!!.ballType

        // Add points and calculate difference
        calcPlayerPoints(ballPotted, 1)
        calcPointsDiffAndRemain(ballPotted, 1)
    }

    fun onMiss() {
        frameStack.push(Shot(crtPlayer, NOBALL, ShotType.MISS, Action.Switch))
        switchPlayer()
    }

    private fun calcPlayerPoints(ballPotted: Ball, polarity: Int) {
        crtPoints.value = crtPoints.value?.plus(polarity * ballPotted.points)
    }

    private fun calcPointsDiffAndRemain(ballPotted: Ball, polarity: Int) {
        _pointsDiff.value = abs(pointsPlayerA.value!! - pointsPlayerB.value!!)
        _pointsRemaining.value = when (ballPotted) {
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
                _frameState.value = ballStack.peek()!!.ballType
                crtPlayer = lastShot.player
            }
            ShotType.MISS -> switchPlayer()
        }
    }
}

class Match {
    var matchScore: Int = 0
}