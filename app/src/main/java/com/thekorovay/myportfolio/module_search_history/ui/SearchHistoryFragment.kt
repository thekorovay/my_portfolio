package com.thekorovay.myportfolio.module_search_history.ui

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
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.database.DatabaseSearchRequest
import com.thekorovay.myportfolio.databinding.FragmentSearchHistoryBinding
import com.thekorovay.myportfolio.module_search_history.ui.recycler_view.HistoryClickListener
import com.thekorovay.myportfolio.module_search_history.ui.recycler_view.HistoryRecyclerViewAdapter
import com.thekorovay.myportfolio.module_search_history.viewmodel.SearchHistoryViewModel

class SearchHistoryFragment: Fragment() {
    private lateinit var binding: FragmentSearchHistoryBinding
    private val viewModel by lazy {
        ViewModelProvider(this, SearchHistoryViewModel.Factory(requireActivity().application))
            .get(SearchHistoryViewModel::class.java)
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
            HistoryClickListener { request, view -> startSearchRequest(request, view) }
        )
        binding.rvSearchHistory.adapter = historyAdapter

        viewModel.searchHistory.observe(viewLifecycleOwner) { historyEntries ->
            historyAdapter.submitList(historyEntries)
            binding.isNoHistoryMessageVisible = historyEntries.isEmpty()
        }

        binding.btnClearHistory.setOnClickListener { viewModel.clearHistory() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun startSearchRequest(request: DatabaseSearchRequest, sharedView: View) {
        val searchRequest = request.toSearchRequest()
        val extras = FragmentNavigatorExtras(sharedView to request.dateTime)

        findNavController().navigate(
            SearchHistoryFragmentDirections.actionSearchHistoryFragmentToSearchParamsFragment(searchRequest),
            extras
        )
    }
}