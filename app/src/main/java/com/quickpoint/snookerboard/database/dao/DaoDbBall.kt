package com.quickpoint.snookerboard.database.dao

import androidx.room.*
import com.quickpoint.snookerboard.database.SnookerDatabase.Companion.TABLE_MATCH_BALL
import com.quickpoint.snookerboard.database.models.DbBall

@Dao
interface DaoDbBall {
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

    @Query("SELECT * FROM $TABLE_MATCH_BALL ORDER by ballId ASC")
    fun getMatchBalls(): List<DbBall>

    @Query("DELETE FROM $TABLE_MATCH_BALL WHERE ballId = :ballId")
    fun deleteMatchBall(ballId: Long)

    @Query("DELETE FROM $TABLE_MATCH_BALL")
    fun clear(): Int

    @Query("DELETE FROM $TABLE_MATCH_BALL WHERE frameId = :frameId")
    fun deleteCrtFrameBalls(frameId: Long)
}