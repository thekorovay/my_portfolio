package com.thekorovay.myportfolio.entities

import android.os.Parcelable
import com.thekorovay.myportfolio.domain.entities.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class UIUser (
    val name: String?
): Parcelable {
    companion object {
        fun fromUser(user: User?) = user?.let { UIUser(it.name) }
    }
}