package com.example.snookerscore.fragments.gamestatistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.repository.SnookerRepository

class GameStatsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = SnookerDatabase.getDatabase(application)
    private val snookerRepository = SnookerRepository(database)

    val frames = snookerRepository.frames
}