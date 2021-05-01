package com.example.snookerscore.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.snookerscore.database.*
import com.example.snookerscore.domain.*
import com.example.snookerscore.network.NetworkRankingContainer
import com.example.snookerscore.network.RankingsApi
import com.example.snookerscore.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SnookerRepository(private val database: SnookerDatabase) {

    private val snookerDbDao = database.snookerDatabaseDao

    // Rankings
    val rankings: LiveData<List<DomainRanking>> = Transformations.map(snookerDbDao.getAllRankings()) {
        it.asDomainRankings()
    }

    suspend fun refreshRankings() {
        withContext(Dispatchers.IO) {
            try {
                val rankings = NetworkRankingContainer(RankingsApi.retrofitService.getRankings("MoneyRankings", "2020"))
                val listPlayers = RankingsApi.retrofitService.getPlayers("10", "p", "2020")
                snookerDbDao.insertAllRankings(*rankings.asDatabaseModel(listPlayers))
            } catch (e: Exception) {
                Timber.e("Failure: ${e.message}")
            }
        }
    }

    // Frames
    val frames: LiveData<ArrayList<Pair<DomainPlayerScore, DomainPlayerScore>>> = Transformations.map(snookerDbDao.getMatchScore()) {
        it.asDomainFrameScoreList()
    }

    suspend fun addFrames(frameScore: CurrentScore) {
        withContext(Dispatchers.IO) {
            snookerDbDao.insertMatchScore(frameScore.getFirst().asDbFrameScore())
            snookerDbDao.insertMatchScore(frameScore.getSecond().asDbFrameScore())
        }
    }

    suspend fun deleteMatchFrames() {
        withContext(Dispatchers.IO) {
            snookerDbDao.deleteMatchScore()
        }
    }

    suspend fun getTotals(playerId: Int): DomainPlayerScore {
        return withContext(Dispatchers.IO) {
            return@withContext DomainPlayerScore(
                -2,
                playerId,
                snookerDbDao.getSumOfFramePoints(playerId),
                snookerDbDao.getMaxMatchPoints(playerId),
                snookerDbDao.getSumOfSuccessShots(playerId),
                snookerDbDao.getSumOfMissedShots(playerId),
                snookerDbDao.getSumOfFouls(playerId),
                snookerDbDao.getMaxBreak(playerId)
            )
        }
    }

    // State
    suspend fun saveCurrentMatch(frame: DomainFrame) = withContext(Dispatchers.IO) {
        snookerDbDao.apply {
            insertCrtFrame(frame.asDbFrame())
            insertCrtScore(frame.asDbCrtScore())
            insertCrtBreaks(frame.asDbBreak())
            frame.frameStack.forEach {
                insertCrtPots(it.asDbPot())
            }
            insertCrtBalls(frame.asDbBallStack())
        }
    }

    suspend fun deleteCurrentMatch() = withContext(Dispatchers.IO) {
        snookerDbDao.apply {
            deleteCrtScore()
            deleteCrtBreaks()
            deleteCrtPots()
            deleteCrtBallStack()
            deleteCrtFrame()
        }
    }

    val currentFrame: LiveData<MutableList<DomainFrame>> = Transformations.map(snookerDbDao.getCrtFrame()) {
        it.asDomainFrame()
    }
}