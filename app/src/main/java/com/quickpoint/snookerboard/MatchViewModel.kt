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
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.*
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
        if (_displayFrame.value?.isFrameEnded() == true) assignEventMatchAction(MatchAction.FRAME_ENDED_DIALOG)
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
        sharedPref.setMatchInProgress(true)
    }

    fun cancelMatch() { // When actioned from options menu
        Timber.i("cancelMatch()")
        RULES.resetRules()
        FREEBALLINFO.resetFreeball()
        resetMatch()
        sharedPref.setMatchInProgress(false)
    }

    private fun resetMatch() { // When starting a new match or cancelling an existing match
        Timber.i("resetMatch()")
        score.resetMatchScore()
        assignEventMatchAction(MatchAction.FRAME_RESET)
        viewModelScope.launch {
            snookerRepository.deleteCurrentMatch()
        }
    }

    fun saveAndStartNewFrame() = score.apply { // When confirmed from generic dialog or if decided on match end from a generic dialog
        Timber.i("saveAndStartNewFrame()")
        getWinner().addMatchPointAndAssignFrameId()
        assignEventMatchAction(MatchAction.FRAME_SAVE)
        viewModelScope.launch {
            snookerRepository.saveCurrentFrame(displayFrame.value!!)
            assignEventMatchAction(MatchAction.FRAME_RESET)
            RULES.frameCount += 1
        }
    }

    // Frame & Match ending
    fun queryEndFrameOrMatch(matchAction: MatchAction): MatchAction { // When actioned from options menu or if last ball has been potted
        Timber.i("queryEndFrameOrMatch($matchAction)")
        return if (score.isMatchEnding(RULES.frames)) { // If the frame would push player to win, assign a MATCH ending action
            if (displayFrame.value!!.isFrameOver()) MatchAction.MATCH_ENDED
            else MatchAction.MATCH_TO_END_DIALOG
        } else when { // Else assign a match action for a MATCH end query or else assign a FRAME ending action
            matchAction == MatchAction.MATCH_ENDED_DIALOG -> MatchAction.MATCH_TO_END_DIALOG
            displayFrame.value!!.isFrameOver() -> MatchAction.FRAME_ENDED
            else -> MatchAction.FRAME_TO_END_DIALOG
        }
    }

    fun matchEnded(matchAction: MatchAction) { // When the match ends, reset frame only so you can access the data for the stats screen - temp solution until firebase is created
        Timber.i("matchEnded()")
        if (matchAction == MatchAction.MATCH_ENDED_DISCARD_FRAME_DIALOG) assignEventMatchAction(MatchAction.FRAME_RESET)
        else saveAndStartNewFrame() // TEMP - the last frame should be saved, but a new one should not be started
        sharedPref.setMatchInProgress(false)
    }

    // Separate threads
    suspend fun saveMatch() = sharedPref.apply { // When the back button is pressed or when instance state is saved
        Timber.i("saveMatch()")
        savePrefNames(app)
        if (score.hasMatchStarted()) {
            snookerRepository.saveCurrentFrame(displayFrame.value!!)
            savePrefRulesAndFreeball(app)
            setMatchInProgress(true)
        } else {
            setMatchInProgress(false)
        }
    }
}