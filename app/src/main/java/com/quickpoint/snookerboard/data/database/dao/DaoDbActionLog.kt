package com.quickpoint.snookerboard.data.database.dao

import androidx.room.*
import com.quickpoint.snookerboard.data.database.SnookerDatabase.Companion.TABLE_MATCH_ACTION_LOG
import com.quickpoint.snookerboard.data.database.models.DbActionLog

@Dao
interface DaoDbActionLog {
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

    @Query("SELECT * FROM $TABLE_MATCH_ACTION_LOG")
    fun getDebugFrameActions(): List<DbActionLog>

    @Query("DELETE FROM $TABLE_MATCH_ACTION_LOG WHERE frameId = :frameId")
    fun deleteCrtFrameActions(frameId: Long)

    @Query("DELETE FROM $TABLE_MATCH_ACTION_LOG")
    fun clear(): Int
}