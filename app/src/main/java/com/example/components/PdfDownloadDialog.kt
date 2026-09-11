package com.example.components

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CommodityItem
import com.example.model.PackagingMaterial
import com.example.ui.theme.*
import com.example.utils.PdfReportGenerator
import java.io.File

@Composable
fun PdfDownloadSuccessDialog(
  file: File,
  commodity: CommodityItem,
  material: PackagingMaterial,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val fileSizeKb = (file.length() / 1024).coerceAtLeast(1)
  var isCopied by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    containerColor = Color.White,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .background(PaleSageLight, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PictureAsPdf,
            contentDescription = null,
            tint = ForestGreenPrimary,
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "Report Downloaded",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary
          )
          Text(
            text = "MoFPI Official PDF Certificate",
            fontSize = 12.sp,
            color = TextSecondary
          )
        }
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Document Card Info
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = PaleSageTint.copy(alpha = 0.4f)),
          border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${commodity.emoji} ${commodity.name}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = ForestGreenPrimary
              )
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = ForestGreenPrimary
              ) {
                Text(
                  text = "Batch ${material.batchId}",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = material.name,
              fontSize = 12.sp,
              color = TextPrimary,
              fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFE5E7EB))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("File Name", fontSize = 10.sp, color = TextSecondary)
                Text(
                  text = file.name,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = TextPrimary,
                  maxLines = 1
                )
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("Size", fontSize = 10.sp, color = TextSecondary)
                Text("$fileSizeKb KB", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
              }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Folder,
                contentDescription = null,
                tint = MintLeafAccent,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Saved to: App Documents folder",
                fontSize = 10.5.sp,
                color = TextSecondary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Security / Verification Badge
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .background(PaleSageLight, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.Verified, contentDescription = null, tint = MintLeafAccent, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            "Digitally stamped with SHA256 batch traceability hash",
            fontSize = 11.sp,
            color = ForestGreenPrimary,
            fontWeight = FontWeight.Medium
          )
        }
      }
    },
    confirmButton = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = {
            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = "MoFPI ECO WRAP Report: ${commodity.name} -> ${material.name} (Batch: ${material.batchId}, Eco Score: ${material.ecoScore}/10)"
            val clip = ClipData.newPlainText("Packaging Report", text)
            clipboard.setPrimaryClip(clip)
            isCopied = true
            Toast.makeText(context, "Report details & Batch ID copied to clipboard!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier
            .weight(1f)
            .testTag("dialog_share_pdf_btn"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreenPrimary)
        ) {
          Icon(if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (isCopied) "Copied" else "Copy Info", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .weight(1f)
            .testTag("dialog_open_pdf_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    },
    dismissButton = {}
  )
}
