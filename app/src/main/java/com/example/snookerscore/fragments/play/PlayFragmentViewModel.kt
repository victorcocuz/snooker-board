package com.example.snookerscore.fragments.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snookerscore.utils.Event

class PlayFragmentViewModel: ViewModel() {
    // Live Data
    private val _eventFrames = MutableLiveData<Event<Int>>()
    val eventFrames: LiveData<Event<Int>> = _eventFrames

    private val _eventReds = MutableLiveData<Event<Int>>()
    val eventReds: LiveData<Event<Int>> = _eventReds

    private val _eventFoulModifier = MutableLiveData<Event<Int>>()
    val eventFoulModifier: LiveData<Event<Int>> = _eventFoulModifier

    init {
        setFrames(2)
        setReds(15)
        setFoulModifier(0)
    }

    fun setFrames(number: Int) {
        _eventFrames.value = Event(number)
    }

    fun setReds(number: Int) {
        _eventReds.value = Event(number)
    }

    fun setFoulModifier(number: Int) {
        _eventFoulModifier.value = Event(number)
    }
}