package com.batteryrepair.erp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val fullName: String = "",
    val role: UserRole = UserRole.STAFF,
    val phoneNumber: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val profileImageUrl: String = ""
) : Parcelable

enum class UserRole(val displayName: String) {
    ADMIN("Admin"),
    STAFF("Staff"),
    TECHNICIAN("Technician");
    
    fun hasPermission(permission: Permission): Boolean {
        return when (this) {
            ADMIN -> true // Admin has all permissions
            STAFF -> permission in staffPermissions
            TECHNICIAN -> permission in technicianPermissions
        }
    }
    
    companion object {
        private val staffPermissions = setOf(
            Permission.CREATE_BATTERY_ENTRY,
            Permission.VIEW_BATTERIES,
            Permission.ASSIGN_TECHNICIAN,
            Permission.CREATE_INVOICE,
            Permission.VIEW_CUSTOMERS,
            Permission.UPDATE_BATTERY_STATUS,
            Permission.GENERATE_QR_CODE,
            Permission.PRINT_RECEIPT
        )
        
        private val technicianPermissions = setOf(
            Permission.VIEW_ASSIGNED_BATTERIES,
            Permission.UPDATE_REPAIR_STATUS,
            Permission.ADD_REPAIR_NOTES,
            Permission.UPDATE_BATTERY_VALUES
        )
    }
}

enum class Permission {
    // Battery Management
    CREATE_BATTERY_ENTRY,
    VIEW_BATTERIES,
    VIEW_ASSIGNED_BATTERIES,
    UPDATE_BATTERY_STATUS,
    DELETE_BATTERY,
    ASSIGN_TECHNICIAN,
    
    // Customer Management
    VIEW_CUSTOMERS,
    CREATE_CUSTOMER,
    UPDATE_CUSTOMER,
    DELETE_CUSTOMER,
    
    // User Management
    VIEW_USERS,
    CREATE_USER,
    UPDATE_USER,
    DELETE_USER,
    ASSIGN_ROLES,
    
    // Billing
    CREATE_INVOICE,
    VIEW_INVOICES,
    UPDATE_INVOICE,
    DELETE_INVOICE,
    
    // Repair Management
    UPDATE_REPAIR_STATUS,
    ADD_REPAIR_NOTES,
    UPDATE_BATTERY_VALUES,
    
    // QR Code & Printing
    GENERATE_QR_CODE,
    PRINT_RECEIPT,
    
    // Settings & Configuration
    CONFIGURE_FIREBASE,
    MANAGE_SETTINGS,
    BACKUP_DATA,
    RESTORE_DATA,
    
    // Reports
    VIEW_REPORTS,
    EXPORT_DATA
}