package com.udacity.asteroidradar.domain

import com.udacity.asteroidradar.data.local.DatabaseAsteroid
import com.udacity.asteroidradar.data.local.DatabasePictureOfDay
import com.udacity.asteroidradar.data.remote.PictureOfDay

fun List<Asteroid>.asDatabaseModel(): Array<DatabaseAsteroid> {
    return this.map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous,
        )
    }.toTypedArray()
}

fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return this.map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous,
        )
    }
}

val DatabasePictureOfDay.asDomainModel: PictureOfDay
    get() = PictureOfDay(
        url = this.url,
        mediaType = this.mediaType,
        title = this.title
    )

val PictureOfDay.asDatabaseModel: DatabasePictureOfDay
    get() = DatabasePictureOfDay(
        url = this.url,
        mediaType = this.mediaType,
        title = this.title
    )