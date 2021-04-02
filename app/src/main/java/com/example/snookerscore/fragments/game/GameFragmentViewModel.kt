package com.example.snookerscore.fragments.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class GameFragmentViewModel : ViewModel() {
    var frame: Frame = Frame()
    private val _frameState = frame.frameState
    val frameState: LiveData<BallType>
        get() = _frameState
    val pointsDiff = frame.pointsDiff
    val pointsLeft = frame.pointsRemaining

    val shotType = MutableLiveData<ShotType>()
    val isFoulDialogOpen = MutableLiveData(false)

    // Dialog stuff
    val foulCheck = MutableLiveData(false)
    private var selectedBall : Ball? = null
    private var foulAction : FoulAction? = null
    private var foulRemoveRed = false
    val foulDataValidate = MutableLiveData<Boolean>()

    fun onHit() {
        shotType.value = ShotType.HIT
    }

    fun onMiss() {
        frame.onMiss()
        shotType.value = ShotType.MISS
    }

    fun onFoul() {
        foulCheck.value = true
        shotType.value = ShotType.FOUL
    }

    fun onFreeBall() {
        shotType.value = ShotType.FREEBALL
    }

    fun onFoulAction(foulAction: FoulAction) {
        this.foulAction = foulAction
    }

    fun onRemoveRed(removeRed: Boolean) {
        foulRemoveRed = removeRed
    }

    fun onFoulConfirmed() {
        if (selectedBall != null && foulAction != null) {
            manageFoulActions()
            if (foulRemoveRed) frame.removeBall()
            foulCheck.value = false
            onScored(selectedBall!!)
        } else {
        }
    }

    fun onFoulCanceled() {
        Timber.e("canceled")
        foulCheck.value = false
    }

    fun onScored(ball: Ball) {
        Timber.e("score")
        if (foulCheck.value == false) frame.addScore(ball)
        else selectedBall = ball
    }

    fun manageFoulActions() {

    }

    fun onFrameComplete() {
        frame.resetFrame()
    }

    fun onUndo() {
        frame.undo()
    }
}