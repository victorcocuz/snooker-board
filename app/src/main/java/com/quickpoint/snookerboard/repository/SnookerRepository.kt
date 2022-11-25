package com.quickpoint.snookerboard.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.quickpoint.snookerboard.database.DbFrameWithScoreAndBreakWithPotsAndBallStack
import com.quickpoint.snookerboard.database.SnookerDatabase
import com.quickpoint.snookerboard.database.asDomainActionLogs
import com.quickpoint.snookerboard.database.asDomainFrameScoreList
import com.quickpoint.snookerboard.domain.DomainActionLog
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.domain.DomainScore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

// Used to connect database to domain packages
class SnookerRepository(database: SnookerDatabase) {

    private val snookerDbDao = database.snookerDatabaseDao

    // Return total score for end of game statistics
    suspend fun getTotals(playerId: Int): DomainScore {
        return withContext(Dispatchers.IO) {
            return@withContext DomainScore(
                -2,
                -2,
                playerId,
                snookerDbDao.getSumOfFramePoints(playerId),
                snookerDbDao.getMaxMatchPoints(playerId),
                snookerDbDao.getSumOfSuccessShots(playerId),
                snookerDbDao.getSumOfMissedShots(playerId),
                snookerDbDao.getSumOfSafetySuccessShots(playerId),
                snookerDbDao.getSumOfSafetyMissedShots(playerId),
                snookerDbDao.getSumOfSnookers(playerId),
                snookerDbDao.getSumOfFouls(playerId),
                snookerDbDao.getMaxBreak(playerId)
            )
        }
    }

    // Get crt frame from database
    suspend fun getCrtFrame(): DbFrameWithScoreAndBreakWithPotsAndBallStack? {
        return withContext(Dispatchers.IO) {
//            val frameCount = snookerDbDao.getMatchFrameCount()
//            Timber.e("GET: frame count: $frameCount")
//            Timber.e("GET: score: ${snookerDbDao.getCrtScore(0)?.framePoints}-${snookerDbDao.getCrtScore(1)?.framePoints}")
//            val frameBreaks = snookerDbDao.getCurrentFrameBreaks(frameCount.toLong())
//            Timber.e("GET: breaks: $frameBreaks")
//            frameBreaks.lastOrNull()?.breakId?.let {
//                Timber.e("GET: last break id: $it")
//                Timber.e("GET: pots count: ${snookerDbDao.getCurrentBreakPotsCount(it)}")
//            }
//            Timber.e("GET: pots: ${frameBreaks.lastOrNull()?.breakId?.let { snookerDbDao.getCurrentBreakPots(it) }}")
//            Timber.e("GET: ball count is: ${snookerDbDao.getMatchBallsCount()}")
//            Timber.e("GET: debug actions: ${snookerDbDao.getDebugFrameActions()}")
            return@withContext snookerDbDao.getCrtFrame()
        }
    }

    suspend fun saveCurrentFrame(frame: DomainFrame) = withContext(Dispatchers.IO) {
        snookerDbDao.apply {
            // Frame
            insertOrUpdateMatchFrame(frame.asDbFrame())

            // Score
            for (dbScore in frame.asDbCrtScore()) {
//                Timber.e("update score $dbScore")
                insertOrUpdateMatchScore(dbScore)
            }

            // Breaks - Only check breaks from current frame
            val dbBreaks = frame.asDbBreaks()
            dbBreaks.lastOrNull()?.let { dbLastBreak ->
//                Timber.e("update break $dbLastBreak")
                insertOrUpdateMatchBreak(dbLastBreak)
            }

            for (dbBreak in getCurrentFrameBreaks(frame.frameId)) { // If break exists in frameStack, but not in Db, remove from Db
                if (!dbBreaks.map { it.breakId }.contains(dbBreak.breakId)) {
//                    Timber.e("delete break $dbBreak")
                    deleteMatchBreak(dbBreak.breakId)
                }
            }

            // Pots - only check pots from current break
            frame.frameStack.lastOrNull()?.apply {
                val dbBreakPots = this.asDbPots(this.breakId)
                dbBreakPots.lastOrNull()?.let { dbBreakPot ->
//                    Timber.e("update pot $dbBreakPot")
                    insertOrUpdateBreakPot(dbBreakPot)
                }
                for (dbPot in getCurrentBreakPots(this.breakId)) { // If pot exists in break, but not in Db, remove from Db
                    if (!dbBreakPots.map { it.potId }.contains(dbPot.potId)) {
//                        Timber.e("delete pot $dbPot")
                        deleteBreakPot(dbPot.potId)
                    }
                }
            }

            // Ballstack - Only check balls for current frame
            val dbBallStack = frame.asDbBallStack()
            for (dbBall in dbBallStack) insertOrUpdateMatchBall(dbBall)
            for (dbBallId in getMatchBalls().map { it.ballId }) { // If ball exists in ballStack, but not in Db, remove from Db
                if (!dbBallStack.map { it.ballId }.contains(dbBallId)) deleteMatchBall(dbBallId)
            }

            // Debug Actions - Only check actions for current frame
            frame.asDbDebugFrameActions().lastOrNull()?.let{
//                Timber.e("action $it")
                insertOrUpdateDebugFrameActions(it)
            }
        }
        Timber.i("saveCurrentFrame(): id: ${frame.frameId} score: ${frame.score[0].framePoints} vs ${frame.score[1].framePoints} ")
    }

    // Delete the latest frame from the database
    suspend fun deleteCurrentFrame(frameId: Long) = withContext(Dispatchers.IO) {
        snookerDbDao.apply {
            deleteCurrentFrame(frameId)
            deleteCurrentFrameScore(frameId)
            val currentBreaks = getCurrentFrameBreaks(frameId)
            currentBreaks.forEach {
                deleteCurrentBreakPots(it.breakId)
            }
            deleteCurrentFrameBreaks(frameId)
            deleteCurrentFrameBalls(frameId)
            deleteCurrentDebugFrameActions(frameId)
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
            deleteDebugFrameActions()
        }
        Timber.i("deleteCurrentMatch()")
    }

    // Get the current score from the database
    val score: LiveData<ArrayList<Pair<DomainScore, DomainScore>>> = Transformations.map(snookerDbDao.getMatchScore()) {
        it.asDomainFrameScoreList()
    }

    // Get debug action list
    suspend fun getDebugFrameActionList(): List<DomainActionLog> {
        return withContext(Dispatchers.IO) {
            return@withContext snookerDbDao.getDebugFrameActions().asDomainActionLogs()
        }
    }
}