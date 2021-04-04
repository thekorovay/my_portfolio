package com.thekorovay.myportfolio.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.thekorovay.myportfolio.database.NewsDatabase
import com.thekorovay.myportfolio.database.toArticles
import com.thekorovay.myportfolio.domain_model.Article
import com.thekorovay.myportfolio.domain_model.SearchRequest
import com.thekorovay.myportfolio.module_news.network.FIRST_PAGE_NUMBER
import com.thekorovay.myportfolio.module_news.network.LoadingState
import com.thekorovay.myportfolio.module_news.network.newsApi

class ArticlesRepository(private val database: NewsDatabase) {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    val articles: LiveData<List<Article>> = Transformations.map(database.articlesDao().getArticles()) {
        it.toArticles()
    }


    private var nextPageNumber = FIRST_PAGE_NUMBER


    /**
     * Loads next page of articles for specified parameters from [newsApi] changing [loadingState]
     * according to loading result. Automatically handles [nextPageNumber]. Caches fetched
     * articles in [database] and saves last search request in [database].
     */
    suspend fun loadMoreNews(request: SearchRequest) {
        _loadingState.postValue(LoadingState.LOADING)

        try {
            val response = newsApi.requestNewsArticlesAsync(
                request.query,
                request.safeSearchEnabled,
                request.pageSize,
                nextPageNumber
            )

            when {
                response.isError -> {
                    _loadingState.postValue(LoadingState.ERROR)
                }
                response.isEmpty -> {
                    _loadingState.postValue(LoadingState.EMPTY_PAGE)

                    // Clear cache and update history only when loading first page
                    if (nextPageNumber == FIRST_PAGE_NUMBER) {
                        clearCacheAndUpdateHistory(database, request)
                    }
                }
                else -> {
                    // Successfully received new articles
                    _loadingState.postValue(LoadingState.SUCCESS)

                    // Clear cache and update history only when loading first page
                    if (nextPageNumber == FIRST_PAGE_NUMBER) {
                        clearCacheAndUpdateHistory(database, request)
                    }

                    // Add new articles to cache
                    database.articlesDao().insertAll(*response.databaseArticles)

                    nextPageNumber++
                }
            }
        } catch (e: Exception) {
            _loadingState.postValue(LoadingState.ERROR)
        }
    }

    private suspend fun clearCacheAndUpdateHistory(database: NewsDatabase, request: SearchRequest) {
        database.run {
            articlesDao().clearAll()
            searchHistoryDao().insertAll(request.toDatabaseSearchRequest())
        }
    }
}