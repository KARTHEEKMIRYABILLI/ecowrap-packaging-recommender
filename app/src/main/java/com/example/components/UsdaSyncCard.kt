package com.example.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UsdaFoodDataService
import com.example.model.CommodityItem
import com.example.model.UsdaFetchState
import com.example.model.UsdaFoodItem
import com.example.ui.theme.*
import com.example.utils.rememberAppHaptics
import kotlinx.coroutines.launch

/**
 * Interactive card component that queries the USDA FoodData Central API
 * and allows 1-tap auto-population of food moisture & fat content into the active form.
 */
@Composable
fun UsdaSyncCard(
  commodity: CommodityItem,
  onApplyUsdaData: (moisture: Double, fat: Double, itemDescription: String) -> Unit,
  modifier: Modifier = Modifier,
  compact: Boolean = false
) {
  val haptics = rememberAppHaptics()
  val coroutineScope = rememberCoroutineScope()
  val usdaService = remember { UsdaFoodDataService() }

  var fetchState by remember { mutableStateOf<UsdaFetchState>(UsdaFetchState.Idle) }
  var customSearchQuery by remember { mutableStateOf("") }
  var isSearchExpanded by remember { mutableStateOf(false) }

  val executeFetch = { query: String ->
    coroutineScope.launch {
      haptics.performTick()
      fetchState = UsdaFetchState.Loading(query)
      val result = usdaService.fetchFoodData(query)
      result.onSuccess { (foodItem, isLive) ->
        haptics.performSuccess()
        fetchState = UsdaFetchState.Success(
          query = query,
          foodItem = foodItem,
          isLiveApi = isLive,
          message = if (isLive) "Live API match from USDA FoodData Central" else "Verified USDA SR Legacy dataset"
        )
      }.onFailure { err ->
        haptics.performError()
        fetchState = UsdaFetchState.Error(err.message ?: "Failed to fetch USDA data", query)
      }
    }
  }

  // Auto-fetch when user navigates or when commodity changes if idle
  LaunchedEffect(commodity.id) {
    if (fetchState is UsdaFetchState.Idle) {
      executeFetch(commodity.name)
    }
  }

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.5.dp, Color(0xFF0284C7).copy(alpha = 0.25f)),
    modifier = modifier
      .fillMaxWidth()
      .testTag("usda_sync_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(if (compact) 12.dp else 16.dp)
    ) {
      // Header with USDA Emblem Badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
              Brush.linearGradient(
                listOf(Color(0xFF0369A1), Color(0xFF0284C7))
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Text("🏛️", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "USDA FoodData Central",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0369A1)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0xFFE0F2FE)
            ) {
              Text(
                text = "ARS Official API",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0284C7),
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
              )
            }
          }
          Text(
            text = "Verified nutrient & moisture database",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }

        IconButton(
          onClick = { isSearchExpanded = !isSearchExpanded },
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
            contentDescription = "Search USDA",
            tint = Color(0xFF0284C7),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      // Optional search field for custom USDA food lookup
      AnimatedVisibility(visible = isSearchExpanded) {
        Column(modifier = Modifier.padding(top = 10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = customSearchQuery,
              onValueChange = { customSearchQuery = it },
              placeholder = { Text("e.g. Apples raw, gala, fuji...", fontSize = 12.sp) },
              singleLine = true,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .height(48.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0284C7),
                unfocusedBorderColor = CardBorder
              )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                if (customSearchQuery.isNotBlank()) {
                  executeFetch(customSearchQuery)
                }
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
              modifier = Modifier.height(48.dp)
            ) {
              Text("Search", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      when (val state = fetchState) {
        is UsdaFetchState.Idle -> {
          Button(
            onClick = { executeFetch(commodity.name) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("fetch_usda_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Fetch USDA Data for '${commodity.name}'", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }

        is UsdaFetchState.Loading -> {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF0F9FF), RoundedCornerShape(12.dp))
              .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(12.dp))
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(18.dp),
              strokeWidth = 2.dp,
              color = Color(0xFF0284C7)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Querying USDA FoodData Central for '${state.query}'...",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFF0369A1)
            )
          }
        }

        is UsdaFetchState.Success -> {
          val item = state.foodItem
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF0F9FF), RoundedCornerShape(12.dp))
              .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(12.dp))
              .padding(12.dp)
          ) {
            // Matched Item Header
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = Color(0xFF059669),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = item.description,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.weight(1f)
              )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "FDC ID #${item.fdcId} • ${item.dataType} • ${state.message}",
              fontSize = 10.5.sp,
              color = Color(0xFF0369A1)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Nutrient Grid Cards
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Moisture Card
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text("Moisture (Water)", fontSize = 10.sp, color = TextSecondary)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "${item.moisturePercent}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0284C7)
                  )
                  Text("(${item.moisturePercent}g / 100g)", fontSize = 9.5.sp, color = TextMuted)
                }
              }

              // Fat Content Card
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text("Total Lipid (Fat)", fontSize = 10.sp, color = TextSecondary)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "${item.fatPercent}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFD97706)
                  )
                  Text("(${item.fatPercent}g / 100g)", fontSize = 9.5.sp, color = TextMuted)
                }
              }

              // Protein Card
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text("Protein", fontSize = 10.sp, color = TextSecondary)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "${item.proteinGrams}g",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF16A34A)
                  )
                  Text("(${item.carbGrams}g Carbs)", fontSize = 9.5.sp, color = TextMuted)
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Button: Auto-populate Food Properties form with USDA values
            Button(
              onClick = {
                haptics.performSuccess()
                onApplyUsdaData(item.moisturePercent, item.fatPercent, item.description)
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .testTag("apply_usda_data_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Auto-Populate Form with USDA Data", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        is UsdaFetchState.Error -> {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFFEF2F2), RoundedCornerShape(12.dp))
              .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(12.dp))
              .padding(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "USDA Query Notice: ${state.message}",
                fontSize = 11.5.sp,
                color = Color(0xFFB91C1C),
                fontWeight = FontWeight.Medium
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
              onClick = { executeFetch(state.lastQuery ?: commodity.name) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
              Text("Retry USDA Search", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
