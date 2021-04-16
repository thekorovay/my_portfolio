package com.thekorovay.myportfolio.module_news.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.thekorovay.myportfolio.database.EasyPrefs
import com.thekorovay.myportfolio.database.getNewsDatabase
import com.thekorovay.myportfolio.domain_model.Article
import com.thekorovay.myportfolio.domain_model.SearchRequest
import com.thekorovay.myportfolio.network.EasyFirebase
import com.thekorovay.myportfolio.repositories.SearchHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class SearchParamsViewModel(application: Application): AndroidViewModel(application) {

    private val prefs = EasyPrefs(application)
    private val repository = SearchHistoryRepository(
            EasyFirebase.getInstance(application),
            getNewsDatabase(application)
    ).apply {
        subscribeToSearchHistory()
    }

    /**
     * Client (GUI) should observe this property even if client won't use it, because
     * viewModel needs at least one observer on this property to handle cached articles properly
     */
    val userSignInStateChanged: LiveData<Boolean> = Transformations.map(repository.firebaseUser) { user ->
        val userChanged = user?.uid != prefs.lastUserId
        if (userChanged) {
            clearCache()
        }

        prefs.lastUserId = user?.uid

        userChanged
    }

    private var clearCacheJob: Job? = null

    private fun clearCache() {
        val job = clearCacheJob
        if (job != null && job.isActive) {
            return
        }

        clearCacheJob = CoroutineScope(Dispatchers.IO).launch {
            repository.clearCache()
        }
    }


    val lastRequest: LiveData<SearchRequest?> = repository.lastRequest
    val lastResults: LiveData<List<Article>> = repository.articles

    private val _newSearchRequest = MutableLiveData<SearchRequest?>()
    val newSearchRequest: LiveData<SearchRequest?> = _newSearchRequest

    private val _invalidQueryFlag = MutableLiveData(false)
    val invalidQueryFlag: LiveData<Boolean> = _invalidQueryFlag


    fun search(
        query: String,
        safeSearchEnabled: Boolean,
        thumbnailsEnabled: Boolean,
        pageSize: Int
    ) {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) {
            _invalidQueryFlag.value = true
            return
        }

        _newSearchRequest.value = SearchRequest(
            LocalDateTime.now().toString(),
            trimmedQuery,
            safeSearchEnabled,
            thumbnailsEnabled,
            pageSize
        )
    }

    fun setInvalidQueryWarningShown() { _invalidQueryFlag.value = false }

    fun setNavigationToResultsCompleted() { _newSearchRequest.value = null }

    override fun onCleared() {
        repository.unsubscribeFromSearchHistory()
    }
}