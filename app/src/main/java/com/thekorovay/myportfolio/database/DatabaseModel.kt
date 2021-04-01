package com.thekorovay.myportfolio.database

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thekorovay.myportfolio.search_news.domain_model.Article

@Keep
@Entity(tableName = ARTICLES_TABLE_NAME)
data class DatabaseArticle(
    @PrimaryKey val id: String,

    val title: String,
    val description: String,
    val body: String,

    @ColumnInfo(name = "date_published") val datePublished: String,

    @ColumnInfo(name = "source_url") val sourceUrl: String,
    @ColumnInfo(name = "source_name") val sourceName: String,

    @ColumnInfo(name = "thumb_url") val thumbUrl: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String?
) 

fun List<DatabaseArticle>.toArticles(): List<Article> {
    return map { dbArticle ->
        Article(
            id = dbArticle.id,
            title = dbArticle.title,
            description = dbArticle.description,
            body = dbArticle.body,
            datePublished = dbArticle.datePublished,
            sourceUrl = dbArticle.sourceUrl,
            sourceName = dbArticle.sourceName,
            thumbUrl = dbArticle.thumbUrl ?: "",
            imageUrl = dbArticle.imageUrl ?: ""
        )
    }
}