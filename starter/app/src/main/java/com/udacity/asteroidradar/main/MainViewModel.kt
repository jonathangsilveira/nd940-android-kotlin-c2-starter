package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.udacity.asteroidradar.data.remote.PictureOfDay
import com.udacity.asteroidradar.data.repo.AsteroidsRepository
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.worker.FeedWorker

class MainViewModel(private val repo: AsteroidsRepository) : ViewModel() {

    val pictureOfDay: LiveData<PictureOfDay?> = repo.pictureOfDay

    val asteroids: LiveData<List<Asteroid>> = repo.asteroids

    private val workInfoLiveData: LiveData<MutableList<WorkInfo>> = WorkManager.getInstance()
        .getWorkInfosByTagLiveData(FeedWorker.WORK_NAME)

    val loading = workInfoLiveData.map { workInfoList ->
        val workInfo = workInfoList.firstOrNull()
        workInfo?.state == WorkInfo.State.RUNNING
    }

    class Factory(private val repo: AsteroidsRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repo) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}