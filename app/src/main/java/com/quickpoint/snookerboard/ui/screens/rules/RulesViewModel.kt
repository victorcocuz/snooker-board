package com.quickpoint.snookerboard.ui.screens.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.core.ScreenEvents
import com.quickpoint.snookerboard.core.base.Event
import com.quickpoint.snookerboard.core.utils.MatchAction
import com.quickpoint.snookerboard.core.utils.MatchAction.SNACK_HANDICAP_FRAME_LIMIT
import com.quickpoint.snookerboard.core.utils.MatchAction.SNACK_HANDICAP_MATCH_LIMIT
import com.quickpoint.snookerboard.core.utils.MatchAction.SNACK_NO_STARTING_PLAYER
import com.quickpoint.snookerboard.data.database.SnookerDatabase
import com.quickpoint.snookerboard.data.database.models.asDomain
import com.quickpoint.snookerboard.domain.models.DomainPlayer
import com.quickpoint.snookerboard.domain.models.asDbPlayer
import com.quickpoint.snookerboard.domain.models.hasNoName
import com.quickpoint.snookerboard.domain.repository.DataStoreRepository
import com.quickpoint.snookerboard.domain.repository.GameRepository
import com.quickpoint.snookerboard.domain.utils.MatchSettings
import com.quickpoint.snookerboard.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RulesViewModel @Inject constructor(
    val dataStoreRepository: DataStoreRepository,
    val gameRepository: GameRepository,
    val database: SnookerDatabase,
) : ViewModel() {

    private val daoDbPlayer = database.daoDbPlayer
    private val _players = MutableStateFlow<List<DomainPlayer>>(emptyList())
    val players: StateFlow<List<DomainPlayer>> = _players

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (daoDbPlayer.getPlayerCount() == 0) {
                    daoDbPlayer.insertPlayers(
                        listOf(
                            DomainPlayer(0, "Ronnie", "O'Sullivan"),
                            DomainPlayer(1, "John", "Higgins")
                        ).asDbPlayer()
                    )
                }
            }

            daoDbPlayer.getPlayers().collect {
                _players.value = it.asDomain()
            }
        }
    }

    fun onPlayerNameChange(player: DomainPlayer) =
        viewModelScope.launch { // Update players names, save changes in DataStore and notify the composable to recompose
            withContext(Dispatchers.IO) {
                daoDbPlayer.updatePlayer(player.asDbPlayer())
            }
            _eventPlayerNameChange.emit(Event(Unit))
        }

    private val _eventPlayerNameChange = MutableSharedFlow<Event<Unit>>()
    val eventPlayerNameChange = _eventPlayerNameChange.asSharedFlow()


    private fun onEmit(screenEvent: ScreenEvents) = viewModelScope.launch {
        _eventSharedFlow.emit(screenEvent)
    }

    private val _eventSharedFlow = MutableSharedFlow<ScreenEvents>()
    val eventSharedFlow = _eventSharedFlow.asSharedFlow()

    fun startMatchQuery() = onEmit(
        when {
            _players.value[0].hasNoName() || _players.value[1].hasNoName() -> ScreenEvents.SnackAction(
                MatchAction.SNACK_PLAYER_NAME_INCOMPLETE
            )

            (MatchSettings.startingPlayer < 0) -> ScreenEvents.SnackAction(SNACK_NO_STARTING_PLAYER)
            else -> ScreenEvents.Navigate(Screen.Game.route)
        }
    )

    // Update settings, save changes in DataStore and notify the composable to recompose
    fun onMatchSettingsChange(key: String, value: Int) = viewModelScope.launch {
        when {
            MatchSettings.handicapFrameExceedsLimit(key, value) -> onEmit(ScreenEvents.SnackAction(SNACK_HANDICAP_FRAME_LIMIT))
            MatchSettings.handicapMatchExceedsLimit(key, value) -> onEmit(ScreenEvents.SnackAction(SNACK_HANDICAP_MATCH_LIMIT))
            else -> MatchSettings.updateSettings(key, value)
        }
        _eventMatchSettingsChange.emit(Event(Unit))
    }

    private val _eventMatchSettingsChange = MutableSharedFlow<Event<Unit>>()
    val eventMatchSettingsChange = _eventMatchSettingsChange.asSharedFlow()
}