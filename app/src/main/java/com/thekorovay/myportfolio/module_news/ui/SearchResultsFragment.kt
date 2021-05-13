package com.thekorovay.myportfolio.module_news.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.thekorovay.myportfolio.MyApplication
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentSearchResultsBinding
import com.thekorovay.myportfolio.domain_model.Article
import com.thekorovay.myportfolio.module_news.ui.recycler_view.NewsRecyclerViewAdapter
import com.thekorovay.myportfolio.module_news.viewmodels.SearchResultsViewModel
import com.thekorovay.myportfolio.network.LoadingState
import com.thekorovay.myportfolio.module_news.ui.recycler_view.NewsItemClickListener
import com.thekorovay.myportfolio.module_news.ui.recycler_view.NewsListItem
import com.thekorovay.myportfolio.module_news.ui.recycler_view.ShowMoreClickListener
import com.thekorovay.myportfolio.tools.setupNavUpButton
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class SearchResultsFragment: Fragment() {

    private val args: SearchResultsFragmentArgs by navArgs()
    private lateinit var binding: FragmentSearchResultsBinding
    @Inject lateinit var viewModel: SearchResultsViewModel

    private var isMoreResultsAvailable = true
    private var isListVisible = MutableLiveData(false)

    override fun onAttach(context: Context) {
        (context.applicationContext as MyApplication).appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_results,
            container,
            false
        )

        isListVisible.observe(viewLifecycleOwner) { visibility ->
            binding.isRecyclerViewVisible = visibility
        }

        binding.toolbar.setupNavUpButton(findNavController())

        val newsAdapter = NewsRecyclerViewAdapter(
            NewsItemClickListener { article -> readArticle(article) },
            ShowMoreClickListener { showMoreNews() }
        )
        binding.rvSearchResults.adapter = newsAdapter

        lifecycleScope.launchWhenStarted {
            viewModel.loadingState.collect { state ->
                if (state == LoadingState.SUCCESS) {
                    isListVisible.postValue(true)
                }

                isMoreResultsAvailable = state != LoadingState.EMPTY_PAGE

                binding.isProgressBarVisible = state == LoadingState.LOADING
                binding.isErrorScreenVisible = !isListVisible.value!! && state == LoadingState.ERROR
                binding.isNoResultsMessageVisible = !isListVisible.value!! && !isMoreResultsAvailable

                if (state == LoadingState.ERROR && isListVisible.value!!) {
                    showLoadingErrorSnack()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.articles.collect { articles ->
                // Map Articles to ListItems with or without thumbs
                val listItems = articles.map {
                    if (args.searchRequest.thumbnailsEnabled) {
                        NewsListItem.ArticleItem(it)
                    } else {
                        NewsListItem.ArticleNoThumbnailItem(it)
                    }
                }.toMutableList()

                // Show or hide the Show More button
                if (!args.showingLastSearchResults && isMoreResultsAvailable && listItems.isNotEmpty()) {
                    listItems.add(NewsListItem.ShowMoreNewsItem)
                }

                newsAdapter.submitList(listItems)
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
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        // Initiate loading news for the first page or just show the recyclerview
        if (!args.showingLastSearchResults) {
            showMoreNews()
        } else {
            isListVisible.value = true
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