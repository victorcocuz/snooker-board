package com.example.snookerscore.fragments.game.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snookerscore.fragments.game.*
import com.example.snookerscore.utils.Event

class FoulDialogViewModel : ViewModel() {
    private var ballClicked: Ball? = null
    private var actionClicked: Action? = null
    private var freeBall = false
    private var removeRed = false

    private val _eventCancelDialog = MutableLiveData<Event<Unit>>()
    val eventCancelDialog: LiveData<Event<Unit>> = _eventCancelDialog

    private val _eventFoulNotValid = MutableLiveData<Event<Unit>>()
    val eventFoulNotValid: LiveData<Event<Unit>> = _eventFoulNotValid

    private val _foul = MutableLiveData<Event<Shot>>()
    val foul: LiveData<Event<Shot>> = _foul

    fun onBallClicked(ball: Ball, shotType: ShotType) {
        ballClicked = ball
    }

    fun onActionClicked(action: Action) {
        actionClicked = action
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
        if (ballClicked != null && actionClicked != null) {
            _foul.value = Event(
                Shot(
                    CurrentPlayer.PlayerA,
                    ballClicked!!,
                    ShotType.FOUL,
                    actionClicked!!
                )
            )
        } else {
            _eventFoulNotValid.value = Event(Unit)
        }
    }
}