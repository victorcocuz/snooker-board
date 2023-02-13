package com.quickpoint.snookerboard

import android.app.Application
import android.content.Context
import com.quickpoint.snookerboard.database.SnookerDatabase
import com.quickpoint.snookerboard.repository.SnookerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

// Override the Application class to add plugins and setup recurring work
@Suppress("unused")
class SnookerApp : Application() {

    init {
        instance = this
    }

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    companion object {
        private var instance: SnookerApp? = null
        private fun applicationContext(): Context = instance!!.applicationContext
        fun application(): Application = instance!!
        fun repository() = SnookerRepository(SnookerDatabase.getDatabase(applicationContext()))
    }

    // Apply all plugins and setup - reinstate delayed recurring work when needed
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.i(getString(R.string.helper_first_line))
    }
}