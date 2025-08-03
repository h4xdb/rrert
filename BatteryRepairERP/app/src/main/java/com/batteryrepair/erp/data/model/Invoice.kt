package com.batteryrepair.erp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Invoice(
    val id: String = "",
    val invoiceNumber: String = "",
    val batteryId: String = "",
    val customerId: String = "",
    val customerInfo: Customer = Customer(),
    val batteryInfo: BatteryDetails = BatteryDetails(),
    val items: List<InvoiceItem> = emptyList(),
    val serviceCharges: Double = 0.0,
    val partsTotal: Double = 0.0,
    val subtotal: Double = 0.0,
    val taxRate: Double = 0.0,
    val taxAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val paymentDate: Long = 0L,
    val dueDate: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val notes: String = "",
    val customerSignature: String = "",
    val technicianSignature: String = "",
    val pdfPath: String = "",
    val isEmailSent: Boolean = false,
    val emailSentAt: Long = 0L,
    val warrantyPeriod: String = "",
    val warrantyStartDate: Long = 0L
) : Parcelable

@Parcelize
data class InvoiceItem(
    val id: String = "",
    val itemType: ItemType = ItemType.PART,
    val name: String = "",
    val description: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val taxRate: Double = 0.0,
    val taxAmount: Double = 0.0,
    val discountPercentage: Double = 0.0,
    val discountAmount: Double = 0.0,
    val finalAmount: Double = 0.0
) : Parcelable

enum class ItemType(val displayName: String) {
    PART("Part"),
    SERVICE("Service"),
    LABOR("Labor"),
    TESTING("Testing"),
    OTHER("Other")
}

enum class PaymentStatus(val displayName: String) {
    PENDING("Pending"),
    PAID("Paid"),
    PARTIALLY_PAID("Partially Paid"),
    OVERDUE("Overdue"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded")
}

enum class PaymentMethod(val displayName: String) {
    CASH("Cash"),
    CARD("Card"),
    UPI("UPI"),
    NET_BANKING("Net Banking"),
    CHEQUE("Cheque"),
    CREDIT("Credit")
}