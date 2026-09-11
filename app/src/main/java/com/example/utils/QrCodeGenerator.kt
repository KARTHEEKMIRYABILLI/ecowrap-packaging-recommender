package com.example.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.LruCache
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import org.json.JSONObject

/**
 * Lightweight, Low-Memory QR Code Generator using the ZXing library.
 * Includes in-memory LRU caching to eliminate redundant bitmap allocations on budget hardware.
 */
object QrCodeGenerator {

  // Memory-efficient in-memory bitmap cache (max 6 bitmaps cached, ~1.5 MB RAM ceiling)
  private val qrBitmapCache = object : LruCache<String, Bitmap>(6) {}

  /**
   * Constructs a standardized supply-chain JSON string payload.
   */
  fun buildBatchJsonPayload(
    batchId: String,
    commodity: String,
    material: String,
    expectedShelfLifeDays: Int,
    ecoScore: Double,
    date: String = "10 Sep 2026",
    organization: String = "MoFPI Certified Partner"
  ): String {
    val json = JSONObject()
    json.put("batchId", batchId)
    json.put("commodity", commodity)
    json.put("recommendedMaterial", material)
    json.put("expectedShelfLifeDays", expectedShelfLifeDays)
    json.put("ecoScore", ecoScore)
    json.put("issueDate", date)
    json.put("organization", organization)
    json.put("system", "MoFPI ECO WRAP (SIH26236)")
    json.put("verificationStatus", "VERIFIED_COMPLIANT")
    return json.toString(2)
  }

  /**
   * Generates an Android Bitmap from the provided content string using ZXing QRCodeWriter.
   * Checks LRU cache first to prevent costly BitMatrix allocations and garbage collector pressure.
   */
  fun generateQrBitmap(
    content: String,
    size: Int = 512,
    darkColor: Int = Color.parseColor("#0F3D26"), // ForestGreenPrimary
    lightColor: Int = Color.WHITE
  ): Bitmap? {
    val cacheKey = "$content|$size|$darkColor|$lightColor"
    qrBitmapCache.get(cacheKey)?.let { return it }

    return try {
      val hints = HashMap<EncodeHintType, Any>(3).apply {
        put(EncodeHintType.CHARACTER_SET, "UTF-8")
        put(EncodeHintType.MARGIN, 1)
        put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M)
      }

      val bitMatrix = QRCodeWriter().encode(
        content,
        BarcodeFormat.QR_CODE,
        size,
        size,
        hints
      )

      val width = bitMatrix.width
      val height = bitMatrix.height
      val pixels = IntArray(width * height)

      for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
          pixels[offset + x] = if (bitMatrix.get(x, y)) darkColor else lightColor
        }
      }

      // Use RGB_565 for 50% lower memory usage on budget hardware (2 bytes/pixel vs 4 bytes/pixel)
      val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
      bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
      qrBitmapCache.put(cacheKey, bitmap)
      bitmap
    } catch (e: Exception) {
      null
    }
  }

  /**
   * Clears the QR bitmap cache to immediately free RAM when low-memory warnings are triggered.
   */
  fun clearCache() {
    qrBitmapCache.evictAll()
  }
}
