package com.quickpoint.snookerboard.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SnookerDatabaseDao {
    // General
    @Query("SELECT (SELECT COUNT(*) FROM match_frames_table) == 0")
    fun isEmpty(): Boolean

    // Current Match Frame
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatchFrame(vararg frame: DbFrame)

    @Query("SELECT COUNT(*) FROM match_frames_table")
    fun getMatchFrameCount(): LiveData<Int>

    @Transaction
    @Query("SELECT * FROM match_frames_table")
    fun getMatchFrames(): LiveData<List<DbFrameWithScoreAndBreakWithPotsAndBallStack>>

    @Transaction
    @Query("SELECT * FROM match_frames_table WHERE frameId = (SELECT MAX(frameId) FROM match_frames_table)")
    fun getCrtFrame(): DbFrameWithScoreAndBreakWithPotsAndBallStack?

    @Transaction
    @Query("SELECT * FROM match_frames_table WHERE frameId = :frameId")
    fun getCurrentFrame(frameId: Int): LiveData<DbFrameWithScoreAndBreakWithPotsAndBallStack?>

    @Transaction
    @Query("SELECT * FROM match_frames_table WHERE frameId = :frameId")
    fun getFrameById(frameId: Int): LiveData<DbFrameWithScoreAndBreakWithPotsAndBallStack>

    @Query("DELETE FROM match_frames_table")
    fun deleteMatchFrames()

    @Query("DELETE FROM match_frames_table WHERE frameId = :frameId")
    fun deleteCurrentFrame(frameId: Int)

    // Current Match Score
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatchScore(score: List<DbScore>)

    @Query("SELECT * FROM match_score_table ORDER BY frameId ASC")
    fun getMatchScore(): LiveData<List<DbScore>>

    @Query("DELETE FROM match_score_table WHERE frameId = :frameId")
    fun deleteCurrentFrameScore(frameId: Int)

    @Query("DELETE FROM match_score_table")
    fun deleteMatchScore()

    // Current Match Breaks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatchBreaks(matchBreaks: List<DbBreak>): List<Long>

    @Query("SELECT * FROM match_breaks_table WHERE frameId = :frameId")
    fun getCurrentFrameBreaks(frameId: Int): List<DbBreak>

    @Query("DELETE FROM match_breaks_table")
    fun deleteMatchBreaks()

    @Query("DELETE FROM match_breaks_table WHERE frameId = :frameId")
    fun deleteCurrentFrameBreaks(frameId: Int)

    // Current Match Pots
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBreakPots(matchPots: List<DbPot>)

    @Query("DELETE FROM match_pots_table")
    fun deleteBreakPots()

    @Query("DELETE FROM match_pots_table WHERE breakId = :breakId")
    fun deleteCurrentBreakPots(breakId: Long)

    // Current Match Balls
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatchBalls(matchBalls: List<DbBall>)

    @Query("DELETE FROM match_ball_stack_table")
    fun deleteMatchBalls()

    @Query("DELETE FROM match_ball_stack_table WHERE frameId = :frameId")
    fun deleteCurrentFrameBalls(frameId: Int)

    // Totals
    @Query("SELECT SUM(framePoints) FROM match_score_table WHERE playerId = :id")
    fun getSumOfFramePoints(id: Int): Int

    @Query("SELECT MAX(matchPoints) FROM match_score_table WHERE playerId = :id")
    fun getMaxMatchPoints(id: Int): Int

    @Query("SELECT SUM(successShots) FROM match_score_table WHERE playerId = :id")
    fun getSumOfSuccessShots(id: Int): Int

    @Query("SELECT SUM(missedShots) FROM match_score_table WHERE playerId = :id")
    fun getSumOfMissedShots(id: Int): Int

    @Query("SELECT SUM(safetySuccessShots) FROM match_score_table WHERE playerId = :id")
    fun getSumOfSafetySuccessShots(id: Int): Int

    @Query("SELECT SUM(safetyMissedShots) FROM match_score_table WHERE playerId = :id")
    fun getSumOfSafetyMissedShots(id: Int): Int

    @Query("SELECT SUM(snookers) FROM match_score_table WHERE playerId = :id")
    fun getSumOfSnookers(id: Int): Int

    @Query("SELECT SUM(fouls) FROM match_score_table WHERE playerId = :id")
    fun getSumOfFouls(id: Int): Int

    @Query("SELECT MAX(highestBreak) FROM match_score_table WHERE playerId = :id")
    fun getMaxBreak(id: Int): Int
}