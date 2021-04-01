package com.thekorovay.myportfolio.search_news.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentSearchParamsBinding
import com.thekorovay.myportfolio.search_news.domain_model.SearchRequest
import com.thekorovay.myportfolio.search_news.repository.NewsSharedPreferences

//import com.thekorovay.myportfolio.news.SearchParamsFragmentDirections

class SearchParamsFragment: Fragment() {
    private lateinit var binding: FragmentSearchParamsBinding
    private lateinit var prefs: NewsSharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_params,
            container,
            false
        )

        setupSpinner()

        binding.btnSearch.setOnClickListener { search() }
        binding.btnShowLastSearch.setOnClickListener { showLastSearchResults() }

        binding.isLastSearchButtonActive = prefs.lastSearchQuery != null

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = NewsSharedPreferences(requireContext())
    }

    private fun setupSpinner() {
        val arrAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.results_per_page_values,
            R.layout.spinner_item
        ).apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
        }

        binding.spinnerPageSize.apply {
            adapter = arrAdapter
        }
    }

    private fun search() {
        hideKeyboardIfShown()

        val query = binding.etQuery.text.toString().trim()
        if (query.isEmpty()) {
            showEmptyQueryWarning()
            return
        }

        val safeSearch = binding.switchSafeSearch.isChecked
        val thumbnails = binding.switchThumbnails.isChecked
        val pageSize = binding.spinnerPageSize.selectedItem.toString().toInt()

        findNavController().navigate(
            SearchParamsFragmentDirections.actionSearchParamsFragmentToSearchResultsFragment(
                searchRequest = SearchRequest(query, safeSearch, thumbnails, pageSize),
                lastSearchQuery = null
            )
        )
    }

    private fun hideKeyboardIfShown() {
        activity?.run {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            currentFocus?.let { focusedView ->
                imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
            }
        }
    }

    private fun showLastSearchResults() {
        hideKeyboardIfShown()

        findNavController().navigate(
            SearchParamsFragmentDirections.actionSearchParamsFragmentToSearchResultsFragment(
                searchRequest = SearchRequest.getEmpty(),
                lastSearchQuery = prefs.lastSearchQuery
            )
        )
    }

    private fun showEmptyQueryWarning() {
        Snackbar.make(binding.root, R.string.snack_enter_query, Snackbar.LENGTH_SHORT).show()
    }
}