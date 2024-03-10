package com.quickpoint.snookerboard.domain.models

import com.quickpoint.snookerboard.data.database.models.DbAppReleaseNotes
import com.quickpoint.snookerboard.data.database.models.DbAppReleaseVersion

data class DomainAppReleaseDetails(
    val versionNumber: String,
    val notes: MutableList<String>,
)

fun DomainAppReleaseDetails.asDbAppReleaseVersion() = DbAppReleaseVersion(
    versionNumber = this.versionNumber,
)

fun DomainAppReleaseDetails.asDbAppReleaseNotes(): List<DbAppReleaseNotes> = notes.map {
    DbAppReleaseNotes(
        versionNumber = this.versionNumber,
        notes = it
    )
}
