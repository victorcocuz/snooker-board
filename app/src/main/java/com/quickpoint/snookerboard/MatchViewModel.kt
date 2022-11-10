package com.quickpoint.snookerboard

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.domain.DomainMatchInfo.RULES
import com.quickpoint.snookerboard.domain.MatchState
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.getSharedPref
import com.quickpoint.snookerboard.utils.updateState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MatchViewModel(
    private val app: Application,
    private val snookerRepository: SnookerRepository,
) : AndroidViewModel(app) {

    // Variables
    suspend fun getCrtFrame() = snookerRepository.getCrtFrame()

    private val _keepSplashScreen = MutableStateFlow(true)
    val keepSplashScreen = _keepSplashScreen.asStateFlow()
    fun transitionToFragment(fragment: Fragment) = viewModelScope.launch {
        delay(200)
        fragment.startPostponedEnterTransition()
        _keepSplashScreen.value = false
    }

    fun updateState(matchState: MatchState) {
        RULES.setMatchState(matchState)
        app.getSharedPref().updateState()
        updateRules(-1)
    }

    // Observables
    private val _eventRules = MutableLiveData<RULES>()
    val eventRules: LiveData<RULES> = _eventRules
    fun updateRules(_unused: Int) {
        _eventRules.value = RULES
    }

    private val _displayFrame = MutableLiveData<DomainFrame>()
    val displayFrame: LiveData<DomainFrame> = _displayFrame
    fun updateFrameInfo(domainFrame: DomainFrame) {
        _displayFrame.value = domainFrame
        updateRules(-1)
        Timber.i(app.resources.getString(R.string.helper_update_frame_info))
    }

    private val _eventMatchAction = MutableLiveData<Event<MatchAction>>()
    val eventMatchAction: LiveData<Event<MatchAction>> = _eventMatchAction
    fun assignEventMatchAction(matchAction: MatchAction) { // Such as cancelling a match, ending a frame, etc.
        _eventMatchAction.value = Event(matchAction)
    }

    // Actions
    fun deleteCrtFrameFromDb() = viewModelScope.launch { // After loading the game, will delete the current frame from the db
        Timber.i("deleteCrtFrameFromDb()")
        snookerRepository.deleteCurrentFrame(RULES.frameCount)
    }

    fun saveFrameAndOrEndMatch(matchAction: MatchAction) = viewModelScope.launch { // When decided on match end from a generic dialog
        Timber.i("saveAndResetFrame(): $matchAction")
        when (matchAction) {
            FRAME_TO_END, FRAME_ENDED -> {
                snookerRepository.saveCurrentFrame(displayFrame.value!!)
                assignEventMatchAction(FRAME_START_NEW)
            }
            MATCH_ENDED_DISCARD_FRAME -> {
                assignEventMatchAction(NAV_TO_POST_MATCH)
            }
            else -> {
                snookerRepository.saveCurrentFrame(displayFrame.value!!)
                assignEventMatchAction(NAV_TO_POST_MATCH)
            }
        }
    }

    fun saveMatchOnSavedInstance() = viewModelScope.launch { // When instance state is saved, save frame only if match is in progress
        snookerRepository.saveCurrentFrame(_displayFrame.value!!)
    }

    fun deleteMatchFromDb() = viewModelScope.launch { // When starting a new match or cancelling an existing match
        snookerRepository.deleteCurrentMatch()
    }

    // Helper methods
    fun queryEndFrameOrMatch(matchAction: MatchAction): MatchAction { // When actioned from options menu or if last ball has been potted
        Timber.i("queryEndFrameOrMatch($matchAction)")
        return if (_displayFrame.value!!.isMatchEnding()) { // If the frame would push player to win, assign a MATCH ending action
            if (displayFrame.value!!.isFrameOver()) MATCH_ENDED
            else MATCH_TO_END
        } else when { // Else assign a match action for a MATCH end query or else assign a FRAME ending action
            matchAction == MATCH_ENDING_DIALOG -> MATCH_TO_END
            displayFrame.value!!.isFrameOver() -> FRAME_ENDED
            else -> FRAME_TO_END
        }
    }
}