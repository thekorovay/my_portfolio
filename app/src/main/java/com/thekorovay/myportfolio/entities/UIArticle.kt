package com.thekorovay.myportfolio.entities

import android.os.Parcelable
import androidx.annotation.Keep
import com.thekorovay.myportfolio.domain.entities.Article
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UIArticle (
    val id: String,

    val title: String,
    val description: String,
    val body: String,

    val datePublished: String,

    val sourceUrl: String,
    val sourceName: String,

    val thumbUrl: String,
    val imageUrl: String
): Parcelable {
    companion object {
        fun fromArticle(article: Article) = UIArticle(
            id = article.id,
            title = article.title,
            description = article.description,
            body = article.body,
            datePublished = article.datePublished,
            sourceUrl = article.sourceUrl,
            sourceName = article.sourceName,
            thumbUrl = article.thumbUrl,
            imageUrl = article.imageUrl
        )
    }
}