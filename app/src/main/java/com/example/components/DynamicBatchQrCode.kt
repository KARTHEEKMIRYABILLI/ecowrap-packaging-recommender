package com.example.components

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.utils.QrCodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Custom Composable that dynamically generates and renders a ZXing-powered QR code Bitmap
 * encoding active batch details into a structured JSON string.
 * Automatically regenerates whenever the active batch record or parameters change.
 */
@Composable
fun DynamicBatchQrCode(
  batchId: String,
  commodityName: String,
  materialName: String,
  expectedShelfLifeDays: Int,
  ecoScore: Double,
  date: String = "10 Sep 2026",
  modifier: Modifier = Modifier,
  onQrClick: (() -> Unit)? = null
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  var showPayloadDialog by remember { mutableStateOf(false) }

  // 1. Build standardized supply chain JSON payload
  val jsonPayload = remember(batchId, commodityName, materialName, expectedShelfLifeDays, ecoScore, date) {
    QrCodeGenerator.buildBatchJsonPayload(
      batchId = batchId,
      commodity = commodityName,
      material = materialName,
      expectedShelfLifeDays = expectedShelfLifeDays,
      ecoScore = ecoScore,
      date = date
    )
  }

  // 2. Generate QR Bitmap asynchronously when payload updates
  var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var isGenerating by remember { mutableStateOf(true) }

  LaunchedEffect(jsonPayload) {
    isGenerating = true
    val bmp = withContext(Dispatchers.Default) {
      QrCodeGenerator.generateQrBitmap(
        content = jsonPayload,
        size = 512,
        darkColor = android.graphics.Color.parseColor("#0F3D26"),
        lightColor = android.graphics.Color.WHITE
      )
    }
    qrBitmap = bmp
    isGenerating = false
  }

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.2.dp, MintLeafAccent.copy(alpha = 0.5f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = modifier
      .fillMaxWidth()
      .clickable {
        if (onQrClick != null) onQrClick() else showPayloadDialog = true
      }
      .testTag("dynamic_batch_qr_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header tag: Live Dynamic ZXing Generator status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(9.dp)
              .clip(CircleShape)
              .background(if (isGenerating) Color(0xFFF59E0B) else MintLeafAccent)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isGenerating) "Generating ZXing QR..." else "Dynamic ZXing QR Code",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = PaleSageLight
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.Verified,
              contentDescription = null,
              tint = ForestGreenPrimary,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              "JSON Encoded",
              fontSize = 10.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = ForestGreenPrimary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // QR Code Display Frame
      Box(
        modifier = Modifier
          .size(190.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0xFFF8FAFC))
          .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
          .padding(8.dp),
        contentAlignment = Alignment.Center
      ) {
        if (qrBitmap != null) {
          Image(
            bitmap = qrBitmap!!.asImageBitmap(),
            contentDescription = "Dynamic Batch QR Code for $batchId",
            modifier = Modifier.fillMaxSize()
          )
        } else {
          CircularProgressIndicator(
            color = ForestGreenPrimary,
            modifier = Modifier.size(36.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Batch Meta Subtitle
      Text(
        text = "Batch: $batchId • $commodityName",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Shelf Life: $expectedShelfLifeDays Days • Eco Score: $ecoScore/10",
        fontSize = 11.5.sp,
        color = ForestGreenPrimary,
        fontWeight = FontWeight.SemiBold
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Interactive Action Hint
      Surface(
        onClick = { showPayloadDialog = true },
        shape = RoundedCornerShape(8.dp),
        color = PaleSageTint,
        modifier = Modifier.padding(horizontal = 8.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            Icons.Default.Info,
            contentDescription = "Inspect Payload",
            tint = ForestGreenPrimary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Tap to view encoded supply-chain JSON",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = ForestGreenPrimary
          )
        }
      }
    }
  }

  // Encoded JSON Inspection Dialog
  if (showPayloadDialog) {
    Dialog(onDismissRequest = { showPayloadDialog = false }) {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.QrCode, contentDescription = null, tint = ForestGreenPrimary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                "Encoded Batch JSON",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
              )
            }
            IconButton(onClick = {
              clipboardManager.setText(AnnotatedString(jsonPayload))
              Toast.makeText(context, "JSON Payload copied to clipboard", Toast.LENGTH_SHORT).show()
            }) {
              Icon(Icons.Default.ContentCopy, contentDescription = "Copy JSON", tint = TextSecondary)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            "The following structured data is dynamically encoded into this ZXing QR code for real-time supply chain verification:",
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF1E293B),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = jsonPayload,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              color = Color(0xFF38BDF8),
              modifier = Modifier.padding(12.dp),
              lineHeight = 15.sp
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = { showPayloadDialog = false },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Close", fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }
  }
}
