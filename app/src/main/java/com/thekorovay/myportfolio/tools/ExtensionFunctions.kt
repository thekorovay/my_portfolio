package com.thekorovay.myportfolio.tools

import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.MaterialToolbar
import com.thekorovay.myportfolio.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.formatAsDateTime(pattern: String): String {
    val localDateTime = LocalDateTime.parse(this)
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return localDateTime.format(formatter)
}

fun MaterialToolbar.setupNavMenu(navController: NavController, menuResource: Int) {
    inflateMenu(menuResource)
    setOnMenuItemClickListener { item -> NavigationUI.onNavDestinationSelected(item, navController) }
}

fun MaterialToolbar.setupNavUpButton(navController: NavController) {
    setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
    setNavigationOnClickListener { navController.navigateUp() }
}