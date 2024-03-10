package com.quickpoint.snookerboard.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.quickpoint.snookerboard.data.database.SnookerDatabase
import com.quickpoint.snookerboard.data.database.models.DbAppReleaseVersion
import com.quickpoint.snookerboard.data.database.models.DbAppReleaseVersionWithAppReleaseNotes
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoDbAppReleaseVersion {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAppReleaseVersion(versionNumber: DbAppReleaseVersion): Long

    @Update
    fun updateAppReleaseVersion(versionNumber: DbAppReleaseVersion): Int

    @Transaction
    suspend fun insertOrUpdateAppReleaseVersion(versionNumber: DbAppReleaseVersion): Long {
        val id = insertAppReleaseVersion(versionNumber)
        return if (id==-1L) {
            updateAppReleaseVersion(versionNumber)
            id
        } else {
            id
        }
    }

    @Query("SELECT * FROM ${SnookerDatabase.TABLE_APP_RELEASE_VERSION} ORDER by versionNumber DESC")
    fun getAppReleaseDetails(): Flow<List<DbAppReleaseVersionWithAppReleaseNotes>>

    @Query("DELETE FROM ${SnookerDatabase.TABLE_APP_RELEASE_VERSION}")
    fun clear(): Int

    @Query("DELETE FROM ${SnookerDatabase.TABLE_APP_RELEASE_VERSION} WHERE versionNumber = :versionNumber")
    fun deleteCrtAppReleaseVersion(versionNumber: String)
}