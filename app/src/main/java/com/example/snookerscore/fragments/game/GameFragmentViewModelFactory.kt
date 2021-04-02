package com.example.snookerscore.fragments.game

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameFragmentViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameFragmentViewModel::class.java)) {
            return GameFragmentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}