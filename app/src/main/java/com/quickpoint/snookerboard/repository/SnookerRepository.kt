package com.quickpoint.snookerboard.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.quickpoint.snookerboard.database.*
import com.quickpoint.snookerboard.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SnookerRepository(database: SnookerDatabase) {

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
            val breakId = insertMatchBreaks(frame.asDbBreak())
            for (i in 0 until frame.frameStack.size) {
                insertBreakPots(frame.frameStack[i].asDbPot(breakId[i]))
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

    suspend fun deleteCurrentFrame(frameId: Int) = withContext(Dispatchers.IO) {
        snookerDbDao.apply {
            deleteCurrentFrame(frameId)
            deleteCurrentFrameScore(frameId)
            val currentBreaks = getCurrentFrameBreaks(frameId)
            currentBreaks.forEach {
                deleteCurrentBreakPots(it.breakId)
            }
            deleteCurrentFrameBreaks(frameId)
            deleteCurrentFrameBalls(frameId)
        }
    }


    private val frameCount: MutableLiveData<Int> = MutableLiveData()
    fun searchByCount(frameId: Int) {
        frameCount.value = frameId
    }

    val crtFrame: LiveData<DbFrameWithScoreAndBreakWithPotsAndBallStack?> = Transformations.switchMap(frameCount) { frameId ->
        snookerDbDao.getCurrentFrame(frameId)
    }

    val score: LiveData<ArrayList<Pair<DomainPlayerScore, DomainPlayerScore>>> = Transformations.map(snookerDbDao.getMatchScore()) {
        it.asDomainFrameScoreList()
    }
}