package com.thekorovay.myportfolio.news.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentSearchArticlesListBinding
import com.thekorovay.myportfolio.news.RecyclerViewAdapter
import com.thekorovay.myportfolio.news.SearchViewModel
import com.thekorovay.myportfolio.news.network.LoadingState
import java.lang.Exception

class SearchResultsFragment: Fragment() {

    private val args: SearchResultsFragmentArgs by navArgs()
    private lateinit var binding: FragmentSearchArticlesListBinding
    private val viewModel: SearchViewModel by viewModels()

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_articles_list,
            container,
            false
        )

        //binding.lifecycleOwner = this

        val newsAdapter = RecyclerViewAdapter()
        binding.rvSearchResults.adapter = newsAdapter

        viewModel.articles.observe(viewLifecycleOwner) { newsAdapter.submitList(it) }

        viewModel.loadingState.observe(viewLifecycleOwner) {
            when(it) {
                LoadingState.LOADING -> showSnack("LOADING")
                LoadingState.ERROR -> showSnack("ERROR")
                LoadingState.EMPTY_PAGE -> showSnack("EMPTY_PAGE")
                LoadingState.SUCCESS -> showSnack("SUCCESS")
            }
        }

        // Initiate loading news articles for the first page of search
        args.run {
            viewModel.requestMoreArticles(
                query,
                safeSearchEnabled,
                thumbnailsEnabled,
                pageSize
            )
        }

        return binding.root
    }

    private fun showSnack(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }
}