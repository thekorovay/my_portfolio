package com.thekorovay.myportfolio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Use supportFragmentManager.findFragmentById() instead of
        // findNavController() to find a navController
        // while using androidx.fragment.app.FragmentContainerView in layout
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomBar.setupWithNavController(navController)

        val topLevelDests = setOf(R.id.placeholder, R.id.searchParamsFragment, R.id.searchHistoryFragment)
        val config = AppBarConfiguration.Builder(topLevelDests)
            .build()
        NavigationUI.setupActionBarWithNavController(this, navController, config)

        // Prevent navigation to top-level destination from itself
        binding.bottomBar.setOnNavigationItemReselectedListener { selected ->
            navController.currentDestination?.let { current ->
                if (selected.itemId != current.id) {
                    val options = NavOptions.Builder()
                        .setPopUpTo(selected.itemId, false)
                        .setLaunchSingleTop(true)
                        .build()

                    navController.navigate(selected.itemId, null, options)
                }
            }
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
}