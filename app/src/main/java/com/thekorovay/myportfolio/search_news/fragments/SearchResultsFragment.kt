package com.thekorovay.myportfolio.search_news.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentSearchArticlesListBinding
import com.thekorovay.myportfolio.search_news.domain_model.Article
import com.thekorovay.myportfolio.search_news.recycler_view.RecyclerViewAdapter
import com.thekorovay.myportfolio.search_news.viewmodel.SearchResultsViewModel
import com.thekorovay.myportfolio.search_news.network.LoadingState
import com.thekorovay.myportfolio.search_news.recycler_view.NewsItemClickListener
import com.thekorovay.myportfolio.search_news.recycler_view.NewsListItem
import com.thekorovay.myportfolio.search_news.recycler_view.ShowMoreClickListener
import com.thekorovay.myportfolio.search_news.viewmodel.SearchViewModelsFactory

class SearchResultsFragment: Fragment() {

    private val args: SearchResultsFragmentArgs by navArgs()
    private lateinit var binding: FragmentSearchArticlesListBinding
    private val viewModel by lazy {
        ViewModelProvider(this, SearchViewModelsFactory(requireActivity().application))
            .get(SearchResultsViewModel::class.java)
    }

    private var isMoreResultsAvailable = true
    private var isListEmpty = true

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
            NewsItemClickListener { article -> readArticle(article) },
            ShowMoreClickListener { showMoreNews() }
        )
        binding.rvSearchResults.adapter = newsAdapter


        viewModel.articles.observe(viewLifecycleOwner) { articles ->
            // Map Articles to ListItems with or without thumbs
            val listItems = articles.map {
                if (args.searchRequest.thumbnailsEnabled) {
                    NewsListItem.ArticleItem(it)
                } else {
                    NewsListItem.ArticleNoThumbnailItem(it)
                }
            }.toMutableList()

            // Show or hide the Show More button
            isListEmpty = listItems.isEmpty()
            if (!args.showingLastSearchResults && isMoreResultsAvailable && !isListEmpty) {
                listItems.add(NewsListItem.ShowMoreNewsItem)
            }

            newsAdapter.submitList(listItems)
        }


        viewModel.loadingState.observe(viewLifecycleOwner) { state ->
            isMoreResultsAvailable = state != LoadingState.EMPTY_PAGE

            binding.isProgressBarVisible = state == LoadingState.LOADING
            binding.isErrorScreenVisible = isListEmpty && state == LoadingState.ERROR
            binding.isNoResultsMessageVisible = isListEmpty && !isMoreResultsAvailable

            if (state == LoadingState.ERROR && !isListEmpty) {
                showLoadingErrorSnack()
            }
        }

        binding.btnReload.setOnClickListener { showMoreNews() }


        return binding.root
    }

    /*
    * This callback is required to make first loading of news since onCreateView() will be called
    * every time user will return back from the ReadArticle fragment */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initiate loading news for the first page or show snack with last search query
        if (!args.showingLastSearchResults) {
            showMoreNews()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.showingLastSearchResults && !viewModel.isLastQuerySnackbarShown) {
            showLastSearchQuerySnack()
            viewModel.isLastQuerySnackbarShown = true
        }
    }

    private fun showMoreNews() {
        viewModel.requestMoreArticles(args.searchRequest)
    }

    private fun showLoadingErrorSnack() {
        Snackbar.make(binding.root, R.string.couldnt_load_news, Snackbar.LENGTH_SHORT)
            .setAction(R.string.try_again) { showMoreNews() }
            .show()
    }

    private fun showLastSearchQuerySnack() {
        val message = getString(R.string.showing_results_for_query, args.searchRequest.query)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun readArticle(article: Article) {
        findNavController().navigate(
            SearchResultsFragmentDirections.actionSearchResultsFragmentToReadArticleFragment(
                article
            )
        )
    }
}