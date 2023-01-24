package com.quickpoint.snookerboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.quickpoint.snookerboard.compose.navigation.Screen
import com.quickpoint.snookerboard.database.asDomainFrame
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.MatchState.*
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.domain.objects.getAsText
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
    private val dataStore: DataStore,
) : AndroidViewModel(app) {

    fun turnOffSplashScreen(msDelay: Long = 0) = viewModelScope.launch {
        delay(msDelay)
        _keepSplashScreen.value = false
    }
    private val _keepSplashScreen = MutableStateFlow(true)
    val keepSplashScreen = _keepSplashScreen.asStateFlow()

    fun loadMatchIfSaved() = viewModelScope.launch { // If db is empty reset rules, otherwise load the most recent frame
        dataStore.loadPreferences()
        snookerRepository.getCrtFrame().let { crtFrame ->
            if (crtFrame == null) Settings.matchState = RULES_IDLE // Helps reset the app when something went wrong after previous reinstall
            else _storedFrame.value = Event(crtFrame.asDomainFrame())
            onEmit(when (Settings.matchState) {
                RULES_IDLE, RULES_PENDING -> NAV_TO_PLAY
                GAME_IN_PROGRESS, GAME_SAVED -> NAV_TO_GAME
                else -> NAV_TO_POST_MATCH
            }
            )
        }
    }
    private val _storedFrame = MutableLiveData<Event<DomainFrame>>()
    val storedFrame: LiveData<Event<DomainFrame>> = _storedFrame

    fun onEmit(action: MatchAction) = viewModelScope.launch {
        _eventSharedFlow.emit(
            when (action) {
                SNACK_HANDICAP_FRAME_LIMIT -> ScreenEvents.ShowSnackbar(app.getString(R.string.snack_f_rules_handicap_frame_limit))
                SNACK_HANDICAP_MATCH_LIMIT -> ScreenEvents.ShowSnackbar(app.getString(R.string.snack_f_rules_handicap_match_limit))
                SNACK_PLAYER_NAME_INCOMPLETE -> ScreenEvents.ShowSnackbar(app.getString(R.string.snack_f_rules_no_player))
                SNACK_NO_STARTING_PLAYER -> ScreenEvents.ShowSnackbar(app.getString(R.string.snack_f_rules_select_no_first))
                NAV_TO_PLAY -> {
                    Settings.matchState = RULES_IDLE
                    ScreenEvents.Navigate(Screen.Rules.route)
                }
                NAV_TO_GAME -> {
                    Settings.matchState = GAME_IN_PROGRESS
                    Timber.i(Settings.getAsText())
                    ScreenEvents.Navigate(Screen.Game.route)
                }
                NAV_TO_SUMMARY -> {
                    Settings.matchState = SUMMARY
                    ScreenEvents.Navigate(Screen.Summary.route)
                }
                NAV_TO_DIALOG_GENERIC -> ScreenEvents.Navigate(Screen.DialogGeneric.route)
                else -> ScreenEvents.ShowSnackbar("")
            }
        )
    }
    private val _eventSharedFlow = MutableSharedFlow<ScreenEvents>()
    val eventSharedFlow = _eventSharedFlow.asSharedFlow()

    fun onToggleChange(key: String?) { // Update toggles, save changes in DataStore and notify the composable to recompose
        app.applicationContext.vibrateOnce()
        when (key) {
            K_BOOL_TOGGLE_ADVANCED_RULES -> dataStore.savePreferences(key, Toggle.AdvancedRules.toggleEnabled())
            K_BOOL_TOGGLE_ADVANCED_STATISTICS -> dataStore.savePreferences(key, Toggle.AdvancedRules.toggleEnabled())
            K_BOOL_TOGGLE_ADVANCED_BREAKS -> dataStore.savePreferences(key, Toggle.AdvancedRules.toggleEnabled())
        }
        _eventToggleChange.value = Event(Unit)
    }
    private val _eventToggleChange = MutableLiveData<Event<Unit>>()
    val eventToggleChange: LiveData<Event<Unit>> = _eventToggleChange

    fun deleteCrtFrameFromDb() = viewModelScope.launch {
        snookerRepository.deleteCurrentFrame(Settings.crtFrame)
    }

    fun deleteMatchFromDb() = viewModelScope.launch { // When starting a new match or cancelling an existing match
        Settings.matchState = RULES_IDLE
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