package com.thekorovay.myportfolio.module_news.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.thekorovay.myportfolio.database.getNewsDatabase
import com.thekorovay.myportfolio.domain_model.SearchRequest

class SearchParamsViewModel(application: Application): AndroidViewModel(application) {

    private val database = getNewsDatabase(application)

    val lastRequest: LiveData<SearchRequest?> = Transformations.map(database.searchHistoryDao().getLastRequest()) {
        it?.toSearchRequest()
    }

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

        _newSearchRequest.value = SearchRequest(trimmedQuery, safeSearchEnabled, thumbnailsEnabled, pageSize)
    }

    fun setInvalidQueryWarningShown() { _invalidQueryFlag.value = false }

    fun setNavigationToResultsCompleted() { _newSearchRequest.value = null }
}