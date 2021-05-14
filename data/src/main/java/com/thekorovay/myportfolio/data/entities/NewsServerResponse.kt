package com.thekorovay.myportfolio.data.entities

import androidx.annotation.Keep
import com.squareup.moshi.Json

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