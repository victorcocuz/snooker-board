package com.example.snookerscore.fragments.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class PlayFragmentViewModel: ViewModel() {
    // Live Data
    private val _eventFrames = MutableLiveData<Int>()
    val eventFrames: LiveData<Int> = _eventFrames

    private val _eventReds = MutableLiveData<Int>()
    val reds: LiveData<Int> = _eventReds

    private val _eventFoulModifier = MutableLiveData<Int>()
    val eventFoulModifier: LiveData<Int> = _eventFoulModifier

    private val _eventBreaksFirst = MutableLiveData<Int>()
    val eventBreaksFirst: LiveData<Int> = _eventBreaksFirst

    init {
        setFrames(2)
        setReds(15)
        setFoulModifier(0)
        setBreaksFirst(0)
    }

    fun setFrames(number: Int) {
        _eventFrames.value = number
    }

    fun setReds(number: Int) {
        _eventReds.value = number
        Timber.e("reds ${reds.value}")
    }

    fun setFoulModifier(number: Int) {
        _eventFoulModifier.value = number
        Timber.e("reds ${reds.value}")
    }

    fun setBreaksFirst(position: Int) {
        _eventBreaksFirst.value = if (position == 2) (0..1).random() else position
    }
}