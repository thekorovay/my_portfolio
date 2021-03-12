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
import com.thekorovay.myportfolio.news.recycler_view.RecyclerViewAdapter
import com.thekorovay.myportfolio.news.SearchViewModel
import com.thekorovay.myportfolio.news.network.LoadingState
import com.thekorovay.myportfolio.news.recycler_view.NewsItemClickListener
import com.thekorovay.myportfolio.news.recycler_view.NewsListItem
import com.thekorovay.myportfolio.news.recycler_view.ShowMoreClickListener
import java.lang.Exception

class SearchResultsFragment: Fragment() {

    private val args: SearchResultsFragmentArgs by navArgs()
    private lateinit var binding: FragmentSearchArticlesListBinding
    private val viewModel: SearchViewModel by viewModels()

    private var isMoreResultsAvailable = true
    private var isError = false
    private val shouldShowMoreButton
        get() = isMoreResultsAvailable && !isError

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

        val newsAdapter = RecyclerViewAdapter(
            NewsItemClickListener { id -> readArticle(id) },
            ShowMoreClickListener { showMoreNews() }
        )
        binding.rvSearchResults.adapter = newsAdapter

        viewModel.articles.observe(viewLifecycleOwner) { articles ->
            // Map Articles to ListItems with or without thumbs
            val listItems = articles.map {
                if (args.thumbnailsEnabled) {
                    NewsListItem.ArticleItem(it)
                } else {
                    NewsListItem.ArticleNoThumbnailItem(it)
                }
            }.toMutableList()

            // Show or hide the Show More button
            if (shouldShowMoreButton && listItems.isNotEmpty()) {
                listItems.add(NewsListItem.ShowMoreNewsItem)
            }

            newsAdapter.submitList(listItems)
        }

        viewModel.loadingState.observe(viewLifecycleOwner) {
            when(it) {
                LoadingState.LOADING -> {
                    showSnack("LOADING")
                    isError = false
                }
                LoadingState.ERROR -> {
                    showSnack("ERROR")
                    isError = true
                }
                LoadingState.EMPTY_PAGE -> {
                    showSnack("EMPTY_PAGE")
                    isMoreResultsAvailable = false
                }
                LoadingState.SUCCESS -> showSnack("SUCCESS")
                else -> throw Exception("Unknown loading state ${it.name}")
            }
        }

        // Initiate loading news for the first page of search
        showMoreNews()

        return binding.root
    }

    private fun showMoreNews() {
        args.run {
            viewModel.requestMoreArticles(
                query,
                safeSearchEnabled,
                thumbnailsEnabled,
                pageSize
            )
        }
    }

    private fun readArticle(id: String) {
        //todo
        showSnack(id)
    }

    private fun showSnack(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }
}