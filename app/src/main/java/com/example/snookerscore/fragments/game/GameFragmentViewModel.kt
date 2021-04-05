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
        _displayPlayer.value = crtPlayer
        rerack()
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
        getFrameStatus()
        calcPointsDiffandRemain()
    }

    // Handler functions
    fun handleFoulDialog(pot: Pot, removeRed: Boolean, freeBall: Boolean) {
        calcPoints(crtPlayer.switchPlayers(), pot.ball, pot.potType, 1)
        if (pot.potAction == PotAction.Switch) switchPlayers() else switchToRed()
        if (removeRed) removeOrAddRed(PotType.REMOVE_RED)
        cancelFreeBall()
        if (freeBall) addFreeBall()
    }

    fun onBallClicked(pot: Pot) {
        frameStack.push(
            when (freeBall) {
                true -> Shot(crtPlayer, _frameState.value!!, Pot(pot.ball, PotType.FREEBALL, PotAction.Continue))
                false -> Shot(crtPlayer, _frameState.value!!, Pot(pot.ball, pot.potType, PotAction.Continue))
            }
        )
        if (freeBall) freeBall = !freeBall
        removeBall()
        calcPoints(crtPlayer, pot.ball, pot.potType, 1)
    }

    fun onSafeClicked() {
        cancelFreeBall()
        frameStack.push(Shot(crtPlayer, _frameState.value!!, Pot(Balls.NOBALL, PotType.SAFE, PotAction.Switch)))
        switchPlayers()
    }

    fun onMissClicked() {
        cancelFreeBall()
        frameStack.push(Shot(crtPlayer, _frameState.value!!, Pot(Balls.NOBALL, PotType.MISS, PotAction.Switch)))
        switchPlayers()
    }

    fun onFoulClicked() {
        _eventFoul.value = Event(Unit)
    }

    fun onAddRedClicked() {
        switchToRed()
        removeOrAddRed(PotType.ADD_RED)
    }

    fun onRerackClicked() = resetFrame()

    fun onEndFrameClicked() {
        resetFrame()
    }

    fun onEndMatchClicked() {

    }

    fun onUndoClicked() {
        val lastShot = frameStack.pop()
        when (lastShot.pot.potType) {
            PotType.HIT -> {
                calcPoints(crtPlayer, lastShot.pot.ball, lastShot.pot.potType, -1)
                ballStack.push(
                    when (ballStack.size) {
                        in arrayOf(7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35) -> Balls.COLOR
                        else -> lastShot.pot.ball
                    }
                )
                _frameState.value = ballStack.peek()!!.ballType
                crtPlayer = lastShot.player
            }
            PotType.MISS -> switchPlayers()
        }
    }

    // Helpers
    private fun addFreeBall() {
        ballStack.push(Balls.COLOR)
        ballStack.push(Balls.RED)
        freeBall = true
        getFrameStatus()
        calcPointsDiffandRemain()
    }

    private fun cancelFreeBall() {
        if (freeBall) {
            freeBall = !freeBall
            removeOrAddRed(PotType.REMOVE_RED)
        }
    }

    private fun removeOrAddRed(potType: PotType) {
        removeBall()
        if (potType == PotType.REMOVE_RED) switchToRed()
        calcPoints(crtPlayer, Balls.RED, potType, 1)
        frameStack.push(Shot(crtPlayer, _frameState.value!!, Pot(Balls.RED, potType, PotAction.Continue)))
    }

    private fun switchPlayers() {
        crtPlayer = crtPlayer.switchPlayers()
        switchToRed()
    }

    private fun switchToRed() {
        if (ballStack.peek() == Balls.COLOR) removeBall()
    }

    private fun removeBall() {
        ballStack.pop()
        getFrameStatus()
    }

    private fun getFrameStatus() {
        _frameState.value = ballStack.peek()!!.ballType
        _ballStackSize.value = ballStack.size

    }

    private fun calcPoints(crtPlayer: CurrentPlayer, ballPotted: Ball, potType: PotType, pol: Int) {
        val points = when (potType) {
            PotType.FOUL -> when (ballPotted.points) {
                in 1..4 -> 4
                else -> ballPotted.points
            }
            PotType.REMOVE_RED -> 0
            else -> ballPotted.points
        }
        crtPlayer.addFramePoints(pol * points)
        _displayPlayer.value = crtPlayer
        calcPointsDiffandRemain()
    }

    private fun calcPointsDiffandRemain() {
        _pointsDiff.value = abs(crtPlayer.getFirstPlayer().framePoints - crtPlayer.getSecondPlayer().framePoints)
        _pointsRemaining.value = when (ballStack.size) {
            in 0..6 -> _pointsRemaining.value?.minus(8 - ballStack.size)
            else -> 27 + ((ballStack.size - 7) / 2) * 8
        }
    }
}