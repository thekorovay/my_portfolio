package com.thekorovay.myportfolio.module_news.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentSearchArticlesListBinding
import com.thekorovay.myportfolio.domain_model.Article
import com.thekorovay.myportfolio.module_news.ui.recycler_view.NewsRecyclerViewAdapter
import com.thekorovay.myportfolio.module_news.viewmodels.SearchResultsViewModel
import com.thekorovay.myportfolio.module_news.network.LoadingState
import com.thekorovay.myportfolio.module_news.ui.recycler_view.NewsItemClickListener
import com.thekorovay.myportfolio.module_news.ui.recycler_view.NewsListItem
import com.thekorovay.myportfolio.module_news.ui.recycler_view.ShowMoreClickListener
import com.thekorovay.myportfolio.module_news.viewmodels.SearchViewModelsFactory

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


        val newsAdapter = NewsRecyclerViewAdapter(
            NewsItemClickListener { article, view -> readArticle(article, view) },
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

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        // Initiate loading news for the first page or show snack with last search query
        if (!args.showingLastSearchResults) {
            showMoreNews()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

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

    private fun readArticle(article: Article, sharedView: View) {
        val extras = FragmentNavigatorExtras(sharedView to article.id)
        findNavController().navigate(
            SearchResultsFragmentDirections.actionSearchResultsFragmentToReadArticleFragment(
                article
            ),
            extras
        )
    }
}