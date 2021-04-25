package com.example.snookerscore.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SnookerDatabaseDao {

    // Ranking
    @Insert
    fun insertRanking(ranking: DatabaseRanking)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllRankings(vararg rankings: DatabaseRanking)

    @Update
    fun updateRanking(ranking: DatabaseRanking)

    @Query("SELECT * from rankings_table WHERE position = :id")
    fun getRanking (id: Int): DatabaseRanking

    @Query("DELETE FROM rankings_table")
    fun clearRanking()

    @Query("SELECT * FROM rankings_table ORDER BY position ASC")
    fun getAllRankings(): LiveData<List<DatabaseRanking>>

    // Score
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatchScore(vararg frame: DatabaseScore)

    @Query("SELECT * FROM match_score_table ORDER BY frameCount ASC")
    fun getMatchScore(): LiveData<List<DatabaseScore>>

    @Query("DELETE FROM match_score_table")
    fun deleteMatchScore()

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

    // Current Match
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCrtScore(frame: DatabaseCrtScore)

    @Query("SELECT * FROM current_score_table")
    fun getCrtScore(): LiveData<List<DatabaseCrtScore>>

    @Query("DELETE FROM current_score_table")
    fun deleteCrtScore()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCrtBreaks(matchBreaks: List<DatabaseBreak>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCrtPots(matchPots: List<DatabasePot>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCrtBalls(matchBalls: List<DatabaseBall>)

    @Query("SELECT * FROM match_breaks_table ")
    fun getCrtBreaks(): LiveData<List<BreakWithPots>>

    @Query("SELECT * FROM match_ball_stack_table")
    fun getBallStack(): LiveData<List<DatabaseBall>>

    @Query("DELETE FROM match_breaks_table")
    fun deleteCrtBreaks()

    @Query("DELETE FROM match_pots_table")
    fun deleteCrtPots()

    @Query("DELETE FROM match_ball_stack_table")
    fun deleteCrtBallStack()
}