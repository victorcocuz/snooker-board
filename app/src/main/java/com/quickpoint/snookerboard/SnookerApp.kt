package com.quickpoint.snookerboard

import android.app.Application
import com.quickpoint.snookerboard.core.utils.parseChangelog
import com.quickpoint.snookerboard.data.database.SnookerDatabase
import com.quickpoint.snookerboard.data.repository.AppReleaseDetailsRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SnookerApp : Application() {
    @Inject
    lateinit var database: SnookerDatabase

    @Inject
    lateinit var versionsRepository: AppReleaseDetailsRepository

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.i(getString(R.string.helper_first_line))

        CoroutineScope(SupervisorJob()).launch {
            versionsRepository.saveReleaseDetails(parseChangelog(applicationContext))
        }
    }
}