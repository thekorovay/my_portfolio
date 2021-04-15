package com.thekorovay.myportfolio.module_search_history.ui.recycler_view

import androidx.recyclerview.widget.DiffUtil
import com.thekorovay.myportfolio.domain_model.SearchRequest

object HistoryDiffCallback: DiffUtil.ItemCallback<SearchRequest>() {
    override fun areItemsTheSame(oldItem: SearchRequest, newItem: SearchRequest): Boolean {
        return oldItem.dateTime == newItem.dateTime
    }

    override fun areContentsTheSame(oldItem: SearchRequest, newItem: SearchRequest): Boolean {
        return oldItem == newItem
    }
}