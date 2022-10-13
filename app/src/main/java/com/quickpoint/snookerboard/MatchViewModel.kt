package com.quickpoint.snookerboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.domain.DomainMatchInfo.RULES
import com.quickpoint.snookerboard.domain.MatchState.*
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.*
import kotlinx.coroutines.launch
import timber.log.Timber

class MatchViewModel(
    private val app: Application,
    private val snookerRepository: SnookerRepository,
) : AndroidViewModel(app) {

    // Variables
    val isFrameLoadingToggle = snookerRepository.isFrameLoadingToggle
    val crtFrame = snookerRepository.crtFrame

    // Observables
    private val _eventRules = MutableLiveData(RULES)
    val eventRules: LiveData<RULES> = _eventRules
    fun updateRules(_unused: Int) {
        _eventRules.value = RULES
    }

    private val _displayFrame = MutableLiveData<DomainFrame>()
    val displayFrame: LiveData<DomainFrame> = _displayFrame
    fun updateFrameInfo(domainFrame: DomainFrame) {
        updateRules(-1)
        _displayFrame.value = domainFrame
        _displayFrame.value?.apply {
            if (isFrameEnded()) assignEventMatchAction(FRAME_ENDED_DIALOG)
        }
        Timber.i(app.resources.getString(R.string.helper_update_frame_info))
    }

    private val _eventMatchAction = MutableLiveData<Event<MatchAction>>()
    val eventMatchAction: LiveData<Event<MatchAction>> = _eventMatchAction
    fun assignEventMatchAction(matchAction: MatchAction) { // Such as cancelling a match, ending a frame, etc.
        _eventMatchAction.value = Event(matchAction)
    }

    // Db Actions
    fun startNewMatch() {
        Timber.i("startNewMatch()")
        RULES.state = IN_PROGRESS
        assignEventMatchAction(MATCH_START_NEW)
    }

    fun loadMatchAPointToCrtFrame() = viewModelScope.launch { // Actioned from main menu to load the match
        Timber.i("loadMatchPartAPointToCurrentFrame()")
        snookerRepository.searchByCount(RULES.frameCount)
        snookerRepository.loadFrameToggle(true)
    }

    fun loadMatchCDeleteCrtFrame() = viewModelScope.launch { // After loading the game, will delete the current frame from the db
        Timber.i("loadMatchPartCDeleteCurrentFrame()")
        snookerRepository.loadFrameToggle(false)
        snookerRepository.deleteCurrentFrame(RULES.frameCount)
    }

    fun saveFrameToDb(matchAction: MatchAction) = viewModelScope.launch { // When decided on match end from a generic dialog
        Timber.i("saveAndResetFrame(): $matchAction")
        snookerRepository.saveCurrentFrame(displayFrame.value!!)
        if (matchAction == GO_TO_POST_MATCH) RULES.state = POST_MATCH
        assignEventMatchAction(matchAction)
    }

    fun saveMatchOnSavedInstance() = viewModelScope.launch { // When instance state is saved, save frame only if match is in progress
        if (RULES.state == IN_PROGRESS) snookerRepository.saveCurrentFrame(_displayFrame.value!!)
    }

    fun deleteMatchFromDb() = viewModelScope.launch { // When starting a new match or cancelling an existing match
        RULES.state = IDLE
        snookerRepository.deleteCurrentMatch()
    }

    // Helper methods
    fun queryEndFrameOrMatch(matchAction: MatchAction): MatchAction { // When actioned from options menu or if last ball has been potted
        Timber.i("queryEndFrameOrMatch($matchAction)")
        return if (_displayFrame.value!!.isMatchEnding()) { // If the frame would push player to win, assign a MATCH ending action
            if (displayFrame.value!!.isFrameOver()) MATCH_ENDED
            else MATCH_TO_END_DIALOG
        } else when { // Else assign a match action for a MATCH end query or else assign a FRAME ending action
            matchAction == MATCH_ENDED_DIALOG -> MATCH_TO_END_DIALOG
            displayFrame.value!!.isFrameOver() -> FRAME_ENDED
            else -> FRAME_TO_END_DIALOG
        }
    }
}