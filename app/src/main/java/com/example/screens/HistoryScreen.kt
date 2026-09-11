package com.example.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HistoryAssessmentItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
  historyItems: List<HistoryAssessmentItem>,
  onSelectHistoryItem: (HistoryAssessmentItem) -> Unit,
  onClearAll: () -> Unit,
  onOpenFeedback: (HistoryAssessmentItem) -> Unit,
  onStartNewRecommendation: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedFilter by remember { mutableStateOf("All") }
  var showClearConfirmDialog by remember { mutableStateOf(false) }

  val filterOptions = listOf("All", "Fruits", "Vegetables", "Recent")

  val filteredList = remember(historyItems, searchQuery, selectedFilter) {
    historyItems.filter { item ->
      val matchesSearch = item.commodityName.contains(searchQuery, ignoreCase = true) ||
          item.recommendedMaterial.contains(searchQuery, ignoreCase = true) ||
          item.date.contains(searchQuery, ignoreCase = true)

      val matchesFilter = when (selectedFilter) {
        "Fruits" -> item.category.equals("Fruits", ignoreCase = true)
        "Vegetables" -> item.category.equals("Vegetables", ignoreCase = true)
        "Recent" -> true // Handled by sorting if needed
        else -> true
      }
      matchesSearch && matchesFilter
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(AppBackground)
      .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    // Header Row: "Recommendation History" and "Clear All" action
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Recommendation History",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreenPrimary
        )
        Text(
          text = "${filteredList.size} evaluation record${if (filteredList.size == 1) "" else "s"}",
          fontSize = 13.sp,
          color = TextSecondary
        )
      }

      if (historyItems.isNotEmpty()) {
        TextButton(
          onClick = { showClearConfirmDialog = true },
          colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFDC2626)),
          modifier = Modifier.testTag("clear_all_history_button")
        ) {
          Icon(Icons.Outlined.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Clear All", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Search Field
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Filter by commodity or date...", color = TextMuted, fontSize = 13.sp) },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary, modifier = Modifier.size(20.dp)) },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(18.dp))
          }
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .shadow(2.dp, RoundedCornerShape(12.dp)),
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = MintLeafAccent,
        unfocusedBorderColor = CardBorder
      ),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Filter Chips Row: [All], [Fruits], [Vegetables], [Recent]
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(filterOptions, key = { it }) { filter ->
        val isSelected = selectedFilter == filter
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = if (isSelected) ForestGreenPrimary else Color.White,
          border = BorderStroke(1.dp, if (isSelected) ForestGreenPrimary else CardBorder),
          shadowElevation = if (isSelected) 2.dp else 0.dp,
          modifier = Modifier.clickable { selectedFilter = filter }
        ) {
          Text(
            text = filter,
            color = if (isSelected) Color.White else TextPrimary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // List or Empty State
    if (filteredList.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(32.dp)
        ) {
          Box(
            modifier = Modifier
              .size(80.dp)
              .background(PaleSageLight, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Outlined.HistoryEdu,
              contentDescription = null,
              tint = ForestGreenPrimary,
              modifier = Modifier.size(42.dp)
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = if (searchQuery.isNotEmpty()) "No results matching '$searchQuery'" else "No history yet",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Run an AI packaging recommendation to see past evaluation records here.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(18.dp))
          Button(
            onClick = onStartNewRecommendation,
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Start New Assessment", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.weight(1f)
      ) {
        items(filteredList, key = { it.id }) { item ->
          HistoryCardItem(
            item = item,
            onClick = { onSelectHistoryItem(item) },
            onRateFeedback = { onOpenFeedback(item) }
          )
        }
        item {
          Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom nav bar
        }
      }
    }
  }

  // Clear Confirmation Dialog
  if (showClearConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showClearConfirmDialog = false },
      title = { Text("Clear Recommendation History?", fontWeight = FontWeight.Bold, color = ForestGreenPrimary) },
      text = { Text("Are you sure you want to remove all saved packaging evaluation records? This action cannot be undone.", fontSize = 14.sp) },
      confirmButton = {
        Button(
          onClick = {
            onClearAll()
            showClearConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
        ) {
          Text("Clear All", color = Color.White)
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showClearConfirmDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun HistoryCardItem(
  item: HistoryAssessmentItem,
  onClick: () -> Unit,
  onRateFeedback: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, CardBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("history_card_${item.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top Row: Commodity Thumbnail, Title & Date, Material Badge, Trailing Arrow
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Thumbnail
        Box(
          modifier = Modifier
            .size(46.dp)
            .background(PaleSageLight, RoundedCornerShape(12.dp)),
          contentAlignment = Alignment.Center
        ) {
          Text(item.emoji, fontSize = 26.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Commodity Title & Date
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${item.commodityName} • ${item.date}",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary
          )
          Spacer(modifier = Modifier.height(3.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = PaleSageTint
          ) {
            Text(
              text = item.recommendedMaterial,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = ForestGreenPrimary,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        // Trailing arrow button `>` to reopen full results
        IconButton(
          onClick = onClick,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "View Details",
            tint = ForestGreenPrimary,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Metrics Row inside Card: Suitability Score chip ("92% Match"), Shelf Life ("16.4 Days"), Eco Score ("8.4/10")
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFF9FAFB), RoundedCornerShape(10.dp))
          .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Suitability Score Chip
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = MintLeafAccent
          ) {
            Text(
              "${item.suitabilityScore}%",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text("Match", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        }

        // Expected Shelf Life
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Outlined.Timer, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("${item.expectedShelfLifeDays} Days", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        // Eco Score
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Outlined.Eco, contentDescription = null, tint = MintLeafAccent, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("${item.ecoScore}/10 Eco", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        // Rate/Feedback Action
        IconButton(
          onClick = onRateFeedback,
          modifier = Modifier.size(24.dp)
        ) {
          Icon(Icons.Outlined.RateReview, contentDescription = "Give Feedback", tint = MintLeafAccent, modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}
