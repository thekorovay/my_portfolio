package com.thekorovay.myportfolio.module_news.viewmodels
 
import androidx.lifecycle.*
import com.thekorovay.myportfolio.domain.interactors.ArticlesInteractor
import com.thekorovay.myportfolio.domain.interactors.SearchHistoryInteractor
import com.thekorovay.myportfolio.entities.UIArticle
import com.thekorovay.myportfolio.entities.UISearchRequest
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class SearchParamsViewModel @Inject constructor(
    articlesInteractor: ArticlesInteractor,
    historyInteractor: SearchHistoryInteractor,
): ViewModel() {

    val lastRequest = historyInteractor.lastRequest.map { UISearchRequest.fromSearchRequest(it) }
    val lastResults = articlesInteractor.cachedArticles.map { list ->
        list.map { UIArticle.fromArticle(it) }
    }

    private val _newSearchRequest = MutableLiveData<UISearchRequest?>()
    val newSearchRequest: LiveData<UISearchRequest?> = _newSearchRequest

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

        _newSearchRequest.value = UISearchRequest(
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