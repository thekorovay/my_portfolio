package com.thekorovay.myportfolio.module_news.ui.recycler_view

import com.thekorovay.myportfolio.entities.UIArticle

class NewsItemClickListener(val listener: (article: UIArticle) -> Unit) {
    fun onClick(article: UIArticle) = listener(article)
}

class ShowMoreClickListener(val listener: () -> Unit) {
    fun onClick() = listener()
}