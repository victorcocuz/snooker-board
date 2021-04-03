package com.example.snookerscore.fragments.game.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snookerscore.fragments.game.Ball
import com.example.snookerscore.fragments.game.Foul
import com.example.snookerscore.fragments.game.FoulAction
import com.example.snookerscore.utils.Event

class FoulDialogViewModel: ViewModel() {
    private var ballClicked: Ball? = null
    private var actionClicked: FoulAction? = null
    private var removeRed = false

    private val _eventCancelDialog = MutableLiveData<Event<Unit>>()
    val eventCancelDialog: LiveData<Event<Unit>> = _eventCancelDialog

    private val _eventFoulNotValid = MutableLiveData<Event<Unit>>()
    val eventFoulNotValid: LiveData<Event<Unit>> = _eventFoulNotValid

    private val _foul = MutableLiveData<Event<Foul>>()
    val foul: LiveData<Event<Foul>> = _foul

    fun onBallClicked(ball: Ball) {
        ballClicked = ball
    }

    fun onActionClicked(action: FoulAction) {
        actionClicked = action
    }

    fun onRemoveRedClicked() {
        removeRed = !removeRed
    }

    fun onCancelClicked() {
        _eventCancelDialog.value = Event(Unit)
    }

    fun onConfirmClicked() {
        if (ballClicked != null && actionClicked != null) {
            _foul.value = Event(Foul(ballClicked!!, actionClicked!!, removeRed))
        } else {
            _eventFoulNotValid.value = Event(Unit)
        }
    }
}