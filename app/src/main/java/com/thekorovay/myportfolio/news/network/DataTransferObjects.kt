package com.thekorovay.myportfolio.news.network

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.thekorovay.myportfolio.news.Article

const val RESPONSE_TYPE_NEWS = "news"

@Keep
data class NewsServerResponse(
    @Json(name = "_type") val type: String,
    @Json(name = "value") val networkArticles: List<NetworkArticle>
) {
    val isError get() = type != RESPONSE_TYPE_NEWS
    val isEmpty get() = networkArticles.isEmpty()

    fun toArticlesList(): List<Article> {
        return networkArticles.map { networkArticle ->
            Article(
                id = networkArticle.id,
                title = networkArticle.title,
                description = networkArticle.description,
                body = networkArticle.body,
                datePublished = networkArticle.datePublished,
                sourceUrl = networkArticle.url,
                sourceName = networkArticle.provider.name,
                thumbUrl = networkArticle.image.thumbnail,
                imageUrl = networkArticle.image.url
            )
        }
    }
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