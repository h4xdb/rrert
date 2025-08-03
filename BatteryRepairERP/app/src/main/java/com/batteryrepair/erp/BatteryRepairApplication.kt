package com.batteryrepair.erp

import android.app.Application
import com.batteryrepair.erp.data.firebase.FirebaseConfig
import com.batteryrepair.erp.utils.PreferenceManager

class BatteryRepairApplication : Application() {
    
    lateinit var preferenceManager: PreferenceManager
        private set
    
    lateinit var firebaseConfig: FirebaseConfig
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize singletons
        preferenceManager = PreferenceManager(this)
        firebaseConfig = FirebaseConfig.getInstance()
        
        // Initialize Firebase if already configured
        if (firebaseConfig.isFirebaseConfigured(this)) {
            val config = firebaseConfig.getFirebaseConfig(this)
            config?.let {
                firebaseConfig.configureFirebase(
                    this,
                    it.databaseUrl,
                    it.apiKey,
                    it.projectId
                )
            }
        }
    }
    
    companion object {
        // Hardcoded admin credentials for offline first-time setup
        const val ADMIN_USERNAME = "admin"
        const val ADMIN_PASSWORD = "admin123"
        const val ADMIN_USER_ID = "offline_admin_001"
    }
}