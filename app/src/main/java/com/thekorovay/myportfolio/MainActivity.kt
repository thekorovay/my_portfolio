package com.thekorovay.myportfolio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.thekorovay.myportfolio.databinding.ActivityMainBinding

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
            .findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomBar.run {
            setupWithNavController(navController)

            // Prevent navigation to top-level destination from itself
            setOnNavigationItemReselectedListener { selected ->
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

            // Save backstack of the search news and login flow
            setOnNavigationItemSelectedListener { menuItem ->
                var poppedBackStack = false

                if (menuItem.itemId == R.id.searchParamsFragment) {
                    if (!poppedBackStack) {
                        poppedBackStack = navController.popBackStack(R.id.readArticleFragment, false)
                    }

                    if (!poppedBackStack) {
                        poppedBackStack = navController.popBackStack(R.id.searchResultsFragment, false)
                    }
                }

                // Todo: save backstack of login flow too

                val navOptions = NavOptions.Builder()
                    .setPopUpTo(menuItem.itemId, false)
                    .setLaunchSingleTop(true)
                    .build()

                if (!poppedBackStack) {
                    navController.navigate(menuItem.itemId, null, navOptions)
                }

                // Select item in BottomNavView
                true
            }
        }

        // IF THERE'S SINGLE TOOLBAR IN ACTIVITY LAYOUT
        /*// Allow fragments to set own Toolbar menu items
        setSupportActionBar(binding.mainToolbar)
        // Force Toolbar expanding in all destinations
        navController.addOnDestinationChangedListener { _, _, _ ->
            binding.appBarLayout.setExpanded(true, true)
        } */
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
}