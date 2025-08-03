package com.batteryrepair.erp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Customer(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pinCode: String = "",
    val alternatePhoneNumber: String = "",
    val gstNumber: String = "",
    val customerType: CustomerType = CustomerType.INDIVIDUAL,
    val batteryHistory: List<String> = emptyList(), // Battery IDs
    val totalRepairs: Int = 0,
    val lastVisit: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val notes: String = "",
    val isActive: Boolean = true,
    val loyaltyPoints: Int = 0,
    val discountPercentage: Double = 0.0
) : Parcelable

enum class CustomerType(val displayName: String) {
    INDIVIDUAL("Individual"),
    BUSINESS("Business"),
    DEALER("Dealer"),
    WHOLESALE("Wholesale")
}