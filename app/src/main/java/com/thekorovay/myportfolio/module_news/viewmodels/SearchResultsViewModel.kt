package com.thekorovay.myportfolio.module_news.viewmodels

import androidx.lifecycle.*
import com.thekorovay.myportfolio.domain_model.Article
import com.thekorovay.myportfolio.domain_model.SearchRequest
import com.thekorovay.myportfolio.network.LoadingState
import com.thekorovay.myportfolio.repositories.SearchHistoryRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class SearchResultsViewModel @Inject constructor(private val repository: SearchHistoryRepository): ViewModel() {

    private var job = Job()

    val articles: LiveData<List<Article>> = repository.articles
    val loadingState: LiveData<LoadingState> = repository.newsLoadingState

    var isLastQuerySnackbarShown = false


    fun requestMoreArticles(request: SearchRequest) {
        // Prevent repeating clicks on Show More button
        if (loadingState.value == LoadingState.LOADING) {
            return
        }

        job.cancel()
        job = Job()

        val scope = CoroutineScope(job + Dispatchers.IO)

        scope.launch {
            repository.loadMoreNews(request)
        }
    }
}