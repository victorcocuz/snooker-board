package com.example.snookerscore.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.database.asDomainFrames
import com.example.snookerscore.database.asDomainRankings
import com.example.snookerscore.domain.DomainRanking
import com.example.snookerscore.fragments.game.Frame
import com.example.snookerscore.fragments.game.asDatabaseFrame
import com.example.snookerscore.network.NetworkRankingContainer
import com.example.snookerscore.network.RankingsApi
import com.example.snookerscore.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SnookerRepository(private val database: SnookerDatabase) {

    // Rankings
    val rankings: LiveData<List<DomainRanking>> = Transformations.map(database.snookerDatabaseDao.getAllRankings()) {
        it.asDomainRankings()
    }

    suspend fun refreshRankings() {
        withContext(Dispatchers.IO) {
            try {
                val rankings = NetworkRankingContainer(RankingsApi.retrofitService.getRankings("MoneyRankings", "2020"))
                val listPlayers = RankingsApi.retrofitService.getPlayers("10", "p","2020")
                database.snookerDatabaseDao.insertAllRankings(*rankings.asDatabaseModel(listPlayers))
            } catch (e: Exception) {
                Timber.e("Failure: ${e.message}")
            }
        }
    }

    // Frames
    val frames: LiveData<List<Frame>> = Transformations.map(database.snookerDatabaseDao.getAllFrames()) {
        it.asDomainFrames()
    }

    suspend fun addFrames(frame: Frame) {
        withContext(Dispatchers.IO) {
            Timber.e("frame ${frame.asDatabaseFrame()}")
            database.snookerDatabaseDao.insertFrame(frame.asDatabaseFrame())
        }
    }

    suspend fun removeFrames() {
        withContext(Dispatchers.IO) {
            database.snookerDatabaseDao.deleteFrames()
        }
    }
}