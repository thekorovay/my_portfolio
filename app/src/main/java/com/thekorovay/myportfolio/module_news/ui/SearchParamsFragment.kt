package com.thekorovay.myportfolio.module_news.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.thekorovay.myportfolio.MyApplication
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentSearchParamsBinding
import com.thekorovay.myportfolio.entities.UISearchRequest
import com.thekorovay.myportfolio.module_news.viewmodels.SearchParamsViewModel
import com.thekorovay.myportfolio.tools.setPageSize
import com.thekorovay.myportfolio.tools.setupNavMenu
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class SearchParamsFragment: Fragment() {

    private lateinit var binding: FragmentSearchParamsBinding
    @Inject lateinit var viewModel: SearchParamsViewModel

    private val args: SearchParamsFragmentArgs by navArgs()

    private var lastRequest: UISearchRequest? = null

    override fun onAttach(context: Context) {
        (context.applicationContext as MyApplication).appComponent.inject(this)
        super.onAttach(context)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

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

        binding.toolbar.setupNavMenu(findNavController(), R.menu.about_app_menu)

        setupSpinner()

        binding.btnSearch.setOnClickListener { search() }
        binding.btnShowLastSearch.setOnClickListener { showLastSearchResults() }

        lifecycleScope.launchWhenStarted {
            viewModel.lastRequest.collect { request ->
                binding.isLastRequestAvailable = request != null
                this@SearchParamsFragment.lastRequest = request
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.lastResults.collect { cachedArticles ->
                binding.isLastResultsAvailable = cachedArticles.isNotEmpty()
            }
        }

        viewModel.invalidQueryFlag.observe(viewLifecycleOwner) { isInvalidQuery ->
            if (isInvalidQuery) {
                showEmptyQueryWarning()
                viewModel.setInvalidQueryWarningShown()
            }
        }

        viewModel.newSearchRequest.observe(viewLifecycleOwner) { request ->
            navigateToSearchResults(request, false)
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
        navigateToSearchResults(lastRequest, true)
    }

    private fun showEmptyQueryWarning() {
        Snackbar.make(binding.root, R.string.snack_enter_query, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun navigateToSearchResults(request: UISearchRequest?, showingLastSearchResults: Boolean) {
        request?.let {
            findNavController().navigate(
                SearchParamsFragmentDirections.actionSearchParamsFragmentToSearchResultsFragment(
                    searchRequest = it,
                    showingLastSearchResults = showingLastSearchResults
                )
            )

            viewModel.setNavigationToResultsCompleted()
        }
    }
}