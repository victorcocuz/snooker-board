package com.quickpoint.snookerboard.fragments.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainBall.FREEBALL
import com.quickpoint.snookerboard.domain.DomainBall.NOBALL
import com.quickpoint.snookerboard.domain.DomainPot.FOULATTEMPT
import com.quickpoint.snookerboard.domain.DomainPot.FREE
import com.quickpoint.snookerboard.domain.PotAction.FIRST
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.domain.objects.FrameToggles.FRAMETOGGLES
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.JobQueue
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.utils.livedata.ValueKeeperLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel(
    app: Application,
    private val snookerRepository: SnookerRepository,
) : AndroidViewModel(app) {

    // Variables
    var score: MutableList<DomainScore> = mutableListOf()
    var ballStack: MutableList<DomainBall> = mutableListOf()
    var frameStack: MutableList<DomainBreak> = mutableListOf()
    private var actionLogs: MutableList<DomainActionLog> = mutableListOf()
    private var isUpdateInProgress = false // Deactivate all buttons & options menu if frame is updating
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

    private val _toggles = MutableLiveData(FRAMETOGGLES)
    val toggles: LiveData<FRAMETOGGLES> = _toggles
    fun onToggleLongClicked() {
        FRAMETOGGLES.toggleLongShot()
        _toggles.postValue(FRAMETOGGLES)
    }

    fun onToggleRestClicked() {
        FRAMETOGGLES.toggleRestShot()
        _toggles.postValue(FRAMETOGGLES)
    }

    private val _crtPlayer = MutableLiveData<Int>()
    val crtPlayer: LiveData<Int> = _crtPlayer
    private fun onEventFrameUpdated(actionLog: DomainActionLog) = jobQueue.submit {
        _displayFrame.postValue(DomainFrame(Settings.crtFrame, ballStack, score, frameStack, actionLogs, Settings.availablePoints))
        if (actionLogs.size > 0) snookerRepository.saveCurrentFrame(_displayFrame.value!!)
        actionLogs.addLog(actionLog)
        _crtPlayer.value = Settings.crtPlayer
        _toggles.postValue(FRAMETOGGLES)
        onEventGameAction(FRAME_UPDATED)
    }

    // Match actions
    fun loadMatch(frame: DomainFrame?) = frame?.let { // Will load latest frame once observed from play fragment
        jobQueue = JobQueue()
        score = it.score
        ballStack = it.ballStack
        frameStack = it.frameStack
        onEventGameAction(TRANSITION_TO_FRAGMENT)
        onEventFrameUpdated(DomainActionLog("loadMatch(): ${it.getTextInfo()}"))
    }

    fun resetMatch() { // When starting new match, cancelling or ending an existing match
        jobQueue = JobQueue()
        score.resetMatch()
        resetFrame(MATCH_START_NEW)
        onEventGameAction(TRANSITION_TO_FRAGMENT)
        onEventFrameUpdated(DomainActionLog("resetMatch()"))
    }

    fun resetFrame(matchAction: MatchAction) { // Reset all frame values on match reset, frame rerack and frame start new
        FRAMETOGGLES.setFreeballInactive()
        Settings.resetFrameAndGetFirstPlayer(matchAction)
        score.resetFrame(matchAction)
        ballStack.resetBalls()
        frameStack.clear()
        onEventFrameUpdated(DomainActionLog("resetFrame()"))
    }

    fun endFrame(matchAction: MatchAction) { // Update frame data in match view model
        score.endFrame()
        onEventFrameUpdated(DomainActionLog("endFrame()"))
        if (matchAction in listOf(FRAME_MISS_FORFEIT, FRAME_TO_END, FRAME_ENDED)) onEventGameAction(FRAME_START_NEW, true)
        if (matchAction in listOf(MATCH_TO_END, MATCH_ENDED)) onEventGameAction(NAV_TO_POST_MATCH, true)
    }

    @JvmOverloads // Assign pot action
    fun assignPot(potType: PotType?, ball: DomainBall = NOBALL(), action: PotAction = FIRST) {
        if (!isUpdateInProgress) {
            isUpdateInProgress = true
            if (potType == null) handleUndo()
            else {
                val pot = potType.getPotFromType(ball, action, FRAMETOGGLES.getShotType())
                if (handlePotExceptionsBefore(pot)) {
                    isUpdateInProgress = false
                    return
                }
                handlePot(if (pot.ball is FREEBALL) FREE(ball = FREEBALL(points = pot.ball.points),
                    shotType = FRAMETOGGLES.getShotType()) else pot)
                handlePotExceptionsPost(pot)
            }
            isUpdateInProgress = false
        }
    }

    // Handle Pot
    private fun handlePotExceptionsBefore(pot: DomainPot): Boolean = onEventGameAction(when {
        ballStack.isLastBall() && (pot.potType in listOfPotTypesForNoBallSnackbar) -> SNACK_NO_BALL
        pot is FOULATTEMPT -> FOUL_DIALOG
        else -> null
    })

    private fun handlePot(pot: DomainPot) {
        pot.potId = Settings.assignUniqueId()
        FRAMETOGGLES.handlePotFreeballToggle(pot)
        ballStack.onPot(pot.potType, pot.potAction)
        frameStack.onPot(pot, score[Settings.crtPlayer].pointsWithoutReturn, score)
        score.calculatePoints(pot, 1, ballStack.foulValue())
        val actionLog = pot.getActionLog("handlePot()", ballStack.lastOrNull()?.ballType, frameStack.size)
        Settings.setNextPlayerFromPotAction(pot.potAction)
        onEventFrameUpdated(actionLog)
    }

    private fun handlePotExceptionsPost(pot: DomainPot) {
        if (Settings.counterRetake == 3) viewModelScope.launch {
            delay(200)
            onEventGameAction(FRAME_MISS_FORFEIT_DIALOG)
        }
        if (pot.potType == TYPE_FOUL && ballStack.isLastBlack() && !score.isFrameEqual()) onEventGameAction(FRAME_LAST_BLACK_FOULED_DIALOG, true)
        if (ballStack.isLastBall()) onEventGameAction(if (score.isFrameEqual()) FRAME_RESPOT_BLACK_DIALOG else FRAME_ENDING_DIALOG)
    }

    // Handle Undo
    private fun handleUndo() {
        Settings.crtPlayer = frameStack.last().player
        val pot = frameStack.removeLastPotFromFrameStack(score)
        val actionLog = pot.getActionLog("HandleUndo()", ballStack.lastOrNull()?.ballType, frameStack.size)
        ballStack.onUndo(pot.potType, pot.potAction, frameStack)
        FRAMETOGGLES.handleUndoFreeballToggle(pot.potType, frameStack.lastPotType())
        score.calculatePoints(pot, -1, ballStack.foulValue())
        onEventFrameUpdated(actionLog)
        handleUndoExceptionsPost(pot)
    }

    private fun handleUndoExceptionsPost(pot: DomainPot) = onEventGameAction(when (pot.potType) {
        TYPE_LAST_BLACK_FOULED -> FRAME_UNDO
        TYPE_FREE_ACTIVE -> FRAME_UNDO
        TYPE_FOUL, TYPE_REMOVE_RED -> if (frameStack.lastPotType() == TYPE_REMOVE_RED) FRAME_UNDO else null
        else -> null
    }, pot.potType in listOf(TYPE_FOUL, TYPE_REMOVE_RED, TYPE_FREE_ACTIVE))

    // Checker methods
    fun isFrameMathematicallyOver() = ballStack.availablePoints() < score.frameScoreDiff()
    fun isRemoveColorAvailable() = ballStack.isInColors() && frameStack.lastPotType() == TYPE_FREE
}