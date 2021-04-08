package com.thekorovay.myportfolio.tools

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.color.MaterialColors
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

@BindingAdapter("textWithFade")
fun setTextWithFade(textView: TextView, string: String) {
    val fadeAwayAnim = AnimationUtils.loadAnimation(textView.context, R.anim.fade_away)
    val fadeInAnim = AnimationUtils.loadAnimation(textView.context, R.anim.fade_in)

    fadeAwayAnim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) { }
        override fun onAnimationRepeat(animation: Animation?) { }
        override fun onAnimationEnd(animation: Animation?) {
            textView.text = string
            textView.startAnimation(fadeInAnim)
        }
    })

    textView.startAnimation(fadeAwayAnim)
}

@BindingAdapter("isActive")
fun Button.setActive(isActive: Boolean) {
    isEnabled = isActive
    setTextColor(MaterialColors.getColor(
        this,
        if (isActive) R.attr.colorPrimary else R.attr.colorPrimaryVariant
    ))
}

@BindingAdapter("isActive")
fun EditText.setActive(isActive: Boolean) {
    isEnabled = isActive
    setTextColor(MaterialColors.getColor(
        this,
        if (isActive) R.attr.colorPrimary else R.attr.colorPrimaryVariant
    ))
}

@BindingAdapter("pageSize")
fun Spinner.setPageSize(pageSize: Int) {
    val stringArray = context.resources.getStringArray(R.array.results_per_page_values)
    val intArray = stringArray.map { it.toInt() }
    val position = intArray.indexOf(pageSize)
    setSelection(position)
}