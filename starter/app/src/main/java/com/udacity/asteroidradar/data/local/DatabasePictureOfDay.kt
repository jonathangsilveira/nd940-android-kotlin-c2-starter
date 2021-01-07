package com.udacity.asteroidradar.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "picture_of_day")
data class DatabasePictureOfDay(
    @PrimaryKey val url: String,
    @ColumnInfo(name = "media_type") val mediaType: String,
    val title: String
)