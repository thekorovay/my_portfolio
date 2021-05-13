package com.thekorovay.myportfolio.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ValueEventListener
import com.thekorovay.myportfolio.database.NewsDatabase
import com.thekorovay.myportfolio.database.toArticles
import com.thekorovay.myportfolio.database.toSearchRequests
import com.thekorovay.myportfolio.domain_model.Article
import com.thekorovay.myportfolio.domain_model.SearchRequest
import com.thekorovay.myportfolio.network.*
import java.lang.Exception
import javax.inject.Inject

class SearchHistoryRepository @Inject constructor(
    private val newsApi: NewsService,
    private val firebase: EasyFirebase,
    private val database: NewsDatabase
) {

    /*  ARTICLES CACHE  */

    private val _newsLoadingState = MutableLiveData<LoadingState>()
    val newsLoadingState: LiveData<LoadingState>
        get() = _newsLoadingState

    val articles: LiveData<List<Article>> = Transformations.map(database.articlesDao().getArticles()) {
        it.toArticles()
    }

    private var nextPageNumber = FIRST_PAGE_NUMBER


    /**
     * Loads next page of articles for specified parameters from [newsApi] changing [newsLoadingState]
     * according to loading result. Automatically handles [nextPageNumber]. Caches fetched
     * articles in [database] and saves last search request in [database].
     */
    suspend fun loadMoreNews(request: SearchRequest) {
        _newsLoadingState.postValue(LoadingState.LOADING)

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
        _newsLoadingState.postValue(LoadingState.ERROR)
    }

    private suspend fun onNewsLoadingEmptyPage(request: SearchRequest) {
        _newsLoadingState.postValue(LoadingState.EMPTY_PAGE)

        // Clear cache and update history only when loading first page
        if (nextPageNumber == FIRST_PAGE_NUMBER) {
            clearCache()
            updateHistory(request)
        }
    }

    private suspend fun onNewsLoadingSuccess(request: SearchRequest, response: NewsServerResponse) {
        _newsLoadingState.postValue(LoadingState.SUCCESS)

        // Clear cache and update history only when loading first page
        if (nextPageNumber == FIRST_PAGE_NUMBER) {
            clearCache()
            updateHistory(request)
        }

        // Add new articles to cache
        database.articlesDao().insertAll(*response.databaseArticles)

        nextPageNumber++
    }

    suspend fun clearCache() {
        database.articlesDao().clearAll()
    }



    /*  SEARCH HISTORY  */

    val firebaseException: Exception? get() = firebase.exception
    val firebaseState: LiveData<EasyFirebase.State> = firebase.state
    val firebaseUser: LiveData<FirebaseUser?> = firebase.user

    private var searchHistoryListener: ValueEventListener? = null

    private val isSignedIn
        get() = firebaseUser.value != null


    private val firebaseSearchHistory: LiveData<List<SearchRequest>> =
        Transformations.map(firebase.searchHistory) { it.toSearchRequests() }

    private val localSearchHistory: LiveData<List<SearchRequest>> = Transformations.map(
        database.searchHistoryDao().getHistory()
    ) {
        it.toSearchRequests()
    }

    val searchHistory: LiveData<List<SearchRequest>> = if (isSignedIn) {
        firebaseSearchHistory
    } else {
        localSearchHistory
    }


    private val localLastRequest: LiveData<SearchRequest?> = Transformations.map(
            database.searchHistoryDao().getLastRequest()
    ) {
        it?.toSearchRequest()
    }

    private val firebaseLastRequest: LiveData<SearchRequest?> = Transformations.map(
            firebase.lastRequest
    ) {
        it?.toSearchRequest()
    }

    val lastRequest: LiveData<SearchRequest?> = if (isSignedIn) {
        firebaseLastRequest
    } else {
        localLastRequest
    }

    fun subscribeToSearchHistory() {
        searchHistoryListener = firebase.getSearchHistoryListener()
    }

    fun unsubscribeFromSearchHistory() {
        firebase.removeSearchHistoryListener(searchHistoryListener)
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