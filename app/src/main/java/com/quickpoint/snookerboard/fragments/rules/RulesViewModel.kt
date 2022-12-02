package com.quickpoint.snookerboard.fragments.rules

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.domain.DomainPlayer.PLAYER01
import com.quickpoint.snookerboard.domain.DomainPlayer.PLAYER02
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS

class RulesViewModel: ViewModel() {
    private val _eventRulesAction = MutableLiveData<Event<MatchAction>>()
    val eventRulesAction: LiveData<Event<MatchAction>> = _eventRulesAction
    fun onEventRulesAction(matchAction: MatchAction) { // Such as cancelling a match, ending a frame, etc.
        _eventRulesAction.value = Event(matchAction)
    }

    private val _eventRules = MutableLiveData<SETTINGS>()
    val eventRules: LiveData<SETTINGS> = _eventRules
    fun updateRules(_unused: Int) {
        _eventRules.value = SETTINGS
    }

    fun startMatchQuery() = onEventRulesAction(when {
        PLAYER01.hasNoName() || PLAYER02.hasNoName() -> SNACKBAR_NO_PLAYER
        (SETTINGS.firstPlayer < 0) -> SNACKBAR_NO_FIRST
        else -> MATCH_PLAY
    })
}