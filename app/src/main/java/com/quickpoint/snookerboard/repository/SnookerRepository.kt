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
import com.quickpoint.snookerboard.utils.MatchSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

// Used to connect database to domain packages
class SnookerRepository constructor(database: SnookerDatabase) {

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
                snookerDbDao.getMaxBreak(playerId),
                snookerDbDao.getSumOfLongShotsSuccess(playerId),
                snookerDbDao.getSumOfLongShotsMissed(playerId),
                snookerDbDao.getSumOfRestShotsSuccess(playerId),
                snookerDbDao.getSumOfRestShotsMissed(playerId),
                snookerDbDao.getMaxPointsWithNoReturn(playerId)
            )
        }
    }

    // Get crt frame from database
    suspend fun getCrtFrame(): DbFrameWithScoreAndBreakWithPotsAndBallStack? {
        return withContext(Dispatchers.IO) {
            val crtFrame = snookerDbDao.getCrtFrame()
            Timber.i("getCrtFrame(): State is: ${MatchSettings.SETTINGS.matchState}, CrtFrame is: ${crtFrame?.frame?.frameId}, frameCount is: ${MatchSettings.SETTINGS.crtFrame}")
            return@withContext crtFrame
        }
    }

    suspend fun saveCurrentFrame(frame: DomainFrame) = withContext(Dispatchers.IO) {
        snookerDbDao.apply {
            // Frame
            insertOrUpdateMatchFrame(frame.asDbFrame())

            // Score
            for (dbScore in frame.asDbCrtScore()) insertOrUpdateMatchScore(dbScore)

            // Breaks - Only check breaks from current frame
            val dbBreaks = frame.asDbBreaks()
            dbBreaks.lastOrNull()?.let { insertOrUpdateMatchBreak(it) }

            for (dbBreakId in getCurrentFrameBreaks(frame.frameId).map { it.breakId }) { // If break exists in frameStack, but not in Db, remove from Db
                if (!dbBreaks.map { it.breakId }.contains(dbBreakId)) deleteMatchBreak(dbBreakId)
            }

            // Pots - only check pots from current break
            frame.frameStack.lastOrNull()?.apply {
                val dbBreakPots = asDbPots(breakId)
                dbBreakPots.lastOrNull()?.let { insertOrUpdateBreakPot(it) }
                for (dbPotId in getCurrentBreakPots(breakId).map { it.potId }) { // If pot exists in break, but not in Db, remove from Db
                    if (!dbBreakPots.map { it.potId }.contains(dbPotId)) deleteBreakPot(dbPotId)
                }
            }

            // Ballstack - Only check balls for current frame
            val dbBallStack = frame.asDbBallStack()
            for (dbBall in dbBallStack) insertOrUpdateMatchBall(dbBall)
            for (dbBallId in getMatchBalls().map { it.ballId }) { // If ball exists in ballStack, but not in Db, remove from Db
                if (!dbBallStack.map { it.ballId }.contains(dbBallId)) deleteMatchBall(dbBallId)
            }

            // Debug Actions - Only check actions for current frame
            frame.asDbDebugFrameActions().lastOrNull()?.let { insertOrUpdateDebugFrameActions(it) }
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