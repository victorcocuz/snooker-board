package com.quickpoint.snookerboard.fragments.play

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.quickpoint.snookerboard.domain.DomainPlayer.PLAYER01
import com.quickpoint.snookerboard.domain.DomainPlayer.PLAYER02
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.MatchRules
import com.quickpoint.snookerboard.utils.MatchRules.*
import timber.log.Timber

class PlayViewModel(
    app: Application,
) : AndroidViewModel(app) {
    private val _eventPlayAction = MutableLiveData<Event<MatchAction>>()
    val eventPlayAction: LiveData<Event<MatchAction>> = _eventPlayAction
    fun onEventPlayAction(matchAction: MatchAction) { // Such as cancelling a match, ending a frame, etc.
        _eventPlayAction.value = Event(matchAction)
    }

    private val _eventRules = MutableLiveData<RULES>()
    val eventRules: LiveData<RULES> = _eventRules
    fun updateRules(_unused: Int) {
        _eventRules.value = RULES
    }

    fun startMatchQuery() = onEventPlayAction(when {
        PLAYER01.hasNoName() || PLAYER02.hasNoName() -> SNACKBAR_NO_PLAYER
        (RULES.firstPlayer < 0) -> SNACKBAR_NO_FIRST
        else -> MATCH_PLAY
    })
}