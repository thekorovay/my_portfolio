package com.thekorovay.myportfolio.tools

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.formatAsDateTime(pattern: String): String {
    val localDateTime = LocalDateTime.parse(this)
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return localDateTime.format(formatter)
}