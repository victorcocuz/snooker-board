package com.example.snookerscore.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.snookerscore.database.*
import com.example.snookerscore.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SnookerRepository(private val database: SnookerDatabase) {

    private val snookerDbDao = database.snookerDatabaseDao

    // Rankings
    //    val rankings: LiveData<List<DomainRanking>> = Transformations.map(snookerDbDao.getAllRankings()) {
    //        it.asDomainRankings()
    //    }
    //
    //    suspend fun refreshRankings() {
    //        withContext(Dispatchers.IO) {
    //            try {
    //                val rankings = NetworkRankingContainer(RankingsApi.retrofitService.getRankings("MoneyRankings", "2020"))
    //                val listPlayers = RankingsApi.retrofitService.getPlayers("10", "p", "2020")
    //                snookerDbDao.insertAllRankings(*rankings.asDatabaseModel(listPlayers))
    //            } catch (e: Exception) {
    //                Timber.e("Failure: ${e.message}")
    //            }
    //        }
    //    }

    // Frames
    //    val frames: LiveData<ArrayList<Pair<DomainPlayerScore, DomainPlayerScore>>> = Transformations.map(snookerDbDao.getMatchScore()) {
    //        it.asDomainFrameScoreList()
    //    }
    //
    //    suspend fun addFrames(frameScore: CurrentScore) {
    //        withContext(Dispatchers.IO) {
    //            snookerDbDao.insertMatchScore(frameScore.getFirst().asDbFrameScore())
    //            snookerDbDao.insertMatchScore(frameScore.getSecond().asDbFrameScore())
    //        }
    //    }
    //
    //    suspend fun deleteMatchFrames() {
    //        withContext(Dispatchers.IO) {
    //            snookerDbDao.deleteMatchScore()
    //        }
    //    }

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
    suspend fun saveCurrentFrame(frame: DomainFrame) = withContext(Dispatchers.IO) {
        snookerDbDao.apply {
            insertMatchFrame(frame.asDbFrame())
            insertMatchScore(frame.asDbCrtScore())
            insertMatchBreaks(frame.asDbBreak())
            frame.frameStack.forEach {
                insertBreakPots(it.asDbPot())
            }
            insertMatchBalls(frame.asDbBallStack())
        }
    }

    suspend fun deleteCurrentMatch() = withContext(Dispatchers.IO) {
        snookerDbDao.apply {
            deleteMatchScore()
            deleteMatchBreaks()
            deleteBreakPots()
            deleteMatchBalls()
            deleteMatchFrames()
        }
    }

    suspend fun deleteCurrentFrame() = withContext(Dispatchers.IO) {
        snookerDbDao.apply {
            deleteCurrentFrame()
            deleteCurrentFrameScore()
            val currentBreaks = getCurrentFrameBreaks()
            currentBreaks.forEach {
                deleteCurrentBreakPots(it.breakId)
            }
            deleteCurrentFrameBreaks()
            deleteCurrentFrameBalls()
        }
    }

    val currentFrame: LiveData<DomainFrame> = Transformations.map(snookerDbDao.getCrtFrame()) {
        it.asDomainFrame()
    }

    val score: LiveData<ArrayList<Pair<DomainPlayerScore, DomainPlayerScore>>> = Transformations.map(snookerDbDao.getMatchScore()) {
        it.asDomainFrameScoreList()
    }
}