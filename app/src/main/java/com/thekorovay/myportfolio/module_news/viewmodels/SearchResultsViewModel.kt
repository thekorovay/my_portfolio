package com.thekorovay.myportfolio.module_news.viewmodels

import androidx.lifecycle.*
import com.thekorovay.myportfolio.domain.entities.ArticlesLoadingState
import com.thekorovay.myportfolio.domain.interactors.ArticlesInteractor
import com.thekorovay.myportfolio.entities.UIArticle
import com.thekorovay.myportfolio.entities.UISearchRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchResultsViewModel @Inject constructor(private val interactor: ArticlesInteractor): ViewModel() {

    private var job = Job()

    val articles = interactor.cachedArticles.map { list ->
        list.map { UIArticle.fromArticle(it) }
    }
    val loadingState = interactor.articlesLoadingState

    var isLastQuerySnackbarShown = false


    fun requestMoreArticles(request: UISearchRequest) {
        // Prevent repeating clicks on Show More button
        if (loadingState.value == ArticlesLoadingState.LOADING) {
            return
        }

        job.cancel()
        job = Job()

        val scope = CoroutineScope(job + Dispatchers.IO)

        scope.launch {
            interactor.loadArticles(request.toSearchRequest())
        }
    }
}