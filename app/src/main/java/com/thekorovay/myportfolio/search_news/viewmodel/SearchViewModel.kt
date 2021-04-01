package com.thekorovay.myportfolio.search_news.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.thekorovay.myportfolio.database.getNewsDatabase
import com.thekorovay.myportfolio.search_news.domain_model.Article
import com.thekorovay.myportfolio.search_news.domain_model.SearchRequest
import com.thekorovay.myportfolio.search_news.network.LoadingState
import com.thekorovay.myportfolio.search_news.repository.ArticlesRepository
import com.thekorovay.myportfolio.search_news.repository.NewsSharedPreferences
import kotlinx.coroutines.*

class SearchViewModel(application: Application): AndroidViewModel(application) {

    private val database = getNewsDatabase(application)
    private val prefs = NewsSharedPreferences(application)
    private val repository = ArticlesRepository(database, prefs)

    private var job = Job()

    val articles: LiveData<List<Article>> = repository.articles
    val loadingState: LiveData<LoadingState> = repository.loadingState

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

    /**
     * Factory for constructing SearchViewModel with context parameter
     */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }
}