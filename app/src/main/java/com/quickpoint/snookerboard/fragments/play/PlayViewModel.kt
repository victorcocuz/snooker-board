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

class PlayViewModel(
    app: Application,
) : AndroidViewModel(app) {
    private val _eventPlayAction = MutableLiveData<Event<MatchAction>>()
    val eventPlayAction: LiveData<Event<MatchAction>> = _eventPlayAction
    private fun assignEventPlayAction(matchAction: MatchAction) { // Such as cancelling a match, ending a frame, etc.
        _eventPlayAction.value = Event(matchAction)
    }

    fun startMatchQuery() = assignEventPlayAction(when {
        PLAYER01.hasNoName() || PLAYER02.hasNoName() -> SNACKBAR_NO_PLAYER
        (MatchRules.RULES.firstPlayer < 0) -> SNACKBAR_NO_FIRST
        else -> MATCH_PLAY
    })
}