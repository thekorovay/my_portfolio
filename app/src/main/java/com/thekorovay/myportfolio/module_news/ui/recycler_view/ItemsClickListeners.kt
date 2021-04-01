package com.thekorovay.myportfolio.module_news.ui.recycler_view

import com.thekorovay.myportfolio.domain_model.Article

class NewsItemClickListener(val listener: (article: Article) -> Unit) {
    fun onClick(article: Article) = listener(article)
}

class ShowMoreClickListener(val listener: () -> Unit) {
    fun onClick() = listener()
}