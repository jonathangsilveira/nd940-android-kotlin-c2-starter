package com.udacity.asteroidradar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DatabaseAsteroid::class],
    version = 1,
    exportSchema = false
)
abstract class AsteroidRadarDatabase: RoomDatabase() {

    abstract val asteroidDao: AsteroidDao

    companion object {

        private lateinit var INSTANCE: AsteroidRadarDatabase

        fun getDatabase(context: Context): AsteroidRadarDatabase {
            synchronized(this) {
                if (!::INSTANCE.isInitialized)
                    INSTANCE = createDatabase(context)
            }
            return INSTANCE
        }

        private fun createDatabase(context: Context): AsteroidRadarDatabase {

            return Room.databaseBuilder(
                context.applicationContext,
                AsteroidRadarDatabase::class.java,
                "asteroid_radar"
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

}