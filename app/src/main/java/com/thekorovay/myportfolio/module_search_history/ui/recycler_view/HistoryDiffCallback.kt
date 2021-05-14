package com.thekorovay.myportfolio.module_search_history.ui.recycler_view

import androidx.recyclerview.widget.DiffUtil
import com.thekorovay.myportfolio.entities.UISearchRequest

object HistoryDiffCallback: DiffUtil.ItemCallback<UISearchRequest>() {
    override fun areItemsTheSame(oldItem: UISearchRequest, newItem: UISearchRequest): Boolean {
        return oldItem.dateTime == newItem.dateTime
    }

    override fun areContentsTheSame(oldItem: UISearchRequest, newItem: UISearchRequest): Boolean {
        return oldItem == newItem
    }
}