package com.quickpoint.snookerboard.fragments.gamedialogs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.domain.DomainBall
import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction

class DialogViewModel : ViewModel() {

    // Dialog events must be observed separately to allow to close dialog window before taking action
    var isGenericDialogShown by mutableStateOf(false)
        private set
    fun onOpenDialog() {
        isGenericDialogShown = true
    }
    fun onDismissDialog() {
        isGenericDialogShown = false
    }

    private val _eventDialogAction = MutableLiveData<Event<MatchAction>>()
    val eventDialogAction: LiveData<Event<MatchAction>> = _eventDialogAction
    fun onEventDialogAction(matchAction: MatchAction) {
        _eventDialogAction.value = Event(matchAction)
        onDismissDialog()
    }

    // Foul Dialog observables and helpers
    private val _ballClicked = MutableLiveData<DomainBall?>(null) // Only local, not sure if needed
    val ballClicked = _ballClicked
    fun onBallClicked(ball: DomainBall) {
        _ballClicked.value = ball
    }

    private val _eventDialogReds = MutableLiveData(0)
    val eventDialogReds: LiveData<Int> = _eventDialogReds
    val onDialogReds = fun(value: Int) {
        _eventDialogReds.postValue(value)
    }

    private val _actionClicked = MutableLiveData<PotAction?>(null)
    val onActionClicked: LiveData<PotAction?> = _actionClicked
    fun onActionClicked(action: PotAction) {
        _actionClicked.value = action
        Toggle.FreeBall.isEnabled = false
//        _toggles.postValue(FrameToggles.FRAMETOGGLES)
    }

    //
//    private val _toggles = MutableLiveData(FrameToggles.FRAMETOGGLES)
//    val toggles: LiveData<FrameToggles.FRAMETOGGLES> = _toggles
    fun onToggleFreeballedClicked() {
        Toggle.FreeBall.toggleEnabled()
//        _toggles.postValue(FrameToggles.FRAMETOGGLES)
    }

    fun foulIsValid() = _ballClicked.value != null && onActionClicked.value != null
    fun resetFoul() {
        _ballClicked.value = null
        _eventDialogReds.value = 0
        _actionClicked.value = null
        Toggle.FreeBall.isEnabled = false
//        _toggles.value = FrameToggles.FRAMETOGGLES
    }

}