package com.thekorovay.myportfolio.module_search_history.ui.recycler_view

import com.thekorovay.myportfolio.domain_model.SearchRequest

class HistoryClickListener(val listener: (request: SearchRequest) -> Unit) {
    fun onClick(request: SearchRequest) = listener(request)
}