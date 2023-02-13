package com.quickpoint.snookerboard.database.dao

import androidx.room.*
import com.quickpoint.snookerboard.database.SnookerDatabase.Companion.TABLE_MATCH_FRAMES
import com.quickpoint.snookerboard.database.models.DbFrame
import com.quickpoint.snookerboard.database.models.DbFrameWithScoreAndBreakWithPotsAndBallStack

@Dao
interface DaoDbFrame {
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
    @Query("SELECT * FROM $TABLE_MATCH_FRAMES WHERE frameId = (SELECT MAX(frameId) FROM $TABLE_MATCH_FRAMES)")
    fun getCrtFrame(): DbFrameWithScoreAndBreakWithPotsAndBallStack?

    @Query("DELETE FROM $TABLE_MATCH_FRAMES")
    fun clear(): Int

    @Query("DELETE FROM $TABLE_MATCH_FRAMES WHERE frameId = :frameId")
    fun deleteCrtFrame(frameId: Long)
}