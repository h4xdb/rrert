package com.batteryrepair.erp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Battery(
    val id: String = "",
    val qrCode: String = "",
    val customerId: String = "",
    val customerInfo: Customer = Customer(),
    val batteryDetails: BatteryDetails = BatteryDetails(),
    val repairInfo: RepairInfo = RepairInfo(),
    val status: BatteryStatus = BatteryStatus.INWARD,
    val statusHistory: List<StatusUpdate> = emptyList(),
    val assignedTechnicianId: String = "",
    val assignedTechnicianName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val notes: String = "",
    val invoiceId: String = "",
    val isDelivered: Boolean = false,
    val deliveredAt: Long = 0L,
    val deliveredBy: String = ""
) : Parcelable

@Parcelize
data class BatteryDetails(
    val type: String = "",
    val brand: String = "",
    val model: String = "",
    val serialNumber: String = "",
    val voltageAtArrival: Double = 0.0,
    val voltageAfterRepair: Double = 0.0,
    val capacity: String = "",
    val complaint: String = "",
    val physicalCondition: String = "",
    val warrantyStatus: String = "",
    val purchaseDate: String = "",
    val images: List<String> = emptyList()
) : Parcelable

@Parcelize
data class RepairInfo(
    val diagnosis: String = "",
    val partsReplaced: List<PartUsed> = emptyList(),
    val repairNotes: String = "",
    val testResults: String = "",
    val repairStartTime: Long = 0L,
    val repairEndTime: Long = 0L,
    val estimatedCompletionTime: Long = 0L,
    val actualCompletionTime: Long = 0L,
    val repairSuccess: Boolean = false,
    val failureReason: String = "",
    val finalTestVoltage: Double = 0.0,
    val qualityCheckPassed: Boolean = false,
    val technicianSignature: String = ""
) : Parcelable

@Parcelize
data class PartUsed(
    val partName: String = "",
    val partNumber: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val supplier: String = "",
    val warrantyPeriod: String = ""
) : Parcelable

@Parcelize
data class StatusUpdate(
    val status: BatteryStatus = BatteryStatus.INWARD,
    val timestamp: Long = System.currentTimeMillis(),
    val updatedBy: String = "",
    val updatedByName: String = "",
    val notes: String = "",
    val location: String = ""
) : Parcelable

enum class BatteryStatus(val displayName: String, val colorRes: String) {
    INWARD("Inward", "status_pending"),
    ASSIGNED("Assigned", "status_in_progress"),
    IN_PROGRESS("In Progress", "status_in_progress"),
    COMPLETED("Completed", "status_completed"),
    QUALITY_CHECK("Quality Check", "status_in_progress"),
    READY_FOR_DELIVERY("Ready for Delivery", "status_completed"),
    DELIVERED("Delivered", "status_delivered"),
    CANCELLED("Cancelled", "status_cancelled"),
    ON_HOLD("On Hold", "status_pending"),
    REQUIRES_APPROVAL("Requires Approval", "status_pending");
    
    fun getNextPossibleStatuses(): List<BatteryStatus> {
        return when (this) {
            INWARD -> listOf(ASSIGNED, CANCELLED, ON_HOLD)
            ASSIGNED -> listOf(IN_PROGRESS, ON_HOLD, CANCELLED)
            IN_PROGRESS -> listOf(COMPLETED, REQUIRES_APPROVAL, ON_HOLD, CANCELLED)
            COMPLETED -> listOf(QUALITY_CHECK, READY_FOR_DELIVERY)
            QUALITY_CHECK -> listOf(READY_FOR_DELIVERY, IN_PROGRESS) // Back to repair if failed
            READY_FOR_DELIVERY -> listOf(DELIVERED)
            DELIVERED -> emptyList() // Final state
            CANCELLED -> emptyList() // Final state
            ON_HOLD -> listOf(ASSIGNED, IN_PROGRESS, CANCELLED)
            REQUIRES_APPROVAL -> listOf(IN_PROGRESS, CANCELLED)
        }
    }
    
    fun canBeModifiedBy(userRole: UserRole): Boolean {
        return when (this) {
            INWARD, ASSIGNED -> userRole in listOf(UserRole.ADMIN, UserRole.STAFF)
            IN_PROGRESS, COMPLETED -> userRole in listOf(UserRole.ADMIN, UserRole.STAFF, UserRole.TECHNICIAN)
            QUALITY_CHECK, READY_FOR_DELIVERY, DELIVERED -> userRole in listOf(UserRole.ADMIN, UserRole.STAFF)
            CANCELLED, ON_HOLD, REQUIRES_APPROVAL -> userRole in listOf(UserRole.ADMIN, UserRole.STAFF)
        }
    }
}