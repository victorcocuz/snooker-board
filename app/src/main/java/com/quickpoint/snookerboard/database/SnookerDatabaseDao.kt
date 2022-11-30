package com.quickpoint.snookerboard.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SnookerDatabaseDao {
    // Current Match Frame
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMatchFrame(frame: DbFrame): Long

    @Update
    fun updateMatchFrame(frame: DbFrame): Int

    @Transaction
    suspend fun insertOrUpdateMatchFrame(frame: DbFrame): Long {
        val id = insertMatchFrame(frame)
        return if (id==-1L) {
            updateMatchFrame(frame)
            frame.frameId
        } else {
            id
        }
    }

    @Transaction
    @Query("SELECT * FROM match_frames_table WHERE frameId = (SELECT MAX(frameId) FROM match_frames_table)")
    fun getCrtFrame(): DbFrameWithScoreAndBreakWithPotsAndBallStack?

    @Query("DELETE FROM match_frames_table")
    fun deleteMatchFrames(): Int

    @Query("DELETE FROM match_frames_table WHERE frameId = :frameId")
    fun deleteCurrentFrame(frameId: Long)

    // Current Match Score
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMatchScore(score: DbScore): Long

    @Update
    fun updateMatchScore(score: DbScore): Int

    @Transaction
    suspend fun insertOrUpdateMatchScore(score: DbScore): Long {
        val id = insertMatchScore(score)
        return if (id==-1L) {
            updateMatchScore(score)
            score.frameId
        } else {
            id
        }
    }

    @Query("SELECT * FROM match_score_table ORDER BY frameId ASC")
    fun getMatchScore(): LiveData<List<DbScore>>

    @Query("DELETE FROM match_score_table WHERE frameId = :frameId")
    fun deleteCurrentFrameScore(frameId: Long)

    @Query("DELETE FROM match_score_table")
    fun deleteMatchScore(): Int

    // Current Match Breaks
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMatchBreak(matchBreak: DbBreak): Long

    @Update
    fun updateMatchBreak(matchBreak: DbBreak): Int

    @Transaction
    suspend fun insertOrUpdateMatchBreak(matchBreak: DbBreak): Long {
        val id = insertMatchBreak(matchBreak)
        return if (id==-1L) {
            updateMatchBreak(matchBreak)
            matchBreak.breakId
        } else {
            id
        }
    }

    @Query("SELECT * FROM match_breaks_table WHERE frameId = :frameId ORDER by breakId ASC")
    fun getCurrentFrameBreaks(frameId: Long): List<DbBreak>

    @Query("DELETE FROM match_breaks_table WHERE breakId = :breakId")
    fun deleteMatchBreak(breakId: Long)

    @Query("DELETE FROM match_breaks_table")
    fun deleteMatchBreaks(): Int

    @Query("DELETE FROM match_breaks_table WHERE frameId = :frameId")
    fun deleteCurrentFrameBreaks(frameId: Long)

    // Current Match Pots
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBreakPot(matchPot: DbPot): Long

    @Update
    fun updateBreakPot(matchPot: DbPot): Int

    @Transaction
    suspend fun insertOrUpdateBreakPot(matchPot: DbPot): Long {
        val id = insertBreakPot(matchPot)
        return if (id==-1L) {
            updateBreakPot(matchPot)
            matchPot.potId
        } else {
            id
        }
    }

    @Query("SELECT * FROM match_pots_table WHERE breakId = :breakId ORDER by potId ASC")
    fun getCurrentBreakPots(breakId: Long): List<DbPot>

    @Query("DELETE FROM match_pots_table WHERE potId = :potId")
    fun deleteBreakPot(potId: Long)

    @Query("DELETE FROM match_pots_table")
    fun deleteBreakPots(): Int

    @Query("DELETE FROM match_pots_table WHERE breakId = :breakId")
    fun deleteCurrentBreakPots(breakId: Long)

    // Current Match Balls
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMatchBall(matchBall: DbBall): Long

    @Update
    fun updateMatchBall(matchBall: DbBall): Int

    @Transaction
    suspend fun insertOrUpdateMatchBall(matchBall: DbBall): Long {
        val id = insertMatchBall(matchBall)
        return if (id==-1L) {
            updateMatchBall(matchBall)
            matchBall.ballId
        } else {
            id
        }
    }

    @Query("SELECT * FROM match_ball_stack_table ORDER by ballId ASC")
    fun getMatchBalls(): List<DbBall>

    @Query("DELETE FROM match_ball_stack_table WHERE ballId = :ballId")
    fun deleteMatchBall(ballId: Long)

    @Query("DELETE FROM match_ball_stack_table")
    fun deleteMatchBalls(): Int

    @Query("DELETE FROM match_ball_stack_table WHERE frameId = :frameId")
    fun deleteCurrentFrameBalls(frameId: Long)

    // Current match debug actions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDebugFrameActions(debugFrameActions: DbActionLog) : Long

    @Update
    fun updateDebugFrameActions(debugFrameActions: DbActionLog): Int

    @Transaction
    suspend fun insertOrUpdateDebugFrameActions(debugFrameActions: DbActionLog): Long {
        val id = insertDebugFrameActions(debugFrameActions)
        return if (id==-1L) {
            updateDebugFrameActions(debugFrameActions)
            debugFrameActions.actionId
        } else {
            id
        }
    }

    @Query("SELECT * FROM match_debug_action_table")
    fun getDebugFrameActions(): List<DbActionLog>

    @Query("DELETE FROM match_debug_action_table WHERE frameId = :frameId")
    fun deleteCurrentDebugFrameActions(frameId: Long)

    @Query("DELETE FROM match_debug_action_table")
    fun deleteDebugFrameActions(): Int

    // Totals
    @Query("SELECT SUM(framePoints) FROM match_score_table WHERE playerId = :playerId")
    fun getSumOfFramePoints(playerId: Int): Int

    @Query("SELECT MAX(matchPoints) FROM match_score_table WHERE playerId = :playerId")
    fun getMaxMatchPoints(playerId: Int): Int

    @Query("SELECT SUM(successShots) FROM match_score_table WHERE playerId = :playerId")
    fun getSumOfSuccessShots(playerId: Int): Int

    @Query("SELECT SUM(missedShots) FROM match_score_table WHERE playerId = :playerId")
    fun getSumOfMissedShots(playerId: Int): Int

    @Query("SELECT SUM(safetySuccessShots) FROM match_score_table WHERE playerId = :playerId")
    fun getSumOfSafetySuccessShots(playerId: Int): Int

    @Query("SELECT SUM(safetyMissedShots) FROM match_score_table WHERE playerId = :playerId")
    fun getSumOfSafetyMissedShots(playerId: Int): Int

    @Query("SELECT SUM(snookers) FROM match_score_table WHERE playerId = :playerId")
    fun getSumOfSnookers(playerId: Int): Int

    @Query("SELECT SUM(fouls) FROM match_score_table WHERE playerId = :playerId")
    fun getSumOfFouls(playerId: Int): Int

    @Query("SELECT MAX(highestBreak) FROM match_score_table WHERE playerId = :playerId")
    fun getMaxBreak(playerId: Int): Int
}