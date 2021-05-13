package com.thekorovay.myportfolio.module_search_history.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.thekorovay.myportfolio.MyApplication
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentSearchHistoryBinding
import com.thekorovay.myportfolio.domain_model.SearchRequest
import com.thekorovay.myportfolio.module_search_history.ui.recycler_view.HistoryClickListener
import com.thekorovay.myportfolio.module_search_history.ui.recycler_view.HistoryRecyclerViewAdapter
import com.thekorovay.myportfolio.module_search_history.viewmodel.SearchHistoryViewModel
import com.thekorovay.myportfolio.network.EasyFirebase
import java.lang.Exception
import javax.inject.Inject

class SearchHistoryFragment: Fragment() {
    private lateinit var binding: FragmentSearchHistoryBinding
    @Inject lateinit var viewModel: SearchHistoryViewModel

    override fun onAttach(context: Context) {
        (context.applicationContext as MyApplication).appComponent.inject(this)
        super.onAttach(context)
    }

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

        binding.btnClearHistory.setOnClickListener { confirmClearHistory() }

        return binding.root
    }

    private fun confirmClearHistory() {
        val dialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.clear_history_dialog_title)
                .setMessage(R.string.clear_history_dialog_message)
                .setPositiveButton(R.string.dialog_btn_clear) { _, _ -> viewModel.clearHistory() }
                .setNegativeButton(R.string.dialog_btn_cancel) { _, _ -> /* Do nothing */ }
                .create()

        dialog.show()
    }

    private fun showErrorMessage(exception: Exception?) {
        val message = exception?.localizedMessage ?: getString(R.string.unknown_error)

        val dialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.error_dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_btn_close) { _, _ -> }
                .create()

        dialog.show()
    }

    private fun startSearchRequest(request: SearchRequest) {
        findNavController().navigate(
            SearchHistoryFragmentDirections.actionSearchHistoryFragmentToSearchParamsFragment(request)
        )
    }
}