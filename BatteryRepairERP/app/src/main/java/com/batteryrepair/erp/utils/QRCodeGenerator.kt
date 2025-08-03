package com.batteryrepair.erp.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*

class QRCodeGenerator {
    
    companion object {
        private const val DEFAULT_QR_SIZE = 512
        private const val DEFAULT_MARGIN = 1
        
        /**
         * Generate QR code bitmap from text
         */
        fun generateQRCode(
            text: String,
            size: Int = DEFAULT_QR_SIZE,
            margin: Int = DEFAULT_MARGIN
        ): Result<Bitmap> {
            return try {
                val writer = QRCodeWriter()
                val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                    put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
                    put(EncodeHintType.MARGIN, margin)
                    put(EncodeHintType.CHARACTER_SET, "UTF-8")
                }
                
                val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints)
                val bitmap = createBitmapFromBitMatrix(bitMatrix)
                
                Result.success(bitmap)
            } catch (e: WriterException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
        
        /**
         * Generate unique QR code data for battery
         */
        fun generateBatteryQRData(
            batteryId: String,
            customerId: String,
            timestamp: Long = System.currentTimeMillis()
        ): String {
            // Format: BATTERY_ID|CUSTOMER_ID|TIMESTAMP|CHECKSUM
            val baseData = "$batteryId|$customerId|$timestamp"
            val checksum = generateChecksum(baseData)
            return "$baseData|$checksum"
        }
        
        /**
         * Generate unique battery ID
         */
        fun generateBatteryId(): String {
            val timestamp = System.currentTimeMillis()
            val random = (1000..9999).random()
            return "BAT${timestamp}${random}"
        }
        
        /**
         * Parse QR code data back to components
         */
        fun parseQRData(qrData: String): BatteryQRInfo? {
            return try {
                val parts = qrData.split("|")
                if (parts.size == 4) {
                    val batteryId = parts[0]
                    val customerId = parts[1]
                    val timestamp = parts[2].toLong()
                    val checksum = parts[3]
                    
                    // Verify checksum
                    val baseData = "$batteryId|$customerId|$timestamp"
                    if (generateChecksum(baseData) == checksum) {
                        BatteryQRInfo(batteryId, customerId, timestamp, true)
                    } else {
                        BatteryQRInfo(batteryId, customerId, timestamp, false)
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
        
        /**
         * Validate QR code format
         */
        fun isValidBatteryQR(qrData: String): Boolean {
            val info = parseQRData(qrData)
            return info?.isValid == true
        }
        
        private fun createBitmapFromBitMatrix(bitMatrix: BitMatrix): Bitmap {
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            
            return bitmap
        }
        
        private fun generateChecksum(data: String): String {
            return data.hashCode().toString(16).uppercase()
        }
    }
    
    data class BatteryQRInfo(
        val batteryId: String,
        val customerId: String,
        val timestamp: Long,
        val isValid: Boolean
    )
}