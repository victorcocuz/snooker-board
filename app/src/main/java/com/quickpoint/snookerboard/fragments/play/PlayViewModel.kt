package com.quickpoint.snookerboard.fragments.play

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.DomainMatchInfo.RULES
import com.quickpoint.snookerboard.domain.DomainPlayer.PLAYER01
import com.quickpoint.snookerboard.domain.DomainPlayer.PLAYER02
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.MATCH_PLAY
import com.quickpoint.snookerboard.utils.toast

class PlayViewModel(
    private val app: Application
): AndroidViewModel(app) {
    private val _eventPlayAction = MutableLiveData<Event<MatchAction>>()
    val eventPlayAction: LiveData<Event<MatchAction>> = _eventPlayAction
    private fun assignEventPlayAction(matchAction: MatchAction) { // Such as cancelling a match, ending a frame, etc.
        _eventPlayAction.value = Event(matchAction)
    }

    fun startMatchQuery() = when {
        PLAYER01.hasNoName() || PLAYER02.hasNoName() -> app.toast(app.getString(R.string.toast_play_no_name))
        (RULES.first < 0) -> app.toast(app.getString(R.string.toast_play_select_who_breaks))
        else -> assignEventPlayAction(MATCH_PLAY)
    }
}