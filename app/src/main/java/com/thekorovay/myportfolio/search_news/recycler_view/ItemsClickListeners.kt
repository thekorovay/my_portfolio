package com.thekorovay.myportfolio.search_news.recycler_view

import com.thekorovay.myportfolio.search_news.domain_model.Article

class NewsItemClickListener(val listener: (article: Article) -> Unit) {
    fun onClick(article: Article) = listener(article)
}

class ShowMoreClickListener(val listener: () -> Unit) {
    fun onClick() = listener()
}