package com.quickpoint.snookerboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.domain.DomainBall
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo.FREEBALLINFO
import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction

class DialogViewModel : ViewModel() {
    // Dialog events must be observed separately to allow to close dialog window before taking action
    private val _eventDialogAction = MutableLiveData<Event<MatchAction>>()
    val eventDialogAction: LiveData<Event<MatchAction>> = _eventDialogAction
    fun onEventDialogAction(matchAction: MatchAction) {
        _eventDialogAction.value = Event(matchAction)
    }

    // Foul Dialog observables and helpers
    private val _ballClicked = MutableLiveData<DomainBall?>(null) // Only local, not sure if needed
    val ballClicked = _ballClicked
    fun onBallClicked(ball: DomainBall) {
        _ballClicked.value = ball
    }

    private val _eventDialogReds = MutableLiveData(0)
    val eventDialogReds: LiveData<Int> = _eventDialogReds
    val onDialogReds = fun (value: Int) {
        _eventDialogReds.postValue(value)
    }

    private val _actionClicked = MutableLiveData<PotAction?>(null)
    val actionClicked: LiveData<PotAction?> = _actionClicked
    fun onActionClicked(action: PotAction) {
        _actionClicked.value = action
    }

    private val _freeballInfo = MutableLiveData(FREEBALLINFO)
    val freeballInfo: LiveData<FREEBALLINFO> = _freeballInfo
    fun onFreeballClicked() {
        FREEBALLINFO.toggleActive()
        _freeballInfo.postValue(FREEBALLINFO)
    }

    fun foulIsValid() = _ballClicked.value != null && actionClicked.value != null
    fun resetFoul() {
        _ballClicked.value = null
        _eventDialogReds.value = 0
        _actionClicked.value = null
        FREEBALLINFO.setInactive()
        _freeballInfo.value = FREEBALLINFO
    }
}