package com.udacity.asteroidradar.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.data.local.AsteroidRadarDatabase
import com.udacity.asteroidradar.data.remote.PictureOfDay
import com.udacity.asteroidradar.data.remote.WebService
import com.udacity.asteroidradar.data.remote.parseAsteroidsJsonResult
import com.udacity.asteroidradar.data.remote.parsePictureOfDayJsonResult
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.asDatabaseModel
import com.udacity.asteroidradar.domain.asDomainModel
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepositoryImpl(
    private val database: AsteroidRadarDatabase,
    private val webService: WebService
): AsteroidsRepository {

    override suspend fun fetchAndSaveAsteroids() {
        val asteroids = fetch()
        save(asteroids)
    }

    private fun save(asteroids: List<Asteroid>) {
        database.runInTransaction {
            database.asteroidDao.run {
                deleteAsteroids()
                insertAsteroids(*asteroids.asDatabaseModel())
            }
        }
    }

    private suspend fun fetch(): List<Asteroid> {
        val (startDate, endDate) = dateRange
        val feed = webService.getFeed(
            apiKey = BuildConfig.API_KEY,
            startDate = startDate,
            endDate = endDate
        )
        val json = JSONObject(feed)
        return parseAsteroidsJsonResult(json)
    }

    private val dateRange: Pair<String, String>
        get() {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
            val endDate = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            return dateFormat.format(calendar.time) to endDate
        }

    override fun getAsteroids(filter: AsteroidsRepository.Filters): LiveData<List<Asteroid>> {
        val calendar = Calendar.getInstance()
        val dateFormat: DateFormat =
            SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val asteroids = when (filter) {
            AsteroidsRepository.Filters.DAY -> {
                val date = dateFormat.format(calendar.time)
                database.asteroidDao.getAsteroids(date)
            }
            AsteroidsRepository.Filters.WEEK -> {
                val startDate = dateFormat.format(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, 7)
                val endDate = dateFormat.format(calendar.time)
                database.asteroidDao.getAsteroids(startDate, endDate)
            }
            AsteroidsRepository.Filters.ALL -> database.asteroidDao.getAllAsteroids()
        }
        return asteroids.map { it.asDomainModel() }
    }

    override suspend fun fetchAndSavePictureOfDay() {
        val domain = fetchPictureOfDay()
        save(domain)
    }

    override val pictureOfDay: LiveData<PictureOfDay?> =
        database.asteroidDao.getPictureOfDay().map { it?.asDomainModel }

    private fun save(domain: PictureOfDay) {
        val model = domain.asDatabaseModel
        database.runInTransaction {
            database.asteroidDao.run {
                deletePictureOfDay()
                insertPictureOfDay(model)
            }
        }
    }

    private suspend fun fetchPictureOfDay(): PictureOfDay {
        val json = webService.getImageOfTheDay(apiKey = BuildConfig.API_KEY)
        return parsePictureOfDayJsonResult(json)
    }

}