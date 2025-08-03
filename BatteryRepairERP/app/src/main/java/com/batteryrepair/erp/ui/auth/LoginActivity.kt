package com.batteryrepair.erp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.batteryrepair.erp.BatteryRepairApplication
import com.batteryrepair.erp.R
import com.batteryrepair.erp.data.repository.AuthRepository
import com.batteryrepair.erp.databinding.ActivityLoginBinding
import com.batteryrepair.erp.ui.main.MainActivity
import com.batteryrepair.erp.ui.setup.SetupActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var app: BatteryRepairApplication
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        app = application as BatteryRepairApplication
        authRepository = AuthRepository(this, app.preferenceManager, app.firebaseConfig)
        
        setupUI()
        setupClickListeners()
    }
    
    private fun setupUI() {
        // Pre-fill admin credentials for demo purposes
        binding.etUsername.setText("admin")
        binding.etPassword.setText("admin123")
    }
    
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            performLogin()
        }
        
        binding.tvForgotPassword.setOnClickListener {
            // Handle forgot password - for now just show message
            Toast.makeText(this, "Contact system administrator", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun performLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        if (validateInput(username, password)) {
            showLoading(true)
            
            lifecycleScope.launch {
                try {
                    val result = authRepository.login(username, password)
                    
                    result.onSuccess { user ->
                        showLoading(false)
                        Toast.makeText(
                            this@LoginActivity,
                            "Welcome, ${user.fullName}!",
                            Toast.LENGTH_SHORT
                        ).show()
                        
                        // Navigate based on user and app state
                        val nextActivity = if (user.id == BatteryRepairApplication.ADMIN_USER_ID && 
                                              !app.firebaseConfig.isFirebaseConfigured(this@LoginActivity)) {
                            SetupActivity::class.java
                        } else {
                            MainActivity::class.java
                        }
                        
                        startActivity(Intent(this@LoginActivity, nextActivity))
                        finish()
                    }
                    
                    result.onFailure { error ->
                        showLoading(false)
                        showError(error.message ?: "Login failed")
                    }
                } catch (e: Exception) {
                    showLoading(false)
                    showError("An unexpected error occurred")
                }
            }
        }
    }
    
    private fun validateInput(username: String, password: String): Boolean {
        var isValid = true
        
        if (username.isEmpty()) {
            binding.tilUsername.error = "Username is required"
            isValid = false
        } else {
            binding.tilUsername.error = null
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 3) {
            binding.tilPassword.error = "Password must be at least 3 characters"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }
        
        return isValid
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressLogin.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !show
        binding.etUsername.isEnabled = !show
        binding.etPassword.isEnabled = !show
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        
        // Clear password field on error
        binding.etPassword.text?.clear()
        binding.etPassword.requestFocus()
    }
    
    override fun onBackPressed() {
        // Disable back button on login screen
        // User must login to proceed
    }
}