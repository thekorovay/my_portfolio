package com.thekorovay.myportfolio.network

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.thekorovay.myportfolio.database.DatabaseArticle
import com.thekorovay.myportfolio.domain_model.SearchRequest
import java.time.LocalDateTime

const val RESPONSE_TYPE_NEWS = "news"

@Keep
data class NewsServerResponse(
    @Json(name = "_type") val type: String,
    @Json(name = "value") val networkArticles: List<NetworkArticle>
) {
    val isError get() = type != RESPONSE_TYPE_NEWS
    val isEmpty get() = networkArticles.isEmpty()

    val databaseArticles
        get() = networkArticles.map { networkArticle ->
            DatabaseArticle(
                id = networkArticle.id,
                title = networkArticle.title,
                description = networkArticle.description,
                body = networkArticle.body,
                datePublished = networkArticle.datePublished,
                sourceUrl = networkArticle.url,
                sourceName = networkArticle.provider.name,
                thumbUrl = networkArticle.image.thumbnail.takeIf { it.isNotEmpty() },
                imageUrl = networkArticle.image.url.takeIf { it.isNotEmpty() }
            )
        }.toTypedArray()
}

@Keep
data class NetworkArticle(
    val id: String,

    val title: String,
    val description: String,
    val body: String,

    val datePublished: String,

    val url: String,
    val provider: Provider,

    val image: Image
)

@Keep
data class Provider(val name: String)

@Keep
data class Image(val url: String, val thumbnail: String)



// Firebase Realtime Database requires no-argument constructor so set default values for fields
@Keep
data class FirebaseSearchRequest(
        val date_time: String = LocalDateTime.now().toString(),
        val query: String = "",
        val safe_search: Boolean = true,
        val thumbnails: Boolean = true,
        val page_size: Int = 10
) {
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