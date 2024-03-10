package com.quickpoint.snookerboard.data.repository

import com.quickpoint.snookerboard.data.database.SnookerDatabase
import com.quickpoint.snookerboard.data.database.models.asDomain
import com.quickpoint.snookerboard.domain.models.DomainAppReleaseDetails
import com.quickpoint.snookerboard.domain.models.asDbAppReleaseNotes
import com.quickpoint.snookerboard.domain.models.asDbAppReleaseVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AppReleaseDetailsRepository @Inject constructor(
    database: SnookerDatabase,
) {
    private val daoDbVersionDetails = database.daoDbAppReleaseVersion
    private val daoDbAppReleaseNotes = database.daoDbAppReleaseNotes

    var releaseDetails = daoDbVersionDetails.getAppReleaseDetails().map { it.asDomain() }

    suspend fun saveReleaseDetails(versionDetails: List<DomainAppReleaseDetails>) = withContext(Dispatchers.IO) {
        versionDetails.forEach { appReleaseDetails ->
            daoDbVersionDetails.insertAppReleaseVersion(appReleaseDetails.asDbAppReleaseVersion())
            appReleaseDetails.asDbAppReleaseNotes().forEach {
                Timber.e("$it")
                daoDbAppReleaseNotes.insertAppReleaseNotes(it) }
        }
    }
}