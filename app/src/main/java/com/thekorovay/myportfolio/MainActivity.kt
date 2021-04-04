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

        setupNavController()

        setupBottomBar()

        // IF THERE'S SINGLE TOOLBAR IN ACTIVITY LAYOUT
        // setupSingleToolbar()
    }

    private fun setupNavController() {
        // Use supportFragmentManager.findFragmentById() instead of
        // findNavController() to find a navController
        // while using androidx.fragment.app.FragmentContainerView in layout
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setupBottomBar() {
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
        }
    }

    /*private fun setupSingleToolbar() {
        // Allow fragments to set own Toolbar menu items
        setSupportActionBar(binding.mainToolbar)
        // Force Toolbar expanding in all destinations
        navController.addOnDestinationChangedListener { _, _, _ ->
            binding.appBarLayout.setExpanded(true, true)
        }
    }*/

    override fun onSupportNavigateUp() = navController.navigateUp()
}