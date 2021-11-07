package com.quickpoint.snookerboard.fragments.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayViewModel: ViewModel() {
    // Live Data
    private val _eventFrames = MutableLiveData<Int>()
    val eventFrames: LiveData<Int> = _eventFrames
    fun setFrames(number: Int) {
        _eventFrames.value = number
    }

    private val _eventReds = MutableLiveData<Int>()
    val reds: LiveData<Int> = _eventReds
    fun setReds(number: Int) {
        _eventReds.value = number
    }

    private val _eventFoulModifier = MutableLiveData<Int>()
    val eventFoulModifier: LiveData<Int> = _eventFoulModifier
    fun setFoulModifier(number: Int) {
        _eventFoulModifier.value = number
    }

    private val _eventBreaksFirst = MutableLiveData<Int>()
    val eventBreaksFirst: LiveData<Int> = _eventBreaksFirst
    fun setBreaksFirst(position: Int) {
        _eventBreaksFirst.value = if (position == 2) (0..1).random() else position
    }

    init {
        setFrames(2)
        setReds(15)
        setFoulModifier(0)
        setBreaksFirst(0)
    }
}