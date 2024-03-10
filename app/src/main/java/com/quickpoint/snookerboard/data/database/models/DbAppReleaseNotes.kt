package com.quickpoint.snookerboard.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quickpoint.snookerboard.data.database.SnookerDatabase

@Entity(tableName = SnookerDatabase.TABLE_APP_RELEASE_NOTES)
data class DbAppReleaseNotes(
    @PrimaryKey(autoGenerate = false)
    val notes: String,
    val versionNumber: String
)

fun List<DbAppReleaseNotes>.asDomain(): MutableList<String> = map { it.notes }.toMutableList()