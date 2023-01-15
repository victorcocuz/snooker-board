package com.quickpoint.snookerboard.fragments.rules

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.domain.DomainPlayer
import com.quickpoint.snookerboard.domain.DomainPlayer.PLAYER01
import com.quickpoint.snookerboard.domain.DomainPlayer.PLAYER02
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel.RulesUpdateAction.RULES_AVAILABLE_FRAMES
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel.RulesUpdateAction.RULES_AVAILABLE_REDS
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel.RulesUpdateAction.RULES_FOUL_MODIFIER
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel.RulesUpdateAction.RULES_HANDICAP_FRAME
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel.RulesUpdateAction.RULES_HANDICAP_MATCH
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel.RulesUpdateAction.RULES_STARTING_PLAYER
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.MATCH_PLAY
import com.quickpoint.snookerboard.utils.MatchAction.SNACK_HANDICAP_FRAME_LIMIT
import com.quickpoint.snookerboard.utils.MatchAction.SNACK_HANDICAP_MATCH_LIMIT
import com.quickpoint.snookerboard.utils.MatchAction.SNACK_NO_FIRST
import com.quickpoint.snookerboard.utils.MatchAction.SNACK_NO_PLAYER
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS
import kotlin.math.absoluteValue

class RulesViewModel : ViewModel() {
    // Observables
    private val _eventRulesAction = MutableLiveData<Event<MatchAction>>()
    val eventRulesAction: LiveData<Event<MatchAction>> = _eventRulesAction
    fun onEventRulesAction(matchAction: MatchAction) { // Such as cancelling a match, ending a frame, etc.
        _eventRulesAction.value = Event(matchAction)
    }

    var player01FirstName by mutableStateOf("")
    var player01LastName by mutableStateOf("")
    var player02FirstName by mutableStateOf("")
    var player02LastName by mutableStateOf("")

    private val _eventPlayer01 = MutableLiveData<DomainPlayer>()
    val eventPlayer01: LiveData<DomainPlayer> = _eventPlayer01
    private val _eventPlayer02 = MutableLiveData<DomainPlayer>()
    val eventPlayer02: LiveData<DomainPlayer> = _eventPlayer02
    fun updatePlayer() {
        _eventPlayer01.value = PLAYER01
        _eventPlayer02.value = PLAYER02
    }

    private val _eventRules = MutableLiveData<SETTINGS>()
    val eventRules: LiveData<SETTINGS> = _eventRules

    private val _eventRulesUpdated = MutableLiveData<Event<Unit>>()
    val eventRulesUpdated: LiveData<Event<Unit>> = _eventRulesUpdated

    fun updateRules() {
        _eventRules.value = SETTINGS
    }

    enum class RulesUpdateAction { RULES_STARTING_PLAYER, RULES_AVAILABLE_FRAMES, RULES_AVAILABLE_REDS, RULES_FOUL_MODIFIER, RULES_HANDICAP_FRAME, RULES_HANDICAP_MATCH, RULES_VOID }

    fun updateAction(rulesUpdateAction: RulesUpdateAction, newValue: Int) {
        when (rulesUpdateAction) {
            RULES_STARTING_PLAYER -> SETTINGS.startingPlayer = if (newValue == 2) (0..1).random() else newValue
            RULES_AVAILABLE_FRAMES -> {
                SETTINGS.availableFrames = newValue
                SETTINGS.handicapMatch = 0
            }

            RULES_AVAILABLE_REDS -> {
                SETTINGS.availableReds = newValue
                SETTINGS.handicapFrame = 0

            }

            RULES_FOUL_MODIFIER -> SETTINGS.foulModifier = newValue
            RULES_HANDICAP_FRAME -> {
                if ((SETTINGS.handicapFrame + newValue).absoluteValue >= SETTINGS.availableReds * 8 + 27)
                    onEventRulesAction(SNACK_HANDICAP_FRAME_LIMIT)
                else SETTINGS.handicapFrame += newValue
            }

            RULES_HANDICAP_MATCH -> {
                if ((SETTINGS.handicapMatch + newValue).absoluteValue == SETTINGS.availableFrames)
                    onEventRulesAction(SNACK_HANDICAP_MATCH_LIMIT)
                else SETTINGS.handicapMatch += newValue
            }

            RulesUpdateAction.RULES_VOID -> {}
        }
        _eventRulesUpdated.value = Event(Unit)
    }

    // Helpers
    fun updateFirstPlayer(value: Int) {
        SETTINGS.startingPlayer = if (value == 2) (0..1).random() else value
        updateRules()
    }

    fun updateMaxFramesAvailable(value: Int) {
        SETTINGS.availableFrames = value
        SETTINGS.handicapMatch = 0
        updateRules()
    }

    fun updateReds(value: Int) {
        SETTINGS.availableReds = value
        SETTINGS.handicapFrame = 0
        updateRules()
    }

    fun updateFoul(value: Int) {
        SETTINGS.foulModifier = value
        updateRules()
    }

    fun updateHandicapFrame(value: Int) {
        if ((SETTINGS.handicapFrame + value).absoluteValue >= SETTINGS.availableReds * 8 + 27) onEventRulesAction(SNACK_HANDICAP_FRAME_LIMIT)
        else SETTINGS.handicapFrame += value
        updateRules()
    }

    fun updateHandicapMatch(value: Int) {
        if ((SETTINGS.handicapMatch + value).absoluteValue == SETTINGS.availableFrames) onEventRulesAction(SNACK_HANDICAP_MATCH_LIMIT)
        else SETTINGS.handicapMatch += value
        updateRules()
    }

    fun startMatchQuery() {
        onEventRulesAction(
            when {
                PLAYER01.hasNoName() || PLAYER02.hasNoName() -> SNACK_NO_PLAYER
                (SETTINGS.startingPlayer < 0) -> SNACK_NO_FIRST
                else -> MATCH_PLAY
            }
        )
    }
}