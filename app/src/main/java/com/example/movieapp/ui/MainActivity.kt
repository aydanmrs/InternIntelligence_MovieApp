package com.example.movieapp.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Find NavHostFragment and set up NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // Set up BottomNavigationView with NavController
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        // Listen for destination changes to show/hide BottomNavigationView
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.homeFragment || destination.id == R.id.searchFragment2 ||
                destination.id == R.id.savedFragment || destination.id == R.id.downloadsFragment2 ||
                destination.id == R.id.profileFragment
            ) {
                showBottomNavigationView()
            } else {
                hideBottomNavigationView()
            }
        }

        // Handle BottomNavigationView item selection
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    navController.popBackStack(R.id.homeFragment, false)
                    true
                }

                R.id.searchFragment2 -> {
                    navController.popBackStack(R.id.searchFragment2, false)
                    navController.navigate(R.id.searchFragment2)
                    true
                }

                R.id.savedFragment -> {
                    navController.popBackStack(R.id.savedFragment, false)
                    navController.navigate(R.id.savedFragment)
                    true
                }

                R.id.downloadsFragment2 -> {
                    navController.popBackStack(R.id.downloadsFragment2, false)
                    navController.navigate(R.id.downloadsFragment2)
                    true
                }

                R.id.profileFragment -> {
                    navController.popBackStack(R.id.profileFragment, false)
                    navController.navigate(R.id.profileFragment)
                    true
                }

                else -> false
            }
        }
    }

    // Hide BottomNavigationView
    private fun hideBottomNavigationView() {
        binding.bottomNav.visibility = View.GONE
    }

    // Show BottomNavigationView
    private fun showBottomNavigationView() {
        binding.bottomNav.visibility = View.VISIBLE
    }
}