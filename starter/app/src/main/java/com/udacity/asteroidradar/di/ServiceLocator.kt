package com.udacity.asteroidradar.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.udacity.asteroidradar.data.local.AsteroidRadarDatabase
import com.udacity.asteroidradar.data.remote.Network
import com.udacity.asteroidradar.data.repo.AsteroidRepositoryImpl
import com.udacity.asteroidradar.data.repo.AsteroidsRepository
import com.udacity.asteroidradar.main.MainViewModel

object ServiceLocator {

    fun provideDatabase(context: Context): AsteroidRadarDatabase = AsteroidRadarDatabase.getDatabase(context)

    fun provideAsteroidsRepository(context: Context): AsteroidsRepository {
        return AsteroidRepositoryImpl(
            database = provideDatabase(context),
            webService = Network.service
        )
    }

    fun provideMainViewModel(context: Context): ViewModelProvider.Factory {
        val repo = provideAsteroidsRepository(context)
        return MainViewModel.Factory(repo)
    }

}