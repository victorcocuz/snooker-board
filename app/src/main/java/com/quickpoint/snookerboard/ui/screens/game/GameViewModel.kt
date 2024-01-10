package com.quickpoint.snookerboard.ui.screens.game

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.quickpoint.snookerboard.BuildConfig
import com.quickpoint.snookerboard.core.utils.Constants
import com.quickpoint.snookerboard.core.utils.JobQueue
import com.quickpoint.snookerboard.core.utils.MatchAction
import com.quickpoint.snookerboard.core.utils.MatchAction.*
import com.quickpoint.snookerboard.core.utils.sendEmail
import com.quickpoint.snookerboard.data.DataStore
import com.quickpoint.snookerboard.data.K_BOOL_TOGGLE_FREEBALL
import com.quickpoint.snookerboard.data.K_BOOL_TOGGLE_LONG_SHOT
import com.quickpoint.snookerboard.data.K_BOOL_TOGGLE_REST_SHOT
import com.quickpoint.snookerboard.domain.models.*
import com.quickpoint.snookerboard.domain.models.DomainBall.FREEBALL
import com.quickpoint.snookerboard.domain.models.DomainBall.NOBALL
import com.quickpoint.snookerboard.domain.models.DomainPot.FOULATTEMPT
import com.quickpoint.snookerboard.domain.models.DomainPot.FREE
import com.quickpoint.snookerboard.domain.models.PotAction.FIRST
import com.quickpoint.snookerboard.domain.models.PotType.*
import com.quickpoint.snookerboard.domain.repository.DataStoreRepository
import com.quickpoint.snookerboard.domain.repository.GameRepository
import com.quickpoint.snookerboard.domain.utils.MatchSettings
import com.quickpoint.snookerboard.ui.navigation.MenuItem
import com.quickpoint.snookerboard.ui.navigation.MenuItemIds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    // Variables
    var score: MutableList<DomainScore> = mutableListOf()
    var ballStack: MutableList<DomainBall> = mutableListOf()
    var frameStack: MutableList<DomainBreak> = mutableListOf()
    private var actionLogs: MutableList<DomainActionLog> = mutableListOf()
    private var isUpdateInProgress = false // Deactivate all buttons & options menu if frame is updating
    private lateinit var jobQueue: JobQueue

    // Observables
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
    private fun onEventFrameUpdated(actionLog: DomainActionLog) = jobQueue.submit {
        _frameState.value =
            DomainFrame(MatchSettings.crtFrame, ballStack, score, frameStack, actionLogs.toList(), MatchSettings.maxFramePoints)
        if (actionLogs.size > 0) gameRepository.saveCrtFrame(_frameState.value)
        actionLogs.addLog(actionLog)
        onEventGameAction(FRAME_UPDATED)
        savePref(K_BOOL_TOGGLE_LONG_SHOT, false)
        savePref(K_BOOL_TOGGLE_REST_SHOT, false)
    }

    fun savePref(key: String, value: Boolean) = dataStoreRepository.savePrefs(key, value)

    // Match actions
    fun loadMatch(frame: DomainFrame?) = frame?.let {
        jobQueue = JobQueue()
        score = it.score.toMutableList()
        ballStack = it.ballStack.toMutableList()
        frameStack = it.frameStack.toMutableList()
        onEventFrameUpdated(DomainActionLog("loadMatch(): ${it.getTextInfo()}"))
    }

    fun resetMatch() {
        jobQueue = JobQueue()
        score.resetMatch()
        resetFrame(MATCH_START_NEW)
        onEventFrameUpdated(DomainActionLog("resetMatch()"))
    }

    fun resetFrame(matchAction: MatchAction) {
        savePref(K_BOOL_TOGGLE_FREEBALL, false)
        MatchSettings.resetFrameAndGetFirstPlayer(matchAction)
        score.resetFrame(matchAction)
        ballStack.resetBalls()
        frameStack.clear()
        onEventFrameUpdated(DomainActionLog("resetFrame()"))
    }

    fun endFrame(matchAction: MatchAction) {
        score.endFrame()
        onEventFrameUpdated(DomainActionLog("endFrame()"))
        if (matchAction in listOf(FRAME_MISS_FORFEIT, FRAME_TO_END, FRAME_ENDED)) onEventGameAction(FRAME_START_NEW, true)
        if (matchAction in listOf(MATCH_TO_END, MATCH_ENDED)) onEventGameAction(NAV_TO_POST_MATCH, true)
    }

    fun assignPot(potType: PotType?, ball: DomainBall = NOBALL(), action: PotAction = FIRST) = viewModelScope.launch {
        if (!isUpdateInProgress) {
            isUpdateInProgress = true
            if (potType == null) handleUndo()
            else {
                val pot = potType.getPotFromType(ball, action, dataStoreRepository.getShotType())
                if (handlePotExceptionsBefore(pot)) isUpdateInProgress = false
                else {
                    handlePot(
                        if (pot.ball is FREEBALL) FREE(
                            ball = FREEBALL(points = pot.ball.points),
                            shotType = dataStoreRepository.getShotType()
                        ) else pot
                    )
                    handlePotExceptionsPost(pot)
                }
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
        pot.potId = MatchSettings.uniqueId
        handlePotFreeballToggle(pot.potType)
        ballStack.onPot(pot.potType, pot.potAction)
        frameStack.onPot(pot, score[MatchSettings.crtPlayer].pointsWithoutReturn, score)
        score.calculatePoints(pot, 1, ballStack.foulValue())
        val actionLog = pot.getActionLog("handlePot()", ballStack.lastOrNull()?.ballType, frameStack.size)
        MatchSettings.crtPlayer = MatchSettings.getCrtPlayerFromPotAction(pot.potAction)
        onEventFrameUpdated(actionLog)
    }

    private fun handlePotExceptionsPost(pot: DomainPot) {
        if (MatchSettings.counterRetake == 3) viewModelScope.launch {
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
        MatchSettings.crtPlayer = frameStack.last().player
        val pot = frameStack.removeLastPotFromFrameStack(score)
        val actionLog = pot.getActionLog("HandleUndo()", ballStack.lastOrNull()?.ballType, frameStack.size)
        ballStack.onUndo(pot.potType, pot.potAction, frameStack)
        handleUndoFreeballToggle(pot.potType, frameStack.lastPotType())
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

    fun emailLogs(context: Context) = viewModelScope.launch {
        val logs = gameRepository.getDomainActionLogs().toString()
        val json = Gson().toJson(gameRepository.getDomainActionLogs())
        val body = "${MatchSettings.getAsText()} \n\n $json \n\n $logs"
        Timber.e(json)
        context.sendEmail(arrayOf(BuildConfig.ADMIN_EMAIL), Constants.EMAIL_SUBJECT_LOGS, body)
    }

    fun onMenuItemSelected(menuItem: MenuItem) {
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

    private fun handlePotFreeballToggle(potType: PotType) { // Control freeball visibility and selection
        when (potType) {
            TYPE_FREE_ACTIVE -> dataStoreRepository.savePrefAndSwitchBoolValue(K_BOOL_TOGGLE_FREEBALL)
            TYPE_HIT, TYPE_FREE, TYPE_MISS, TYPE_SAFE, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> dataStoreRepository.savePrefs(
                K_BOOL_TOGGLE_FREEBALL, false)
            TYPE_ADDRED, TYPE_REMOVE_RED, TYPE_REMOVE_COLOR, TYPE_LAST_BLACK_FOULED, TYPE_RESPOT_BLACK, TYPE_FOUL_ATTEMPT -> {}
        }
    }

    private fun handleUndoFreeballToggle(potType: PotType, lastPotType: PotType?) {
        when (potType) {
            TYPE_FREE -> dataStoreRepository.savePrefAndSwitchBoolValue(K_BOOL_TOGGLE_FREEBALL)
            TYPE_FREE_ACTIVE -> dataStoreRepository.savePrefs(K_BOOL_TOGGLE_FREEBALL, false)
            TYPE_SAFE, TYPE_MISS, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> when (lastPotType) {
                TYPE_FREE_ACTIVE -> dataStoreRepository.savePrefAndSwitchBoolValue(K_BOOL_TOGGLE_FREEBALL)
                else -> dataStoreRepository.savePrefs(K_BOOL_TOGGLE_FREEBALL, false)
            }
            TYPE_HIT, TYPE_ADDRED, TYPE_REMOVE_RED, TYPE_REMOVE_COLOR, TYPE_LAST_BLACK_FOULED, TYPE_RESPOT_BLACK, TYPE_FOUL_ATTEMPT -> {}
        }
    }
}