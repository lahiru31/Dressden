package com.dressden.app.ui.activities

import android.location.Location
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dressden.app.R
import com.dressden.app.databinding.ActivityMainBinding
import com.dressden.app.ui.activities.base.BaseActivity
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeComponents()
        setupNavigation()
        setupBottomNavigation()
    }

    private fun initializeComponents() {
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize Maps
        MapsInitializer.initialize(applicationContext)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_categories,
                R.id.navigation_cart,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setupWithNavController(navController)
        
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home,
                R.id.navigation_categories,
                R.id.navigation_cart,
                R.id.navigation_profile -> {
                    binding.bottomNav.visibility = android.view.View.VISIBLE
                }
                else -> binding.bottomNav.visibility = android.view.View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun handleLocationUpdate(location: Location) {
        // Update relevant UI components with new location
        // For example, update map position or nearby store list
    }

    override fun handleSensorUpdate(sensorName: String, values: FloatArray) {
        when (sensorName) {
            "Accelerometer" -> {
                // Handle accelerometer updates
                // For example, detect device orientation changes
            }
            "Gyroscope" -> {
                // Handle gyroscope updates
                // For example, detect rotation gestures
            }
            "Proximity" -> {
                // Handle proximity updates
                // For example, detect when device is near face
            }
        }
    }

    override fun onAllPermissionsGranted() {
        super.onAllPermissionsGranted()
        // Additional initialization that requires permissions
        // For example, start location-based features
    }

    override fun onSomePermissionsDenied() {
        super.onSomePermissionsDenied()
        // Handle limited functionality mode
        // For example, disable location-based features
    }
}
