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
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.MatchState
import com.quickpoint.snookerboard.domain.objects.MatchState.*
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*
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
    private val dataStore: DataStore
) : AndroidViewModel(app) {

    private val _eventSharedFlow = MutableSharedFlow<ScreenEvents>()
    val eventSharedFlow = _eventSharedFlow.asSharedFlow()
    fun onEmit(action: MatchAction) = viewModelScope.launch {
        _eventSharedFlow.emit(when(action) {
            SNACK_HANDICAP_FRAME_LIMIT -> ScreenEvents.ShowSnackbar(app.getString(R.string.snack_f_rules_handicap_frame_limit))
            SNACK_HANDICAP_MATCH_LIMIT -> ScreenEvents.ShowSnackbar(app.getString(R.string.snack_f_rules_handicap_match_limit))
            SNACK_PLAYER_NAME_INCOMPLETE -> ScreenEvents.ShowSnackbar(app.getString(R.string.snack_f_rules_no_player))
            SNACK_NO_STARTING_PLAYER -> ScreenEvents.ShowSnackbar(app.getString(R.string.snack_f_rules_select_no_first))
            NAV_TO_PLAY -> ScreenEvents.Navigate(Screen.RulesScreen.route)
            NAV_TO_GAME -> {
                Timber.i(Settings.getAsText())
                ScreenEvents.Navigate(Screen.GameScreen.route)
            }
            else -> ScreenEvents.ShowSnackbar("")
        })
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

    // Update toggles, save changes in DataStore and notify the composable to recompose
    private val _eventToggleChange = MutableLiveData<Event<Unit>>()
    val eventToggleChange: LiveData<Event<Unit>> = _eventToggleChange
    fun onToggleChange(key: String?) {
        app.applicationContext.vibrateOnce()
        when (key) {
            K_INT_TOGGLE_ADVANCED_RULES -> dataStore.savePreferences(key, Toggle.AdvancedRules.toggleEnabled())
            K_INT_TOGGLE_ADVANCED_STATISTICS -> dataStore.savePreferences(key, Toggle.AdvancedRules.toggleEnabled())
            K_INT_TOGGLE_ADVANCED_BREAKS -> dataStore.savePreferences(key, Toggle.AdvancedRules.toggleEnabled())
        }
        _eventToggleChange.value = Event(Unit)
    }

    private val _matchState = MutableLiveData<Event<MatchState>>()
    val matchState: LiveData<Event<MatchState>> = _matchState
    fun updateState(matchState: MatchState) = app.sharedPref().apply {
        if (matchState == NONE) loadPref() else Settings.setMatchState(matchState)
        if (matchState in listOf(RULES_PENDING, GAME_SAVED)) savePref() else updateState()
        if (matchState != NONE) _matchState.value = Event(Settings.matchState)
    }

    // Separate threads
    private val _storedFrame = MutableLiveData<Event<DomainFrame>>()
    val storedFrame: LiveData<Event<DomainFrame>> = _storedFrame
    fun loadMatchIfSaved() = viewModelScope.launch { // If db is empty reset rules, otherwise load the most recent frame
        Timber.i("loadMatchIfSaved()")
        updateState(NONE) // Load shared preferences
        snookerRepository.getCrtFrame().let { crtFrame ->
            when (Settings.matchState) {
                GAME_IN_PROGRESS -> deleteMatchFromDb() // Helps reset the match when debug-reinstalling from android studio
                GAME_SAVED, SUMMARY -> {
                    if (crtFrame == null) updateState(RULES_IDLE) // Helps reset the app when something went wrong after previous reinstall
                    else {
                        _storedFrame.value = Event(crtFrame.asDomainFrame())
                        updateState(if (Settings.matchState == GAME_SAVED) GAME_IN_PROGRESS else SUMMARY)
                    }
                }

                RULES_IDLE, RULES_PENDING -> updateState(RULES_IDLE) // Idle helps reset the match when debug-reinstalling from android studio
                else -> Timber.e("No implementation for state ${Settings.matchState} at this point")
            }
        }
    }

    fun deleteCrtFrameFromDb() = viewModelScope.launch {
        snookerRepository.deleteCurrentFrame(Settings.crtFrame)
    }

    fun deleteMatchFromDb() = viewModelScope.launch { // When starting a new match or cancelling an existing match
        updateState(RULES_IDLE)
        Settings.resetRules()
        snookerRepository.deleteCurrentMatch()
    }

    fun emailLogs() = viewModelScope.launch {
        val logs = snookerRepository.getDebugFrameActionList().toString()
        val json = Gson().toJson(snookerRepository.getDebugFrameActionList())
        val body = "${Settings.getAsText()} \n\n $json \n\n $logs"
        Timber.e(json)
        app.sendEmail(arrayOf(BuildConfig.ADMIN_EMAIL), EMAIL_SUBJECT_LOGS, body)
    }
}