package com.thekorovay.myportfolio.domain.repositories

import com.thekorovay.myportfolio.domain.entities.Article
import com.thekorovay.myportfolio.domain.entities.ArticlesLoadingState
import com.thekorovay.myportfolio.domain.entities.SearchHistoryState
import com.thekorovay.myportfolio.domain.entities.SearchRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.lang.Exception

interface SearchNewsRepository {
    var searchHistoryException: Exception?
    var articlesLoadingException: Exception?

    val articlesLoadingState: StateFlow<ArticlesLoadingState>
    val searchHistoryState: StateFlow<SearchHistoryState>

    val cachedArticles: Flow<List<Article>>

    val searchHistory: StateFlow<List<SearchRequest>>


    suspend fun loadArticles(request: SearchRequest)

    suspend fun clearSearchHistory()

    fun setLoadingErrorHandled()
    fun setHistoryErrorHandled()
}