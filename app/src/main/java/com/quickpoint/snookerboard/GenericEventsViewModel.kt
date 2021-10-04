package com.quickpoint.snookerboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.domain.DomainBall
import com.quickpoint.snookerboard.domain.DomainPot
import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction

class GenericEventsViewModel : ViewModel() {
    // Events observables
    private val _eventMatchActionQueried = MutableLiveData<Event<MatchAction>>()
    val eventMatchActionQueried: LiveData<Event<MatchAction>> = _eventMatchActionQueried
    fun onEventMatchActionQueried(matchAction: MatchAction) {
        _eventMatchActionQueried.value = Event(matchAction)
    }

    private val _eventMatchActionConfirmed = MutableLiveData<Event<MatchAction>>()
    val eventMatchActionConfirmed: LiveData<Event<MatchAction>> = _eventMatchActionConfirmed
    fun onEventMatchActionConfirmed(matchAction: MatchAction) {
        _eventMatchActionConfirmed.value = Event(matchAction)
    }

    // Foul Dialog observables and helpers
    private val _ballClicked = MutableLiveData<DomainBall?>(null) // Only local, not sure if needed
    fun onBallClicked(ball: DomainBall) {
        _ballClicked.value = ball
    }

    private val _actionClicked = MutableLiveData<PotAction?>(null)
    val actionClicked: LiveData<PotAction?> = _actionClicked
    fun onActionClicked(action: PotAction) {
        _actionClicked.value = action
        if (action == PotAction.CONTINUE) _isFreeBall.value = false
    }

    private val _isFreeBall = MutableLiveData(false)
    val isFreeBall: LiveData<Boolean> = _isFreeBall
    fun onFreeballClicked() {
        _isFreeBall.value = !_isFreeBall.value!!
    }

    private val _isRemoveRed = MutableLiveData(false)
    val isRemoveRed: LiveData<Boolean> = _isRemoveRed
    fun onRemoveRedClicked() {
        _isRemoveRed.value = !_isRemoveRed.value!!
    }

    fun foulIsValid() = _ballClicked.value != null && actionClicked.value != null
    fun getFoul() = DomainPot.FOUL(_ballClicked.value!!, actionClicked.value!!)
    fun resetFoul() {
        _actionClicked.value = null
        _ballClicked.value = null
        _isFreeBall.value = false
        _isRemoveRed.value = false
    }
}