package com.quickpoint.snookerboard.fragments.rules

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.ScreenEvents
import com.quickpoint.snookerboard.domain.objects.DomainPlayer.Player01
import com.quickpoint.snookerboard.domain.objects.DomainPlayer.Player02
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.setPlayerName
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class RulesViewModel(
    private val app: Application,
    private val dataStore: DataStore,
) : AndroidViewModel(app) {
    // Observables
    private val _eventRulesAction = MutableLiveData<Event<MatchAction>>()
    val eventRulesAction: LiveData<Event<MatchAction>> = _eventRulesAction

    private val _eventSharedFlow = MutableSharedFlow<ScreenEvents>()
    val eventSharedFlow = _eventSharedFlow.asSharedFlow()
    private fun onEmit(matchAction: MatchAction) = viewModelScope.launch {
        _eventSharedFlow.emit(ScreenEvents.SnookerEvent(matchAction))
    }

    fun startMatchQuery() = onEmit(
        when {
            Player01.hasNoName() || Player02.hasNoName() -> SNACK_PLAYER_NAME_INCOMPLETE
            (Settings.startingPlayer < 0) -> SNACK_NO_STARTING_PLAYER
            else -> NAV_TO_GAME
        }
    )

    // Update players names, save changes in DataStore and notify the composable to recompose
    private val _eventPlayerNameChange = MutableLiveData<Event<Unit>>()
    val eventPlayerNameChange: LiveData<Event<Unit>> = _eventPlayerNameChange
    fun onPlayerNameChange(key: String, value: String) {
        setPlayerName(key, value)
        dataStore.savePreferences(key, value)
        _eventPlayerNameChange.value = Event(Unit)
    }

    // Update match settings, save changes in DataStore and notify the composable to recompose
    private val _eventMatchSettingsChange = MutableLiveData<Event<Unit>>()
    val eventMatchSettingsChange: LiveData<Event<Unit>> = _eventMatchSettingsChange
    fun onMatchSettingsChange(key: String, value: Int) = Settings.apply {
        when (key) {
            K_INT_MATCH_STARTING_PLAYER -> dataStore.savePreferences(key, setSettingsValue(key, value))
            K_INT_MATCH_AVAILABLE_FRAMES -> {
                dataStore.savePreferences(key, setSettingsValue(key, value))
                dataStore.savePreferences(K_INT_MATCH_HANDICAP_MATCH, setSettingsValue(K_INT_MATCH_HANDICAP_MATCH, 0))
            }
            K_INT_MATCH_AVAILABLE_REDS -> {
                dataStore.savePreferences(key, setSettingsValue(key, value))
                dataStore.savePreferences(K_INT_MATCH_HANDICAP_FRAME, setSettingsValue(K_INT_MATCH_HANDICAP_FRAME, 0))
            }
            K_INT_MATCH_FOUL_MODIFIER -> dataStore.savePreferences(key, setSettingsValue(key, value))
            K_INT_MATCH_HANDICAP_FRAME -> {
                if ((handicapFrame + value).absoluteValue >= Settings.availableReds * 8 + 27) onEmit(SNACK_HANDICAP_FRAME_LIMIT)
                else dataStore.savePreferences(key, setSettingsValue(key, value))
            }
            K_INT_MATCH_HANDICAP_MATCH -> {
                if ((handicapMatch + value).absoluteValue == Settings.availableFrames) onEmit(SNACK_HANDICAP_MATCH_LIMIT)
                else dataStore.savePreferences(key, setSettingsValue(key, value))
            }
            else -> {} // Not Implemented
        }
        _eventMatchSettingsChange.value = Event(Unit)
    }
}