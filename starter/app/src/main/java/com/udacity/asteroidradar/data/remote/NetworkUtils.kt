package com.udacity.asteroidradar.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.domain.Asteroid
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

fun parseAsteroidsJsonResult(jsonResult: JSONObject): ArrayList<Asteroid> {
    val nearEarthObjectsJson = jsonResult.getJSONObject("near_earth_objects")

    val asteroidList = ArrayList<Asteroid>()

    val nextSevenDaysFormattedDates = getNextSevenDaysFormattedDates()
    for (formattedDate in nextSevenDaysFormattedDates) {
        val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(formattedDate)

        for (i in 0 until dateAsteroidJsonArray.length()) {
            val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
            val id = asteroidJson.getLong("id")
            val codename = asteroidJson.getString("name")
            val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
            val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                .getJSONObject("kilometers").getDouble("estimated_diameter_max")

            val closeApproachData = asteroidJson
                .getJSONArray("close_approach_data").getJSONObject(0)
            val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                .getDouble("kilometers_per_second")
            val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                .getDouble("astronomical")
            val isPotentiallyHazardous = asteroidJson
                .getBoolean("is_potentially_hazardous_asteroid")

            val asteroid = Asteroid(id, codename, formattedDate, absoluteMagnitude,
                estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous)
            asteroidList.add(asteroid)
        }
    }

    return asteroidList
}

fun parsePictureOfDayJsonResult(json: String): PictureOfDay {
    val rootJson = JSONObject(json)
    val mediaType = rootJson.getString("media_type")
    val title = rootJson.getString("title")
    val url = rootJson.getString("url")
    return PictureOfDay(mediaType, title, url)
}

private fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()

    val calendar = Calendar.getInstance()
    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        formattedDateList.add(dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return formattedDateList
}

object Network {

    private val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(ScalarsOrMoshiConverterFactory.create())
        .client(client)
        .build()

    val service = retrofit.create<WebService>()
}

interface WebService {

    @GET("planetary/apod")
    @JsonResponse
    suspend fun getImageOfTheDay(@Query("api_key") apiKey: String): PictureOfDay

    @GET("neo/rest/v1/feed")
    @StringResponse
    suspend fun getFeed(
        @Query("api_key") apiKey: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): String

}

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention
internal annotation class StringResponse

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention
internal annotation class JsonResponse

class ScalarsOrMoshiConverterFactory: Converter.Factory() {
    private val scalarConverterFactory = ScalarsConverterFactory.create()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        annotations.forEach { annotation ->
            return when (annotation) {
                is StringResponse -> scalarConverterFactory
                    .responseBodyConverter(type, annotations, retrofit)
                is JsonResponse -> MoshiConverterFactory.create(moshi)
                    .responseBodyConverter(type, annotations, retrofit)
                else -> null
            }
        }
        return null
    }
    companion object {
        fun create() = ScalarsOrMoshiConverterFactory()
    }
}