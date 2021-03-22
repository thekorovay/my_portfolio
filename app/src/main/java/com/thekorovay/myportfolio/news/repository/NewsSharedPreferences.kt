package com.thekorovay.myportfolio.news.repository

import android.content.Context

class NewsSharedPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("news_preferences", Context.MODE_PRIVATE)

    var lastSearchQuery: String?
        get() = prefs.getString("last_query", null)
        set(query) = prefs.edit().putString("last_query", query).apply()
}