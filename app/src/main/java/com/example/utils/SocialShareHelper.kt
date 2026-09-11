package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.model.CommodityItem
import com.example.model.PackagingMaterial
import com.example.model.StorageConfig
import java.io.File

/**
 * Helper utility to construct and launch Android Share Intents for packaging reports,
 * sustainability summaries, and PDF certificate attachments.
 */
object SocialShareHelper {

  fun buildReportShareText(
    commodity: CommodityItem,
    material: PackagingMaterial,
    storage: StorageConfig
  ): String {
    val verificationCode = material.batchId.hashCode().toString(16).takeLast(8).uppercase()
    return """
      🌿 ECO WRAP — Sustainable Packaging Advisory Report
      ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      📦 Commodity: ${commodity.emoji} ${commodity.name} (${commodity.category})
      🛡️ Recommended Material: ${material.name}
      ✨ Suitability Match: ${material.score}% | Eco-Score: ${material.ecoScore}/10
      ⏳ Predicted Shelf Life: ${material.expectedShelfLife} Days
      🌡️ Storage Condition: ${storage.temperatureC.toInt()}°C, ${storage.relativeHumidityPercent.toInt()}% RH (${storage.transportationType})
      🔬 Barrier Specs: OTR ${material.technicalSpecs.otrValue} • WVTR ${material.technicalSpecs.wvtrValue}
      🧪 MAP Flush: ${material.mapSpecs.o2Percentage} O₂ / ${material.mapSpecs.co2Percentage} CO₂ / ${material.mapSpecs.n2Percentage} N₂
      🌱 Material Structure: ${material.technicalSpecs.packagingStructure} (${material.filmThickness})
      🏷️ Batch Certificate ID: ${material.batchId}
      🔗 Verification Link: https://ecowrap.mofpi.gov.in/verify/${material.batchId}
      ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      Certified via MoFPI Horticultural Preservation Engine.
    """.trimIndent()
  }

  fun shareReportViaChooser(
    context: Context,
    commodity: CommodityItem,
    material: PackagingMaterial,
    storage: StorageConfig
  ) {
    try {
      val text = buildReportShareText(commodity, material, storage)
      val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_SUBJECT, "ECO WRAP Advisory: ${commodity.name} -> ${material.name}")
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
      }
      val shareIntent = Intent.createChooser(sendIntent, "Share Packaging Report via")
      shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(shareIntent)
    } catch (e: Exception) {
      Toast.makeText(context, "Unable to share: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
  }

  fun sharePdfReport(
    context: Context,
    pdfFile: File,
    commodity: CommodityItem,
    material: PackagingMaterial
  ) {
    try {
      val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        pdfFile
      )
      val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_SUBJECT, "ECO WRAP PDF Report: ${commodity.name} - ${material.name}")
        putExtra(Intent.EXTRA_TEXT, "Attached is the verified MoFPI Packaging Compliance Certificate for ${commodity.name} (${material.batchId}).")
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "application/pdf"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      val chooser = Intent.createChooser(shareIntent, "Share PDF Certificate via")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
    } catch (e: Exception) {
      Toast.makeText(context, "Error sharing PDF: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
  }

  fun shareToSpecificPackage(
    context: Context,
    packageName: String,
    commodity: CommodityItem,
    material: PackagingMaterial,
    storage: StorageConfig
  ) {
    try {
      val text = buildReportShareText(commodity, material, storage)
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_SUBJECT, "ECO WRAP Recommendation: ${commodity.name}")
        setPackage(packageName)
      }
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
    } catch (_: Exception) {
      // Fallback to standard chooser if specific app is not installed
      shareReportViaChooser(context, commodity, material, storage)
    }
  }
}
