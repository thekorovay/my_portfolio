package com.thekorovay.myportfolio.data.entities

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thekorovay.myportfolio.domain.entities.SearchRequest

@Keep
@Entity(tableName = "search_history")
data class DatabaseSearchRequest(
    @PrimaryKey @ColumnInfo(name = "date_time") val dateTime: String,
    val query: String,
    @ColumnInfo(name = "safe_search_enabled") val safeSearchEnabled: Boolean,
    @ColumnInfo(name = "thumbs_enabled") val thumbnailsEnabled: Boolean,
    @ColumnInfo(name = "page_size") val pageSize: Int
) {
    companion object {
        fun fromSearchRequest(request: SearchRequest) = DatabaseSearchRequest(
            dateTime = request.dateTime,
            query = request.query,
            safeSearchEnabled = request.safeSearchEnabled,
            thumbnailsEnabled = request.thumbnailsEnabled,
            pageSize = request.pageSize
        )
    }

    fun toSearchRequest() = SearchRequest(
        dateTime = this.dateTime,
        query = this.query,
        safeSearchEnabled = this.safeSearchEnabled,
        thumbnailsEnabled = this.thumbnailsEnabled,
        pageSize = this.pageSize
    )
}

fun List<DatabaseSearchRequest>.toSearchRequests(): List<SearchRequest> {
    return map { dbRequest ->
        dbRequest.toSearchRequest()
    }
}