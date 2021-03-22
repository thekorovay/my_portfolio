package com.thekorovay.myportfolio.tools

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.thekorovay.myportfolio.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@BindingAdapter("imageUrlRound")
fun ImageView.setRoundImageUrl(url: String) {
    Glide.with(context)
        .load(url)
        .circleCrop()
        .placeholder(R.drawable.ic_loading_rotating_inset_20)
        .error(R.drawable.ic_baseline_broken_image_200)
        .into(this)
}

@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(url: String) {
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.ic_loading_rotating_inset_33)
        .error(R.drawable.ic_baseline_broken_image_200)
        .into(this)
}

@BindingAdapter("articleDate")
fun TextView.setArticleDate(raw: String) {
    val localDateTime = LocalDateTime.parse(raw)
    val formatter = DateTimeFormatter.ofPattern(context.getString(R.string.datetime_format))
    val stringDateTime = localDateTime.format(formatter)
    text = context.getString(R.string.from_date, stringDateTime)
}

@BindingAdapter("articleSource")
fun TextView.setArticleSource(source: String) {
    text = context.getString(R.string.source_template, source)
}

@BindingAdapter("isActive")
fun Button.setActive(isActive: Boolean) {
    isEnabled = isActive
    setTextColor(ContextCompat.getColor(
        context,
        if (isActive) R.color.blue_700 else R.color.grey_500
    ))
}