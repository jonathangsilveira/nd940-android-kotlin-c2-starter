package com.udacity.asteroidradar.main

import androidx.lifecycle.*
import com.udacity.asteroidradar.data.remote.PictureOfDay
import com.udacity.asteroidradar.data.repo.AsteroidsRepository
import com.udacity.asteroidradar.domain.Asteroid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val repo: AsteroidsRepository) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _pictureOfDay = MutableLiveData<PictureOfDay?>()
    val pictureOfDay: LiveData<PictureOfDay?>
        get() = _pictureOfDay

    val asteroids: LiveData<List<Asteroid>> = repo.asteroids

    init {
        refreshDataFromNetwork()
    }

    private fun refreshDataFromNetwork() {
        refreshPictureOfDay()
        refreshAsteroids()
    }

    private fun refreshAsteroids() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val io = Dispatchers.IO
                withContext(context = io) {
                    repo.fetchAndSaveAsteroids()
                }
            } finally {
                _loading.value = false
            }
        }
    }
    private fun refreshPictureOfDay() {
        viewModelScope.launch {
            _pictureOfDay.value = withContext(context = Dispatchers.IO) {
                repo.getPictureOfDay()
            }
        }
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