package com.quickpoint.snookerboard.data.database.dao

import androidx.room.*
import com.quickpoint.snookerboard.data.database.SnookerDatabase.Companion.TABLE_MATCH_BREAKS
import com.quickpoint.snookerboard.data.database.models.DbBreak

@Dao
interface DaoDbBreak {
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

    @Query("SELECT * FROM $TABLE_MATCH_BREAKS WHERE frameId = :frameId ORDER by breakId ASC")
    fun getCrtFrameBreaks(frameId: Long): List<DbBreak>

    @Query("DELETE FROM $TABLE_MATCH_BREAKS WHERE breakId = :breakId")
    fun deleteMatchBreak(breakId: Long)

    @Query("DELETE FROM $TABLE_MATCH_BREAKS")
    fun clear(): Int

    @Query("DELETE FROM $TABLE_MATCH_BREAKS WHERE frameId = :frameId")
    fun deleteCrtFrameBreaks(frameId: Long)
}