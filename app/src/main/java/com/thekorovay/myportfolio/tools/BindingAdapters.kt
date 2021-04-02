package com.thekorovay.myportfolio.tools

import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.thekorovay.myportfolio.R

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
    val formatted = raw.formatAsDateTime(context.getString(R.string.datetime_format))
    text = context.getString(R.string.from_date, formatted)
}

@BindingAdapter("historyDateTime")
fun TextView.setHistoryDateTime(raw: String) {
    text = raw.formatAsDateTime(context.getString(R.string.datetime_format_with_seconds))
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
        if (isActive) R.color.blue_500 else R.color.grey_500
    ))
}

@BindingAdapter("pageSize")
fun Spinner.setPageSize(pageSize: Int) {
    val stringArray = context.resources.getStringArray(R.array.results_per_page_values)
    val intArray = stringArray.map { it.toInt() }
    val position = intArray.indexOf(pageSize)
    setSelection(position)
}