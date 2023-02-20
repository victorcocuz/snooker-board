package com.quickpoint.snookerboard.ui.fragments.gamedialogs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.base.Event
import com.quickpoint.snookerboard.domain.DomainBall
import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.utils.MatchAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DialogViewModel : ViewModel() {

    // Dialog events must be observed separately to allow to close dialog window before taking action
    var isGenericDialogShown by mutableStateOf(false)
        private set

    fun onOpenGenericDialog() {
        isGenericDialogShown = true
    }
    fun onDismissGenericDialog() {
        isGenericDialogShown = false
    }

    private val _eventDialogAction = MutableLiveData<Event<MatchAction>>()
    val eventDialogAction: LiveData<Event<MatchAction>> = _eventDialogAction
    fun onEventDialogAction(matchAction: MatchAction) {
        _eventDialogAction.value = Event(matchAction)
        onDismissGenericDialog()
    }

    // Foul Dialog observables and helpers
    var isFoulDialogShown by mutableStateOf(false)
        private set

    fun onOpenFoulDialog() {
        isFoulDialogShown = true
    }

    private val _ballClicked = MutableLiveData<DomainBall?>(null) // Only local, not sure if needed
    val ballClicked = _ballClicked
    fun onBallClicked(ball: DomainBall) {
        _ballClicked.value = ball
    }

    private val _eventDialogReds = MutableStateFlow(0)
    val eventDialogReds = _eventDialogReds.asStateFlow()
    fun onDialogReds(value: Int) {
        _eventDialogReds.value = value
    }

    private val _actionClicked = MutableStateFlow(PotAction.SWITCH)
    val actionClicked = _actionClicked.asStateFlow()
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

    fun foulIsValid() = _ballClicked.value != null
    fun onDismissFoulDialog() {
        _ballClicked.value = null
        _eventDialogReds.value = 0
        _actionClicked.value = PotAction.SWITCH
        Toggle.FreeBall.isEnabled = false
        isFoulDialogShown = false
//        _toggles.value = FrameToggles.FRAMETOGGLES
    }

}