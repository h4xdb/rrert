package com.batteryrepair.erp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.batteryrepair.erp.BatteryRepairApplication
import com.batteryrepair.erp.R
import com.batteryrepair.erp.ui.auth.LoginActivity
import com.batteryrepair.erp.ui.main.MainActivity
import com.batteryrepair.erp.ui.setup.SetupActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    
    private lateinit var app: BatteryRepairApplication
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        app = application as BatteryRepairApplication
        
        lifecycleScope.launch {
            // Show splash for at least 2 seconds
            delay(2000)
            
            // Determine next screen based on app state
            val nextActivity = when {
                // First launch - show setup
                app.preferenceManager.isFirstLaunch() -> SetupActivity::class.java
                
                // User already logged in - go to main activity
                app.preferenceManager.isUserLoggedIn() -> MainActivity::class.java
                
                // Default - show login
                else -> LoginActivity::class.java
            }
            
            startActivity(Intent(this@SplashActivity, nextActivity))
            finish()
        }
    }
}