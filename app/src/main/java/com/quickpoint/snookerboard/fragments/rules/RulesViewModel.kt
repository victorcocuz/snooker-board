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
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.K_INT_MATCH_HANDICAP_FRAME
import com.quickpoint.snookerboard.utils.K_INT_MATCH_HANDICAP_MATCH
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class RulesViewModel(
    private val app: Application,
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
        Player01.setPlayerName(key, value)
        Player02.setPlayerName(key, value)
        _eventPlayerNameChange.value = Event(Unit)
    }

    // Update match settings, save changes in DataStore and notify the composable to recompose
    private val _eventMatchSettingsChange = MutableLiveData<Event<Unit>>()
    val eventMatchSettingsChange: LiveData<Event<Unit>> = _eventMatchSettingsChange
    fun onMatchSettingsChange(key: String, value: Int) = Settings.apply {
        when {
            key == K_INT_MATCH_HANDICAP_FRAME && (handicapFrame + value).absoluteValue >= availableReds * 8 + 27 -> onEmit(SNACK_HANDICAP_FRAME_LIMIT)
            key == K_INT_MATCH_HANDICAP_MATCH && (handicapMatch + value).absoluteValue == availableFrames -> onEmit(SNACK_HANDICAP_MATCH_LIMIT)
            else -> updateSettings(key, value)
        }
        _eventMatchSettingsChange.value = Event(Unit)
    }
}