package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.worker.FeedWorker
import com.udacity.asteroidradar.worker.PictureOfDayWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AsteroidRadarApp: Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private fun delayedInit() = applicationScope.launch {
        setupRecurringWork()
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        schedulePictureWorker(constraints)
        scheduleFeedWorker(constraints)
    }

    private fun scheduleFeedWorker(constraints: Constraints) {
        val repeatingRequest =
            PeriodicWorkRequestBuilder<FeedWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .addTag(FeedWorker.WORK_NAME)
                .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            FeedWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest
        )
    }

    private fun schedulePictureWorker(constraints: Constraints) {
        val repeatingRequest =
            PeriodicWorkRequestBuilder<PictureOfDayWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            PictureOfDayWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest
        )
    }

}