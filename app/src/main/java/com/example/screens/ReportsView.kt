package com.example.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.DynamicBatchQrCode
import com.example.components.DynamicQrCodeWidget
import com.example.components.EnvironmentalImpactVisualizer
import com.example.model.CommodityItem
import com.example.model.PackagingMaterial
import com.example.model.StorageConfig
import com.example.ui.theme.*

@Composable
fun ReportsView(
  commodity: CommodityItem,
  storage: StorageConfig,
  material: PackagingMaterial,
  onShareReport: () -> Unit,
  onDownloadPdf: () -> Unit
) {
  var showDownloadSuccess by remember { mutableStateOf(false) }
  var selectedBatch by remember { mutableStateOf(material.batchId) }

  val sampleBatches = listOf(
    Pair("PW-2026-001", "Tomato"),
    Pair("PW-2026-002", "Mango"),
    Pair("PW-2026-003", "Potato")
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(AppBackground)
      .padding(horizontal = 20.dp, vertical = 16.dp)
      .verticalScroll(rememberScrollState())
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Packaging Reports",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreenPrimary
        )
        Text(
          text = "MoFPI Supply Chain Batch Traceability",
          fontSize = 13.sp,
          color = TextSecondary
        )
      }

      IconButton(onClick = onShareReport) {
        Icon(Icons.Default.Share, contentDescription = "Share", tint = ForestGreenPrimary)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Batch Filter Chips
    Text("Active Batch Records", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
    Spacer(modifier = Modifier.height(6.dp))
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(sampleBatches) { batch ->
        val isSelected = selectedBatch == batch.first
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (isSelected) PaleSageTint else Color.White,
          border = BorderStroke(1.dp, if (isSelected) MintLeafAccent else CardBorder),
          modifier = Modifier.clickable { selectedBatch = batch.first }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              "${batch.first} (${batch.second})",
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) ForestGreenPrimary else TextPrimary
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (showDownloadSuccess) {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PaleSageTint),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreenPrimary)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            "Report PDF downloaded to device storage!",
            fontSize = 13.sp,
            color = ForestGreenPrimary,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Product Header Card: Commodity Thumbnail + Packaging Title
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, CardBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(54.dp)
            .background(PaleSageLight, RoundedCornerShape(12.dp)),
          contentAlignment = Alignment.Center
        ) {
          Text(commodity.emoji, fontSize = 36.sp)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = commodity.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary
          )
          Text(
            text = "Packaging: ${material.name}",
            fontSize = 13.sp,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Batch: $selectedBatch • Certified",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MintLeafAccent
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Data Table Card
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, CardBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        ReportTableRow("Expected Shelf Life", "${material.expectedShelfLife} days")
        HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))

        ReportTableRow("Storage Temperature", "${storage.temperatureC.toInt()}°C")
        HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))

        ReportTableRow("Relative Humidity", "${storage.relativeHumidityPercent.toInt()}%")
        HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))

        ReportTableRow("Packaging Thickness", material.filmThickness)
        HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))

        ReportTableRow("Estimated Cost", material.costPerKg)
        HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))

        ReportTableRow("Eco Score", "${material.ecoScore} / 10")
        HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))

        ReportTableRow("Batch ID", selectedBatch)
        HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))

        ReportTableRow("Date", material.date)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Task 2: Dynamically generated ZXing QR code encoding active batch details into JSON string
    DynamicBatchQrCode(
      batchId = selectedBatch,
      commodityName = commodity.name,
      materialName = material.name,
      expectedShelfLifeDays = material.expectedShelfLife.toInt(),
      ecoScore = material.ecoScore.toDouble(),
      date = material.date,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(18.dp))

    // Task 1: D3 / Recharts visualization showing environmental impact reduction
    EnvironmentalImpactVisualizer(
      recommendedMaterialName = material.name,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Action Buttons
    Button(
      onClick = {
        showDownloadSuccess = true
        onDownloadPdf()
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("download_report_pdf_btn"),
      colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
      shape = RoundedCornerShape(14.dp)
    ) {
      Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Download Full Report (PDF)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }

    Spacer(modifier = Modifier.height(80.dp)) // padding for bottom bar
  }
}

@Composable
private fun ReportTableRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      fontSize = 13.sp,
      color = TextSecondary
    )
    Text(
      text = value,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = ForestGreenPrimary
    )
  }
}
