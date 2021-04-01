package com.thekorovay.myportfolio.search_news.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchViewModelsFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SearchResultsViewModel::class.java) ->
                SearchResultsViewModel(application) as T
            modelClass.isAssignableFrom(SearchParamsViewModel::class.java) ->
                SearchParamsViewModel(application) as T
            else ->
                throw IllegalArgumentException("Unable to construct viewModel")
        }
    }
}