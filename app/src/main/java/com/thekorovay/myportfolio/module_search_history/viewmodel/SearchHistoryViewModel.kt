package com.thekorovay.myportfolio.module_search_history.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.thekorovay.myportfolio.domain.interactors.SearchHistoryInteractor
import com.thekorovay.myportfolio.entities.UISearchRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class SearchHistoryViewModel @Inject constructor(private val interactor: SearchHistoryInteractor): ViewModel() {

    val exception: Exception? get() = interactor.exception
    val state = interactor.searchHistoryState
    val searchHistory = interactor.history.map { list ->
        Log.e("***", "search history viewmodel: ${list.size}")
        list.map { UISearchRequest.fromSearchRequest(it) }
    }

    private var clearHistoryJob: Job? = null


    fun clearHistory() {
        val job = clearHistoryJob
        if (job != null && job.isActive) {
            return
        }

        clearHistoryJob = CoroutineScope(Dispatchers.IO).launch {
            interactor.clearSearchHistory()
        }
    }

    fun setErrorMessageDisplayed() {
        interactor.setErrorHandled()
    }
}