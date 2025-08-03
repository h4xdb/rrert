package com.batteryrepair.erp.data.firebase

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FirebaseConfig private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: FirebaseConfig? = null
        
        fun getInstance(): FirebaseConfig {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirebaseConfig().also { INSTANCE = it }
            }
        }
        
        private const val PREFS_NAME = "firebase_config"
        private const val KEY_FIREBASE_URL = "firebase_url"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_PROJECT_ID = "project_id"
        private const val KEY_IS_CONFIGURED = "is_configured"
        private const val KEY_SHOP_NAME = "shop_name"
        private const val KEY_SHOP_LOGO_URL = "shop_logo_url"
    }
    
    private var isConfigured = false
    private var firebaseDatabase: FirebaseDatabase? = null
    private var firebaseAuth: FirebaseAuth? = null
    
    fun isFirebaseConfigured(context: Context): Boolean {
        val prefs = getPrefs(context)
        isConfigured = prefs.getBoolean(KEY_IS_CONFIGURED, false)
        return isConfigured
    }
    
    fun configureFirebase(
        context: Context,
        databaseUrl: String,
        apiKey: String,
        projectId: String
    ): Result<Unit> {
        return try {
            // Save configuration to preferences
            saveConfiguration(context, databaseUrl, apiKey, projectId)
            
            // Initialize Firebase with custom configuration
            val options = FirebaseOptions.Builder()
                .setDatabaseUrl(databaseUrl)
                .setApiKey(apiKey)
                .setProjectId(projectId)
                .setApplicationId("1:000000000000:android:0000000000000000000000") // Placeholder
                .build()
            
            // Initialize Firebase app if not already initialized
            val app = try {
                FirebaseApp.getInstance("BatteryRepairERP")
            } catch (e: IllegalStateException) {
                FirebaseApp.initializeApp(context, options, "BatteryRepairERP")
            }
            
            // Initialize Firebase services
            firebaseDatabase = FirebaseDatabase.getInstance(app)
            firebaseAuth = FirebaseAuth.getInstance(app)
            
            // Enable offline persistence
            firebaseDatabase?.setPersistenceEnabled(true)
            
            isConfigured = true
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun testConnection(context: Context): Result<Unit> {
        return try {
            if (!isFirebaseConfigured(context)) {
                return Result.failure(Exception("Firebase not configured"))
            }
            
            // Simple test to check if we can access the database
            firebaseDatabase?.reference?.child("test")?.setValue("connection_test")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getDatabase(): FirebaseDatabase? = firebaseDatabase
    
    fun getAuth(): FirebaseAuth? = firebaseAuth
    
    fun getDatabaseReference(path: String) = firebaseDatabase?.reference?.child(path)
    
    fun getShopName(context: Context): String {
        return getPrefs(context).getString(KEY_SHOP_NAME, "Battery Repair Shop") ?: "Battery Repair Shop"
    }
    
    fun setShopName(context: Context, shopName: String) {
        getPrefs(context).edit().putString(KEY_SHOP_NAME, shopName).apply()
    }
    
    fun getShopLogoUrl(context: Context): String {
        return getPrefs(context).getString(KEY_SHOP_LOGO_URL, "") ?: ""
    }
    
    fun setShopLogoUrl(context: Context, logoUrl: String) {
        getPrefs(context).edit().putString(KEY_SHOP_LOGO_URL, logoUrl).apply()
    }
    
    private fun saveConfiguration(
        context: Context,
        databaseUrl: String,
        apiKey: String,
        projectId: String
    ) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putString(KEY_FIREBASE_URL, databaseUrl)
            .putString(KEY_API_KEY, apiKey)
            .putString(KEY_PROJECT_ID, projectId)
            .putBoolean(KEY_IS_CONFIGURED, true)
            .apply()
    }
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun getFirebaseConfig(context: Context): FirebaseConfigData? {
        val prefs = getPrefs(context)
        if (!prefs.getBoolean(KEY_IS_CONFIGURED, false)) {
            return null
        }
        
        return FirebaseConfigData(
            databaseUrl = prefs.getString(KEY_FIREBASE_URL, "") ?: "",
            apiKey = prefs.getString(KEY_API_KEY, "") ?: "",
            projectId = prefs.getString(KEY_PROJECT_ID, "") ?: ""
        )
    }
    
    fun clearConfiguration(context: Context) {
        getPrefs(context).edit().clear().apply()
        isConfigured = false
        firebaseDatabase = null
        firebaseAuth = null
    }
}

data class FirebaseConfigData(
    val databaseUrl: String,
    val apiKey: String,
    val projectId: String
)