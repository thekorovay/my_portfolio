package com.thekorovay.myportfolio.module_search_history.ui.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thekorovay.myportfolio.databinding.CardSearchHistoryEntryBinding
import com.thekorovay.myportfolio.domain_model.SearchRequest

class HistoryViewHolder private constructor(
    private val binding: CardSearchHistoryEntryBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): HistoryViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = CardSearchHistoryEntryBinding.inflate(inflater, parent, false)
            return HistoryViewHolder(binding)
        }
    }

    fun bind(
        request: SearchRequest,
        historyClickListener: HistoryClickListener
    ) {
        binding.request = request
        binding.clickListener = historyClickListener
    }
}