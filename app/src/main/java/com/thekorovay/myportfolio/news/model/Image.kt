package com.thekorovay.myportfolio.news.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep @Parcelize
data class Image(val url: String, val thumbnail: String): Parcelable