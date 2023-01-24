package com.quickpoint.snookerboard.fragments.rules

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.ScreenEvents
import com.quickpoint.snookerboard.domain.objects.DomainPlayer.Player01
import com.quickpoint.snookerboard.domain.objects.DomainPlayer.Player02
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings.updateSettings
import com.quickpoint.snookerboard.domain.objects.handicapFrameExceedsLimit
import com.quickpoint.snookerboard.domain.objects.handicapMatchExceedsLimit
import com.quickpoint.snookerboard.domain.objects.setPlayerName
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RulesViewModel(
    private val app: Application,
) : AndroidViewModel(app) {

    private fun onEmit(matchAction: MatchAction) = viewModelScope.launch {
        _eventSharedFlow.emit(ScreenEvents.SnookerEvent(matchAction))
    }
    private val _eventSharedFlow = MutableSharedFlow<ScreenEvents>()
    val eventSharedFlow = _eventSharedFlow.asSharedFlow()

    fun startMatchQuery() = onEmit(
        when {
            Player01.hasNoName() || Player02.hasNoName() -> SNACK_PLAYER_NAME_INCOMPLETE
            (Settings.startingPlayer < 0) -> SNACK_NO_STARTING_PLAYER
            else -> NAV_TO_GAME
        }
    )

    // Update settings, save changes in DataStore and notify the composable to recompose
    fun onPlayerNameChange(key: String, value: String) = viewModelScope.launch{ // Update players names, save changes in DataStore and notify the composable to recompose
        Player01.setPlayerName(key, value)
        Player02.setPlayerName(key, value)
        _eventPlayerNameChange.emit(Event(Unit))
    }
    private val _eventPlayerNameChange = MutableSharedFlow<Event<Unit>>()
    val eventPlayerNameChange = _eventPlayerNameChange.asSharedFlow()

    fun onMatchSettingsChange(key: String, value: Int) = viewModelScope.launch {
        when {
            Settings.handicapFrameExceedsLimit(key, value) -> onEmit(SNACK_HANDICAP_FRAME_LIMIT)
            Settings.handicapMatchExceedsLimit(key, value) -> onEmit(SNACK_HANDICAP_MATCH_LIMIT)
            else -> updateSettings(key, value)
        }
        _eventMatchSettingsChange.emit(Event(Unit))
    }
    private val _eventMatchSettingsChange = MutableSharedFlow<Event<Unit>>()
    val eventMatchSettingsChange = _eventMatchSettingsChange.asSharedFlow()
}