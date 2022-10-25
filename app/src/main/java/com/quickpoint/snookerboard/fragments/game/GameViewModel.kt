package com.quickpoint.snookerboard.fragments.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo.FREEBALLINFO
import com.quickpoint.snookerboard.domain.DomainMatchInfo.RULES
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.*
import timber.log.Timber

class GameViewModel : ViewModel() {

    // Observables
    private val _isUpdateInProgress = MutableLiveData(false)
    val isUpdateInProgress: LiveData<Boolean> = _isUpdateInProgress // Deactivate all buttons & options menu if frame is updating

    private val _freeballControls = MutableLiveData(FREEBALLINFO)
    val freeballControls: LiveData<FREEBALLINFO> = _freeballControls

    private val _eventFrameAction = MutableLiveData<Event<MatchAction>>()
    val eventFrameAction: LiveData<Event<MatchAction>> = _eventFrameAction

    private fun assignFrameEvent(matchAction: MatchAction): Boolean {
        _eventFrameAction.value = Event(matchAction)
        return true
    }

    private fun onEventFrameUpdated() {
        _freeballControls.value = FREEBALLINFO
        _isUpdateInProgress.value = false
        assignFrameEvent(FRAME_UPDATED)
    }

    // Variables
    var score: CurrentScore = CurrentScore.SCORE01
    var ballStack: MutableList<DomainBall> = mutableListOf()
    var frameStack: MutableList<DomainBreak> = mutableListOf()

    // Match actions
    fun loadMatchBloadFrame(frame: DomainFrame?) = frame?.let { // Will load latest frame once observed from play fragment
        Timber.i("loadMatchPartBLoadFrame(): ${frame.getTextInfo()}")
        score = it.frameScore.asCurrentScore() ?: score
        ballStack = it.ballStack
        frameStack = it.frameStack
        onEventFrameUpdated()
    }

    fun resetMatch(matchAction: MatchAction) { // When starting new match, cancelling or ending an existing match
        Timber.i("resetMatch()")
        if (matchAction == MATCH_CANCEL) RULES.resetRules()
        FREEBALLINFO.resetFreeball()
        score.resetMatch()
        resetFrame(matchAction)
    }

    fun resetFrame(matchAction: MatchAction) { // Reset all frame values on match end if chosen to discard current frame, when resetting match , when starting a new frame or on rerack action from the options menu
        RULES.resetFrameAndGetFirstPlayer(matchAction)
        score = score.getCrtPlayerFromRules()
        score.resetFrame()
        ballStack.resetBalls()
        frameStack.clear()
        Timber.i("resetFrame() frameId: ${RULES.frameCount}")
        onEventFrameUpdated()
    }

    fun saveFrame() { // Update frame data in match view model
        Timber.i("saveFrame()")
        score.addMatchPointAndAssignFrameId()
        onEventFrameUpdated()
    }

    // Handle Pot
    fun handlePot(pot: DomainPot) {
        if (handleExceptionsBeforePot(pot)) return
        computePot(pot)
        handleExceptionsAfterPot(pot)?.let { assignFrameEvent(it) }
    }

    private fun computePot(pot: DomainPot) {
        _isUpdateInProgress.value = true
        FREEBALLINFO.handlePotFreeballInfo(pot)
        ballStack.handlePotBallStack(pot.potType)
        frameStack.handlePot(pot)
        score.apply {
            calculatePoints(pot, 1, ballStack.last())
            highestBreak = frameStack.findMaxBreak()
        }
        Timber.i("handlePot: ${pot.potType}, ball: ${pot.ball.ballType}, player: ${score.getPlayerAsInt()}, breakCount: ${frameStack.size}, ballStackLast: ${ballStack.lastOrNull()?.ballType}")
        RULES.setNextPlayerFromPotAction(pot.potAction)
        score = score.getCrtPlayerFromRules()
        onEventFrameUpdated()
    }

    private fun handleExceptionsBeforePot(pot: DomainPot): Boolean = when {
        ballStack.isLastBall() && (pot.potType in listOf(TYPE_HIT, TYPE_MISS, TYPE_SAFE, TYPE_FOUL)) -> assignFrameEvent(FRAME_NO_BALL)
        pot == DomainPot.FOULATTEMPT -> assignFrameEvent(FOUL_DIALOG)
        else -> false
    }

    private fun handleExceptionsAfterPot(pot: DomainPot): MatchAction? = when {
        pot.potType == TYPE_HIT && ballStack.isLastBall() -> if (score.isFrameEqual()) FRAME_RESPOT_BLACK_DIALOG else FRAME_ENDING_DIALOG
        pot.isFreeballAvailable() -> FRAME_FREEAVAILABLE
        else -> null
    }

    // Handle Undo
    fun handleUndo() {
        _isUpdateInProgress.value = true
        RULES.crtPlayer = frameStack.last().player
        score = score.getCrtPlayerFromRules()
        val pot = frameStack.removeLastPotFromFrameStack()
        Timber.i("handleUndo: ${pot.potType}, ball: ${pot.ball.ballType}, player: ${score.getPlayerAsInt()}, breakCount ${frameStack.size}")
        ballStack.handleUndoBallStack(pot, frameStack.lastBallType())
        FREEBALLINFO.handleUndoFreeballInfo(pot.potType, frameStack.lastPotType())
        score.apply {
            calculatePoints(pot, -1, ballStack.last())
            highestBreak = frameStack.findMaxBreak()
        }
        onEventFrameUpdated()
        handleUndoExceptions(pot)?.let { assignFrameEvent(it) }
    }

    private fun handleUndoExceptions(pot: DomainPot): MatchAction? = when {
        pot.potType == TYPE_FREEAVAILABLE -> FRAME_UNDO
        else -> null
    }
}