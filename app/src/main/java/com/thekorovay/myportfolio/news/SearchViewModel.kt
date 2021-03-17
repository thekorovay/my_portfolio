package com.thekorovay.myportfolio.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thekorovay.myportfolio.news.model.Article
import com.thekorovay.myportfolio.news.network.LoadingState
import com.thekorovay.myportfolio.news.network.newsApi
import kotlinx.coroutines.*

class SearchViewModel: ViewModel() {

    private val _articles = MutableLiveData(listOf<Article>())
    val articles: LiveData<List<Article>>
        get() = _articles

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState



    private var nextPageNumber = 1
    private var job = Job()


    fun requestMoreArticles(
        query: String,
        safeSearchEnabled: Boolean,
        thumbnailsEnabled: Boolean,
        pageSize: Int
    ) {
        // Prevent repeating clicks on Show More button
        if (loadingState.value == LoadingState.LOADING) {
            return
        }
        _loadingState.value = LoadingState.LOADING

        job.cancel()
        job = Job()

        val scope = CoroutineScope(job + Dispatchers.IO)

        scope.launch {
            try {
                val response = newsApi.getNewsArticlesAsync(
                    query,
                    safeSearchEnabled,
                    thumbnailsEnabled,
                    pageSize,
                    nextPageNumber
                )

                when {
                    response.isError -> _loadingState.postValue(LoadingState.ERROR)
                    response.isEmpty -> _loadingState.postValue(LoadingState.EMPTY_PAGE)
                    else -> {
                        // Successfully received new articles
                        _loadingState.postValue(LoadingState.SUCCESS)
                        nextPageNumber++
                        _articles.postValue(_articles.value!!.plus(response.articles))
                    }
                }
            } catch (e: Exception) {
                _loadingState.postValue(LoadingState.ERROR)
            }
        }
    }
}