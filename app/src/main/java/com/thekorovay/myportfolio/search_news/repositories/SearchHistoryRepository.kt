package com.thekorovay.myportfolio.search_news.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.thekorovay.myportfolio.database.DatabaseSearchRequest
import com.thekorovay.myportfolio.database.NewsDatabase
import com.thekorovay.myportfolio.database.toArticles
import com.thekorovay.myportfolio.search_news.domain_model.Article
import com.thekorovay.myportfolio.search_news.domain_model.SearchRequest
import com.thekorovay.myportfolio.search_news.network.FIRST_PAGE_NUMBER
import com.thekorovay.myportfolio.search_news.network.LoadingState
import com.thekorovay.myportfolio.search_news.network.newsApi

class SearchHistoryRepository(private val database: NewsDatabase) {
    val lastRequest: LiveData<SearchRequest?> = Transformations.map(database.searchHistoryDao().getLastRequest()) {
        it?.toSearchRequest()
    }
}