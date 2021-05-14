package com.thekorovay.myportfolio.module_search_history.ui.recycler_view

import com.thekorovay.myportfolio.entities.UISearchRequest

class HistoryClickListener(val listener: (request: UISearchRequest) -> Unit) {
    fun onClick(request: UISearchRequest) = listener(request)
}