package com.thekorovay.myportfolio.repositories

import com.thekorovay.myportfolio.database.NewsDatabase
import com.thekorovay.myportfolio.database.toArticles
import com.thekorovay.myportfolio.database.toSearchRequests
import com.thekorovay.myportfolio.domain_model.Article
import com.thekorovay.myportfolio.domain_model.SearchRequest
import com.thekorovay.myportfolio.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class SearchHistoryRepository @Inject constructor(
    private val newsApi: NewsService,
    private val firebase: EasyFirebase,
    private val database: NewsDatabase
) {

    init {
        CoroutineScope(Dispatchers.Default).launch {
            firebase.userChanged.collect { userChanged ->
                if (userChanged) {
                    isSignedIn = firebase.user.value != null
                    clearCache()

                    firebase.setUserChangeHandled()
                }
            }
        }
    }

    /*  ARTICLES CACHE  */

    private val _newsLoadingState: MutableStateFlow<LoadingState?> = MutableStateFlow(null)
    val newsLoadingState: StateFlow<LoadingState?> = _newsLoadingState

    val articles: Flow<List<Article>> = database.articlesDao().getArticles().map { it.toArticles() }

    private var nextPageNumber = FIRST_PAGE_NUMBER


    /**
     * Loads next page of articles for specified parameters from [newsApi] changing [newsLoadingState]
     * according to loading result. Automatically handles [nextPageNumber]. Caches fetched
     * articles in [database] and saves last search request in [database].
     */
    suspend fun loadMoreNews(request: SearchRequest) {
        _newsLoadingState.value = LoadingState.LOADING

        try {
            val response = newsApi.requestNewsArticlesAsync(
                    request.query,
                    request.safeSearchEnabled,
                    request.pageSize,
                    nextPageNumber
            )

            when {
                response.isError -> onNewsLoadingError()
                response.isEmpty -> onNewsLoadingEmptyPage(request)
                else -> onNewsLoadingSuccess(request, response)
            }
        } catch (e: Exception) {
            onNewsLoadingError()
        }
    }

    private fun onNewsLoadingError() {
        _newsLoadingState.value = LoadingState.ERROR
    }

    private suspend fun onNewsLoadingEmptyPage(request: SearchRequest) {
        _newsLoadingState.value = LoadingState.EMPTY_PAGE

        // Clear cache and update history only when loading first page
        if (nextPageNumber == FIRST_PAGE_NUMBER) {
            clearCache()
            updateHistory(request)
        }
    }

    private suspend fun onNewsLoadingSuccess(request: SearchRequest, response: NewsServerResponse) {
        _newsLoadingState.value = LoadingState.SUCCESS

        // Clear cache and update history only when loading first page
        if (nextPageNumber == FIRST_PAGE_NUMBER) {
            clearCache()
            updateHistory(request)
        }

        // Add new articles to cache
        database.articlesDao().insertAll(*response.databaseArticles)

        nextPageNumber++
    }

    private suspend fun clearCache() {
        database.articlesDao().clearAll()
    }



    /*  SEARCH HISTORY  */

    val firebaseException: Exception? get() = firebase.exception
    val firebaseState: StateFlow<EasyFirebase.State> = firebase.state

    private var isSignedIn = firebase.user.value != null


    private val firebaseSearchHistory: Flow<List<SearchRequest>> = firebase.searchHistory.map { it.toSearchRequests() }

    private val localSearchHistory: Flow<List<SearchRequest>> = database.searchHistoryDao().getHistory().map { it.toSearchRequests() }

    val searchHistory: Flow<List<SearchRequest>> = if (isSignedIn) {
        firebaseSearchHistory
    } else {
        localSearchHistory
    }


    private val localLastRequest: Flow<SearchRequest?> = database.searchHistoryDao().getLastRequest().map { it?.toSearchRequest() }

    private val firebaseLastRequest: Flow<SearchRequest?> = firebaseSearchHistory.map { it.firstOrNull() }

    val lastRequest: Flow<SearchRequest?> = if (isSignedIn) {
        firebaseLastRequest
    } else {
        localLastRequest
    }

    fun flushFirebaseErrorState() {
        firebase.flushErrorState()
    }

    suspend fun clearHistory() {
        if (isSignedIn) {
            firebase.clearSearchHistory()
        } else {
            database.searchHistoryDao().clearAll()
        }
    }

    private fun updateHistory(request: SearchRequest) {
        if (isSignedIn) {
            firebase.updateSearchHistory(request.toFirebaseSearchRequest())
        } else {
            database.searchHistoryDao().insert(request.toDatabaseSearchRequest())
        }
    }
}