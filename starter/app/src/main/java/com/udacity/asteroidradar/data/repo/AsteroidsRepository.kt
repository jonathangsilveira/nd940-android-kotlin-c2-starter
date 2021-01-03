package com.udacity.asteroidradar.data.repo

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.Asteroid

interface AsteroidsRepository {
    suspend fun fetchAndSaveAsteroids()
    val asteroids: LiveData<List<Asteroid>>
}