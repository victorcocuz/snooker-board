package com.quickpoint.snookerboard.fragments.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo.FREEBALLINFO
import com.quickpoint.snookerboard.domain.DomainMatchInfo.*
import com.quickpoint.snookerboard.domain.DomainPot.*
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.utils.Event
import timber.log.Timber

class GameViewModel : ViewModel() {

    // Observables
    private val _isUpdateInProgress = MutableLiveData(false)
    val isUpdateInProgress: LiveData<Boolean> = _isUpdateInProgress // Deactivate all buttons & options menu if frame is updating

    private val _freeballControls = MutableLiveData(FREEBALLINFO)
    val freeballControls: LiveData<FREEBALLINFO> = _freeballControls

    private val _eventFrameUpdated = MutableLiveData<Event<Boolean>>()
    val eventFrameUpdated: LiveData<Event<Boolean>> = _eventFrameUpdated
    private fun onEventFrameUpdated() {
        _freeballControls.value = FREEBALLINFO
        _isUpdateInProgress.value = false
        _eventFrameUpdated.value = Event(true)
    }

    // Variables
    var player: CurrentPlayer = CurrentPlayer.PLAYER01
    var ballStack: MutableList<DomainBall> = mutableListOf()
    var frameStack: MutableList<DomainBreak> = mutableListOf()

    // Handler functions
    fun loadMatchBLoadFrame(frame: DomainFrame?) = frame?.let { // Will load latest frame once observed from play fragment
        Timber.i("loadMatchPartBLoadFrame()")
        player = it.frameScore.asCurrentScore() ?: player
        frameStack = it.frameStack
        ballStack = it.ballStack
        RULES.frameMax = it.frameMax
        onEventFrameUpdated()
    }

    fun resetFrame() { // Reset all frame values on match end if chosen to discard current frame, when resetting match , when starting a new frame or on rerack action from the options menu
        Timber.i("resetFrame()")
        RULES.frameMax = RULES.reds * 8 + 27
        player.getFirst().resetFrameScore()
        player.getSecond().resetFrameScore()
        player = player.getFirstPlayerFromRules()
        frameStack.clear()
        ballStack.clear()
        ballStack.addBalls(WHITE(), BLACK(), PINK(), BLUE(), BROWN(), GREEN(), YELLOW())
        repeat(RULES.reds) { ballStack.addBalls(COLOR(), RED()) }
        onEventFrameUpdated()
    }

    fun handlePot(pot: DomainPot) {
        _isUpdateInProgress.value = true
        frameStack.apply { // Add to frameStack all pots, but remove repeated freeball toggles
            if (pot.potType == TYPE_FREETOGGLE && lastPotType() == TYPE_FREETOGGLE) removeLastPotFromFrameStack()
            else addToFrameStack(pot)
        }

        FREEBALLINFO.handlePotFreeballInfo(pot)
        ballStack.apply {
            player.calculatePoints(pot, 1, last(), frameStack)
            handlePotBallStack(pot.potType, player.isFrameEqual())
        }

        Timber.i("handlePot: ${pot.potType}, ball: ${pot.ball.ballType}, player: ${player.getPlayerAsInt()}, breakCount: ${frameStack.size}")
        player = player.getCrtPlayerFromRules()
        onEventFrameUpdated()
        if (pot.isFreeballAvailable()) handlePot(FREEAVAILABLE) // make freeball toggle available after foul
    }

    fun handleUndo() {
        _isUpdateInProgress.value = true
        val pot = frameStack.removeLastPotFromFrameStack()
        player = player.getCrtPlayerFromRules()
        Timber.i("handleUndo ${pot.potType}, ball: ${pot.ball.ballType}, player: ${player.getPlayerAsInt()}, breakCount ${frameStack.size}")
        ballStack.handleUndoBallStack(pot, frameStack)
        FREEBALLINFO.handleUndoFreeballInfo(pot, frameStack)
        player.calculatePoints(pot, -1, ballStack.last(), frameStack)
        onEventFrameUpdated()
    }
}