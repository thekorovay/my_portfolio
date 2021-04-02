package com.thekorovay.myportfolio.module_news.ui.recycler_view

import android.view.View
import com.thekorovay.myportfolio.domain_model.Article

class NewsItemClickListener(val listener: (article: Article, sharedView: View) -> Unit) {
    fun onClick(article: Article, sharedView: View) = listener(article, sharedView)
}

class ShowMoreClickListener(val listener: () -> Unit) {
    fun onClick() = listener()
}