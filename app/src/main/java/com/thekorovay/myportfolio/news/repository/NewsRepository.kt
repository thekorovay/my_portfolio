package com.thekorovay.myportfolio.news.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.thekorovay.myportfolio.news.database.NewsDatabase
import com.thekorovay.myportfolio.news.database.toArticles
import com.thekorovay.myportfolio.news.domain_model.Article
import com.thekorovay.myportfolio.news.network.FIRST_PAGE_NUMBER
import com.thekorovay.myportfolio.news.network.LoadingState
import com.thekorovay.myportfolio.news.network.newsApi
import com.thekorovay.myportfolio.news.repository.NewsSharedPreferences

class NewsRepository(
    private val database: NewsDatabase,
    private val preferences: NewsSharedPreferences
) {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    val articles: LiveData<List<Article>> = Transformations.map(database.newsDao().getArticles()) {
        it.toArticles()
    }


    private var nextPageNumber = FIRST_PAGE_NUMBER


    /**
     * Loads next page of articles for specified parameters from [newsApi] changing [loadingState]
     * according to loading result. Automatically handles [nextPageNumber]. Caches fetched
     * articles in [database] and sets last search query in [preferences] on success.
     */
    suspend fun loadMoreNews(
        query: String,
        safeSearchEnabled: Boolean,
        pageSize: Int
    ) {
        _loadingState.postValue(LoadingState.LOADING)

        // Clear previous search results cache
        if (nextPageNumber == FIRST_PAGE_NUMBER) {
            database.newsDao().clearAll()
        }

        try {
            val response = newsApi.requestNewsArticlesAsync(
                query,
                safeSearchEnabled,
                pageSize,
                nextPageNumber
            )

            when {
                response.isError -> _loadingState.postValue(LoadingState.ERROR)
                response.isEmpty -> _loadingState.postValue(LoadingState.EMPTY_PAGE)
                else -> {
                    // Successfully received new articles
                    _loadingState.postValue(LoadingState.SUCCESS)
                    preferences.lastSearchQuery = query
                    nextPageNumber++
                    database.newsDao().insertAll(*response.databaseArticles)
                }
            }
        } catch (e: Exception) {
            _loadingState.postValue(LoadingState.ERROR)
        }
    }
}