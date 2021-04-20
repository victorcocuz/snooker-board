package com.example.snookerscore.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.database.asDomainFrameScoreList
import com.example.snookerscore.database.asDomainRankings
import com.example.snookerscore.domain.DomainRanking
import com.example.snookerscore.fragments.game.CurrentScore
import com.example.snookerscore.fragments.game.FrameScore
import com.example.snookerscore.fragments.game.asDatabaseFrameScore
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
                val listPlayers = RankingsApi.retrofitService.getPlayers("10", "p", "2020")
                database.snookerDatabaseDao.insertAllRankings(*rankings.asDatabaseModel(listPlayers))
            } catch (e: Exception) {
                Timber.e("Failure: ${e.message}")
            }
        }
    }

    // Frames
    val frames: LiveData<ArrayList<Pair<FrameScore, FrameScore>>> = Transformations.map(database.snookerDatabaseDao.getAllFrames()) {
        it.asDomainFrameScoreList()
    }

    suspend fun addFrames(frameScore: CurrentScore) {
        withContext(Dispatchers.IO) {
            database.snookerDatabaseDao.insertFrame(frameScore.getFirst().asDatabaseFrameScore())
            database.snookerDatabaseDao.insertFrame(frameScore.getSecond().asDatabaseFrameScore())
        }
    }

    suspend fun removeFrames() {
        withContext(Dispatchers.IO) {
            database.snookerDatabaseDao.deleteFrames()
        }
    }

    suspend fun getTotals(playerId: Int): FrameScore {
        return withContext(Dispatchers.IO) {
            return@withContext FrameScore(
                -2,
                playerId,
                database.snookerDatabaseDao.getSumOfFramePoints(playerId),
                database.snookerDatabaseDao.getMaxMatchPoints(playerId),
                database.snookerDatabaseDao.getSumOfSuccessShots(playerId),
                database.snookerDatabaseDao.getSumOfMissedShots(playerId),
                database.snookerDatabaseDao.getSumOfFouls(playerId),
                database.snookerDatabaseDao.getMaxBreak(playerId)
            )
        }
    }
}