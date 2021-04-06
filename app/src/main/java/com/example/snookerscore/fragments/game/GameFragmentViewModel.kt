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
    val displayPlayer: LiveData<CurrentPlayer> = _displayPlayer

    private val _ballStackSize = MutableLiveData<Int>()
    val ballStackSize: LiveData<Int> = _ballStackSize

    // Variables
    private lateinit var crtPlayer: CurrentPlayer
    private var ballStack = ArrayDeque<Ball>()
    private var frameStack = ArrayDeque<Shot>()
    private var freeBall = false

    // Init
    init {
        resetFrame()
    }

    private fun resetFrame() {
        frameStack.clear()
        crtPlayer = CurrentPlayer.PlayerA
        crtPlayer.getFirstPlayer().framePoints = 0
        crtPlayer.getSecondPlayer().framePoints = 0
        rerack()
        getFrameStatus()
        calcPointsDiffAndRemain()
    }

    private fun rerack() {
        ballStack.clear()
        ballStack.push(Balls.NOBALL)
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
    fun handleFoulDialog(pot: Pot, removeRed: Boolean, freeBall: Boolean) {
        if (removeRed) updateFrameStatus(crtPlayer, Balls.RED, PotType.REMOVE_RED, 1, PotAction.Continue)
        updateFrameStatus(crtPlayer, pot.ball, pot.potType, 1, pot.potAction)
        if (freeBall) addFreeBall()
    }

    fun onBallClicked(pot: Pot) {
        if (freeBall) {
            updateFrameStatus(crtPlayer, pot.ball, PotType.FREEBALL, 1, PotAction.Continue)
            freeBall = false
        } else {
            updateFrameStatus(crtPlayer, pot.ball, pot.potType, 1, PotAction.Continue)
        }
    }

    fun onSafeClicked() = updateFrameStatus(crtPlayer, Balls.NOBALL, PotType.SAFE, 1, PotAction.Switch)

    fun onMissClicked() = updateFrameStatus(crtPlayer, Balls.NOBALL, PotType.MISS, 1, PotAction.Switch)

    fun onFoulClicked() {
        _eventFoul.value = Event(Unit)
    }

    fun onAddRedClicked() = updateFrameStatus(crtPlayer, Balls.RED, PotType.ADD_RED, 1, PotAction.Continue)

    fun onRerackClicked() = resetFrame()

    fun onEndFrameClicked() = resetFrame()

    fun onEndMatchClicked() = endMatch()

    fun onUndoClicked() {

    }

    // Helpers
    private fun addFreeBall() {
        ballStack.push(Balls.COLOR)
        ballStack.push(Balls.RED)
        freeBall = true
        calcPointsDiffAndRemain()
        getFrameStatus()
    }

    private fun switchToRed(potAction: PotAction) {
        if (freeBall) {
            freeBall = false
            updateFrameStatus(crtPlayer, Balls.RED, PotType.REMOVE_RED, 1, PotAction.Continue)
        }
        if (potAction == PotAction.Switch) crtPlayer = crtPlayer.otherPlayer()
        if (ballStack.peek() == Balls.COLOR) removeBalls(1)
    }

    private fun removeBalls(count: Int) { // Remove ball unless there is a tie on the black
        if (ballStack.size == 2 && crtPlayer.framePoints == crtPlayer.otherPlayer().framePoints) return
        for (i in 1..count) ballStack.pop()
        getFrameStatus()
    }

    private fun updateFrameStatus(crtPlayer: CurrentPlayer, ballPotted: Ball, potType: PotType, pol: Int, potAction: PotAction) {
        when (potType) {
            in listOf(PotType.HIT, PotType.FREEBALL) -> removeBalls(1)
            in listOf(PotType.REMOVE_RED, PotType.ADD_RED) -> removeBalls(2)
            else -> switchToRed(potAction)
        }
        calcPoints(crtPlayer, ballPotted, potType, pol)
        frameStack.push(Shot(crtPlayer, _frameState.value!!, Pot(ballPotted, potType, potAction)))
        getFrameStatus()
    }

    private fun calcPoints(crtPlayer: CurrentPlayer, ball: Ball, potType: PotType, pol: Int) {
        val points = when (potType) { // foul from sinking the white should equal min ball foul on the table
            PotType.FOUL ->
                if (ballStack.size in 1..4 && ball == Balls.WHITE) ballStack.peek()!!.foulPoints
                else ball.foulPoints
            PotType.REMOVE_RED -> 0
            else -> ball.points
        }
        if (potType == PotType.FOUL) crtPlayer.otherPlayer().addFramePoints(pol * points)
        else crtPlayer.addFramePoints(pol * points)
        calcPointsDiffAndRemain()
    }

    private fun calcPointsDiffAndRemain() {
        _pointsDiff.value = abs(crtPlayer.getFirstPlayer().framePoints - crtPlayer.getSecondPlayer().framePoints)
        _pointsRemaining.value = when (ballStack.size) {
            in 1..7 -> (-(8 - ballStack.size) * (8 - ballStack.size) - (8 - ballStack.size) + 56) / 2
            else -> 27 + ((ballStack.size - 7) / 2) * 8
        }
        if (pointsRemaining.value == 0) onEndFrameClicked()
    }

    private fun getFrameStatus() {
        _frameState.value = ballStack.peek()!!.ballType
        _ballStackSize.value = ballStack.size
        _displayPlayer.value = crtPlayer
    }

    private fun endMatch() {
    }
}