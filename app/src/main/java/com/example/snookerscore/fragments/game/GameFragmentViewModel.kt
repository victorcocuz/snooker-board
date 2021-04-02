package com.example.snookerscore.fragments.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.snookerscore.utils.toast

class GameFragmentViewModel(application: Application) : AndroidViewModel(application) {
    var frame: Frame = Frame()
    private val _frameState = frame.frameState
    val frameState: LiveData<BallType>
        get() = _frameState
    val pointsDiff = frame.pointsDiff
    val pointsLeft = frame.pointsRemaining
    val app = application

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
        foulCheck.value = false
        app.applicationContext.toast("CANCEL")
    }

    fun onScored(ball: Ball) {
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