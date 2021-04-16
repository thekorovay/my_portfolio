package com.thekorovay.myportfolio.database

import android.content.Context

class EasyPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("default_prefs", Context.MODE_PRIVATE)

    var lastUserId: String?
        get() = prefs.getString("last_uid", null)
        set(value) = prefs.edit().putString("last_uid", value).apply()
}