package com.thekorovay.myportfolio.news.recycler_view

import com.thekorovay.myportfolio.news.model.Article

class NewsItemClickListener(val listener: (articleId: String) -> Unit) {
    fun onClick(article: Article) = listener(article.id)
}

class ShowMoreClickListener(val listener: () -> Unit) {
    fun onClick() = listener()
}