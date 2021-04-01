package com.example.snookerscore.fragments.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class GameViewModel : ViewModel() {
    var frame: Frame = Frame()
    private val _frameState = frame.frameState
    val frameState: LiveData<BallType>
        get() = _frameState
    val pointsDiff = frame.pointsDiff
    val pointsLeft = frame.pointsRemaining

    // Dialog stuff
    val foulCheck = MutableLiveData(false)
    private var selectedBall : Ball? = null
    private var foulAction : FoulAction? = null
    private var foulRemoveRed = false
    val foulDataValidate = MutableLiveData<Boolean>()

    fun onFoul() {
        foulCheck.value = true
    }

    fun onFoulAction(foulAction: FoulAction) {
        Timber.e("foul action")
        this.foulAction = foulAction
    }

    fun onRemoveRed(removeRed: Boolean) {
        Timber.e("onremovered")
        foulRemoveRed = removeRed
    }

    fun onFoulConfirmed() {
        Timber.e("confirmed")
        if ((selectedBall) != null) Timber.e("selectedball")
        if ((foulAction) != null) Timber.e("foulAction")
        if (selectedBall != null && foulAction != null) {
            Timber.e("whatever")
            manageFoulActions()
            if (foulRemoveRed) frame.removeBall()
            foulCheck.value = false
            onScored(selectedBall!!, -1)
        } else {
        }
    }

    fun onFoulCanceled() {
        Timber.e("canceled")
        foulCheck.value = false
    }

    fun onScored(ball: Ball, polarity: Int) {
        Timber.e("score")
        if (foulCheck.value == false) frame.addScore(ball, polarity)
        else selectedBall = ball
    }

    fun manageFoulActions() {

    }

    fun onMiss() {
        frame.onMiss()
    }

    fun onFrameComplete() {
        frame.resetFrame()
    }

    fun onUndo() {
        frame.undo()
    }
}