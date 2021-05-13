package com.thekorovay.myportfolio.module_news.viewmodels
 
import androidx.lifecycle.*
import com.thekorovay.myportfolio.domain_model.SearchRequest
import com.thekorovay.myportfolio.repositories.SearchHistoryRepository
import java.time.LocalDateTime
import javax.inject.Inject

class SearchParamsViewModel @Inject constructor(
    repository: SearchHistoryRepository
): ViewModel() {

    val lastRequest = repository.lastRequest
    val lastResults = repository.articles

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
}