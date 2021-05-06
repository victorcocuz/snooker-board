package com.example.snookerscore.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SnookerDatabaseDao {

    // Ranking
//    @Insert
//    fun insertRanking(ranking: DatabaseRanking)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAllRankings(vararg rankings: DatabaseRanking)
//
//    @Update
//    fun updateRanking(ranking: DatabaseRanking)
//
//    @Query("SELECT * from rankings_table WHERE position = :id")
//    fun getRanking (id: Int): DatabaseRanking
//
//    @Query("DELETE FROM rankings_table")
//    fun clearRanking()
//
//    @Query("SELECT * FROM rankings_table ORDER BY position ASC")
//    fun getAllRankings(): LiveData<List<DatabaseRanking>>

    // Score
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertMatchScore(vararg frame: DbScore)
//
//    @Query("SELECT * FROM match_score_table ORDER BY frameId ASC")
//    fun getMatchScore(): LiveData<List<DbScore>>
//
//    @Query("DELETE FROM match_score_table")
//    fun deleteMatchScore()

    // Totals
    @Query("SELECT SUM(framePoints) FROM match_score_table WHERE playerId = :id")
    fun getSumOfFramePoints(id: Int) : Int

    @Query("SELECT MAX(matchPoints) FROM match_score_table WHERE playerId = :id")
    fun getMaxMatchPoints(id: Int) : Int

    @Query("SELECT SUM(successShots) FROM match_score_table WHERE playerId = :id")
    fun getSumOfSuccessShots(id: Int) : Int

    @Query("SELECT SUM(missedShots) FROM match_score_table WHERE playerId = :id")
    fun getSumOfMissedShots(id: Int) : Int

    @Query("SELECT SUM(fouls) FROM match_score_table WHERE playerId = :id")
    fun getSumOfFouls(id: Int) : Int

    @Query("SELECT MAX(highestBreak) FROM match_score_table WHERE playerId = :id")
    fun getMaxBreak(id: Int) : Int

    // Current Match Frame
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatchFrame(vararg frame: DbFrame)

    @Transaction
    @Query("SELECT * FROM match_frames_table")
    fun getMatchFrames(): LiveData<List<DbFrameWithScoreAndBreakWithPotsAndBallStack>>

    @Transaction
    @Query("SELECT * FROM match_frames_table ORDER BY frameId ASC LIMIT 1")
    fun getCrtFrame(): LiveData<DbFrameWithScoreAndBreakWithPotsAndBallStack>

    @Transaction
    @Query("SELECT * FROM match_frames_table WHERE frameId = :frameId")
    fun getFrameById(frameId: Int): LiveData<DbFrameWithScoreAndBreakWithPotsAndBallStack>

    @Query("DELETE FROM match_frames_table")
    fun deleteMatchFrames()

    @Query("DELETE FROM match_frames_table WHERE frameId = (SELECT max(frameId) FROM match_frames_table)")
    fun deleteCurrentFrame()

    // Current Match Score
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatchScore(score: List<DbScore>)

    @Query("SELECT * FROM match_score_table ORDER BY frameId ASC")
    fun getMatchScore(): LiveData<List<DbScore>>

    @Query("DELETE FROM match_score_table WHERE frameId = (SELECT max(frameId) FROM match_score_table)")
    fun deleteCurrentFrameScore()

    @Query("DELETE FROM match_score_table")
    fun deleteMatchScore()

    // Current Match Breaks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatchBreaks(matchBreaks: List<DbBreak>)

    @Query("SELECT * FROM match_breaks_table WHERE frameId = (SELECT max(frameId) FROM match_breaks_table)")
    fun getCurrentFrameBreaks(): List<DbBreak>

    @Query("DELETE FROM match_breaks_table")
    fun deleteMatchBreaks()

    @Query("DELETE FROM match_breaks_table WHERE frameId = (SELECT max(frameId) FROM match_breaks_table)")
    fun deleteCurrentFrameBreaks()

    // Current Match Pots
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBreakPots(matchPots: List<DbPot>)

    @Query("DELETE FROM match_pots_table")
    fun deleteBreakPots()

    @Query("DELETE FROM match_pots_table WHERE breakId = :breakId")
    fun deleteCurrentBreakPots(breakId: Int)

    // Current Match Balls
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatchBalls(matchBalls: List<DbBall>)

    @Query("DELETE FROM match_ball_stack_table")
    fun deleteMatchBalls()

    @Query("DELETE FROM match_ball_stack_table WHERE frameId = (SELECT max(frameId) FROM match_ball_stack_table)")
    fun deleteCurrentFrameBalls()
}