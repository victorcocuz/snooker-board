package com.quickpoint.snookerboard.data.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.quickpoint.snookerboard.data.database.SnookerDatabase
import com.quickpoint.snookerboard.domain.models.DomainAppReleaseDetails

@Entity(tableName = SnookerDatabase.TABLE_APP_RELEASE_VERSION)
data class DbAppReleaseVersion(
    @PrimaryKey(autoGenerate = false)
    val versionNumber: String
)

data class DbAppReleaseVersionWithAppReleaseNotes(
    @Embedded val appReleaseVersion: DbAppReleaseVersion,
    @Relation(
        parentColumn = "versionNumber",
        entityColumn = "versionNumber"
    )
    val appReleaseNotes: List<DbAppReleaseNotes>
)

fun List<DbAppReleaseVersionWithAppReleaseNotes>.asDomain(): MutableList<DomainAppReleaseDetails> =
    map {
        DomainAppReleaseDetails(
            versionNumber= it.appReleaseVersion.versionNumber,
            notes = it.appReleaseNotes.asDomain()
        )
    }.toMutableList()