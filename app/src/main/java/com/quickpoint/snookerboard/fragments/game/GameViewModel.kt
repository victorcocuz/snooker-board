package com.quickpoint.snookerboard.fragments.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo.FREEBALLINFO
import com.quickpoint.snookerboard.domain.DomainMatchInfo.*
import com.quickpoint.snookerboard.domain.DomainPot.*
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
    var score: CurrentScore = CurrentScore.SCORE01
    var ballStack: MutableList<DomainBall> = mutableListOf()
    var frameStack: MutableList<DomainBreak> = mutableListOf()

    // Handler functions
    fun loadMatchBLoadFrame(frame: DomainFrame?) = frame?.let { // Will load latest frame once observed from play fragment
        Timber.i("loadMatchPartBLoadFrame(): ${frame.getTextInfo()}")
        RULES.frameMax = it.frameMax
        score = it.frameScore.asCurrentScore() ?: score
        ballStack = it.ballStack
        frameStack = it.frameStack
        onEventFrameUpdated()
    }

    fun saveFrame() { // Update frame data in match view model
        Timber.i("saveFrame()")
        onEventFrameUpdated()
    }

    fun resetFrame() { // Reset all frame values on match end if chosen to discard current frame, when resetting match , when starting a new frame or on rerack action from the options menu
        Timber.i("resetFrame()")
        RULES.rerackBalls()
        RULES.nominatePlayerAtTable(score.hasMatchStarted())
        score = score.resetFrameScoreAndNominatePlayer()
        ballStack.rerackBalls()
        frameStack.clear()
        onEventFrameUpdated()
    }

    fun handlePot(pot: DomainPot) {
        _isUpdateInProgress.value = true
        frameStack.handlePot(pot)
        FREEBALLINFO.handlePotFreeballInfo(pot)
        ballStack.apply {
            score.calculatePoints(pot, 1, last(), frameStack)
            handlePotBallStack(pot.potType, score.isFrameEqual())
        }
        Timber.i("handlePot: ${pot.potType}, ball: ${pot.ball.ballType}, player: ${score.getPlayerAsInt()}, breakCount: ${frameStack.size}")
        score = score.getCrtPlayerFromRules()
        onEventFrameUpdated()
        if (pot.isFreeballAvailable()) handlePot(FREEAVAILABLE) // recurrent method to make freeball toggle available after foul
    }

    fun handleUndo() {
        _isUpdateInProgress.value = true
        val pot = frameStack.removeLastPotFromFrameStack()
        score = score.getCrtPlayerFromRules()
        Timber.i("handleUndo ${pot.potType}, ball: ${pot.ball.ballType}, player: ${score.getPlayerAsInt()}, breakCount ${frameStack.size}")
        ballStack.handleUndoBallStack(pot, frameStack.lastBallType())
        FREEBALLINFO.handleUndoFreeballInfo(pot, frameStack)
        score.calculatePoints(pot, -1, ballStack.last(), frameStack)
        onEventFrameUpdated()
    }
}