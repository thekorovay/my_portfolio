package com.thekorovay.myportfolio.module_search_history.viewmodel

import androidx.lifecycle.*
import com.thekorovay.myportfolio.domain.interactors.SearchHistoryInteractor
import com.thekorovay.myportfolio.entities.toUISearchRequests
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
    val searchHistory = interactor.history.map { it.toUISearchRequests() }

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