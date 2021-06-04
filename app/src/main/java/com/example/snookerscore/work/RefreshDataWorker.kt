package com.example.snookerscore.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

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