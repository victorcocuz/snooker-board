package com.quickpoint.snookerboard.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    // This is meant to refresh the data in the app once in a while in the background. not needed at the moment
    override suspend fun doWork(): Result {
//        val database = getDatabase(applicationContext)
//        val repository = SnookerRepository(database)

        return try {
//            repository.refreshRankings()
            Result.success()
        } catch (exception: HttpException) {
            Result.retry()
        }
    }
}