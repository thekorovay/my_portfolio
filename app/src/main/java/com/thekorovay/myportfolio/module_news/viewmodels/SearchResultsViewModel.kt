package com.thekorovay.myportfolio.module_news.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.thekorovay.myportfolio.database.getNewsDatabase
import com.thekorovay.myportfolio.domain_model.Article
import com.thekorovay.myportfolio.domain_model.SearchRequest
import com.thekorovay.myportfolio.network.EasyFirebase
import com.thekorovay.myportfolio.network.LoadingState
import com.thekorovay.myportfolio.repositories.SearchHistoryRepository
import kotlinx.coroutines.*

class SearchResultsViewModel(application: Application): AndroidViewModel(application) {

    private val database = getNewsDatabase(application)
    private val firebase = EasyFirebase.getInstance(application)
    private val searchHistoryRepository = SearchHistoryRepository(firebase, database)

    private var job = Job()

    val articles: LiveData<List<Article>> = searchHistoryRepository.articles
    val loadingState: LiveData<LoadingState> = searchHistoryRepository.newsLoadingState

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
            searchHistoryRepository.loadMoreNews(request)
        }
    }
}