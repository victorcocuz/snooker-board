package com.quickpoint.snookerboard

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.quickpoint.snookerboard.compose.navigation.Screen
import com.quickpoint.snookerboard.database.asDomainFrame
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.domain.DomainPlayer
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.EMAIL_SUBJECT_LOGS
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS
import com.quickpoint.snookerboard.utils.MatchState
import com.quickpoint.snookerboard.utils.MatchState.GAME_IN_PROGRESS
import com.quickpoint.snookerboard.utils.MatchState.GAME_SAVED
import com.quickpoint.snookerboard.utils.MatchState.NONE
import com.quickpoint.snookerboard.utils.MatchState.RULES_IDLE
import com.quickpoint.snookerboard.utils.MatchState.RULES_PENDING
import com.quickpoint.snookerboard.utils.MatchState.SUMMARY
import com.quickpoint.snookerboard.utils.MatchToggleType
import com.quickpoint.snookerboard.utils.MatchToggleType.ADVANCED_BREAKS
import com.quickpoint.snookerboard.utils.MatchToggleType.ADVANCED_RULES
import com.quickpoint.snookerboard.utils.MatchToggleType.ADVANCED_STATISTICS
import com.quickpoint.snookerboard.utils.Toggle.AdvancedBreaks
import com.quickpoint.snookerboard.utils.Toggle.AdvancedRules
import com.quickpoint.snookerboard.utils.Toggle.AdvancedStatistics
import com.quickpoint.snookerboard.utils.loadPref
import com.quickpoint.snookerboard.utils.savePref
import com.quickpoint.snookerboard.utils.sendEmail
import com.quickpoint.snookerboard.utils.sharedPref
import com.quickpoint.snookerboard.utils.updateState
import com.quickpoint.snookerboard.utils.vibrateOnce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber


class MainViewModel(
    private val app: Application,
    private val snookerRepository: SnookerRepository,
) : AndroidViewModel(app) {

    private val _snackBarMessageFlow = MutableSharedFlow<String>()
    val snackbarMessageFlow = _snackBarMessageFlow.asSharedFlow()
    fun onNewSnackbar(message: String) = viewModelScope.launch {
        _snackBarMessageFlow.emit(message)
    }

    private val _eventSharedFlow = MutableSharedFlow<ScreenEvents>()
    val eventSharedFlow = _eventSharedFlow.asSharedFlow()

    sealed class ScreenEvents {
        data class ShowSnackbar(val message: String) : ScreenEvents()
        data class Navigate(val route: String) : ScreenEvents()
    }

    fun startMatchQuery() = viewModelScope.launch {
        _eventSharedFlow.emit(
            when {
                DomainPlayer.PLAYER01.hasNoName() || DomainPlayer.PLAYER02.hasNoName() -> ScreenEvents.ShowSnackbar("No Player")
                (SETTINGS.startingPlayer < 0) -> ScreenEvents.ShowSnackbar("No First")
                else -> ScreenEvents.Navigate(Screen.GameScreen.route)
            }
        )
    }


    // Observables
    private val _keepSplashScreen = MutableStateFlow(true)
    val keepSplashScreen = _keepSplashScreen.asStateFlow()
    fun transitionToFragment(fragment: Fragment, ms: Long) = viewModelScope.launch {
        fragment.startPostponedEnterTransition()
        delay(ms)
        _keepSplashScreen.value = false
        Timber.i("transitionToFragment(): ${fragment.javaClass.simpleName}")
    }

    private val _matchToggleEvent = MutableLiveData<Event<Unit>>()
    val matchToggleEvent: LiveData<Event<Unit>> = _matchToggleEvent

    fun updateMatchToggle(matchToggleType: MatchToggleType?) {
        Timber.e("updateToggle")
        app.applicationContext.vibrateOnce()
        matchToggleType?.let {
            when (it) {
                ADVANCED_RULES -> AdvancedRules.toggleEnabled()
                ADVANCED_STATISTICS -> AdvancedStatistics.toggleEnabled()
                ADVANCED_BREAKS -> AdvancedBreaks.toggleEnabled()
            }
        }
        _matchToggleEvent.value = Event(Unit)
        app.sharedPref().savePref()
    }

    private val _matchState = MutableLiveData<Event<MatchState>>()
    val matchState: LiveData<Event<MatchState>> = _matchState
    fun updateState(matchState: MatchState) = app.sharedPref().apply {
        if (matchState == NONE) loadPref() else SETTINGS.setMatchState(matchState)
        if (matchState in listOf(RULES_PENDING, GAME_SAVED)) savePref() else updateState()
        if (matchState != NONE) _matchState.value = Event(SETTINGS.matchState)
    }

    // Separate threads
    private val _storedFrame = MutableLiveData<Event<DomainFrame>>()
    val storedFrame: LiveData<Event<DomainFrame>> = _storedFrame
    fun loadMatchIfSaved() = viewModelScope.launch { // If db is empty reset rules, otherwise load the most recent frame
        Timber.i("loadMatchIfSaved()")
        updateState(NONE) // Load shared preferences
        updateMatchToggle(null)
        snookerRepository.getCrtFrame().let { crtFrame ->
            when (SETTINGS.matchState) {
                GAME_IN_PROGRESS -> deleteMatchFromDb() // Helps reset the match when debug-reinstalling from android studio
                GAME_SAVED, SUMMARY -> {
                    if (crtFrame == null) updateState(RULES_IDLE) // Helps reset the app when something went wrong after previous reinstall
                    else {
                        _storedFrame.value = Event(crtFrame.asDomainFrame())
                        updateState(if (SETTINGS.matchState == GAME_SAVED) GAME_IN_PROGRESS else SUMMARY)
                    }
                }

                RULES_IDLE, RULES_PENDING -> updateState(RULES_IDLE) // Idle helps reset the match when debug-reinstalling from android studio
                else -> Timber.e("No implementation for state ${SETTINGS.matchState} at this point")
            }
        }
    }

    fun deleteCrtFrameFromDb() = viewModelScope.launch {
        snookerRepository.deleteCurrentFrame(SETTINGS.crtFrame)
    }

    fun deleteMatchFromDb() = viewModelScope.launch { // When starting a new match or cancelling an existing match
        updateState(RULES_IDLE)
        SETTINGS.resetRules()
        snookerRepository.deleteCurrentMatch()
    }

    fun emailLogs() = viewModelScope.launch {
        val logs = snookerRepository.getDebugFrameActionList().toString()
        val json = Gson().toJson(snookerRepository.getDebugFrameActionList())
        val body = "${SETTINGS.getAsText()} \n\n $json \n\n $logs"
        Timber.e(json)
        app.sendEmail(arrayOf(BuildConfig.ADMIN_EMAIL), EMAIL_SUBJECT_LOGS, body)
    }
}