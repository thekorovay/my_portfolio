package com.thekorovay.myportfolio.database

import android.content.SharedPreferences
import javax.inject.Inject

class EasyPrefs @Inject constructor(private val prefs: SharedPreferences) {
    var lastUserId: String?
        get() = prefs.getString("last_uid", null)
        set(value) = prefs.edit().putString("last_uid", value).apply()
}