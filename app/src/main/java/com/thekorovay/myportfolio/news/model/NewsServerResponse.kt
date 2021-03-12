package com.thekorovay.myportfolio.news.model

import com.squareup.moshi.Json
import androidx.annotation.Keep

const val RESPONSE_TYPE_NEWS = "news"

@Keep
data class NewsServerResponse(
    @Json(name = "_type") val type: String,
    @Json(name = "value") val articles: List<Article>
) {
    val isError get() = type != RESPONSE_TYPE_NEWS
    val isEmpty get() = articles.isEmpty()
}
