package com.quickpoint.snookerboard.ui.fragments.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.core.ScreenEvents
import com.quickpoint.snookerboard.core.base.Event
import com.quickpoint.snookerboard.core.utils.MatchAction.*
import com.quickpoint.snookerboard.domain.repository.DataStoreRepository
import com.quickpoint.snookerboard.domain.utils.DomainPlayer.Player01
import com.quickpoint.snookerboard.domain.utils.DomainPlayer.Player02
import com.quickpoint.snookerboard.domain.utils.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.utils.handicapFrameExceedsLimit
import com.quickpoint.snookerboard.domain.utils.handicapMatchExceedsLimit
import com.quickpoint.snookerboard.domain.utils.setPlayerName
import com.quickpoint.snookerboard.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RulesViewModel @Inject constructor(val dataStoreRepository: DataStoreRepository): ViewModel() {

    private fun onEmit(screenEvent: ScreenEvents) = viewModelScope.launch {
        _eventSharedFlow.emit(screenEvent)
    }
    private val _eventSharedFlow = MutableSharedFlow<ScreenEvents>()
    val eventSharedFlow = _eventSharedFlow.asSharedFlow()

    fun startMatchQuery() = onEmit(
        when {
            Player01.hasNoName() || Player02.hasNoName() -> ScreenEvents.SnackEvent(SNACK_PLAYER_NAME_INCOMPLETE)
            (Settings.startingPlayer < 0) -> ScreenEvents.SnackEvent(SNACK_NO_STARTING_PLAYER)
            else -> ScreenEvents.Navigate(Screen.Game.route)
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
            Settings.handicapFrameExceedsLimit(key, value) -> onEmit(ScreenEvents.SnackEvent(SNACK_HANDICAP_FRAME_LIMIT))
            Settings.handicapMatchExceedsLimit(key, value) -> onEmit(ScreenEvents.SnackEvent(SNACK_HANDICAP_MATCH_LIMIT))
            else -> Settings.updateSettings(key, value)
        }
        _eventMatchSettingsChange.emit(Event(Unit))
    }
    private val _eventMatchSettingsChange = MutableSharedFlow<Event<Unit>>()
    val eventMatchSettingsChange = _eventMatchSettingsChange.asSharedFlow()
}