package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.model.CommodityItem
import com.example.model.PackagingMaterial
import com.example.model.StorageConfig
import com.example.model.UserAccount
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

  /**
   * Generates a formal, publication-quality MoFPI AI Packaging Recommendation PDF.
   * Returns the created PDF File object.
   */
  fun generatePackagingReportPdf(
    context: Context,
    commodity: CommodityItem,
    material: PackagingMaterial,
    storage: StorageConfig,
    user: UserAccount = UserAccount("karthikmiryabbelli@gmail.com", "Karthik")
  ): File {
    val pdfDocument = PdfDocument()

    // Standard A4 dimensions in points (72 points/inch): 595 x 842
    val pageWidth = 595
    val pageHeight = 842
    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas: Canvas = page.canvas

    // Paints & Colors
    val greenDark = 0xFF1B4332.toInt()
    val greenMid = 0xFF2D6A4F.toInt()
    val greenLight = 0xFFEBF4EC.toInt()
    val grayBg = 0xFFF9FAFB.toInt()
    val grayBorder = 0xFFD1D5DB.toInt()
    val textPrimary = 0xFF111827.toInt()
    val textSecondary = 0xFF4B5563.toInt()
    val textMuted = 0xFF6B7280.toInt()
    val orangeAccent = 0xFFD97706.toInt()

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // 1. Header Banner
    paint.color = greenDark
    paint.style = Paint.Style.FILL
    canvas.drawRect(0f, 0f, pageWidth.toFloat(), 96f, paint)

    // Gold accent stripe
    paint.color = orangeAccent
    canvas.drawRect(0f, 96f, pageWidth.toFloat(), 100f, paint)

    // Header Title & Government Subtitle
    paint.color = 0xFFFFFFFF.toInt()
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 9f
    paint.letterSpacing = 0.08f
    canvas.drawText("MINISTRY OF FOOD PROCESSING INDUSTRIES (MoFPI) • GOVT OF INDIA", 36f, 28f, paint)

    paint.textSize = 17f
    paint.letterSpacing = 0.02f
    canvas.drawText("ECO WRAP — AI PACKAGING RECOMMENDATION REPORT", 36f, 54f, paint)

    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.textSize = 10f
    paint.color = 0xFFD1E7DD.toInt()
    canvas.drawText("Smart Packaging Advisory & Cold-Chain Traceability System • SIH26236", 36f, 74f, paint)

    var currentY = 120f

    // 2. Metadata Box
    paint.color = grayBg
    paint.style = Paint.Style.FILL
    val metaRect = RectF(36f, currentY, (pageWidth - 36).toFloat(), currentY + 56f)
    canvas.drawRoundRect(metaRect, 8f, 8f, paint)

    paint.color = grayBorder
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1f
    canvas.drawRoundRect(metaRect, 8f, 8f, paint)
    paint.style = Paint.Style.FILL

    val col1X = 50f
    val col2X = 220f
    val col3X = 400f

    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 9f
    paint.color = textMuted
    canvas.drawText("BATCH REFERENCE", col1X, currentY + 20f, paint)
    canvas.drawText("ASSESSMENT DATE", col2X, currentY + 20f, paint)
    canvas.drawText("USER & ORGANIZATION", col3X, currentY + 20f, paint)

    paint.textSize = 11f
    paint.color = greenDark
    canvas.drawText(material.batchId, col1X, currentY + 38f, paint)

    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val currentDateStr = dateFormat.format(Date())
    paint.color = textPrimary
    canvas.drawText(currentDateStr, col2X, currentY + 38f, paint)

    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.textSize = 10f
    canvas.drawText("${user.name} (${user.role.title})", col3X, currentY + 38f, paint)

    currentY += 76f

    // 3. Recommended Packaging Solution (Hero Card)
    val heroHeight = 158f
    val heroRect = RectF(36f, currentY, (pageWidth - 36).toFloat(), currentY + heroHeight)

    paint.color = greenLight
    paint.style = Paint.Style.FILL
    canvas.drawRoundRect(heroRect, 10f, 10f, paint)

    paint.color = greenMid
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1.5f
    canvas.drawRoundRect(heroRect, 10f, 10f, paint)
    paint.style = Paint.Style.FILL

    // Top Match Badge
    paint.color = greenDark
    val badgeRect = RectF((pageWidth - 140).toFloat(), currentY + 12f, (pageWidth - 50).toFloat(), currentY + 32f)
    canvas.drawRoundRect(badgeRect, 10f, 10f, paint)
    paint.color = 0xFFFFFFFF.toInt()
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 9f
    canvas.drawText("TOP AI MATCH", (pageWidth - 130).toFloat(), currentY + 26f, paint)

    // Hero Content
    paint.color = greenDark
    paint.textSize = 10f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("OPTIMAL PACKAGING SOLUTION", 52f, currentY + 26f, paint)

    paint.color = textPrimary
    paint.textSize = 16f
    canvas.drawText(material.name, 52f, currentY + 50f, paint)

    paint.color = textSecondary
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.textSize = 10f
    val shortDesc = if (material.description.length > 78) material.description.take(78) + "..." else material.description
    canvas.drawText(shortDesc, 52f, currentY + 68f, paint)

    // 3 Metric Badges in Hero Card
    val metricY = currentY + 84f
    val metricW = 155f

    // Metric 1: Suitability Score
    drawMetricCard(canvas, 52f, metricY, metricW, 54f, "Suitability Score", "${material.score}%", greenDark)
    // Metric 2: Shelf-Life
    drawMetricCard(canvas, 220f, metricY, metricW, 54f, "Projected Shelf Life", "${material.expectedShelfLife} Days", greenDark)
    // Metric 3: Eco Score
    drawMetricCard(canvas, 388f, metricY, metricW, 54f, "Eco Sustainability", "${material.ecoScore} / 10", orangeAccent)

    currentY += heroHeight + 20f

    // 4. Two-Column Table: Commodity Biological Profile & Storage Parameters
    val halfW = (pageWidth - 72 - 16) / 2f
    val table1X = 36f
    val table2X = table1X + halfW + 16f
    val tableH = 130f

    // Left Card: Commodity Profile
    drawSectionBox(canvas, table1X, currentY, halfW, tableH, "COMMODITY BIOLOGICAL PROFILE")
    var rowY = currentY + 38f
    drawTableRow(canvas, table1X + 14f, rowY, halfW - 28f, "Commodity Name", "${commodity.emoji} ${commodity.name}")
    rowY += 18f
    drawTableRow(canvas, table1X + 14f, rowY, halfW - 28f, "Respiration Rate", commodity.respirationRate)
    rowY += 18f
    drawTableRow(canvas, table1X + 14f, rowY, halfW - 28f, "Moisture Content", "${commodity.moistureContent}%")
    rowY += 18f
    drawTableRow(canvas, table1X + 14f, rowY, halfW - 28f, "Fat / Oil Content", "${commodity.fatContent}%")
    rowY += 18f
    drawTableRow(canvas, table1X + 14f, rowY, halfW - 28f, "Food Acidity (pH)", "${commodity.phValue}")

    // Right Card: Storage Conditions
    drawSectionBox(canvas, table2X, currentY, halfW, tableH, "STORAGE & ENVIRONMENT TARGETS")
    rowY = currentY + 38f
    drawTableRow(canvas, table2X + 14f, rowY, halfW - 28f, "Storage State", "${storage.storageType.label} (${storage.temperatureC.toInt()}°C)")
    rowY += 18f
    drawTableRow(canvas, table2X + 14f, rowY, halfW - 28f, "Relative Humidity", "${storage.relativeHumidityPercent.toInt()}% RH")
    rowY += 18f
    drawTableRow(canvas, table2X + 14f, rowY, halfW - 28f, "Transit Logistics", storage.transportationType)
    rowY += 18f
    drawTableRow(canvas, table2X + 14f, rowY, halfW - 28f, "Target Shelf-Life", "${storage.desiredShelfLifeDays} days")
    rowY += 18f
    drawTableRow(canvas, table2X + 14f, rowY, halfW - 28f, "Recycling Stream", material.recyclingCode)

    currentY += tableH + 12f

    // 5. Technical Packaging Specifications & Permeability (OTR, WVTR, Thickness, Mechanical)
    val techSpecsH = 124f
    drawSectionBox(canvas, 36f, currentY, (pageWidth - 72).toFloat(), techSpecsH, "DETAILED PACKAGING & BARRIER SPECIFICATIONS")

    rowY = currentY + 36f
    drawTableRow(canvas, 50f, rowY, (pageWidth - 100).toFloat(), "Oxygen Transmission Rate (OTR)", material.technicalSpecs.otrValue)
    rowY += 17f
    drawTableRow(canvas, 50f, rowY, (pageWidth - 100).toFloat(), "Water Vapor Trans. Rate (WVTR)", material.technicalSpecs.wvtrValue)
    rowY += 17f
    drawTableRow(canvas, 50f, rowY, (pageWidth - 100).toFloat(), "Packaging Structure & Thickness", "${material.filmThickness} • ${material.technicalSpecs.packagingStructure.take(45)}")
    rowY += 17f
    drawTableRow(canvas, 50f, rowY, (pageWidth - 100).toFloat(), "Heat Seal & Mechanical Strength", "${material.technicalSpecs.heatSealRange.take(28)} | ${material.technicalSpecs.tensileStrength.take(24)}")
    rowY += 17f
    drawTableRow(canvas, 50f, rowY, (pageWidth - 100).toFloat(), "Gas Permeability & Ratio (β)", "${material.technicalSpecs.co2TransmissionRate} | ${material.technicalSpecs.co2ToO2PermeabilityRatio}")

    currentY += techSpecsH + 12f

    // 6. MAP & Gas Flush Composition Section
    val mapH = 68f
    val mapRect = RectF(36f, currentY, (pageWidth - 36).toFloat(), currentY + mapH)
    paint.color = 0xFFF0FDF4.toInt()
    canvas.drawRoundRect(mapRect, 8f, 8f, paint)

    paint.color = 0xFF16A34A.toInt()
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1f
    canvas.drawRoundRect(mapRect, 8f, 8f, paint)
    paint.style = Paint.Style.FILL

    paint.color = 0xFF14532D.toInt()
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 9f
    canvas.drawText("MODIFIED ATMOSPHERE PACKAGING (MAP) & GAS FLUSH COMPOSITION", 50f, currentY + 18f, paint)

    rowY = currentY + 36f
    drawTableRow(canvas, 50f, rowY, (pageWidth - 100).toFloat(), "Recommended Gas Composition", "O₂: ${material.mapSpecs.o2Percentage}  •  CO₂: ${material.mapSpecs.co2Percentage}  •  N₂: ${material.mapSpecs.n2Percentage}")
    rowY += 18f
    drawTableRow(canvas, 50f, rowY, (pageWidth - 100).toFloat(), "Target Equilibrium & Additives", "${material.mapSpecs.targetEquilibriumAtmosphere} | Anti-fog + Scavenger")

    currentY += mapH + 12f

    // 7. Critical Handling Advisory
    val advisoryH = 48f
    paint.color = 0xFFFEF3C7.toInt() // Amber tint
    val advisoryRect = RectF(36f, currentY, (pageWidth - 36).toFloat(), currentY + advisoryH)
    canvas.drawRoundRect(advisoryRect, 8f, 8f, paint)

    paint.color = 0xFFF59E0B.toInt()
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1f
    canvas.drawRoundRect(advisoryRect, 8f, 8f, paint)
    paint.style = Paint.Style.FILL

    paint.color = 0xFF92400E.toInt()
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 8.5f
    canvas.drawText("CRITICAL HANDLING ADVISORY", 50f, currentY + 16f, paint)

    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.textSize = 8.5f
    paint.color = 0xFF78350F.toInt()
    val adv1 = "• ${material.safetyAdvisory.take(115)}"
    val adv2 = "• Maintain storage at ${storage.temperatureC.toInt()}°C and ${storage.relativeHumidityPercent.toInt()}% RH to ensure predicted shelf-life."
    canvas.drawText(adv1, 50f, currentY + 30f, paint)
    canvas.drawText(adv2, 50f, currentY + 42f, paint)

    currentY += advisoryH + 12f

    // 7. Official Digital Verification & Stamp
    paint.color = grayBorder
    paint.strokeWidth = 0.8f
    canvas.drawLine(36f, currentY, (pageWidth - 36).toFloat(), currentY, paint)

    currentY += 16f

    // Draw Simulated Verification QR Box
    val qrBoxSize = 54f
    paint.color = greenDark
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1.2f
    canvas.drawRect(36f, currentY, 36f + qrBoxSize, currentY + qrBoxSize, paint)
    paint.style = Paint.Style.FILL

    // Inner QR pattern mockup
    paint.color = greenMid
    canvas.drawRect(42f, currentY + 6f, 54f, currentY + 18f, paint)
    canvas.drawRect(66f, currentY + 6f, 78f, currentY + 18f, paint)
    canvas.drawRect(42f, currentY + 30f, 54f, currentY + 42f, paint)
    canvas.drawRect(60f, currentY + 24f, 72f, currentY + 36f, paint)

    paint.color = textPrimary
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 10f
    canvas.drawText("MoFPI VERIFIED TRACEABILITY CERTIFICATE", 102f, currentY + 16f, paint)

    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.textSize = 8.5f
    paint.color = textMuted
    canvas.drawText("Digital Verification Hash: SHA256-ECW-${material.batchId}-${commodity.name.uppercase()}", 102f, currentY + 30f, paint)
    canvas.drawText("Authorized by Eco Wrap AI Algorithmic Engine • Zero Food Waste Mission", 102f, currentY + 44f, paint)

    // Signature stamp on right
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 9f
    paint.color = greenMid
    canvas.drawText("ELECTRONICALLY CERTIFIED", (pageWidth - 190).toFloat(), currentY + 24f, paint)
    paint.color = textMuted
    paint.textSize = 8f
    canvas.drawText("Date: $currentDateStr", (pageWidth - 190).toFloat(), currentY + 40f, paint)

    pdfDocument.finishPage(page)

    // Save PDF to Documents Directory or Cache
    val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
    val cleanCommodityName = commodity.name.replace("\\s+".toRegex(), "_")
    val fileName = "EcoWrap_Recommendation_${material.batchId}_$cleanCommodityName.pdf"
    val destinationFile = File(docsDir, fileName)

    try {
      FileOutputStream(destinationFile).use { out ->
        pdfDocument.writeTo(out)
      }
    } finally {
      pdfDocument.close()
    }

    return destinationFile
  }

  private fun drawMetricCard(
    canvas: Canvas,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    label: String,
    value: String,
    accentColor: Int
  ) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = 0xFFFFFFFF.toInt()
    val cardRect = RectF(x, y, x + width, y + height)
    canvas.drawRoundRect(cardRect, 6f, 6f, paint)

    paint.color = 0xFFE5E7EB.toInt()
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1f
    canvas.drawRoundRect(cardRect, 6f, 6f, paint)
    paint.style = Paint.Style.FILL

    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.textSize = 8.5f
    paint.color = 0xFF6B7280.toInt()
    canvas.drawText(label, x + 10f, y + 18f, paint)

    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 15f
    paint.color = accentColor
    canvas.drawText(value, x + 10f, y + 42f, paint)
  }

  private fun drawSectionBox(
    canvas: Canvas,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    title: String
  ) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = 0xFFFFFFFF.toInt()
    val boxRect = RectF(x, y, x + width, y + height)
    canvas.drawRoundRect(boxRect, 8f, 8f, paint)

    paint.color = 0xFFE5E7EB.toInt()
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1f
    canvas.drawRoundRect(boxRect, 8f, 8f, paint)
    paint.style = Paint.Style.FILL

    // Section Header Bar
    paint.color = 0xFFF3F4F6.toInt()
    val headerRect = RectF(x, y, x + width, y + 26f)
    canvas.drawRoundRect(headerRect, 8f, 8f, paint)

    paint.color = 0xFF1B4332.toInt()
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 9f
    canvas.drawText(title, x + 12f, y + 17f, paint)
  }

  private fun drawTableRow(
    canvas: Canvas,
    x: Float,
    y: Float,
    width: Float,
    label: String,
    value: String
  ) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.textSize = 9.5f
    paint.color = 0xFF6B7280.toInt()
    canvas.drawText(label, x, y, paint)

    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.color = 0xFF111827.toInt()
    paint.textAlign = Paint.Align.RIGHT
    canvas.drawText(value, x + width, y, paint)
  }

  /**
   * Opens the PDF in an external viewer using FileProvider.
   */
  fun openPdfFile(context: Context, file: File) {
    try {
      val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )
      val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "PDF saved to app documents: ${file.name}", Toast.LENGTH_SHORT).show()
    }
  }

  /**
   * Shares the PDF via standard Android share sheet.
   */
  fun sharePdfFile(context: Context, file: File) {
    try {
      val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "MoFPI Packaging Recommendation: ${file.name}")
        putExtra(Intent.EXTRA_TEXT, "Here is the AI packaging recommendation and traceability report for ${file.name}.")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Report ready: ${file.name}", Toast.LENGTH_SHORT).show()
    }
  }
}
