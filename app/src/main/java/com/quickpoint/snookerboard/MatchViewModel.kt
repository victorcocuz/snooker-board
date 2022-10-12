package com.quickpoint.snookerboard

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo.*
import com.quickpoint.snookerboard.domain.DomainMatchInfo.*
import com.quickpoint.snookerboard.domain.MatchState.*
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*
import kotlinx.coroutines.launch
import timber.log.Timber

class MatchViewModel(
    private val app: Application,
    private val snookerRepository: SnookerRepository,
    private val sharedPref: SharedPreferences
) : AndroidViewModel(app) {

    // Variables
    private var score: CurrentScore = CurrentScore.SCORE01
    val isUpdateFrame = snookerRepository.isUpdateFrame
    val crtFrame = snookerRepository.crtFrame

    // Observables
    private val _eventRules = MutableLiveData(RULES)
    val eventRules: LiveData<RULES> = _eventRules
    fun updateRules(_unused: Int) {
        _eventRules.value = RULES
    }

    private val _displayFrame = MutableLiveData<DomainFrame>()
    val displayFrame: LiveData<DomainFrame> = _displayFrame
    fun updateFrameInfo(ballStack: MutableList<DomainBall>, frameStack: MutableList<DomainBreak>) {
        updateRules(-1)
        score = score.getCrtPlayerFromRules()
        _displayFrame.value = DomainFrame(
            RULES.frameCount,
            ballStack,
            mutableListOf(
                score.getFirst().asDomainPlayerScore(),
                score.getSecond().asDomainPlayerScore()
            ),
            frameStack,
            RULES.frameMax
        )
        _displayFrame.value?.apply {
            if (isFrameEnded()) assignEventMatchAction(FRAME_ENDED_DIALOG)
            if (RULES.state == IDLE && isMatchInProgress()) RULES.state = IN_PROGRESS
            if (!isMatchInProgress()) RULES.state = IDLE
        }
        Timber.i(app.resources.getString(R.string.helper_update_frame_info))
    }

    private val _eventMatchAction = MutableLiveData<Event<MatchAction>>()
    val eventMatchAction: LiveData<Event<MatchAction>> = _eventMatchAction
    fun assignEventMatchAction(matchAction: MatchAction) { // Such as cancelling a match, ending a frame, etc.
        _eventMatchAction.value = Event(matchAction)
    }

    // Game Fragment actions
    fun loadMatchAPointToCrtFrame() = viewModelScope.launch { // Actioned from main menu to load the match
        Timber.i("loadMatchPartAPointToCurrentFrame()")
        sharedPref.loadPrefRulesAndFreeball(app)
        snookerRepository.searchByCount(sharedPref.getCurrentFrame(app))
        snookerRepository.onFrameUpdate(true)
    }

    fun loadMatchCDeleteCrtFrame() = viewModelScope.launch { // After loading the game, will delete the current frame from the db
        Timber.i("loadMatchPartCDeleteCurrentFrame()")
        snookerRepository.onFrameUpdate(false)
        snookerRepository.deleteCurrentFrame(sharedPref.getInt(app.getString(R.string.sp_match_frame_count), 0))
    }

    fun startNewMatch() { // When actioned from main menu
        Timber.i("startNewMatch()")
        resetMatch()
    }

    fun cancelMatch() { // When actioned from options menu
        Timber.i("cancelMatch()")
        RULES.resetRules()
        resetMatch()
    }

    private fun resetMatch() { // When starting a new match or cancelling an existing match
        Timber.i("resetMatch()")
        FREEBALLINFO.resetFreeball()
        score.resetMatchScore()
        viewModelScope.launch {
            snookerRepository.deleteCurrentMatch()
        }
        assignEventMatchAction(FRAME_RESET)
        RULES.state = IDLE
    }

    fun saveAndResetFrame(matchAction: MatchAction?) { // When confirmed from generic dialog or if decided on match end from a generic dialog
        Timber.i("saveAndResetFrame(): $matchAction")
        viewModelScope.launch {
            snookerRepository.saveCurrentFrame(displayFrame.value!!)
            assignEventMatchAction(FRAME_RESET)
            RULES.frameCount += 1
            if (matchAction == GO_TO_POST_GAME) {
                RULES.state = POST_MATCH
                assignEventMatchAction(GO_TO_POST_GAME)
            }
        }
    }

    // Frame & Match ending
    fun queryEndFrameOrMatch(matchAction: MatchAction): MatchAction { // When actioned from options menu or if last ball has been potted
        Timber.i("queryEndFrameOrMatch($matchAction)")
        return if (score.isMatchEnding(RULES.frames)) { // If the frame would push player to win, assign a MATCH ending action
            if (displayFrame.value!!.isFrameOver()) MATCH_ENDED
            else MATCH_TO_END_DIALOG
        } else when { // Else assign a match action for a MATCH end query or else assign a FRAME ending action
            matchAction == MATCH_ENDED_DIALOG -> MATCH_TO_END_DIALOG
            displayFrame.value!!.isFrameOver() -> FRAME_ENDED
            else -> FRAME_TO_END_DIALOG
        }
    }

    // Separate threads
    fun saveMatch() = sharedPref.apply { // When the back button is pressed or when instance state is saved
        savePrefStateAndNames(app)
        if (RULES.state != IDLE) savePrefRulesAndFreeball(app)
        if (RULES.state == IN_PROGRESS) viewModelScope.launch {
            snookerRepository.saveCurrentFrame(displayFrame.value!!)
        }
    }
}