package com.thekorovay.myportfolio.search_news.domain_model

import android.os.Parcelable
import com.thekorovay.myportfolio.database.DatabaseSearchRequest
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Article(
    val id: String,

    val title: String,
    val description: String,
    val body: String,

    val datePublished: String,

    val sourceUrl: String,
    val sourceName: String,

    val thumbUrl: String,
    val imageUrl: String
): Parcelable

@Parcelize
data class SearchRequest(
    val query: String,
    val safeSearchEnabled: Boolean,
    val thumbnailsEnabled: Boolean,
    val pageSize: Int
): Parcelable {

    fun toDatabaseSearchRequest() = DatabaseSearchRequest(
        dateTime = LocalDateTime.now().toString(),
        query = this.query,
        safeSearchEnabled = this.safeSearchEnabled,
        thumbnailsEnabled = this.thumbnailsEnabled,
        pageSize = this.pageSize
    )
}