package com.thekorovay.myportfolio.domain.entities

data class SearchRequest(
    val dateTime: String,
    val query: String,
    val safeSearchEnabled: Boolean,
    val thumbnailsEnabled: Boolean,
    val pageSize: Int
)