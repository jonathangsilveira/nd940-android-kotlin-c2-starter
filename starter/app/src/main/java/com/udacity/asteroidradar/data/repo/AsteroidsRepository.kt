package com.udacity.asteroidradar.data.repo

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.data.remote.PictureOfDay
import com.udacity.asteroidradar.domain.Asteroid

interface AsteroidsRepository {
    suspend fun fetchAndSaveAsteroids()
    val asteroids: LiveData<List<Asteroid>>
    suspend fun getPictureOfDay(): PictureOfDay
}