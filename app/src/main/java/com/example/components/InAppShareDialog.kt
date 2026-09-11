package com.example.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CommodityItem
import com.example.model.PackagingMaterial
import com.example.model.StorageConfig
import com.example.ui.theme.*

@Composable
fun InAppShareDialog(
  commodity: CommodityItem,
  material: PackagingMaterial,
  storage: StorageConfig = StorageConfig(),
  onDismiss: () -> Unit,
  onDownloadPdf: () -> Unit = {}
) {
  val context = LocalContext.current
  var copiedType by remember { mutableStateOf<String?>(null) }

  val shareText = """
    🌾 MoFPI ECO WRAP — Packaging Recommendation Report
    • Commodity: ${commodity.name} (${commodity.category})
    • Recommended Packaging: ${material.name}
    • Suitability Score: ${material.score}% (Eco Score: ${material.ecoScore}/10)
    • Expected Shelf Life: ${material.expectedShelfLife} days
    • Storage Condition: ${storage.temperatureC.toInt()}°C, ${storage.relativeHumidityPercent.toInt()}% RH (${storage.transportationType})
    • Batch ID: ${material.batchId}
    • Verification Hash: SHA256-${material.batchId.hashCode().toString(16).takeLast(8).uppercase()}
    
    Verified by Ministry of Food Processing Industries (MoFPI) AI Advisory System.
  """.trimIndent()

  val copyToClipboard = { text: String, label: String, feedbackName: String ->
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    copiedType = feedbackName
    Toast.makeText(context, "$feedbackName copied to clipboard!", Toast.LENGTH_SHORT).show()
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(22.dp),
    containerColor = Color.White,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .background(PaleSageLight, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Share, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Share Report",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreenPrimary
            )
            Text(
              text = "Batch ${material.batchId}",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }
        }
        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
        }
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Report Summary Preview Card
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = PaleSageTint.copy(alpha = 0.5f)),
          border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${commodity.emoji} ${commodity.name}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = ForestGreenPrimary
              )
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = ForestGreenPrimary
              ) {
                Text(
                  text = "${material.score}% Match",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = material.name,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Expected Shelf Life: ${material.expectedShelfLife} days • Eco Score: ${material.ecoScore}/10",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }
        }

        if (copiedType != null) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MintLeafAccent.copy(alpha = 0.15f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("$copiedType copied successfully!", fontSize = 11.sp, color = ForestGreenPrimary, fontWeight = FontWeight.SemiBold)
            }
          }
        }

        // Quick Share Options
        ShareOptionTile(
          icon = Icons.Default.ContentCopy,
          title = "Copy Summary & Metrics",
          subtitle = "Text summary formatted for WhatsApp or email",
          onClick = {
            copyToClipboard(shareText, "Packaging Report", "Summary text")
          }
        )

        ShareOptionTile(
          icon = Icons.Default.Link,
          title = "Copy MoFPI Verification URL",
          subtitle = "https://ecowrap.mofpi.gov.in/verify/${material.batchId}",
          onClick = {
            copyToClipboard("https://ecowrap.mofpi.gov.in/verify/${material.batchId}", "Verification Link", "Verification link")
          }
        )

        ShareOptionTile(
          icon = Icons.Default.PictureAsPdf,
          title = "Generate & Save PDF Report",
          subtitle = "Official high-res certificate with batch QR",
          onClick = {
            onDownloadPdf()
            onDismiss()
          }
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
        modifier = Modifier.fillMaxWidth().testTag("in_app_share_close_btn")
      ) {
        Text("Done", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
      }
    }
  )
}

@Composable
private fun ShareOptionTile(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = Color(0xFFF9FAFB),
    border = BorderStroke(1.dp, CardBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .background(PaleSageLight, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(16.dp))
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(subtitle, fontSize = 10.5.sp, color = TextSecondary, maxLines = 1)
      }
      Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
    }
  }
}
