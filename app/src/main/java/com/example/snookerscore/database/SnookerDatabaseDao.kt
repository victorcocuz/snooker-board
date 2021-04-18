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

    // Frames
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFrame(vararg frame: DatabaseFrameScore)

    @Query("SELECT * FROM frames_table ORDER BY frameCount ASC")
    fun getAllFrames(): LiveData<List<DatabaseFrameScore>>

    @Query("DELETE FROM frames_table")
    fun deleteFrames()

    @Query("SELECT SUM(framePoints) FROM frames_table WHERE playerId = :id")
    fun getSumOfFramePoints(id: Int) : Int

    @Query("SELECT MAX(matchPoints) FROM frames_table WHERE playerId = :id")
    fun getMaxMatchPoints(id: Int) : Int

    @Query("SELECT SUM(successShots) FROM frames_table WHERE playerId = :id")
    fun getSumOfSuccessShots(id: Int) : Int

    @Query("SELECT SUM(missedShots) FROM frames_table WHERE playerId = :id")
    fun getSumOfMissedShots(id: Int) : Int

    @Query("SELECT SUM(fouls) FROM frames_table WHERE playerId = :id")
    fun getSumOfFouls(id: Int) : Int

    @Query("SELECT MAX(highestBreak) FROM frames_table WHERE playerId = :id")
    fun getMaxBreak(id: Int) : Int

    // Current Match
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertCrtMatch(crtMatch: DatabaseCrtMatch)
//
//    @Query( "Select * FROM current_match_table LIMIT 1")
//    fun getCurrentMatch(): LiveData<DatabaseCrtMatch>
//
//    @Query("DELETE FROM current_match_table")
//    fun deleteCrtMatch()
}