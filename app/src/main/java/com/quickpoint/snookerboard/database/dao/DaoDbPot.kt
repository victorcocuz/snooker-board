package com.quickpoint.snookerboard.database.dao

import androidx.room.*
import com.quickpoint.snookerboard.database.SnookerDatabase.Companion.TABLE_MATCH_POTS
import com.quickpoint.snookerboard.database.models.DbPot

@Dao
interface DaoDbPot {
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

    @Query("SELECT * FROM $TABLE_MATCH_POTS WHERE breakId = :breakId ORDER by potId ASC")
    fun getCrtBreakPots(breakId: Long): List<DbPot>

    @Query("DELETE FROM $TABLE_MATCH_POTS WHERE potId = :potId")
    fun deleteBreakPot(potId: Long)

    @Query("DELETE FROM $TABLE_MATCH_POTS")
    fun clear(): Int

    @Query("DELETE FROM $TABLE_MATCH_POTS WHERE breakId = :breakId")
    fun deleteCrtBreakPots(breakId: Long)
}