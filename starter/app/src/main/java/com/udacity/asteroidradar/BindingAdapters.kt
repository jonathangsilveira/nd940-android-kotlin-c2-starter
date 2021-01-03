package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
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
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
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
        Picasso.get()
            .load(pictureOfDay.url)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(imageView)
    else
        imageView.setImageResource(R.drawable.placeholder_picture_of_day)
}

@BindingAdapter("imageTitle")
fun bindPictureOfDayTitle(textView: TextView, pictureOfDay: PictureOfDay?) {
    val resources = textView.context.resources
    textView.text = pictureOfDay?.title ?: resources.getText(R.string.image_of_the_day)
}