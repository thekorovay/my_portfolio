package com.thekorovay.myportfolio.tools

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.thekorovay.myportfolio.R

@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(url: String) {
    Glide.with(context)
        .load(url)
        .circleCrop()
        .placeholder(R.drawable.loading_spinner)
        .error(R.drawable.ic_baseline_broken_image_200)
        .into(this)
}