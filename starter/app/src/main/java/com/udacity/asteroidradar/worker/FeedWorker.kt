package com.udacity.asteroidradar.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.di.ServiceLocator
import retrofit2.HttpException

class FeedWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val repo = ServiceLocator.provideAsteroidsRepository(applicationContext)
        return try {
            Log.d(WORK_NAME, "Fetching...")
            repo.fetchAndSaveAsteroids()
            Log.d(WORK_NAME, "Done!")
            Result.success()
        } catch (e: HttpException) {
            Log.d(WORK_NAME, "Retry!")
            Result.retry()
        } catch (e: Exception) {
            Log.d(WORK_NAME, "Failure!")
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "FeedWorker"
    }

}