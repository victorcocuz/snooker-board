package com.example.snookerscore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snookerscore.domain.DomainBall
import com.example.snookerscore.domain.DomainPot
import com.example.snookerscore.domain.MatchAction
import com.example.snookerscore.domain.PotAction
import com.example.snookerscore.utils.Event

class GenericEventsViewModel : ViewModel() {
    // Live Data
    private val _eventFoulQueried = MutableLiveData<Event<Unit>>()
    val eventFoulQueried: LiveData<Event<Unit>> = _eventFoulQueried

    private val _eventMatchActionQueried = MutableLiveData<Event<MatchAction>>()
    val eventMatchActionQueried: LiveData<Event<MatchAction>> = _eventMatchActionQueried

    private val _eventMatchActionConfirmed = MutableLiveData<Event<MatchAction>>()
    val eventMatchActionConfirmed: LiveData<Event<MatchAction>> = _eventMatchActionConfirmed


    // Event handlers
    fun onFoulClicked() {
        _eventFoulQueried.value = Event(Unit)
    }

    fun onEventMatchActionQueried(matchAction: MatchAction) {
        _eventMatchActionQueried.value = Event(matchAction)
    }

    fun onEventMatchActionConfirmed(matchAction: MatchAction) {
        _eventMatchActionConfirmed.value = Event(matchAction)
    }

    // Observables
    private val _actionClicked = MutableLiveData<PotAction?>(null)
    val actionClicked: LiveData<PotAction?> = _actionClicked

    private val _isFreeBall = MutableLiveData(false)
    val isFreeBall: LiveData<Boolean> = _isFreeBall

    private val _isRemoveRed = MutableLiveData(false)
    val isRemoveRed: LiveData<Boolean> = _isRemoveRed

    // Variables
    private var ballClicked: DomainBall? = null

    // Handlers
    fun onBallClicked(ball: DomainBall) {
        ballClicked = ball
    }

    fun onActionClicked(action: PotAction) {
        _actionClicked.value = action
        if (action == PotAction.CONTINUE) _isFreeBall.value = false
    }

    fun onRemoveRedClicked() {
        _isRemoveRed.value = !_isRemoveRed.value!!
    }

    fun onFreeballClicked() {
        _isFreeBall.value = !_isFreeBall.value!!
    }

    fun foulIsValid() = ballClicked != null && actionClicked.value != null

    fun getFoul() : DomainPot {
        return DomainPot.FOUL(ballClicked!!, actionClicked.value!!)
    }

    fun resetFoul() {
        _actionClicked.value = null
        _isFreeBall.value = false
        _isRemoveRed.value = false
        ballClicked = null
    }
}