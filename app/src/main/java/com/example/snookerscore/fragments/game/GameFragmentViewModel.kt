package com.example.snookerscore.fragments.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.snookerscore.utils.Event
import java.util.*
import kotlin.math.abs

class GameFragmentViewModel(application: Application) : AndroidViewModel(application) {
    // Observables
    private val _frameState = MutableLiveData<BallType>()
    val frameState: LiveData<BallType> = _frameState

    private val _pointsDiff = MutableLiveData<Int>()
    val pointsDiff: LiveData<Int> = _pointsDiff

    private val _pointsRemaining = MutableLiveData<Int>()
    val pointsRemaining: LiveData<Int> = _pointsRemaining

    private val _eventFoul = MutableLiveData<Event<Unit>>()
    val eventFoul: LiveData<Event<Unit>> = _eventFoul

    private val _displayPlayer = MutableLiveData<CurrentPlayer>()
    val displayPlayer : LiveData<CurrentPlayer> = _displayPlayer

    // Variables
    private lateinit var crtPlayer: CurrentPlayer
    private var ballStack = ArrayDeque<Ball>()
    private var frameStack = ArrayDeque<Shot>()

    // Init
    init {
        resetFrame()
    }

    private fun resetFrame() {
        frameStack.clear()
        rerack()
        _pointsRemaining.value = 147
        _frameState.value = BallType.RED
        crtPlayer = CurrentPlayer.PlayerA
        crtPlayer.getFirstPlayer().framePoints = 0
        crtPlayer.getSecondPlayer().framePoints = 0
        _displayPlayer.value = crtPlayer
        calcPointsdiff()
    }

    private fun rerack() {
        ballStack.clear()
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

    // Handler functions
    fun onBallClicked(ball: Ball, shotType: ShotType) {
        // Record shot in frame stack and change frame state
        removeBall()
        frameStack.push(Shot(crtPlayer, ball, shotType, Action.Continue))
        _frameState.value = ballStack.peek()!!.ballType

        // Add points and calculate difference
        calcPlayerPoints(ball, 1)
        calcPointsdiff()
        calcPointsRemain(ball, 1)
    }

    fun onSafeClicked() {

    }

    fun onMissClicked() {
        frameStack.push(Shot(crtPlayer, Balls.NOBALL, ShotType.MISS, Action.Switch))
        switchPlayers()
    }

    fun onFoulClicked() {
        _eventFoul.value = Event(Unit)
    }

    fun onRemoveRedClicked() {
        if (ballStack.peek() == Balls.COLOR) removeBall()
        if (ballStack.peek() == Balls.RED) {
            removeBall()
            _pointsRemaining.value = pointsRemaining.value?.minus( Balls.RED.points)?.minus(Balls.BLACK.points)
        }
    }

    fun onRerackClicked() = resetFrame()

    fun onEndFrameClicked() {
        resetFrame()
    }

    fun onEndMatchClicked() {

    }

    fun onUndoClicked() {
        val lastShot = frameStack.pop()
        when (lastShot.shotType) {
            ShotType.HIT -> {
                calcPlayerPoints(lastShot.ball, -1)
                calcPointsRemain(lastShot.ball, -1)
                ballStack.push(
                    when (ballStack.size) {
                        in arrayOf(7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35) -> Balls.COLOR
                        else -> lastShot.ball
                    }
                )
                _frameState.value = ballStack.peek()!!.ballType
                crtPlayer = lastShot.player
            }
            ShotType.MISS -> switchPlayers()
        }
    }

    // Helpers
    private fun switchPlayers() {
        crtPlayer = crtPlayer.switchPlayers()
        _frameState.value = frameState.value!!.resetToRed()
        if (ballStack.peek() == Balls.COLOR) removeBall()
    }
    private fun calcPlayerPoints(ballPotted: Ball, polarity: Int) {
        crtPlayer.addFramePoints(polarity * ballPotted.points)
        _displayPlayer.value = crtPlayer
    }

    private fun calcPointsdiff() {
        _pointsDiff.value = abs(crtPlayer.getFirstPlayer().framePoints - crtPlayer.getSecondPlayer().framePoints)
    }

    private fun calcPointsRemain(ballPotted: Ball, polarity: Int) {
        _pointsRemaining.value = when (ballPotted) {
            Balls.RED -> pointsRemaining.value?.minus(polarity * Balls.RED.points)?.minus(polarity * Balls.BLACK.points)
            else -> {
                if (frameState.value != BallType.COLOR) pointsRemaining.value?.minus(polarity * ballPotted.points)
                return
            }
        }
    }

    private fun removeBall() = ballStack.pop()
}