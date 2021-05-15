package com.thekorovay.myportfolio.data.repositories

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.thekorovay.myportfolio.data.database.NewsDatabase
import com.thekorovay.myportfolio.data.entities.*
import com.thekorovay.myportfolio.data.network.FIRST_PAGE_NUMBER
import com.thekorovay.myportfolio.data.network.NewsService
import com.thekorovay.myportfolio.domain.entities.*
import com.thekorovay.myportfolio.domain.repositories.ProfileRepository
import com.thekorovay.myportfolio.domain.repositories.SearchNewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Singleton

@Singleton
class SearchNewsRepositoryImpl(
    private val newsApi: NewsService,
    private val database: NewsDatabase,
    profileRepo: ProfileRepository
): SearchNewsRepository {


    private val _remoteSearchHistory: MutableStateFlow<List<FirebaseSearchRequest>> = MutableStateFlow(listOf())
    private val remoteSearchHistory: Flow<List<SearchRequest>> = _remoteSearchHistory.map { it.toSearchRequests() }
    private val localSearchHistory: Flow<List<SearchRequest>> = database.searchHistoryDao().getHistory().map { it.toSearchRequests() }
    private val activeSearchHistoryFlow = MutableStateFlow(
        if (profileRepo.user.value == null) localSearchHistory else remoteSearchHistory
    )
    private val _searchHistory = MutableStateFlow<List<SearchRequest>>(listOf())
    override val searchHistory: StateFlow<List<SearchRequest>> = _searchHistory

    init {
        // Clear articles cache every time user log in/out
        CoroutineScope(Dispatchers.Default).launch {
            profileRepo.userChanged.collect { userChanged ->
                if (userChanged) {
                    clearCache()
                    profileRepo.setUserChangeHandled()
                }
            }
        }

        // Control firebase search history listening
        CoroutineScope(Dispatchers.Default).launch {
            profileRepo.user.collect { user ->
                // The order of calls to listenToHistory(), stopListeningHistory() and setting
                // currentUser is important and must be exactly this
                if (user != null) {
                    currentUser = user
                    listenToHistory()
                    activeSearchHistoryFlow.value = remoteSearchHistory
                } else {
                    stopListeningHistory()
                    currentUser = user
                    activeSearchHistoryFlow.value = localSearchHistory
                }
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            activeSearchHistoryFlow.collect { activeFlow ->
                CoroutineScope(Dispatchers.Default).launch {
                    activeFlow.collect { list ->
                        _searchHistory.value = list
                    }
                }
            }
        }
    }

    private var currentUser: User? = profileRepo.user.value

    private var nextPageNumber = FIRST_PAGE_NUMBER

    private var historyListener: ValueEventListener? = null

    override var searchHistoryException: Exception? = null
        set(value) {
            value?.run { _searchHistoryState.value = SearchHistoryState.ERROR }
            field = value
        }

    override var articlesLoadingException: Exception? = null
        set(value) {
            value?.run { _articlesLoadingState.value = ArticlesLoadingState.ERROR }
            field = value
        }

    private val _articlesLoadingState = MutableStateFlow(ArticlesLoadingState.IDLE)
    override val articlesLoadingState: StateFlow<ArticlesLoadingState> = _articlesLoadingState

    private val _searchHistoryState = MutableStateFlow(SearchHistoryState.IDLE)
    override val searchHistoryState: StateFlow<SearchHistoryState> = _searchHistoryState

    override val cachedArticles: Flow<List<Article>> = database.articlesDao().getArticles().map { it.toArticles() }



    /**
     * Loads next page of articles for specified parameters from [newsApi] changing [articlesLoadingState]
     * according to loading result. Automatically handles [nextPageNumber]. Caches fetched
     * articles and saves last search request in [database].
     */
    override suspend fun loadArticles(request: SearchRequest) {
        _articlesLoadingState.value = ArticlesLoadingState.LOADING

        if (request.addToHistory) {
            nextPageNumber = FIRST_PAGE_NUMBER
        }

        try {
            val response = newsApi.requestNewsArticlesAsync(
                request.query,
                request.safeSearchEnabled,
                request.pageSize,
                nextPageNumber
            )

            when {
                response.isError -> onNewsLoadingError()
                response.isEmpty -> onNewsLoadingEmptyPage(request)
                else -> onNewsLoadingSuccess(request, response)
            }
        } catch (e: Exception) {
            onNewsLoadingError()
        }
    }

    override suspend fun clearSearchHistory() {
        if (currentUser != null) {
            clearFirebaseSearchHistory()
        } else {
            database.searchHistoryDao().clearAll()
        }
    }

    override fun setLoadingErrorHandled() {
        if (articlesLoadingState.value == ArticlesLoadingState.ERROR) {
            _articlesLoadingState.value = ArticlesLoadingState.IDLE
        }
    }

    override fun setHistoryErrorHandled() {
        if (searchHistoryState.value == SearchHistoryState.ERROR) {
            _searchHistoryState.value = SearchHistoryState.IDLE
        }
    }

    private fun onNewsLoadingError() {
        _articlesLoadingState.value = ArticlesLoadingState.ERROR
    }

    private suspend fun onNewsLoadingEmptyPage(request: SearchRequest) {
        _articlesLoadingState.value = ArticlesLoadingState.EMPTY_PAGE

        // Clear cache and update history only when loading first page
        if (nextPageNumber == FIRST_PAGE_NUMBER) {
            clearCache()
            updateHistory(request)
        }
    }

    private suspend fun onNewsLoadingSuccess(request: SearchRequest, response: NewsServerResponse) {
        _articlesLoadingState.value = ArticlesLoadingState.SUCCESS

        // Clear cache and update history only when loading first page
        if (request.addToHistory) {
            clearCache()
            updateHistory(request)
        }

        // Add new articles to cache
        database.articlesDao().insertAll(*response.databaseArticles)

        nextPageNumber++
    }

    private suspend fun clearCache() {
        database.articlesDao().clearAll()
    }

    private fun clearFirebaseSearchHistory() {
        currentUser?.let { user ->
            _searchHistoryState.value = SearchHistoryState.BUSY

            getHistoryReference(user.uid).setValue(null)
                .addOnCompleteListener(getOnCompleteListener())
        }
    }

    private fun getOnCompleteListener() = OnCompleteListener<Void> { finishedTask ->
        if (finishedTask.isSuccessful) {
            _searchHistoryState.value = SearchHistoryState.IDLE
        } else {
            searchHistoryException = finishedTask.exception
        }
    }

    private fun updateHistory(request: SearchRequest) {
        if (currentUser != null) {
            updateFirebaseSearchHistory(FirebaseSearchRequest.fromSearchRequest(request))
        } else {
            database.searchHistoryDao().insert(DatabaseSearchRequest.fromSearchRequest(request))
        }
    }

    private fun updateFirebaseSearchHistory(request: FirebaseSearchRequest) {
        currentUser?.let { user ->
            _searchHistoryState.value = SearchHistoryState.BUSY

            getHistoryReference(user.uid)
                .push()
                .setValue(request)
                .addOnCompleteListener(getOnCompleteListener())
        }
    }

    private fun getHistoryReference(userId: String) = Firebase.database.reference
        .child("users")
        .child(userId)
        .child("search_history")

    private fun listenToHistory() {
        currentUser?.let { user ->
            _searchHistoryState.value = SearchHistoryState.BUSY

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val history = mutableListOf<FirebaseSearchRequest>()

                    snapshot.children.reversed().forEach { entrySnap ->
                        entrySnap.getValue<FirebaseSearchRequest>()?.let {
                            history.add(it)
                        }
                    }

                    _remoteSearchHistory.value = history
                    _searchHistoryState.value = SearchHistoryState.IDLE
                }

                @Suppress("ThrowableNotThrown")
                override fun onCancelled(error: DatabaseError) {
                    searchHistoryException = error.toException()
                }
            }

            historyListener = getHistoryReference(user.uid).addValueEventListener(listener)
        }
    }

    private fun stopListeningHistory() {
        val listener = historyListener
        val user = currentUser

        if (user != null && listener != null) {
            getHistoryReference(user.uid).removeEventListener(listener)
            historyListener = null
        }
    }
}