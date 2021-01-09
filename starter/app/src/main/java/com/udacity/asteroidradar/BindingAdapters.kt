package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.data.remote.PictureOfDay

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    val resources = imageView.context.resources
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription = resources.getText(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription = resources.getText(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

/**
 * Binding adapter used to hide the spinner once data is available
 */
@BindingAdapter("isLoading")
fun isLoading(view: View, isLoading: Boolean) {
    view.visibility = if (isLoading) View.VISIBLE else View.GONE
}

@BindingAdapter("pictureOfDay")
fun bindPictureOfDay(imageView: ImageView, pictureOfDay: PictureOfDay?) {
    if (pictureOfDay?.mediaType == "image")
        imageView.loadFrom(pictureOfDay)
    else
        imageView.setImageResource(R.drawable.placeholder_picture_of_day)
}

@BindingAdapter("imageTitle")
fun bindPictureOfDayTitle(textView: TextView, pictureOfDay: PictureOfDay?) {
    val resources = textView.context.resources
    val title = pictureOfDay?.title ?: resources.getString(R.string.image_of_the_day)
    textView.text = title
}

private fun ImageView.loadFrom(pictureOfDay: PictureOfDay?) {
    val imageView = this
    imageView.contentDescription = imageView.loadingContentDescription
    val callback = object : Callback {
        override fun onSuccess() {
            imageView.contentDescription = imageView.loadedContentDescription(pictureOfDay?.title)
        }
        override fun onError(e: Exception?) {}
    }
    Picasso.get()
        .load(pictureOfDay?.url)
        .placeholder(R.drawable.loading_animation)
        .error(R.drawable.ic_broken_image)
        .into(this, callback)
}

private val ImageView.loadingContentDescription: CharSequence
    get() = this.context.resources.getText(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)

private fun ImageView.loadedContentDescription(title: String?) =
    this.context.resources.getText(
        R.string.nasa_picture_of_day_content_description_format,
        title
    )