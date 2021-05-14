package com.thekorovay.myportfolio.domain.interactors

import com.thekorovay.myportfolio.domain.entities.SearchRequest
import com.thekorovay.myportfolio.domain.repositories.SearchNewsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticlesInteractor @Inject constructor(private val repo: SearchNewsRepository) {
    val exception get() = repo.articlesLoadingException

    val articlesLoadingState = repo.articlesLoadingState
    val cachedArticles = repo.cachedArticles

    suspend fun loadArticles(request: SearchRequest) = repo.loadArticles(request)

    fun setErrorHandled() = repo.setLoadingErrorHandled()
}