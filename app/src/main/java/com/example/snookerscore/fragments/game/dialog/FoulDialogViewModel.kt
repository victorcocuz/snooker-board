package com.example.snookerscore.fragments.game.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snookerscore.fragments.game.Ball
import com.example.snookerscore.fragments.game.Pot
import com.example.snookerscore.fragments.game.PotAction
import com.example.snookerscore.fragments.game.PotType
import com.example.snookerscore.utils.Event

class FoulDialogViewModel : ViewModel() {
    // Observables
    private val _eventCancelDialog = MutableLiveData<Event<Unit>>()
    val eventCancelDialog: LiveData<Event<Unit>> = _eventCancelDialog

    private val _eventFoulNotValid = MutableLiveData<Event<Unit>>()
    val eventFoulNotValid: LiveData<Event<Unit>> = _eventFoulNotValid

    private val _foul = MutableLiveData<Event<Pot>>()
    val foul: LiveData<Event<Pot>> = _foul

    private val _actionClicked = MutableLiveData<PotAction?>(null)
    val actionClicked: LiveData<PotAction?> = _actionClicked

    // Variables
    var freeBall = false
    var removeRed = false
    private var ballClicked: Ball? = null

    // Handlers
    fun onBallClicked(ball: Ball) {
        ballClicked = ball
    }

    fun onActionClicked(action: PotAction) {
        _actionClicked.value = action
    }

    fun onRemoveRedClicked() {
        removeRed = !removeRed
    }

    fun onFreeballClicked() {
        freeBall = !freeBall
    }

    fun onCancelClicked() {
        _eventCancelDialog.value = Event(Unit)
    }

    fun onConfirmClicked() {
        if (ballClicked != null && actionClicked.value != null) {
            _foul.value = Event(Pot(ballClicked!!, PotType.FOUL, actionClicked.value!!))
        } else {
            _eventFoulNotValid.value = Event(Unit)
        }
    }
}