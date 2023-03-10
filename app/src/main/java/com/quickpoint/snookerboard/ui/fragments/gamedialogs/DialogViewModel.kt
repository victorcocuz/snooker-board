package com.quickpoint.snookerboard.ui.fragments.gamedialogs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.domain.DomainBall
import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.utils.DataStore
import com.quickpoint.snookerboard.utils.K_BOOL_TOGGLE_FREEBALL
import com.quickpoint.snookerboard.utils.MatchAction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DialogViewModel(
    private val dataStore: DataStore
    ) : ViewModel() {

    // Generic Dialog
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

    // Foul Dialog
    var isFoulDialogShown by mutableStateOf(false)
        private set
    fun onOpenFoulDialog() {
        isFoulDialogShown = true
    }

    var ballClicked : DomainBall? = null
        private set
    fun onBallClicked(ball: DomainBall) {
        ballClicked = ball
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
        dataStore.savePreferences(K_BOOL_TOGGLE_FREEBALL, false)
    }

    fun foulIsValid() = ballClicked != null
    fun onDismissFoulDialog() {
        ballClicked = null
        _eventDialogReds.value = 0
        _actionClicked.value = PotAction.SWITCH
        dataStore.savePreferences(K_BOOL_TOGGLE_FREEBALL, false)
        isFoulDialogShown = false
    }

}