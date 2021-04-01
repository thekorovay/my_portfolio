package com.thekorovay.myportfolio.search_news.recycler_view

import com.thekorovay.myportfolio.search_news.domain_model.Article


sealed class NewsListItem {
    data class ArticleItem(val article: Article) : NewsListItem() {
        override val id = article.id
        override val type = ARTICLE
    }
    data class ArticleNoThumbnailItem(val article: Article) : NewsListItem() {
        override val id = article.id
        override val type = ARTICLE_NO_THUMBNAIL
    }
    object ShowMoreNewsItem: NewsListItem() {
        override val id = "not an article"
        override val type = SHOW_MORE_BUTTON
    }

    abstract val id: String
    abstract val type: Int

    companion object {
        const val SHOW_MORE_BUTTON = -1
        const val ARTICLE = 0
        const val ARTICLE_NO_THUMBNAIL = 1
    }
}