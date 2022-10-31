package com.quickpoint.snookerboard.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.quickpoint.snookerboard.database.DbFrameWithScoreAndBreakWithPotsAndBallStack
import com.quickpoint.snookerboard.database.SnookerDatabase
import com.quickpoint.snookerboard.database.asDomainFrameScoreList
import com.quickpoint.snookerboard.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

// Used to connect database to domain packages
class SnookerRepository(database: SnookerDatabase) {

    private val snookerDbDao = database.snookerDatabaseDao

    // Rankings - this is not in use anymore
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

    // Check if database is empty

    // Return total score for end of game statistics
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

    // Save the latest frame to the database
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
        Timber.i("saveCurrentFrame(): id: ${frame.frameId} score: ${frame.frameScore[0].framePoints} vs ${frame.frameScore[1].framePoints} ")
    }

    // Delete the latest frame from the database
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
        Timber.i("Delete current frame $frameId")
    }

    // Delete match from database
    suspend fun deleteCurrentMatch() = withContext(Dispatchers.IO) {
        snookerDbDao.apply {
            deleteMatchScore()
            deleteMatchBreaks()
            deleteBreakPots()
            deleteMatchBalls()
            deleteMatchFrames()
        }
    }

    val crtFrame: LiveData<DbFrameWithScoreAndBreakWithPotsAndBallStack?> = snookerDbDao.getCrtFrame()

    // Get the current score from the database
    val score: LiveData<ArrayList<Pair<DomainPlayerScore, DomainPlayerScore>>> = Transformations.map(snookerDbDao.getMatchScore()) {
        it.asDomainFrameScoreList()
    }
    val matchFrameCount: LiveData<Int> = snookerDbDao.getMatchFrameCount()
}