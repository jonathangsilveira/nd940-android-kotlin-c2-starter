package com.udacity.asteroidradar.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AsteroidRadarDao {
    @Query(value = """SELECT * FROM asteroids 
        WHERE close_approach_date BETWEEN :startDate AND :endDate 
        ORDER BY close_approach_date ASC""")
    fun getAsteroids(startDate: String, endDate: String = startDate): LiveData<List<DatabaseAsteroid>>
    @Query(value = "SELECT * FROM asteroids ORDER BY close_approach_date ASC")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsteroids(vararg videos: DatabaseAsteroid)
    @Query(value = "DELETE FROM asteroids")
    fun deleteAsteroids()
    @Query(value = "SELECT * FROM picture_of_day LIMIT 1")
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay?>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfDay(picture: DatabasePictureOfDay)
    @Query(value = "DELETE FROM picture_of_day")
    fun deletePictureOfDay()
}
