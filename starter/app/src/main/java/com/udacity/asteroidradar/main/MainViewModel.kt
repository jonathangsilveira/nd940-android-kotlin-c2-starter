package com.udacity.asteroidradar.main

import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.data.repo.AsteroidsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val repo: AsteroidsRepository) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    /*private val _error = MutableLiveData<Int?>()
    val error: LiveData<Int?>
        get() = _error*/

    val asteroids: LiveData<List<Asteroid>> = repo.asteroids

    init {
        refreshDataFromNetwork()
    }

    /*fun onErrorClicked() {
        _error.value = null
    }*/

    private fun refreshDataFromNetwork() = viewModelScope.launch {
        _loading.value = true
        try {
            withContext(context = Dispatchers.IO) {
                repo.fetchAndSaveAsteroids()
            }
        } finally {
            _loading.value = false
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