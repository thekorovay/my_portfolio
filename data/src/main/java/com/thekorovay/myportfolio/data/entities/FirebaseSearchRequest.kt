package com.thekorovay.myportfolio.data.entities

import androidx.annotation.Keep
import com.thekorovay.myportfolio.domain.entities.SearchRequest
import java.time.LocalDateTime

// Firebase Realtime Database requires no-argument constructor so set default values for fields
@Keep
data class FirebaseSearchRequest(
    val date_time: String = LocalDateTime.now().toString(),
    val query: String = "",
    val safe_search: Boolean = true,
    val thumbnails: Boolean = true,
    val page_size: Int = 10
) {
    companion object {
        fun fromSearchRequest(request: SearchRequest) = FirebaseSearchRequest(
            date_time = request.dateTime,
            query = request.query,
            safe_search = request.safeSearchEnabled,
            thumbnails = request.thumbnailsEnabled,
            page_size = request.pageSize
        )
    }

    fun toSearchRequest() = SearchRequest(
        dateTime = this.date_time,
        query = this.query,
        safeSearchEnabled = this.safe_search,
        thumbnailsEnabled = this.thumbnails,
        pageSize = this.page_size
    )
}

fun List<FirebaseSearchRequest>.toSearchRequests(): List<SearchRequest> {
    return map { firebaseRequest ->
        firebaseRequest.toSearchRequest()
    }
}