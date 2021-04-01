package com.thekorovay.myportfolio.module_search_history.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.thekorovay.myportfolio.database.DatabaseSearchRequest
import com.thekorovay.myportfolio.database.getNewsDatabase
import com.thekorovay.myportfolio.repositories.SearchHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchHistoryViewModel(application: Application): AndroidViewModel(application) {

    private val database = getNewsDatabase(application)
    private val repository = SearchHistoryRepository(database)

    private var clearHistoryJob: Job? = null

    val searchHistory: LiveData<List<DatabaseSearchRequest>> = repository.searchHistory

    fun clearHistory() {
        val job = clearHistoryJob
        if (job != null && job.isActive) {
            return
        }

        clearHistoryJob = CoroutineScope(Dispatchers.IO).launch {
            repository.clearHistory()
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(SearchHistoryViewModel::class.java) ->
                    SearchHistoryViewModel(application) as T
                else ->
                    throw IllegalArgumentException("Unable to construct viewModel")
            }
        }
    }
}