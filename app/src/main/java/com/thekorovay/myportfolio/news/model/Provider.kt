package com.thekorovay.myportfolio.news.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep @Parcelize
data class Provider(val name: String): Parcelable