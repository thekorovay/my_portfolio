package com.thekorovay.myportfolio.module_search_history.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.thekorovay.myportfolio.database.getNewsDatabase
import com.thekorovay.myportfolio.domain_model.SearchRequest
import com.thekorovay.myportfolio.network.EasyFirebase
import com.thekorovay.myportfolio.repositories.SearchHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchHistoryViewModel(application: Application): AndroidViewModel(application) {

    private val firebase = EasyFirebase.getInstance(application)
    private val database = getNewsDatabase(application)
    private val repository = SearchHistoryRepository(firebase, database).apply {
        subscribeToSearchHistory()
    }

    val exception: Exception? get() = repository.firebaseException
    val state: LiveData<EasyFirebase.State> = repository.firebaseState

    private var clearHistoryJob: Job? = null

    val searchHistory: LiveData<List<SearchRequest>> = repository.searchHistory

    fun clearHistory() {
        val job = clearHistoryJob
        if (job != null && job.isActive) {
            return
        }

        clearHistoryJob = CoroutineScope(Dispatchers.IO).launch {
            repository.clearHistory()
        }
    }

    fun setErrorMessageDisplayed() {
        repository.flushFirebaseErrorState()
    }

    override fun onCleared() {
        super.onCleared()
        repository.unsubscribeFromSearchHistory()
    }
}