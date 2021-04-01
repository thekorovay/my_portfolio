package com.thekorovay.myportfolio.module_news.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentSearchParamsBinding
import com.thekorovay.myportfolio.domain_model.SearchRequest
import com.thekorovay.myportfolio.module_news.viewmodels.SearchParamsViewModel
import com.thekorovay.myportfolio.module_news.viewmodels.SearchViewModelsFactory
import com.thekorovay.myportfolio.tools.setPageSize

class SearchParamsFragment: Fragment() {

    private lateinit var binding: FragmentSearchParamsBinding

    private val args: SearchParamsFragmentArgs by navArgs()

    private val viewModel by lazy {
        ViewModelProvider(this, SearchViewModelsFactory(requireActivity().application))
            .get(SearchParamsViewModel::class.java)
    }

    private var lastRequest: SearchRequest? = null

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

        viewModel.lastRequest.observe(viewLifecycleOwner) { request ->
            lastRequest = request
            binding.isLastSearchButtonActive = request != null
        }

        viewModel.invalidQueryFlag.observe(viewLifecycleOwner) { isInvalidQuery ->
            if (isInvalidQuery) {
                showEmptyQueryWarning()
                viewModel.setInvalidQueryWarningShown()
            }
        }

        viewModel.newSearchRequest.observe(viewLifecycleOwner) { request ->
            request?.let {
                navigateToSearchResults(it)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val request = args.request

        binding.run {
            if (request != null && etQuery.text.isEmpty()) {
                etQuery.setText(request.query)
                switchSafeSearch.isChecked = request.safeSearchEnabled
                switchThumbnails.isChecked = request.thumbnailsEnabled
                spinnerPageSize.setPageSize(request.pageSize)
            }
        }
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

        val query = binding.etQuery.text.toString()
        val safeSearch = binding.switchSafeSearch.isChecked
        val thumbnails = binding.switchThumbnails.isChecked
        val pageSize = binding.spinnerPageSize.selectedItem.toString().toInt()

        viewModel.search(query, safeSearch, thumbnails, pageSize)
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

        lastRequest?.let { request ->
            findNavController().navigate(
                SearchParamsFragmentDirections.actionSearchParamsFragmentToSearchResultsFragment(
                    searchRequest = request,
                    showingLastSearchResults = true
                )
            )
        }
    }

    private fun showEmptyQueryWarning() {
        Snackbar.make(binding.root, R.string.snack_enter_query, Snackbar.LENGTH_SHORT).show()
    }

    private fun navigateToSearchResults(request: SearchRequest) {
        findNavController().navigate(
            SearchParamsFragmentDirections.actionSearchParamsFragmentToSearchResultsFragment(
                searchRequest = request,
                showingLastSearchResults = false
            )
        )

        viewModel.setNavigationToResultsCompleted()
    }
}