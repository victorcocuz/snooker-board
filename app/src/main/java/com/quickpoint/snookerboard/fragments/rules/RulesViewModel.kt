package com.quickpoint.snookerboard.fragments.rules

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.domain.DomainPlayer
import com.quickpoint.snookerboard.domain.DomainPlayer.PLAYER01
import com.quickpoint.snookerboard.domain.DomainPlayer.PLAYER02
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS
import kotlin.math.absoluteValue

class RulesViewModel : ViewModel() {
    // Observables
    private val _eventRulesAction = MutableLiveData<Event<MatchAction>>()
    val eventRulesAction: LiveData<Event<MatchAction>> = _eventRulesAction
    fun onEventRulesAction(matchAction: MatchAction) { // Such as cancelling a match, ending a frame, etc.
        _eventRulesAction.value = Event(matchAction)
    }

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
    fun updateRules() {
        _eventRules.value = SETTINGS
    }

    // Helpers
    fun updateFirstPlayer(value: Int) {
        SETTINGS.firstPlayer = if (value == 2) (0..1).random() else value
        updateRules()
    }

    fun updateMaxFramesAvailable(value: Int) {
        SETTINGS.maxFramesAvailable = value
        SETTINGS.handicapMatch = 0
        updateRules()
    }

    fun updateReds(value: Int) {
        SETTINGS.reds = value
        SETTINGS.handicapFrame = 0
        updateRules()
    }

    fun updateFoul(value: Int) {
        SETTINGS.foul = value
        updateRules()
    }

    fun updateHandicapFrame(value: Int) {
        if ((SETTINGS.handicapFrame + value).absoluteValue >= SETTINGS.reds * 8 + 27) onEventRulesAction(SNACK_HANDICAP_FRAME_LIMIT)
        else SETTINGS.handicapFrame += value
        updateRules()
    }

    fun updateHandicapMatch(value: Int) {
        if ((SETTINGS.handicapMatch + value).absoluteValue == SETTINGS.maxFramesAvailable) onEventRulesAction(SNACK_HANDICAP_MATCH_LIMIT)
        else SETTINGS.handicapMatch += value
        updateRules()
    }

    fun startMatchQuery() = onEventRulesAction(when {
        PLAYER01.hasNoName() || PLAYER02.hasNoName() -> SNACK_NO_PLAYER
        (SETTINGS.firstPlayer < 0) -> SNACK_NO_FIRST
        else -> MATCH_PLAY
    })
}