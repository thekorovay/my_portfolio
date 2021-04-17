package com.thekorovay.myportfolio.domain_model

import android.os.Parcelable
import androidx.annotation.Keep
import com.thekorovay.myportfolio.database.DatabaseSearchRequest
import com.thekorovay.myportfolio.network.FirebaseSearchRequest
import kotlinx.parcelize.Parcelize

@Keep
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

@Keep
@Parcelize
data class SearchRequest(
    val dateTime: String,
    val query: String,
    val safeSearchEnabled: Boolean,
    val thumbnailsEnabled: Boolean,
    val pageSize: Int
): Parcelable {

    fun toDatabaseSearchRequest() = DatabaseSearchRequest(
        dateTime = this.dateTime,
        query = this.query,
        safeSearchEnabled = this.safeSearchEnabled,
        thumbnailsEnabled = this.thumbnailsEnabled,
        pageSize = this.pageSize
    )

    fun toFirebaseSearchRequest() = FirebaseSearchRequest(
        date_time = this.dateTime,
        query = this.query,
        safe_search = this.safeSearchEnabled,
        thumbnails = this.thumbnailsEnabled,
        page_size = this.pageSize
    )
}