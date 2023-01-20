package com.quickpoint.snookerboard.fragments.rules

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.ScreenEvents
import com.quickpoint.snookerboard.utils.DataStore
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.KEY_INT_MATCH_AVAILABLE_FRAMES
import com.quickpoint.snookerboard.utils.KEY_INT_MATCH_AVAILABLE_REDS
import com.quickpoint.snookerboard.utils.KEY_INT_MATCH_FOUL_MODIFIER
import com.quickpoint.snookerboard.utils.KEY_INT_MATCH_HANDICAP_FRAME
import com.quickpoint.snookerboard.utils.KEY_INT_MATCH_HANDICAP_MATCH
import com.quickpoint.snookerboard.utils.KEY_INT_MATCH_STARTING_PLAYER
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.NAV_TO_GAME
import com.quickpoint.snookerboard.utils.MatchAction.SNACK_HANDICAP_FRAME_LIMIT
import com.quickpoint.snookerboard.utils.MatchAction.SNACK_HANDICAP_MATCH_LIMIT
import com.quickpoint.snookerboard.utils.MatchAction.SNACK_NO_STARTING_PLAYER
import com.quickpoint.snookerboard.utils.MatchSettings.Settings
import com.quickpoint.snookerboard.utils.USER_PLAYER01_FIRST_NAME_KEY
import com.quickpoint.snookerboard.utils.USER_PLAYER01_LAST_NAME_KEY
import com.quickpoint.snookerboard.utils.USER_PLAYER02_FIRST_NAME_KEY
import com.quickpoint.snookerboard.utils.USER_PLAYER02_LAST_NAME_KEY
import com.quickpoint.snookerboard.utils.isNameIncomplete
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class RulesViewModel(
    private val app: Application,
    private val dataStore: DataStore
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
            players.isNameIncomplete() -> MatchAction.SNACK_PLAYER_NAME_INCOMPLETE
            (Settings.startingPlayer < 0) -> SNACK_NO_STARTING_PLAYER
            else -> NAV_TO_GAME
        }
    )

    var players = mapOf(
        USER_PLAYER01_FIRST_NAME_KEY to mutableStateOf(""),
        USER_PLAYER01_LAST_NAME_KEY to mutableStateOf(""),
        USER_PLAYER02_FIRST_NAME_KEY to mutableStateOf(""),
        USER_PLAYER02_LAST_NAME_KEY to mutableStateOf("")
    )

    fun updatePlayerNames(settings: Map<String, String>) = settings.forEach {
        players[it.key]?.value = it.value
        dataStore.save(settings)
    }

    fun updateRules(settings: Map<String, Any>) = settings.forEach{
        when(it.key) {
            KEY_INT_MATCH_AVAILABLE_FRAMES -> Settings.availableFrames = it.value as Int
            KEY_INT_MATCH_AVAILABLE_REDS -> Settings.availableReds = it.value as Int
            KEY_INT_MATCH_FOUL_MODIFIER -> Settings.foulModifier = it.value as Int
            KEY_INT_MATCH_STARTING_PLAYER -> Settings.startingPlayer = it.value as Int
            KEY_INT_MATCH_HANDICAP_FRAME -> Settings.handicapFrame = it.value as Int
            KEY_INT_MATCH_HANDICAP_MATCH -> Settings.handicapMatch = it.value as Int
        }
    }

    private val _eventRulesUpdated = MutableLiveData<Event<Unit>>()
    val eventRulesUpdated: LiveData<Event<Unit>> = _eventRulesUpdated

    fun updateAction(key: String, newValue: Int) = Settings.apply{
        when (key) {
            KEY_INT_MATCH_STARTING_PLAYER -> setValue(mapOf(key to if (newValue == 2) (0..1).random() else newValue), dataStore)
            KEY_INT_MATCH_AVAILABLE_FRAMES -> setValue(mapOf(
                key to newValue,
                    KEY_INT_MATCH_HANDICAP_MATCH to 0
                ), dataStore)
            KEY_INT_MATCH_AVAILABLE_REDS -> setValue(mapOf(
                key to newValue,
                KEY_INT_MATCH_HANDICAP_FRAME to 0
            ), dataStore)
            KEY_INT_MATCH_FOUL_MODIFIER -> setValue(mapOf(key to newValue), dataStore)
            KEY_INT_MATCH_HANDICAP_FRAME -> {
                if ((handicapFrame + newValue).absoluteValue >= Settings.availableReds * 8 + 27)
                    onEmit(SNACK_HANDICAP_FRAME_LIMIT)
                else setValue(mapOf(key to newValue), dataStore)
            }
            KEY_INT_MATCH_HANDICAP_MATCH -> {
                if ((handicapMatch + newValue).absoluteValue == Settings.availableFrames)
                    onEmit(SNACK_HANDICAP_MATCH_LIMIT)
                else setValue(mapOf(key to newValue), dataStore)
            }
            else -> {} // Not Implemented
        }
        _eventRulesUpdated.value = Event(Unit)
    }
}