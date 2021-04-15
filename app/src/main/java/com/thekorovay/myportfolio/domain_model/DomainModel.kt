package com.thekorovay.myportfolio.domain_model

import android.os.Parcelable
import com.thekorovay.myportfolio.database.DatabaseSearchRequest
import com.thekorovay.myportfolio.network.FirebaseSearchRequest
import kotlinx.parcelize.Parcelize
import org.json.JSONException
import org.json.JSONObject
import java.io.InvalidObjectException
import java.io.Serializable

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

    fun toJson() = JSONObject().apply {
        put("date_time", dateTime)
        put("query", query)
        put("safe_search", safeSearchEnabled)
        put("thumbnails", thumbnailsEnabled)
        put("page_size", pageSize)
    }

    companion object {
        fun fromJson(json: JSONObject): SearchRequest? = try {
            SearchRequest(
                    dateTime = json.getString("date_time"),
                    query = json.getString("query"),
                    safeSearchEnabled = json.getBoolean("safe_search"),
                    thumbnailsEnabled = json.getBoolean("thumbnails"),
                    pageSize = json.getInt("page_size"),
            )
        } catch (e: JSONException) {
            null
        }
    }
}