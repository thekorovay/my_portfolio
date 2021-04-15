package com.thekorovay.myportfolio.module_search_history.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentSearchHistoryBinding
import com.thekorovay.myportfolio.domain_model.SearchRequest
import com.thekorovay.myportfolio.module_search_history.ui.recycler_view.HistoryClickListener
import com.thekorovay.myportfolio.module_search_history.ui.recycler_view.HistoryRecyclerViewAdapter
import com.thekorovay.myportfolio.module_search_history.viewmodel.SearchHistoryViewModel
import com.thekorovay.myportfolio.network.EasyFirebase

class SearchHistoryFragment: Fragment() {
    private lateinit var binding: FragmentSearchHistoryBinding
    private val viewModel: SearchHistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_history,
            container,
            false
        )

        val historyAdapter = HistoryRecyclerViewAdapter(
            HistoryClickListener { request -> startSearchRequest(request) }
        )
        binding.rvSearchHistory.adapter = historyAdapter

        viewModel.searchHistory.observe(viewLifecycleOwner) { historyEntries ->
            historyAdapter.submitList(historyEntries)
            binding.isHistoryEmpty = historyEntries.isEmpty()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.isLoading = state == EasyFirebase.State.BUSY

            if (state == EasyFirebase.State.ERROR) {
                showErrorMessage(viewModel.exception)
                viewModel.setErrorMessageDisplayed()
            }
        }

        binding.btnClearHistory.setOnClickListener { viewModel.clearHistory() }

        return binding.root
    }

    private fun showErrorMessage(exception: Exception?) {
        val message = exception?.localizedMessage ?: getString(R.string.unknown_error)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.hide) { /* Just hide the snackbar */ }
                .show()
    }

    private fun startSearchRequest(request: SearchRequest) {
        findNavController().navigate(
            SearchHistoryFragmentDirections.actionSearchHistoryFragmentToSearchParamsFragment(request)
        )
    }
}