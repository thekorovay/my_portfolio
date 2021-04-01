package com.thekorovay.myportfolio.repositories

import androidx.lifecycle.LiveData
import com.thekorovay.myportfolio.database.DatabaseSearchRequest
import com.thekorovay.myportfolio.database.NewsDatabase

class SearchHistoryRepository(private val database: NewsDatabase) {
    val searchHistory: LiveData<List<DatabaseSearchRequest>> = database.searchHistoryDao().getHistory()

    suspend fun clearHistory() {
        database.searchHistoryDao().clearAll()
    }
}