package com.quickpoint.snookerboard.ui.fragments.game

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.quickpoint.snookerboard.BuildConfig
import com.quickpoint.snookerboard.base.Event
import com.quickpoint.snookerboard.base.ValueKeeperLiveData
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainBall.FREEBALL
import com.quickpoint.snookerboard.domain.DomainBall.NOBALL
import com.quickpoint.snookerboard.domain.DomainPot.FOULATTEMPT
import com.quickpoint.snookerboard.domain.DomainPot.FREE
import com.quickpoint.snookerboard.domain.PotAction.FIRST
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.domain.objects.*
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.ui.navigation.MenuItem
import com.quickpoint.snookerboard.ui.navigation.MenuItemIds
import com.quickpoint.snookerboard.utils.Constants
import com.quickpoint.snookerboard.utils.JobQueue
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.sendEmail
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class GameViewModel(
    private val snookerRepository: SnookerRepository,
) : ViewModel() {

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

    private val _eventAction = MutableSharedFlow<MatchAction?>()
    val eventAction = _eventAction.asSharedFlow()
    fun onEventGameAction(matchAction: MatchAction?, queue: Boolean = false): Boolean {
        viewModelScope.launch {
            if (queue) jobQueue.submit { _eventAction.emit(matchAction) }
            else _eventAction.emit(matchAction)
        }
        return matchAction != null
    }

    private val _frameState: MutableStateFlow<DomainFrame> =
        MutableStateFlow(DomainFrame(0, emptyList(), emptyList(), emptyList(), emptyList(), 0))
    val frameState = _frameState.asStateFlow()

    private val _crtPlayer = MutableLiveData<Int>()
    val crtPlayer: LiveData<Int> = _crtPlayer
    private fun onEventFrameUpdated(actionLog: DomainActionLog) = jobQueue.submit {
        _frameState.value =
            DomainFrame(Settings.crtFrame, ballStack, score, frameStack, actionLogs.toList(), Settings.maxFramePoints)
        if (actionLogs.size > 0) snookerRepository.saveCrtFrame(_frameState.value)
        actionLogs.addLog(actionLog)
        _crtPlayer.value = Settings.crtPlayer
        onEventGameAction(FRAME_UPDATED)
    }

    private val _eventSettingsUpdated = MutableSharedFlow<Event<Unit>>()
    val eventSettingsUpdated = _eventSettingsUpdated.asSharedFlow()
    fun onEventSettingsUpdated() = viewModelScope.launch {
        _eventSettingsUpdated.emit(Event(Unit))
    }

    // Match actions
    fun loadMatch(frame: DomainFrame?) = frame?.let { // Will load latest frame once observed from play fragment
        jobQueue = JobQueue()
        score = it.score.toMutableList()
        ballStack = it.ballStack.toMutableList()
        frameStack = it.frameStack.toMutableList()
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
        Toggle.FreeBall.isEnabled = false
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
                val pot = potType.getPotFromType(ball, action, getShotType())
                if (handlePotExceptionsBefore(pot)) {
                    isUpdateInProgress = false
                    return
                }
                handlePot(
                    if (pot.ball is FREEBALL) FREE(
                        ball = FREEBALL(points = pot.ball.points),
                        shotType = getShotType()
                    ) else pot
                )
                handlePotExceptionsPost(pot)
            }
            isUpdateInProgress = false
        }
    }

    // Handle Pot
    private fun handlePotExceptionsBefore(pot: DomainPot): Boolean = onEventGameAction(
        when {
            ballStack.isLastBall() && (pot.potType in listOfPotTypesForNoBallSnackbar) -> SNACK_NO_BALL
            pot is FOULATTEMPT -> FOUL_DIALOG
            else -> null
        }
    )

    private fun handlePot(pot: DomainPot) {
        pot.potId = Settings.assignUniqueId()
        Toggle.FreeBall.handlePotFreeballToggle(pot)
        ballStack.onPot(pot.potType, pot.potAction)
        frameStack.onPot(pot, score[Settings.crtPlayer].pointsWithoutReturn, score)
        score.calculatePoints(pot, 1, ballStack.foulValue())
        val actionLog = pot.getActionLog("handlePot()", ballStack.lastOrNull()?.ballType, frameStack.size)
        Settings.crtPlayer = Settings.getCrtPlayerFromPotAction(pot.potAction)
        onEventFrameUpdated(actionLog)
    }

    private fun handlePotExceptionsPost(pot: DomainPot) {
        if (Settings.counterRetake == 3) viewModelScope.launch {
            delay(200)
            onEventGameAction(FRAME_MISS_FORFEIT_DIALOG)
        }
        if (pot.potType == TYPE_FOUL && ballStack.isLastBlack() && !score.isFrameEqual()) onEventGameAction(
            FRAME_LAST_BLACK_FOULED_DIALOG,
            true
        )
        if (ballStack.isLastBall()) onEventGameAction(if (score.isFrameEqual()) FRAME_RESPOT_BLACK_DIALOG else FRAME_ENDING_DIALOG)
    }

    // Handle Undo
    private fun handleUndo() {
        Settings.crtPlayer = frameStack.last().player
        val pot = frameStack.removeLastPotFromFrameStack(score)
        val actionLog = pot.getActionLog("HandleUndo()", ballStack.lastOrNull()?.ballType, frameStack.size)
        ballStack.onUndo(pot.potType, pot.potAction, frameStack)
        Toggle.FreeBall.handleUndoFreeballToggle(pot.potType, frameStack.lastPotType())
        score.calculatePoints(pot, -1, ballStack.foulValue())
        onEventFrameUpdated(actionLog)
        handleUndoExceptionsPost(pot)
    }

    private fun handleUndoExceptionsPost(pot: DomainPot) = onEventGameAction(
        when (pot.potType) {
            TYPE_LAST_BLACK_FOULED -> FRAME_UNDO
            TYPE_FREE_ACTIVE -> FRAME_UNDO
            TYPE_FOUL, TYPE_REMOVE_RED -> if (frameStack.lastPotType() == TYPE_REMOVE_RED) FRAME_UNDO else null
            else -> null
        }, pot.potType in listOf(TYPE_FOUL, TYPE_REMOVE_RED, TYPE_FREE_ACTIVE)
    )

    // Checker methods
    fun isFrameMathematicallyOver() = ballStack.availablePoints() < score.frameScoreDiff()
    fun isRemoveColorAvailable() = ballStack.isInColors() && frameStack.lastPotType() == TYPE_FREE

    fun deleteCrtFrameFromDb() = viewModelScope.launch {
        snookerRepository.deleteCrtFrame(Settings.crtFrame)
    }

    fun deleteMatchFromDb() = viewModelScope.launch { // When starting a new match or cancelling an existing match
        Settings.matchState = MatchState.RULES_IDLE
        Settings.resetRules()
        snookerRepository.deleteCrtMatch()
    }

    fun emailLogs(context: Context) = viewModelScope.launch {
        val logs = snookerRepository.getDomainActionLogs().toString()
        val json = Gson().toJson(snookerRepository.getDomainActionLogs())
        val body = "${Settings.getAsText()} \n\n $json \n\n $logs"
        Timber.e(json)
        context.sendEmail(arrayOf(BuildConfig.ADMIN_EMAIL), Constants.EMAIL_SUBJECT_LOGS, body)
    }

    fun onMenuItemSelected(menuItem: MenuItem) {
        Timber.e("menuItem ${menuItem.id}")
        when (menuItem.id) {
            MenuItemIds.ID_MENU_ITEM_LOG -> onEventGameAction(FRAME_LOG_ACTIONS_DIALOG)
            MenuItemIds.ID_MENU_ITEM_UNDO -> if (frameStack.isFrameInProgress()) assignPot(null) else onEventGameAction(SNACK_UNDO)
            MenuItemIds.ID_MENU_ITEM_RERACK -> onEventGameAction(if (frameStack.isFrameInProgress()) FRAME_RERACK_DIALOG else SNACK_FRAME_RERACK_DIALOG)
            MenuItemIds.ID_MENU_ITEM_CONCEDE_FRAME -> onEventGameAction(if (!score.isFrameEqual()) FRAME_ENDING_DIALOG else SNACK_FRAME_ENDING_DIALOG)
            MenuItemIds.ID_MENU_ITEM_CONCEDE_MATCH -> onEventGameAction(if (!score.isFrameAndMatchEqual()) MATCH_ENDING_DIALOG else SNACK_MATCH_ENDING_DIALOG)
            MenuItemIds.ID_MENU_ITEM_CANCEL_MATCH -> onEventGameAction(if (score.isMatchInProgress()) MATCH_CANCEL_DIALOG else MATCH_CANCEL)
            else -> {}
        }
    }
}