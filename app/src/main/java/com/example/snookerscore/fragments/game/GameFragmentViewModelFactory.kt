package com.example.snookerscore.fragments.game

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameFragmentViewModelFactory(
    private val application: Application,
    private val matchFrames: Int,
    private val matchReds: Int,
    private val matchFoulModifier: Int,
    private val matchBreaksFirst: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameFragmentViewModel::class.java)) {
            return GameFragmentViewModel(application, matchFrames, matchReds, matchFoulModifier, matchBreaksFirst) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}