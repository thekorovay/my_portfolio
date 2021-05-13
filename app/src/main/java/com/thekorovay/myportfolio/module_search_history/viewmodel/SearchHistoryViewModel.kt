package com.thekorovay.myportfolio.module_search_history.viewmodel

import androidx.lifecycle.*
import com.thekorovay.myportfolio.repositories.SearchHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class SearchHistoryViewModel @Inject constructor(private val repository: SearchHistoryRepository): ViewModel() {

    val exception: Exception? get() = repository.firebaseException
    val state = repository.firebaseState
    val searchHistory = repository.searchHistory

    private var clearHistoryJob: Job? = null


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
}