package com.thekorovay.myportfolio.search_news.domain_model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchRequest(
    val query: String,
    val safeSearchEnabled: Boolean,
    val thumbnailsEnabled: Boolean,
    val pageSize: Int
): Parcelable {
    companion object {
        fun getEmpty() = SearchRequest("", false, false, 0)
    }
}