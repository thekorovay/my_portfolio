package com.thekorovay.myportfolio.entities

import android.os.Parcelable
import androidx.annotation.Keep
import com.thekorovay.myportfolio.domain.entities.SearchRequest
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UISearchRequest (
    val dateTime: String,
    val query: String,
    val safeSearchEnabled: Boolean,
    val thumbnailsEnabled: Boolean,
    val pageSize: Int,
    var addToHistory: Boolean = true
): Parcelable {
    companion object {
        fun fromSearchRequest(searchRequest: SearchRequest?) = searchRequest?.let { request ->
            UISearchRequest(
                dateTime = request.dateTime,
                query = request.query,
                safeSearchEnabled = request.safeSearchEnabled,
                thumbnailsEnabled = request.thumbnailsEnabled,
                pageSize = request.pageSize,
                addToHistory = request.addToHistory
            )
        }
    }

    fun toSearchRequest() = SearchRequest(
        dateTime = this.dateTime,
        query = this.query,
        safeSearchEnabled = this.safeSearchEnabled,
        thumbnailsEnabled = this.thumbnailsEnabled,
        pageSize = this.pageSize,
        addToHistory = this.addToHistory
    )
}

fun List<SearchRequest>.toUISearchRequests(): List<UISearchRequest?> = map {
    UISearchRequest.fromSearchRequest(it)
}