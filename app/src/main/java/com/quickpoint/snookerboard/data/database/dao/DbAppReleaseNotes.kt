package com.quickpoint.snookerboard.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.quickpoint.snookerboard.data.database.SnookerDatabase
import com.quickpoint.snookerboard.data.database.models.DbAppReleaseNotes
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoDbAppReleaseNotes {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAppReleaseNotes(notes: DbAppReleaseNotes): Long

    @Update
    fun updateAppReleaseNotes(notes: DbAppReleaseNotes): Int

    @Transaction
    suspend fun insertOrUpdateAppReleaseNotes(notes: DbAppReleaseNotes): Long {
        val id = insertAppReleaseNotes(notes)
        return if (id==-1L) {
            updateAppReleaseNotes(notes)
            id
        } else {
           id
        }
    }

    @Query("SELECT * FROM ${SnookerDatabase.TABLE_APP_RELEASE_NOTES} ORDER by versionNumber DESC")
    fun getAppReleaseNotes(): Flow<List<DbAppReleaseNotes>>

    @Query("DELETE FROM ${SnookerDatabase.TABLE_APP_RELEASE_NOTES}")
    fun clear(): Int

    @Query("DELETE FROM ${SnookerDatabase.TABLE_APP_RELEASE_NOTES} WHERE notes = :notes")
    fun deleteCrtAppReleaseNotes(notes: String)
}