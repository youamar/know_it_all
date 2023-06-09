package com.example.knowitall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.knowitall.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private var loginFragmentVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        val navController = this.findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
        val navView = findViewById<NavigationView>(R.id.navView)
        val playMenuItem = navView.menu.findItem(R.id.playFragment)
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("isLoggedIn")
            ?.observe(this) { isLoggedIn ->
                playMenuItem.isEnabled = isLoggedIn
            }

        val actionBar = this.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val navController = findNavController(R.id.myNavHostFragment)
            if (navController.currentDestination?.id == R.id.playFragment) {
                // Simulate back button press when in PlayFragment to go back to LoginFragment's position
                navController.popBackStack(R.id.loginFragment, false)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Method to hide the login fragment
    fun hideLoginFragment() {
        if (loginFragmentVisible) {
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment)?.let { fragment ->
                supportFragmentManager.beginTransaction().hide(fragment).commit()
                loginFragmentVisible = false
            }
        }
    }

    // Method to show the login fragment and reestablish the drawer layout
    fun showLoginFragment() {
        if (!loginFragmentVisible) {
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment)?.let { fragment ->
                supportFragmentManager.beginTransaction().show(fragment).commit()
                loginFragmentVisible = true
            }
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    fun hideDrawerLayout() {
        val actionBar = this.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
    }
}