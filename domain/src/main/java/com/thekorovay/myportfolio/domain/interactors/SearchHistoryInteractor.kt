package com.thekorovay.myportfolio.domain.interactors

import com.thekorovay.myportfolio.domain.repositories.SearchNewsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryInteractor @Inject constructor(private val repo: SearchNewsRepository) {
    val exception get() = repo.searchHistoryException

    val searchHistoryState = repo.searchHistoryState
    val history = repo.searchHistory


    suspend fun clearSearchHistory() = repo.clearSearchHistory()

    fun setErrorHandled() = repo.setHistoryErrorHandled()
}