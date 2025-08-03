package com.batteryrepair.erp.ui.setup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.batteryrepair.erp.BatteryRepairApplication
import com.batteryrepair.erp.databinding.ActivitySetupBinding
import com.batteryrepair.erp.ui.main.MainActivity
import kotlinx.coroutines.launch

class SetupActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySetupBinding
    private lateinit var app: BatteryRepairApplication
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        app = application as BatteryRepairApplication
        
        setupClickListeners()
        loadExistingConfig()
    }
    
    private fun setupClickListeners() {
        binding.btnTestConnection.setOnClickListener {
            testFirebaseConnection()
        }
        
        binding.btnSaveConfig.setOnClickListener {
            saveConfiguration()
        }
        
        binding.btnSkipSetup.setOnClickListener {
            skipSetup()
        }
    }
    
    private fun loadExistingConfig() {
        // Load existing Firebase configuration if available
        val config = app.firebaseConfig.getFirebaseConfig(this)
        config?.let {
            binding.etFirebaseUrl.setText(it.databaseUrl)
            binding.etApiKey.setText(it.apiKey)
            binding.etProjectId.setText(it.projectId)
        }
        
        // Load shop name
        val shopName = app.firebaseConfig.getShopName(this)
        binding.etShopName.setText(shopName)
    }
    
    private fun testFirebaseConnection() {
        val databaseUrl = binding.etFirebaseUrl.text.toString().trim()
        val apiKey = binding.etApiKey.text.toString().trim()
        val projectId = binding.etProjectId.text.toString().trim()
        
        if (validateInputs(databaseUrl, apiKey, projectId)) {
            showLoading(true)
            
            lifecycleScope.launch {
                try {
                    // Try to configure Firebase temporarily
                    val result = app.firebaseConfig.configureFirebase(
                        this@SetupActivity,
                        databaseUrl,
                        apiKey,
                        projectId
                    )
                    
                    result.onSuccess {
                        // Test the connection
                        val testResult = app.firebaseConfig.testConnection(this@SetupActivity)
                        testResult.onSuccess {
                            showLoading(false)
                            Toast.makeText(
                                this@SetupActivity,
                                "✅ Connection successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }.onFailure { error ->
                            showLoading(false)
                            Toast.makeText(
                                this@SetupActivity,
                                "❌ Connection failed: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }.onFailure { error ->
                        showLoading(false)
                        Toast.makeText(
                            this@SetupActivity,
                            "❌ Configuration failed: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    showLoading(false)
                    Toast.makeText(
                        this@SetupActivity,
                        "❌ Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    
    private fun saveConfiguration() {
        val databaseUrl = binding.etFirebaseUrl.text.toString().trim()
        val apiKey = binding.etApiKey.text.toString().trim()
        val projectId = binding.etProjectId.text.toString().trim()
        val shopName = binding.etShopName.text.toString().trim()
        
        if (validateInputs(databaseUrl, apiKey, projectId) && shopName.isNotEmpty()) {
            showLoading(true)
            
            lifecycleScope.launch {
                try {
                    // Configure Firebase
                    val result = app.firebaseConfig.configureFirebase(
                        this@SetupActivity,
                        databaseUrl,
                        apiKey,
                        projectId
                    )
                    
                    result.onSuccess {
                        // Save shop configuration
                        app.firebaseConfig.setShopName(this@SetupActivity, shopName)
                        
                        // Mark first launch as completed
                        app.preferenceManager.setFirstLaunchCompleted()
                        
                        showLoading(false)
                        Toast.makeText(
                            this@SetupActivity,
                            "✅ Configuration saved successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        
                        // Navigate to main activity
                        navigateToMain()
                    }.onFailure { error ->
                        showLoading(false)
                        Toast.makeText(
                            this@SetupActivity,
                            "❌ Failed to save configuration: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    showLoading(false)
                    Toast.makeText(
                        this@SetupActivity,
                        "❌ Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun skipSetup() {
        // Save shop name even if skipping Firebase setup
        val shopName = binding.etShopName.text.toString().trim()
        if (shopName.isNotEmpty()) {
            app.firebaseConfig.setShopName(this, shopName)
        }
        
        // Mark first launch as completed
        app.preferenceManager.setFirstLaunchCompleted()
        
        Toast.makeText(
            this,
            "✅ Setup skipped. You can configure Firebase later from Settings.",
            Toast.LENGTH_LONG
        ).show()
        
        navigateToMain()
    }
    
    private fun validateInputs(databaseUrl: String, apiKey: String, projectId: String): Boolean {
        var isValid = true
        
        if (databaseUrl.isEmpty()) {
            binding.tilFirebaseUrl.error = "Firebase URL is required"
            isValid = false
        } else if (!databaseUrl.contains("firebaseio.com") && !databaseUrl.contains("asia-southeast1.firebasedatabase.app")) {
            binding.tilFirebaseUrl.error = "Invalid Firebase URL format"
            isValid = false
        } else {
            binding.tilFirebaseUrl.error = null
        }
        
        if (apiKey.isEmpty()) {
            binding.tilApiKey.error = "API Key is required"
            isValid = false
        } else if (apiKey.length < 20) {
            binding.tilApiKey.error = "API Key seems too short"
            isValid = false
        } else {
            binding.tilApiKey.error = null
        }
        
        if (projectId.isEmpty()) {
            binding.tilProjectId.error = "Project ID is required"
            isValid = false
        } else {
            binding.tilProjectId.error = null
        }
        
        if (binding.etShopName.text.toString().trim().isEmpty()) {
            binding.tilShopName.error = "Shop name is required"
            isValid = false
        } else {
            binding.tilShopName.error = null
        }
        
        return isValid
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressSetup.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnTestConnection.isEnabled = !show
        binding.btnSaveConfig.isEnabled = !show
        binding.btnSkipSetup.isEnabled = !show
        
        // Disable input fields during loading
        binding.etFirebaseUrl.isEnabled = !show
        binding.etApiKey.isEnabled = !show
        binding.etProjectId.isEnabled = !show
        binding.etShopName.isEnabled = !show
    }
    
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    
    override fun onBackPressed() {
        // Disable back button during setup unless Firebase is already configured
        if (app.firebaseConfig.isFirebaseConfigured(this)) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Please complete setup or skip to continue", Toast.LENGTH_SHORT).show()
        }
    }
}