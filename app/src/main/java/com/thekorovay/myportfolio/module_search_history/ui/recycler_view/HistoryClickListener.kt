package com.thekorovay.myportfolio.module_search_history.ui.recycler_view

import android.view.View
import com.thekorovay.myportfolio.database.DatabaseSearchRequest


class HistoryClickListener(val listener: (request: DatabaseSearchRequest, sharedView: View) -> Unit) {
    fun onClick(request: DatabaseSearchRequest, sharedView: View) = listener(request, sharedView)
}