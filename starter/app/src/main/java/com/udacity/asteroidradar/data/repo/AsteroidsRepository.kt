package com.udacity.asteroidradar.data.repo

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.data.remote.PictureOfDay
import com.udacity.asteroidradar.domain.Asteroid

interface AsteroidsRepository {
    suspend fun fetchAndSaveAsteroids()
    fun getAsteroids(filter: Filters): LiveData<List<Asteroid>>
    suspend fun fetchAndSavePictureOfDay()
    val pictureOfDay: LiveData<PictureOfDay?>
    enum class Filters {
        DAY, WEEK, ALL
    }
}