package com.example.snookerscore.fragments.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.snookerscore.utils.Event
import java.util.*

class GameFragmentViewModel(application: Application) : AndroidViewModel(application) {
    // Observables
    private val _frameState = MutableLiveData<BallType>()
    val frameState: LiveData<BallType> = _frameState

    private val _eventFoul = MutableLiveData<Event<Unit>>()
    val eventFoul: LiveData<Event<Unit>> = _eventFoul

    private val _displayPlayer = MutableLiveData<CurrentPlayer>()
    val displayPlayer: LiveData<CurrentPlayer> = _displayPlayer

    private val _ballStackSize = MutableLiveData<Int>()
    val ballStackSize: LiveData<Int> = _ballStackSize

    private val _frameStackSize = MutableLiveData<Int>()
    val frameStackSize: LiveData<Int> = _frameStackSize

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
        crtPlayer = CurrentPlayer.PlayerA
        crtPlayer.getFirstPlayer().framePoints = 0
        crtPlayer.getSecondPlayer().framePoints = 0
        rerack()
        getFrameStatus()
    }

    private fun rerack() = ballStack.apply {
        clear()
        for (ball in listOf(Balls.NOBALL, Balls.BLACK, Balls.PINK, Balls.BLUE, Balls.BROWN, Balls.GREEN, Balls.YELLOW)) push(ball)
        repeat(15) { for (ball in listOf(Balls.COLOR, Balls.RED)) push(ball) }
    }

    // Handler functions
    fun handleFoulDialog(pot: Pot, removeRed: Boolean, freeBall: Boolean) {
        updateFrame(crtPlayer, pot.ball, pot.potType, 1, pot.potAction)
        if (removeRed) updateFrame(crtPlayer, (if (inColors()) ballStack.peek() else Balls.RED), PotType.REMOVE_RED, 1, PotAction.Continue)
        if (freeBall) {
            if (inColors()) ballStack.push(Balls.FREE) else for (ball in listOf(Balls.COLOR, Balls.FREE)) ballStack.push(ball)
            getFrameStatus()
        }
    }

    fun onBallClicked(pot: Pot) = updateFrame(crtPlayer, pot.ball, pot.potType, 1, PotAction.Continue)

    fun onSafeClicked() = updateFrame(crtPlayer, Balls.NOBALL, PotType.SAFE, 1, PotAction.Switch)

    fun onMissClicked() = updateFrame(crtPlayer, Balls.NOBALL, PotType.MISS, 1, PotAction.Switch)

    fun onFoulClicked() {
        _eventFoul.value = Event(Unit)
    }

    fun onAddRedClicked() = updateFrame(crtPlayer, Balls.RED, PotType.ADD_RED, 1, PotAction.Continue)

    fun onRerackClicked() = resetFrame()

    fun onEndFrameClicked() = resetFrame()

    fun onEndMatchClicked() = endMatch()

    fun onUndoClicked() {
        val lastShot = frameStack.pop()
        crtPlayer = lastShot.player
        when (lastShot.pot.potType) {
            PotType.HIT -> ballStack.push(if (isColor()) Balls.COLOR else lastShot.pot.ball)
            PotType.ADD_RED -> for (ball in listOf(Balls.RED, Balls.COLOR)) ballStack.push(ball)
            PotType.FOUL -> {
                if (ballStack.peek()!!.ballType == BallType.FREE) repeat(if (inColors()) 1 else 2) { ballStack.pop() }
                if (isColor()) ballStack.push(Balls.COLOR)
            }
            PotType.REMOVE_RED -> {
                onUndoClicked()
                for (ball in when (isColor()) {
                    true -> listOf(Balls.COLOR, Balls.RED)
                    false -> listOf(Balls.RED, Balls.COLOR)
                }) ballStack.push(ball)
            }
            else -> {  }
        }
        calcPoints(crtPlayer, lastShot.pot.ball, lastShot.pot.potType, -1)
        getFrameStatus()
    }

    // Helpers
    private fun updateFrame(currentPlayer: CurrentPlayer, ball: Ball, potType: PotType, pol: Int, potAction: PotAction) {
        when (potType) {
            in listOf(PotType.HIT, PotType.FREE) -> ballStack.pop()
            in listOf(PotType.REMOVE_RED, PotType.ADD_RED) -> repeat(2) { ballStack.pop() }
            else -> {
                if (potAction == PotAction.Switch) crtPlayer = crtPlayer.otherPlayer() // Switch players
                if (ballStack.peek() == Balls.FREE) repeat(if (inColors()) 1 else 2) { ballStack.pop() }
                if (ballStack.peek() == Balls.COLOR) ballStack.pop()
            }
        }
        calcPoints(currentPlayer, ball, potType, pol)
        if (ballStack.size == 1 && crtPlayer.framePoints == crtPlayer.otherPlayer().framePoints) ballStack.push(Balls.BLACK)
        frameStack.push(Shot(currentPlayer, _frameState.value!!, Pot(ball, potType, potAction)))
        getFrameStatus()
    }

    private fun calcPoints(crtPlayer: CurrentPlayer, ball: Ball, potType: PotType, pol: Int) {
        val points = when (potType) { // foul from sinking the white should equal min ball foul on the table
            PotType.FOUL ->
                if (ballStack.size in 1..4 && ball == Balls.WHITE) ballStack.peek()!!.foulPoints
                else ball.foulPoints
            PotType.REMOVE_RED -> 0
            PotType.FREE -> if (inColors()) 1 else ballStack.peek()!!.points
            else -> ball.points
        }
        if (potType == PotType.FOUL) crtPlayer.otherPlayer().addFramePoints(pol * points)
        else crtPlayer.addFramePoints(pol * points)
    }

    private fun inColors(): Boolean = ballStack.size <= 7
    private fun isColor(): Boolean = ballStack.size in listOf(7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35)

    private fun getFrameStatus() {
        if (ballStack.size == 1) onEndFrameClicked()
        _frameState.value = ballStack.peek()!!.ballType
        _ballStackSize.value = ballStack.size
        _frameStackSize.value = frameStack.size
        _displayPlayer.value = crtPlayer
    }

    private fun endMatch() {
    }
}