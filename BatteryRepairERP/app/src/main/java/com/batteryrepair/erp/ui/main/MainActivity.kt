package com.batteryrepair.erp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.batteryrepair.erp.BatteryRepairApplication
import com.batteryrepair.erp.R
import com.batteryrepair.erp.data.repository.AuthRepository
import com.batteryrepair.erp.databinding.ActivityMainBinding
import com.batteryrepair.erp.ui.auth.LoginActivity
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var app: BatteryRepairApplication
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        app = application as BatteryRepairApplication
        authRepository = AuthRepository(this, app.preferenceManager, app.firebaseConfig)
        
        setupToolbar()
        setupNavigationDrawer()
        setupBottomNavigation()
        setupFAB()
        updateNavigationHeader()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        
        // Set toolbar title based on current user role
        val currentUser = authRepository.getCurrentUser()
        val shopName = app.firebaseConfig.getShopName(this)
        supportActionBar?.title = shopName
        supportActionBar?.subtitle = "Welcome, ${currentUser?.fullName ?: "User"}"
    }
    
    private fun setupNavigationDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.app_name,
            R.string.app_name
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        
        binding.navView.setNavigationItemSelectedListener(this)
        
        // Filter navigation menu based on user role
        filterNavigationMenuByRole()
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    // TODO: Navigate to Dashboard
                    Toast.makeText(this, "Dashboard", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_batteries -> {
                    // TODO: Navigate to Batteries
                    Toast.makeText(this, "Batteries", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_customers -> {
                    // TODO: Navigate to Customers
                    Toast.makeText(this, "Customers", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_billing -> {
                    // TODO: Navigate to Billing
                    Toast.makeText(this, "Billing", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        
        // Set default selection
        binding.bottomNavigation.selectedItemId = R.id.nav_dashboard
    }
    
    private fun setupFAB() {
        binding.fabAdd.setOnClickListener {
            // TODO: Show add options based on current screen
            Toast.makeText(this, "Add new battery entry", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun updateNavigationHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val currentUser = authRepository.getCurrentUser()
        
        // TODO: Update header views with user information
        // val tvUserName = headerView.findViewById<TextView>(R.id.tv_user_name)
        // val tvUserRole = headerView.findViewById<TextView>(R.id.tv_user_role)
        // tvUserName.text = currentUser?.fullName ?: "User"
        // tvUserRole.text = currentUser?.role?.displayName ?: "Unknown"
    }
    
    private fun filterNavigationMenuByRole() {
        val currentUser = authRepository.getCurrentUser()
        val navigationMenu = binding.navView.menu
        
        // Hide user management for non-admin users
        if (currentUser?.role != com.batteryrepair.erp.data.model.UserRole.ADMIN) {
            navigationMenu.findItem(R.id.nav_users)?.isVisible = false
        }
        
        // Add more role-based filtering as needed
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                // TODO: Navigate to Dashboard Fragment
                Toast.makeText(this, "Dashboard", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_batteries -> {
                // TODO: Navigate to Batteries Fragment
                Toast.makeText(this, "Batteries", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_customers -> {
                // TODO: Navigate to Customers Fragment
                Toast.makeText(this, "Customers", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_billing -> {
                // TODO: Navigate to Billing Fragment
                Toast.makeText(this, "Billing", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_users -> {
                // TODO: Navigate to Users Fragment
                Toast.makeText(this, "User Management", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_reports -> {
                // TODO: Navigate to Reports Fragment
                Toast.makeText(this, "Reports", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_settings -> {
                // TODO: Navigate to Settings Fragment
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                performLogout()
            }
        }
        
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    
    private fun performLogout() {
        val result = authRepository.logout()
        result.onSuccess {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            
            // Navigate to login screen
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }.onFailure { error ->
            Toast.makeText(this, "Logout failed: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}