package com.example.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GeminiSearchGroundingService
import com.example.data.GroundedMarketInsight
import com.example.model.CommodityItem
import com.example.model.PackagingMaterial
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun GoogleSearchGroundedIntelCard(
  commodity: CommodityItem,
  material: PackagingMaterial,
  storageType: String = "Cold Storage (4°C, 90% RH)",
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val service = remember { GeminiSearchGroundingService() }

  var insight by remember { mutableStateOf<GroundedMarketInsight?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  fun fetchIntel() {
    isLoading = true
    coroutineScope.launch {
      insight = service.fetchGroundedIntelligence(
        commodityName = commodity.name,
        packagingMaterial = material.name,
        storageType = storageType
      )
      isLoading = false
    }
  }

  LaunchedEffect(commodity.name, material.name) {
    fetchIntel()
  }

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.5.dp, Color(0xFF4285F4).copy(alpha = 0.35f)),
    modifier = modifier
      .fillMaxWidth()
      .testTag("google_search_grounding_card")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top Grounding Brand Banner
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Color(0xFFE8F0FE),
          border = BorderStroke(1.dp, Color(0xFF4285F4).copy(alpha = 0.5f))
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Icon(
              Icons.Default.TravelExplore,
              contentDescription = "Google Search",
              tint = Color(0xFF1A73E8),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Google Search Grounded • gemini-3.5-flash",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1A73E8)
            )
          }
        }

        IconButton(
          onClick = { fetchIntel() },
          modifier = Modifier
            .size(32.dp)
            .testTag("refresh_grounding_button")
        ) {
          if (isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(16.dp),
              color = Color(0xFF1A73E8),
              strokeWidth = 2.dp
            )
          } else {
            Icon(
              Icons.Default.Refresh,
              contentDescription = "Refresh Google Search Intel",
              tint = Color(0xFF1A73E8),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Live Market & Regulatory Intelligence",
        fontSize = 16.sp,
        fontWeight = FontWeight.ExtraBold,
        color = ForestGreenPrimary
      )
      Text(
        text = "Verified through Google Search Grounding for ${commodity.name} & ${material.name}",
        fontSize = 11.sp,
        color = TextSecondary
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Mandi Price and Standards Badges
      insight?.let { currentInsight ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          currentInsight.mandiPriceRange?.let { price ->
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = PaleSageLight,
              border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.4f)),
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Text("Mandi Price Rate", fontSize = 10.sp, color = ForestGreenPrimary, fontWeight = FontWeight.SemiBold)
                Text(price, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              }
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFF3F4F6),
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Text("MoFPI Standard", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
              Text("FSSAI IS 9845 / APEDA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grounding Search Queries Chips
        if (currentInsight.searchQueries.isNotEmpty()) {
          Text(
            text = "Google Search Queries Executed:",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(currentInsight.searchQueries) { query ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F5F9),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(query, fontSize = 10.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
        }

        // Summary Text Box
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFF8FAFC),
          border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = currentInsight.summaryText,
              fontSize = 12.sp,
              color = TextPrimary,
              lineHeight = 17.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grounding Citations and Sources
        if (currentInsight.sources.isNotEmpty()) {
          Text(
            text = "Google Search Grounding Sources & Citations:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))

          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            currentInsight.sources.take(3).forEach { source ->
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .clickable {
                    try {
                      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                      context.startActivity(intent)
                    } catch (_: Exception) {}
                  }
                  .padding(vertical = 4.dp, horizontal = 4.dp)
              ) {
                Icon(
                  Icons.AutoMirrored.Filled.OpenInNew,
                  contentDescription = null,
                  tint = Color(0xFF1A73E8),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = source.title,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color(0xFF1A73E8),
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Text(
            text = "Verified at ${currentInsight.lastUpdated}",
            fontSize = 10.sp,
            color = TextMuted
          )
        }
      }
    }
  }
}
