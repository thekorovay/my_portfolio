package com.thekorovay.myportfolio.module_search_history.ui.recycler_view

import com.thekorovay.myportfolio.database.DatabaseSearchRequest


class HistoryClickListener(val listener: (request: DatabaseSearchRequest) -> Unit) {
    fun onClick(request: DatabaseSearchRequest) = listener(request)
}