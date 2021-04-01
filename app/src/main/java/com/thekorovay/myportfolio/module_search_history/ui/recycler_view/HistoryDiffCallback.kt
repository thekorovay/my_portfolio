package com.thekorovay.myportfolio.module_search_history.ui.recycler_view

import androidx.recyclerview.widget.DiffUtil
import com.thekorovay.myportfolio.database.DatabaseSearchRequest

object HistoryDiffCallback: DiffUtil.ItemCallback<DatabaseSearchRequest>() {
    override fun areItemsTheSame(oldItem: DatabaseSearchRequest, newItem: DatabaseSearchRequest): Boolean {
        return oldItem.dateTime == newItem.dateTime
    }

    override fun areContentsTheSame(oldItem: DatabaseSearchRequest, newItem: DatabaseSearchRequest): Boolean {
        return oldItem == newItem
    }
}