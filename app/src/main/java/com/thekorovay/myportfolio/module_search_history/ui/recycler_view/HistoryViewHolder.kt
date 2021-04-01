package com.thekorovay.myportfolio.module_search_history.ui.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thekorovay.myportfolio.database.DatabaseSearchRequest
import com.thekorovay.myportfolio.databinding.CardSearchHistoryEntryBinding

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
        request: DatabaseSearchRequest,
        historyClickListener: HistoryClickListener
    ) {
        binding.request = request
        binding.clickListener = historyClickListener
    }
}