package com.thekorovay.myportfolio.data.entities

import androidx.annotation.Keep

@Keep
data class NetworkArticle(
    val id: String,

    val title: String,
    val description: String,
    val body: String,

    val datePublished: String,

    val url: String,
    val provider: Provider,

    val image: Image
)