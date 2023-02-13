package com.quickpoint.snookerboard.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.quickpoint.snookerboard.database.SnookerDatabase.Companion.TABLE_MATCH_SCORE
import com.quickpoint.snookerboard.database.models.DbScore

@Dao
interface DaoDbScore {
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

    @Query("SELECT * FROM $TABLE_MATCH_SCORE ORDER BY frameId ASC")
    fun getMatchScore(): LiveData<List<DbScore>>

    @Query("DELETE FROM $TABLE_MATCH_SCORE WHERE frameId = :frameId")
    fun deleteCrtFrameScore(frameId: Long)

    @Query("DELETE FROM $TABLE_MATCH_SCORE")
    fun clear(): Int

    // Totals
    @Query("SELECT SUM(framePoints) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getSumOfFramePoints(playerId: Int): Int

    @Query("SELECT MAX(matchPoints) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getMaxMatchPoints(playerId: Int): Int

    @Query("SELECT SUM(successShots) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getSumOfSuccessShots(playerId: Int): Int

    @Query("SELECT SUM(missedShots) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getSumOfMissedShots(playerId: Int): Int

    @Query("SELECT SUM(safetySuccessShots) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getSumOfSafetySuccessShots(playerId: Int): Int

    @Query("SELECT SUM(safetyMissedShots) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getSumOfSafetyMissedShots(playerId: Int): Int

    @Query("SELECT SUM(snookers) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getSumOfSnookers(playerId: Int): Int

    @Query("SELECT SUM(fouls) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getSumOfFouls(playerId: Int): Int

    @Query("SELECT MAX(highestBreak) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getMaxBreak(playerId: Int): Int

    @Query("SELECT SUM(longShotsSuccess) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getSumOfLongShotsSuccess(playerId: Int): Int

    @Query("SELECT SUM(longShotsMissed) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getSumOfLongShotsMissed(playerId: Int): Int

    @Query("SELECT SUM(restShotsSuccess) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getSumOfRestShotsSuccess(playerId: Int): Int

    @Query("SELECT SUM(restShotsMissed) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getSumOfRestShotsMissed(playerId: Int): Int

    @Query("SELECT MAX(pointsWithNoReturn) FROM $TABLE_MATCH_SCORE WHERE playerId = :playerId")
    fun getMaxPointsWithNoReturn(playerId: Int): Int
}