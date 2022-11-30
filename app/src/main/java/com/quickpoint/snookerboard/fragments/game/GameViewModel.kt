package com.quickpoint.snookerboard.fragments.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainBall.FREEBALL
import com.quickpoint.snookerboard.domain.DomainBall.NOBALL
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo.FREEBALLINFO
import com.quickpoint.snookerboard.domain.DomainPot.FOULATTEMPT
import com.quickpoint.snookerboard.domain.DomainPot.FREE
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.JobQueue
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.MatchRules.RULES
import com.quickpoint.snookerboard.utils.ValueKeeperLiveData
import timber.log.Timber

class GameViewModel(
    private val app: Application,
    private val snookerRepository: SnookerRepository,
) : AndroidViewModel(app) {

    // Variables
    var score: MutableList<DomainScore> = mutableListOf()
    var ballStack: MutableList<DomainBall> = mutableListOf()
    var frameStack: MutableList<DomainBreak> = mutableListOf()
    private var actionLogs: MutableList<DomainActionLog> = mutableListOf()
    private lateinit var jobQueue: JobQueue

    // Observables
    private val _eventGameAction = ValueKeeperLiveData<Event<MatchAction?>>()
    val eventGameAction: ValueKeeperLiveData<Event<MatchAction?>> = _eventGameAction
    fun onEventGameAction(matchAction: MatchAction?, queue: Boolean = false): Boolean {
        if (queue) jobQueue.submit {
            _eventGameAction.postValue(Event(matchAction))
        } else _eventGameAction.postValue(Event(matchAction))
        return matchAction != null
    }

    private val _displayFrame = MutableLiveData<DomainFrame>()
    val displayFrame: LiveData<DomainFrame> = _displayFrame
    private val _freeballInfo = MutableLiveData(FREEBALLINFO)
    val freeballInfo: LiveData<FREEBALLINFO> = _freeballInfo
    private val _crtPlayer = MutableLiveData<Int>()
    val crtPlayer: LiveData<Int> = _crtPlayer
    private fun onEventFrameUpdated(actionLog: DomainActionLog) = jobQueue.submit {
        _displayFrame.postValue(DomainFrame(RULES.frameCount, ballStack, score, frameStack, actionLogs, RULES.frameMax))
        if (actionLogs.size > 0) snookerRepository.saveCurrentFrame(_displayFrame.value!!)
        actionLogs.addLog(actionLog)
        _freeballInfo.value = FREEBALLINFO
        _crtPlayer.value = RULES.crtPlayer
        onEventGameAction(FRAME_UPDATED)
    }

    private val _isUpdateInProgress = MutableLiveData(false)
    val isUpdateInProgress: LiveData<Boolean> = _isUpdateInProgress // Deactivate all buttons & options menu if frame is updating


    // Match actions
    fun loadMatch(frame: DomainFrame?) = frame?.let { // Will load latest frame once observed from play fragment
        jobQueue = JobQueue()
        score = it.score
        ballStack = it.ballStack
        frameStack = it.frameStack
        Timber.i("loadMatch(): ${it.getTextInfo()}")
        onEventGameAction(TRANSITION_TO_FRAGMENT)
        onEventFrameUpdated(DomainActionLog("frameUpdated()"))
    }

    fun resetMatch() { // When starting new match, cancelling or ending an existing match
        jobQueue = JobQueue()
        score.resetMatch()
        resetFrame(MATCH_START_NEW)
        Timber.i("resetMatch()")
        onEventGameAction(TRANSITION_TO_FRAGMENT)
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
        onEventFrameUpdated(DomainActionLog("endFrame()"))
        score.addMatchPointAndAssignFrameId()
        if (matchAction in listOf(FRAME_TO_END, FRAME_ENDED)) onEventGameAction(FRAME_START_NEW, true)
        if (matchAction in listOf(MATCH_TO_END, MATCH_ENDED)) onEventGameAction(NAV_TO_POST_MATCH, true)
    }

    // Handle Pot
    @JvmOverloads
    fun assignPot(potType: PotType?, ball: DomainBall = NOBALL(), action: PotAction = PotAction.CONTINUE) {
        if (isUpdateInProgress.value == false) {
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

    private fun handlePotExceptionsBefore(pot: DomainPot): Boolean = onEventGameAction(when {
        ballStack.isLastBall() && (pot.potType in listOfPotTypesForNoBallSnackbar) -> SNACKBAR_NO_BALL
        pot is FOULATTEMPT -> FOUL_DIALOG
        else -> null
    })

    private fun handlePotExceptionsPost(pot: DomainPot) = onEventGameAction(when {
        pot.potType == TYPE_HIT && ballStack.isLastBall() -> if (score.isFrameEqual()) FRAME_RESPOT_BLACK_DIALOG else FRAME_ENDING_DIALOG
        pot.isFreeballAvailable() -> FRAME_FREE_AVAILABLE
        else -> null
    }, pot.isFreeballAvailable())

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

    private fun handleUndoExceptionsPost(pot: DomainPot) = onEventGameAction(when (pot.potType) {
        TYPE_FREE_AVAILABLE -> FRAME_UNDO
        else -> null
    }, pot.potType == TYPE_FREE_AVAILABLE)

    // Checker methods
    fun isFrameMathematicallyOver() = ballStack.availablePoints() < score.frameScoreDiff()
    fun isRemoveColorAvailable() = ballStack.isInColors() && frameStack.lastPotType() == TYPE_FREE
    fun isRemoveRedAvailable() = ballStack.areRedsOnTheTable() && !FREEBALLINFO.isVisible && frameStack.lastPotType() != TYPE_FOUL
}