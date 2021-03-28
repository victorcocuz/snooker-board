package com.example.snookerscore.fragments.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val _remainingReds = MutableLiveData<Int>()
    val remainingReds: LiveData<Int>
        get() = _remainingReds
    private val _scorePlayerA = MutableLiveData<Int>()
    val scorePlayerA: LiveData<Int>
        get() = _scorePlayerA
    private val _scorePlayerB = MutableLiveData<Int>()
    val scorePlayerB: LiveData<Int>
        get() = _scorePlayerB
    private val _scoreTotalPlayerA = MutableLiveData<Int>()
    val scoreTotalPlayerA: LiveData<Int>
        get() = _scoreTotalPlayerA
    private val _scoreTotalPlayerB = MutableLiveData<Int>()
    val scoreTotalPlayerB: LiveData<Int>
        get() = _scoreTotalPlayerB

    private val _eventFrameComplete = MutableLiveData<Boolean>()
    val eventFrameComplete: LiveData<Boolean>
        get() = _eventFrameComplete



    init {
        resetFrame()
        _scoreTotalPlayerA.value = 0
        _scoreTotalPlayerB.value = 0

    }

    fun onScoredRed() {
        removeRed()
        _scorePlayerA.value = _scorePlayerA.value?.plus(1)
    }

    private fun removeRed() {
        _remainingReds.value?.let {
            if (it > 0) {
                _remainingReds.value = it - 1
            }
        }
    }

    private fun resetFrame() {
        _eventFrameComplete.value = false
        _remainingReds.value = 15
        _scorePlayerA.value = 0
        _scorePlayerB.value = 0
    }

    fun onFrameComplete() {
        _eventFrameComplete.value = true
        resetFrame()
    }
}