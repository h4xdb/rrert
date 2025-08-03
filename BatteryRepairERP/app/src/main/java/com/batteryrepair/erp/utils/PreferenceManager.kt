package com.batteryrepair.erp.utils

import android.content.Context
import android.content.SharedPreferences
import com.batteryrepair.erp.data.model.User
import com.batteryrepair.erp.data.model.UserRole
import com.google.gson.Gson

class PreferenceManager(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "battery_repair_prefs"
        private const val KEY_USER_LOGGED_IN = "user_logged_in"
        private const val KEY_CURRENT_USER = "current_user"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
        private const val KEY_DARK_MODE_ENABLED = "dark_mode_enabled"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        private const val KEY_AUTO_BACKUP_ENABLED = "auto_backup_enabled"
        private const val KEY_LAST_BACKUP_TIME = "last_backup_time"
        private const val KEY_OFFLINE_MODE_ENABLED = "offline_mode_enabled"
        private const val KEY_QR_AUTO_PRINT = "qr_auto_print"
        private const val KEY_DEFAULT_TAX_RATE = "default_tax_rate"
        private const val KEY_INVOICE_PREFIX = "invoice_prefix"
        private const val KEY_NEXT_INVOICE_NUMBER = "next_invoice_number"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    // User Session Management
    fun setUserLoggedIn(user: User) {
        prefs.edit()
            .putBoolean(KEY_USER_LOGGED_IN, true)
            .putString(KEY_CURRENT_USER, gson.toJson(user))
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_USER_ROLE, user.role.name)
            .apply()
    }
    
    fun getCurrentUser(): User? {
        val userJson = prefs.getString(KEY_CURRENT_USER, null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                null
            }
        } else null
    }
    
    fun getCurrentUserId(): String? = prefs.getString(KEY_USER_ID, null)
    
    fun getCurrentUserRole(): UserRole? {
        val roleName = prefs.getString(KEY_USER_ROLE, null)
        return if (roleName != null) {
            try {
                UserRole.valueOf(roleName)
            } catch (e: Exception) {
                null
            }
        } else null
    }
    
    fun isUserLoggedIn(): Boolean = prefs.getBoolean(KEY_USER_LOGGED_IN, false)
    
    fun logout() {
        prefs.edit()
            .putBoolean(KEY_USER_LOGGED_IN, false)
            .remove(KEY_CURRENT_USER)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_ROLE)
            .apply()
    }
    
    // App Settings
    fun isFirstLaunch(): Boolean = prefs.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    
    fun setFirstLaunchCompleted() {
        prefs.edit().putBoolean(KEY_IS_FIRST_LAUNCH, false).apply()
    }
    
    fun isDarkModeEnabled(): Boolean = prefs.getBoolean(KEY_DARK_MODE_ENABLED, false)
    
    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE_ENABLED, enabled).apply()
    }
    
    fun isNotificationEnabled(): Boolean = prefs.getBoolean(KEY_NOTIFICATION_ENABLED, true)
    
    fun setNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply()
    }
    
    fun isAutoBackupEnabled(): Boolean = prefs.getBoolean(KEY_AUTO_BACKUP_ENABLED, true)
    
    fun setAutoBackupEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_BACKUP_ENABLED, enabled).apply()
    }
    
    fun getLastBackupTime(): Long = prefs.getLong(KEY_LAST_BACKUP_TIME, 0L)
    
    fun setLastBackupTime(time: Long) {
        prefs.edit().putLong(KEY_LAST_BACKUP_TIME, time).apply()
    }
    
    fun isOfflineModeEnabled(): Boolean = prefs.getBoolean(KEY_OFFLINE_MODE_ENABLED, false)
    
    fun setOfflineModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_OFFLINE_MODE_ENABLED, enabled).apply()
    }
    
    // QR and Printing Settings
    fun isQRAutoPrintEnabled(): Boolean = prefs.getBoolean(KEY_QR_AUTO_PRINT, false)
    
    fun setQRAutoPrintEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_QR_AUTO_PRINT, enabled).apply()
    }
    
    // Invoice Settings
    fun getDefaultTaxRate(): Double = prefs.getString(KEY_DEFAULT_TAX_RATE, "18.0")?.toDoubleOrNull() ?: 18.0
    
    fun setDefaultTaxRate(rate: Double) {
        prefs.edit().putString(KEY_DEFAULT_TAX_RATE, rate.toString()).apply()
    }
    
    fun getInvoicePrefix(): String = prefs.getString(KEY_INVOICE_PREFIX, "INV") ?: "INV"
    
    fun setInvoicePrefix(prefix: String) {
        prefs.edit().putString(KEY_INVOICE_PREFIX, prefix).apply()
    }
    
    fun getNextInvoiceNumber(): Int = prefs.getInt(KEY_NEXT_INVOICE_NUMBER, 1)
    
    fun incrementInvoiceNumber(): Int {
        val nextNumber = getNextInvoiceNumber()
        prefs.edit().putInt(KEY_NEXT_INVOICE_NUMBER, nextNumber + 1).apply()
        return nextNumber
    }
    
    fun setNextInvoiceNumber(number: Int) {
        prefs.edit().putInt(KEY_NEXT_INVOICE_NUMBER, number).apply()
    }
    
    // Clear all preferences
    fun clearAll() {
        prefs.edit().clear().apply()
    }
    
    // Backup and restore preferences
    fun exportPreferences(): Map<String, Any?> {
        return prefs.all
    }
    
    fun importPreferences(preferences: Map<String, Any?>) {
        val editor = prefs.edit()
        preferences.forEach { (key, value) ->
            when (value) {
                is String -> editor.putString(key, value)
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is Float -> editor.putFloat(key, value)
            }
        }
        editor.apply()
    }
}