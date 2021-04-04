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
                response.isError -> _loadingState.postValue(LoadingState.ERROR)
                response.isEmpty -> _loadingState.postValue(LoadingState.EMPTY_PAGE)
                else -> {
                    // Successfully received new articles
                    _loadingState.postValue(LoadingState.SUCCESS)
                    database.run {
                        // Clear previous search results cache
                        if (nextPageNumber == FIRST_PAGE_NUMBER) {
                            articlesDao().clearAll()
                        }
                        // Add new articles to cache
                        articlesDao().insertAll(*response.databaseArticles)
                        // Add search request to history only once, not after click on 'Show More' button
                        if (nextPageNumber == FIRST_PAGE_NUMBER) {
                            searchHistoryDao().insertAll(request.toDatabaseSearchRequest())
                        }
                    }
                    nextPageNumber++
                }
            }
        } catch (e: Exception) {
            _loadingState.postValue(LoadingState.ERROR)
        }
    }
}