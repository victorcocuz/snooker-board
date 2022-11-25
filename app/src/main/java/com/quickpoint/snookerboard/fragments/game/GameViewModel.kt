package com.quickpoint.snookerboard.fragments.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainBall.FREEBALL
import com.quickpoint.snookerboard.domain.DomainBall.NOBALL
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo.FREEBALLINFO
import com.quickpoint.snookerboard.domain.DomainPot.FOULATTEMPT
import com.quickpoint.snookerboard.domain.DomainPot.FREE
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.MatchRules.RULES
import com.quickpoint.snookerboard.utils.ValueKeeperLiveData
import timber.log.Timber

class GameViewModel : ViewModel() {

    // Variables
    var score: MutableList<DomainScore> = mutableListOf()
    var ballStack: MutableList<DomainBall> = mutableListOf()
    var frameStack: MutableList<DomainBreak> = mutableListOf()
    var actionLogs: MutableList<DomainActionLog> = mutableListOf()

    init {
        Timber.e("start game view model")
    }

    // Observables
    private val _freeballInfo = MutableLiveData(FREEBALLINFO)
    val freeballInfo: LiveData<FREEBALLINFO> = _freeballInfo

    private val _eventFrameAction = ValueKeeperLiveData<Event<MatchAction?>>()
    val eventFrameAction: ValueKeeperLiveData<Event<MatchAction?>> = _eventFrameAction
    fun onEventFrameAction(matchAction: MatchAction?): Boolean {
        _eventFrameAction.postValue(Event(matchAction))
        return matchAction != null
    }

    private val _isUpdateInProgress = MutableLiveData(false)
    val isUpdateInProgress: LiveData<Boolean> = _isUpdateInProgress // Deactivate all buttons & options menu if frame is updating
    private fun onEventFrameUpdated(actionLog: DomainActionLog) {
        _freeballInfo.value = FREEBALLINFO
        actionLogs.add(actionLog)
        onEventFrameAction(FRAME_UPDATED)
    }

    // Match actions
    fun loadMatch(frame: DomainFrame?) = frame?.let { // Will load latest frame once observed from play fragment
        score = it.score
        ballStack = it.ballStack
        frameStack = it.frameStack
        Timber.i("loadMatch(): ${it.getTextInfo()}")
        onEventFrameAction(TRANSITION_TO_FRAGMENT)
    }

    fun resetMatch() { // When starting new match, cancelling or ending an existing match
        score.resetMatch()
        resetFrame(MATCH_START_NEW)
        Timber.i("resetMatch()")
        onEventFrameAction(TRANSITION_TO_FRAGMENT)
    }

    fun resetFrame(matchAction: MatchAction) { // Reset all frame values on match reset, frame rerack and frame start new
        FREEBALLINFO.resetFreeball()
        RULES.resetFrameAndGetFirstPlayer(matchAction)
        score.resetFrame(matchAction)
        ballStack.resetBalls()
        frameStack.clear()
        onEventFrameUpdated(DomainActionLog("resetFrame()"))
    }

    fun endFrame(matchAction: MatchAction) { // Update frame data in match view model
        score.addMatchPointAndAssignFrameId()
        onEventFrameUpdated(DomainActionLog("endFrame()"))
        if (matchAction in listOf(FRAME_TO_END, FRAME_ENDED)) onEventFrameAction(FRAME_START_NEW)
        if (matchAction in listOf(MATCH_TO_END, MATCH_ENDED)) onEventFrameAction(NAV_TO_POST_MATCH)
    }

    // Handle Pot
    @JvmOverloads
    fun assignPot(potType: PotType?, ball: DomainBall = NOBALL(), action: PotAction = PotAction.CONTINUE) {
        if (isUpdateInProgress.value == false ) {
            _isUpdateInProgress.value = true
            if (potType == null) handleUndo()
            else {
                val pot = potType.getPotFromType(ball, action)
                if (handlePotExceptionsBefore(pot)) {
                    _isUpdateInProgress.value = false
                    return
                }
                handlePot(if (pot.ball is FREEBALL) FREE(ball = FREEBALL(points = pot.ball.points)) else pot)
                handlePotExceptionsPost(pot)
            }
            _isUpdateInProgress.value = false
        }
    }

    private fun handlePot(pot: DomainPot) {
        pot.potId = RULES.assignUniqueId()
        FREEBALLINFO.handlePotFreeballInfo(pot)
        ballStack.handlePotBallStack(pot.potType)
        frameStack.assignPot(pot)
        score.calculatePoints(pot, 1, ballStack.foulValue(), frameStack)
        val actionLog = pot.getActionLog("handlePot()", ballStack.lastOrNull()?.ballType, frameStack.size)
        RULES.setNextPlayerFromPotAction(pot.potAction)
        onEventFrameUpdated(actionLog)
    }

    private fun handlePotExceptionsBefore(pot: DomainPot): Boolean = onEventFrameAction(when {
        ballStack.isLastBall() && (pot.potType in listOfPotTypesForNoBallSnackbar) -> SNACKBAR_NO_BALL
        pot is FOULATTEMPT -> FOUL_DIALOG
        else -> null
    })

    private fun handlePotExceptionsPost(pot: DomainPot) = onEventFrameAction(when {
        pot.potType == TYPE_HIT && ballStack.isLastBall() -> if (score.isFrameEqual()) FRAME_RESPOT_BLACK_DIALOG else FRAME_ENDING_DIALOG
        pot.isFreeballAvailable() -> FRAME_FREE_AVAILABLE
        else -> null
    })

    // Handle Undo
    private fun handleUndo() {
        RULES.crtPlayer = frameStack.last().player
        val pot = frameStack.removeLastPotFromFrameStack()
        val actionLog = pot.getActionLog("HandleUndo()", ballStack.lastOrNull()?.ballType, frameStack.size)
        ballStack.handleUndoBallStack(pot.potType, frameStack.lastBall())
        FREEBALLINFO.handleUndoFreeballInfo(pot.potType, frameStack.lastPotType())
        score.calculatePoints(pot, -1, ballStack.foulValue(), frameStack)
        onEventFrameUpdated(actionLog)
        handleUndoExceptionsPost(pot)
    }

    private fun handleUndoExceptionsPost(pot: DomainPot) = onEventFrameAction(when (pot.potType) {
        TYPE_FREE_AVAILABLE -> FRAME_UNDO
        else -> null
    })

    // Checker methods
    fun isFrameMathematicallyOver() = ballStack.availablePoints() < score.frameScoreDiff()
    fun isRemoveColorAvailable() = ballStack.isInColors() && frameStack.lastPotType() == TYPE_FREE
    fun isRemoveRedAvailable() = ballStack.areRedsOnTheTable() && !FREEBALLINFO.isVisible && frameStack.lastPotType() != TYPE_FOUL
}