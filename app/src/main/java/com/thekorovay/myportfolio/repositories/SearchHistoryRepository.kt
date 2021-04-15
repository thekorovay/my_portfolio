package com.thekorovay.myportfolio.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.firebase.database.ValueEventListener
import com.thekorovay.myportfolio.database.NewsDatabase
import com.thekorovay.myportfolio.database.toSearchRequests
import com.thekorovay.myportfolio.domain_model.SearchRequest
import com.thekorovay.myportfolio.network.EasyFirebase
import com.thekorovay.myportfolio.network.toSearchRequests
import java.lang.Exception

class SearchHistoryRepository(
        private val firebase: EasyFirebase,
        private val database: NewsDatabase
) {

    private val isSignedIn
        get() = firebase.user.value != null
    private var searchHistoryListener: ValueEventListener? = null

    val exception: Exception? get() = firebase.exception
    val state: LiveData<EasyFirebase.State> = firebase.state

    private val firebaseSearchHistory: LiveData<List<SearchRequest>> =
        Transformations.map(firebase.searchHistory) { it.toSearchRequests() }

    private val localSearchHistory: LiveData<List<SearchRequest>> = Transformations.map(
        database.searchHistoryDao().getHistory()
    ) {
        it.toSearchRequests()
    }

    val searchHistory: LiveData<List<SearchRequest>> = if (isSignedIn) firebaseSearchHistory else localSearchHistory

    fun subscribeToSearchHistory() {
        searchHistoryListener = firebase.getSearchHistoryListener()
    }

    fun unsubscribeFromSearchHistory() {
        firebase.removeSearchHistoryListener(searchHistoryListener)
    }

    fun flushErrorState() {
        firebase.flushErrorState()
    }

    suspend fun clearHistory() {
        if (isSignedIn) {
            firebase.clearSearchHistory()
        } else {
            database.searchHistoryDao().clearAll()
        }
    }

    fun updateHistory(request: SearchRequest) {
        if (isSignedIn) {
            firebase.updateSearchHistory(request.toFirebaseSearchRequest())
        } else {
            database.searchHistoryDao().insert(request.toDatabaseSearchRequest())
        }
    }
}