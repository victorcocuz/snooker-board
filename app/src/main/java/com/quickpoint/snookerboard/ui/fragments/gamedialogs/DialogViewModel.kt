package com.quickpoint.snookerboard.ui.fragments.gamedialogs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.domain.DomainBall
import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.utils.MatchAction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DialogViewModel : ViewModel() {

    // Dialog events must be observed separately to allow to close dialog window before taking action
    var isGenericDialogShown by mutableStateOf(false)
        private set

    private val _matchActions = MutableStateFlow<List<MatchAction>>(emptyList())
    val matchActions = _matchActions.asStateFlow()
    fun onOpenGenericDialog(matchActions: List<MatchAction>) {
        isGenericDialogShown = true
        _matchActions.value = matchActions
    }

    fun onDismissGenericDialog() {
        isGenericDialogShown = false
    }

    private val _eventDialogAction = MutableSharedFlow<MatchAction>()
    val eventDialogAction = _eventDialogAction.asSharedFlow()
    fun onEventDialogAction(matchAction: MatchAction) = viewModelScope.launch {
        _eventDialogAction.emit(matchAction)
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