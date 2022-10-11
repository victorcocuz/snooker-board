package com.quickpoint.snookerboard

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.work.*
import com.quickpoint.snookerboard.database.SnookerDatabase
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.getSharedPref
import com.quickpoint.snookerboard.utils.loadPrefNames
import com.quickpoint.snookerboard.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

// Override the Application class to add plugins and setup recurring work
class SnookerApplication : Application() {

    init {
        instance = this
    }

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    companion object {
        private var instance: SnookerApplication? = null
        private fun applicationContext(): Context = instance!!.applicationContext
        fun getSnookerRepository() = SnookerRepository(SnookerDatabase.getDatabase(applicationContext()))
    }

    // Apply all plugins and setup delayed recurring work
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i(getString(R.string.helper_first_line))

        getSharedPref().loadPrefNames(this)

        //  delayedInit()
    }

    // Create a delayed initialization within a coroutine to setup recurring work
    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    // Within the recurring work, build a repeating request with given constraints to refresh the application
    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}