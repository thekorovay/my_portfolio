package com.thekorovay.myportfolio.module_search_history.ui.recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.thekorovay.myportfolio.entities.UISearchRequest

class HistoryRecyclerViewAdapter(
    private val historyClickListener: HistoryClickListener
) : ListAdapter<UISearchRequest, HistoryViewHolder>(HistoryDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position), historyClickListener)
    }
}