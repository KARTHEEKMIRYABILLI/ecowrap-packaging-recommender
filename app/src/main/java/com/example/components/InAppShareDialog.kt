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
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.utils.PdfReportGenerator
import com.example.utils.SocialShareHelper
import com.example.utils.rememberAppHaptics

@Composable
fun InAppShareDialog(
  commodity: CommodityItem,
  material: PackagingMaterial,
  storage: StorageConfig = StorageConfig(),
  onDismiss: () -> Unit,
  onDownloadPdf: () -> Unit = {}
) {
  val context = LocalContext.current
  val haptics = rememberAppHaptics()
  var copiedType by remember { mutableStateOf<String?>(null) }
  val isDark = AppTheme.isDark
  val colors = AppTheme.colors

  val shareText = remember(commodity, material, storage) {
    SocialShareHelper.buildReportShareText(commodity, material, storage)
  }

  val copyToClipboard = { text: String, label: String, feedbackName: String ->
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    copiedType = feedbackName
    haptics.performSuccess()
    Toast.makeText(context, "$feedbackName copied to clipboard!", Toast.LENGTH_SHORT).show()
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(22.dp),
    containerColor = colors.cardBackground,
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
              .background(colors.paleSageLight, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Share, contentDescription = null, tint = colors.headerText, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Share Advisory Report",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = colors.headerText
            )
            Text(
              text = "Batch ${material.batchId}",
              fontSize = 11.sp,
              color = colors.textSecondary
            )
          }
        }
        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.textSecondary, modifier = Modifier.size(18.dp))
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
          colors = CardDefaults.cardColors(containerColor = colors.paleSageTint),
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
                color = colors.headerText
              )
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isDark) MintLeafAccent else ForestGreenPrimary
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
              color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Expected Shelf Life: ${material.expectedShelfLife} days • Eco Score: ${material.ecoScore}/10",
              fontSize = 11.sp,
              color = colors.textSecondary
            )
          }
        }

        if (copiedType != null) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MintLeafAccent.copy(alpha = 0.18f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (isDark) MintLeafAccent else ForestGreenPrimary, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("$copiedType copied successfully!", fontSize = 11.sp, color = colors.headerText, fontWeight = FontWeight.SemiBold)
            }
          }
        }

        // Primary Action: Android System Share Chooser (Social & Messaging Apps)
        Button(
          onClick = {
            haptics.performClick()
            SocialShareHelper.shareReportViaChooser(context, commodity, material, storage)
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = if (isDark) MintLeafAccent else ForestGreenPrimary),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("share_system_chooser_btn")
        ) {
          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Share via Messaging & Social Apps", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        // Quick Share Option: WhatsApp / Messaging
        ShareOptionTile(
          icon = Icons.Default.Chat,
          title = "Share directly to WhatsApp / Chat",
          subtitle = "Launch chat app with preformatted summary",
          onClick = {
            haptics.performClick()
            SocialShareHelper.shareToSpecificPackage(context, "com.whatsapp", commodity, material, storage)
            onDismiss()
          }
        )

        // Quick Share Option: Share PDF File Attachment
        ShareOptionTile(
          icon = Icons.Default.PictureAsPdf,
          title = "Share PDF Certificate File",
          subtitle = "Attach official signed PDF report to email or messaging",
          onClick = {
            haptics.performClick()
            try {
              val file = PdfReportGenerator.generatePackagingReportPdf(
                context = context,
                commodity = commodity,
                material = material,
                storage = storage
              )
              SocialShareHelper.sharePdfReport(context, file, commodity, material)
              onDismiss()
            } catch (e: Exception) {
              Toast.makeText(context, "Error preparing PDF: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
          }
        )

        // Quick Share Option: Copy Text Summary
        ShareOptionTile(
          icon = Icons.Default.ContentCopy,
          title = "Copy Summary & Metrics",
          subtitle = "Text summary formatted for clipboard pasting",
          onClick = {
            copyToClipboard(shareText, "Packaging Report", "Summary text")
          }
        )

        // Quick Share Option: Copy MoFPI Verification URL
        ShareOptionTile(
          icon = Icons.Default.Link,
          title = "Copy MoFPI Verification URL",
          subtitle = "https://ecowrap.mofpi.gov.in/verify/${material.batchId}",
          onClick = {
            copyToClipboard("https://ecowrap.mofpi.gov.in/verify/${material.batchId}", "Verification Link", "Verification link")
          }
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) colors.paleSageLight else Color(0xFFF3F4F6)),
        modifier = Modifier.fillMaxWidth().testTag("in_app_share_close_btn")
      ) {
        Text("Done", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
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
  val colors = AppTheme.colors

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = colors.cardBackground,
    border = BorderStroke(1.dp, colors.cardBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .background(colors.paleSageLight, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = colors.headerText, modifier = Modifier.size(16.dp))
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
        Text(subtitle, fontSize = 10.5.sp, color = colors.textSecondary, maxLines = 1)
      }
      Icon(Icons.Default.ChevronRight, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(16.dp))
    }
  }
}
