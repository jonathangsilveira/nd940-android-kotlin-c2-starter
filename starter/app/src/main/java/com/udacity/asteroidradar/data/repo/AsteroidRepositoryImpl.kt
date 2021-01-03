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
                deleteAll()
                insertAll(*asteroids.asDatabaseModel())
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

    override val asteroids: LiveData<List<Asteroid>> = database.asteroidDao.getAsteroids().map {
        it.asDomainModel()
    }

    override suspend fun getPictureOfDay(): PictureOfDay {
        val json = webService.getImageOfTheDay(apiKey = BuildConfig.API_KEY)
        return parsePictureOfDayJsonResult(json)
    }

}