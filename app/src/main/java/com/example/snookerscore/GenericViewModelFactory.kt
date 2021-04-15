package com.example.snookerscore

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snookerscore.fragments.game.GameFragmentViewModel
import com.example.snookerscore.fragments.gamestatistics.GameStatsViewModel
import com.example.snookerscore.fragments.rankings.RankingsFragmentViewModel

class GenericViewModelFactory(
    private val application: Application,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RankingsFragmentViewModel::class.java)) {
            return RankingsFragmentViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(GameStatsViewModel::class.java)) {
            return GameStatsViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(GameFragmentViewModel::class.java)) {
            return GameFragmentViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}