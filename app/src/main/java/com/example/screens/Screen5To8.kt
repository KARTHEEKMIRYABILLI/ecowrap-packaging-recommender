package com.example.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.CommodityGridShimmerLoading
import com.example.components.FoodCommodityVisual
import com.example.components.UnitConverterDialog
import com.example.components.UsdaSyncCard
import com.example.model.*
import com.example.ui.theme.*
import com.example.utils.UnitConverter
import com.example.utils.UnitSystem
import com.example.utils.rememberAppHaptics
import kotlin.math.roundToInt

// -------------------------------------------------------------
// SCREEN 5: SELECT FOOD COMMODITY
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommoditySelectionScreen(
  commodities: List<CommodityItem>,
  selectedCommodity: CommodityItem,
  onSelectCommodity: (CommodityItem) -> Unit,
  onNavigateBack: () -> Unit,
  onNavigateNext: () -> Unit
) {
  val haptics = rememberAppHaptics()
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("Fruits") }
  var isFetchingCategories by remember { mutableStateOf(false) }

  // Trigger shimmer loading animation while fetching food commodity categories
  LaunchedEffect(selectedCategory) {
    isFetchingCategories = true
    kotlinx.coroutines.delay(420)
    isFetchingCategories = false
  }

  // Brief shimmer animation when actively filtering
  LaunchedEffect(searchQuery) {
    if (searchQuery.isNotBlank()) {
      isFetchingCategories = true
      kotlinx.coroutines.delay(280)
      isFetchingCategories = false
    }
  }
  
  data class CategoryTabItem(
    val name: String,
    val icon: String,
    val color: Color,
    val bgColor: Color
  )

  val categoryTabs = listOf(
    CategoryTabItem("Fruits", "🍎", Color(0xFFE11D48), Color(0xFFFFF1F2)),
    CategoryTabItem("Vegetables", "🥦", Color(0xFF16A34A), Color(0xFFF0FDF4)),
    CategoryTabItem("Grains", "🌾", Color(0xFFD97706), Color(0xFFFFFBEB)),
    CategoryTabItem("Dairy", "🥛", Color(0xFF0284C7), Color(0xFFF0F9FF)),
    CategoryTabItem("Meat", "🍗", Color(0xFF9333EA), Color(0xFFFAF5FF))
  )

  val activeCategoryInfo = categoryTabs.firstOrNull { it.name.equals(selectedCategory, ignoreCase = true) }
    ?: categoryTabs[0]

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              "Select Food Commodity",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreenPrimary
            )
            Text(
              "Categorized Horticultural & Agri-Food Database",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("commodity_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ForestGreenPrimary)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground)
      )
    },
    bottomBar = {
      Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
          // Selected Item Quick Preview Bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(PaleSageLight, RoundedCornerShape(12.dp))
              .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, MintLeafAccent, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              FoodCommodityVisual(item = selectedCommodity, size = 24.dp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                selectedCommodity.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
              )
              Text(
                "${selectedCommodity.category} • Moisture: ${selectedCommodity.moistureContent}% • pH: ${selectedCommodity.phValue}",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
            Box(
              modifier = Modifier
                .background(MintLeafAccent, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text("SELECTED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              haptics.performFormSubmit()
              onNavigateNext()
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("commodity_next_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              "Continue with ${selectedCommodity.name}",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
              tint = Color.White
            )
          }
        }
      }
    },
    containerColor = AppBackground
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(6.dp))

      // Search Box
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search ${selectedCategory.lowercase()} by name...", color = TextMuted, fontSize = 13.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(18.dp))
            }
          }
        },
        modifier = Modifier.fillMaxWidth().testTag("commodity_search_input"),
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

      // Category Tabs with Icons and Product Counts: [Fruits], [Vegetables], [Grains], [Dairy], [Meat]
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().testTag("commodity_category_row")
      ) {
        items(categoryTabs, key = { it.name }) { catTab ->
          val isCatSelected = catTab.name.equals(selectedCategory, ignoreCase = true)
          val countInCat = commodities.count { it.category.equals(catTab.name, ignoreCase = true) }
          
          Surface(
            onClick = {
              haptics.performTick()
              selectedCategory = catTab.name
              // Auto-select first item of newly chosen category if current selection is not in it
              val firstInCat = commodities.firstOrNull { it.category.equals(catTab.name, ignoreCase = true) }
              if (firstInCat != null && !selectedCommodity.category.equals(catTab.name, ignoreCase = true)) {
                onSelectCommodity(firstInCat)
              }
            },
            shape = RoundedCornerShape(20.dp),
            color = if (isCatSelected) ForestGreenPrimary else Color.White,
            border = BorderStroke(1.dp, if (isCatSelected) ForestGreenPrimary else CardBorder),
            modifier = Modifier.testTag("commodity_category_tab_${catTab.name.lowercase()}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(catTab.icon, fontSize = 14.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                catTab.name,
                fontSize = 13.sp,
                fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isCatSelected) Color.White else TextPrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .background(
                    if (isCatSelected) Color.White.copy(alpha = 0.25f) else Color(0xFFF3F4F6),
                    CircleShape
                  )
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  "$countInCat",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCatSelected) Color.White else TextSecondary
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Category Header Banner
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = activeCategoryInfo.bgColor,
        border = BorderStroke(1.dp, activeCategoryInfo.color.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(activeCategoryInfo.icon, fontSize = 18.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            "Category: ${activeCategoryInfo.name} • Strictly displaying verified products with biochemical profiles",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = ForestGreenPrimary
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Memoize filtered commodities strictly by category or search query to avoid recalculation on each frame
      val filtered = remember(commodities, selectedCategory, searchQuery) {
        if (searchQuery.isNotBlank()) {
          val queryTrimmed = searchQuery.trim()
          val inCatMatches = commodities.filter {
            it.category.equals(selectedCategory, ignoreCase = true) && it.name.contains(queryTrimmed, ignoreCase = true)
          }
          if (inCatMatches.isNotEmpty()) {
            inCatMatches
          } else {
            commodities.filter { it.name.contains(queryTrimmed, ignoreCase = true) }
          }
        } else {
          commodities.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
      }

      if (isFetchingCategories) {
        CommodityGridShimmerLoading(
          count = 6,
          modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
        )
      } else if (filtered.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍", fontSize = 38.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("No commodities found", fontWeight = FontWeight.Bold, color = ForestGreenPrimary, fontSize = 15.sp)
            Text("Try another food name or switch category tab above", fontSize = 13.sp, color = TextSecondary)
          }
        }
      } else {
        LazyVerticalGrid(
          columns = GridCells.Adaptive(minSize = 100.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxSize().padding(bottom = 8.dp)
        ) {
          items(filtered, key = { it.id }) { item ->
            val isSelected = item.id == selectedCommodity.id
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isSelected) PaleSageLight else Color.White
              ),
              border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MintLeafAccent else CardBorder
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  haptics.performClick()
                  onSelectCommodity(item)
                }
                .testTag("commodity_item_${item.id}")
            ) {
              Box(
                modifier = Modifier.fillMaxSize()
              ) {
                // Active selection checkmark badge
                if (isSelected) {
                  Box(
                    modifier = Modifier
                      .align(Alignment.TopEnd)
                      .padding(8.dp)
                      .size(20.dp)
                      .background(MintLeafAccent, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      Icons.Default.Check,
                      contentDescription = "Selected",
                      tint = Color.White,
                      modifier = Modifier.size(13.dp)
                    )
                  }
                }

                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center,
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                ) {
                  // Visual Illustrated Product Frame (Soft Pastel Rounded Container)
                  Box(
                    modifier = Modifier
                      .size(56.dp)
                      .background(
                        if (isSelected) Color.White else activeCategoryInfo.bgColor,
                        CircleShape
                      )
                      .border(
                        1.dp,
                        if (isSelected) MintLeafAccent else activeCategoryInfo.color.copy(alpha = 0.25f),
                        CircleShape
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    FoodCommodityVisual(item = item, size = 36.dp)
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  Text(
                    text = item.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                  )

                  Text(
                    text = item.subtitle,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                  )

                  Spacer(modifier = Modifier.height(4.dp))

                  // Moisture & pH pill
                  Box(
                    modifier = Modifier
                      .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      "💧 ${item.moistureContent.toInt()}%",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Medium,
                      color = TextSecondary
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// SCREEN 6: DEFAULT PROPERTIES REVIEW ("Food Profile")
// -------------------------------------------------------------
@Composable
fun FoodProfileScreen(
  commodity: CommodityItem,
  onUpdateCommodity: (CommodityItem) -> Unit,
  onNavigateBack: () -> Unit,
  onNavigateNext: () -> Unit
) {
  val haptics = rememberAppHaptics()
  var showEditDialog by remember { mutableStateOf(false) }
  var showUnitConverter by remember { mutableStateOf(false) }

  if (showEditDialog) {
    EditPropertiesDialog(
      commodity = commodity,
      onDismiss = { showEditDialog = false },
      onSave = { updated ->
        onUpdateCommodity(updated)
        showEditDialog = false
      },
      onOpenUnitConverter = { showUnitConverter = true }
    )
  }

  if (showUnitConverter) {
    UnitConverterDialog(
      initialTemperatureC = 8.0,
      initialWeightKg = 1.0,
      onDismiss = { showUnitConverter = false }
    )
  }

  Scaffold(
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("food_profile_back")) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ForestGreenPrimary)
        }
        Text(
          "Food Properties & Profile",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreenPrimary
        )
        Spacer(modifier = Modifier.weight(1f))
        FilledTonalButton(
          onClick = { showUnitConverter = true },
          modifier = Modifier.testTag("food_profile_converter_btn"),
          colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = PaleSageTint,
            contentColor = ForestGreenPrimary
          ),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Icon(Icons.Default.Calculate, contentDescription = "Unit Converter", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Converter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    },
    bottomBar = {
      Surface(
        color = AppBackground,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
      ) {
        Button(
          onClick = {
            haptics.performFormSubmit()
            onNavigateNext()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("food_profile_next_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Proceed to Storage & Environment", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
        }
      }
    },
    containerColor = AppBackground
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 20.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // Top Item Badge: Commodity icon + Name & Classification
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(14.dp)
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .background(PaleSageLight, RoundedCornerShape(14.dp))
              .border(1.dp, MintLeafAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
          ) {
            FoodCommodityVisual(item = commodity, size = 42.dp)
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = commodity.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = PaleSageTint
              ) {
                Text(
                  commodity.category,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreenPrimary,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = commodity.subtitle,
              fontSize = 12.sp,
              color = TextSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // USDA FoodData Central Integration Card (Live Nutrient & Moisture Sync)
      UsdaSyncCard(
        commodity = commodity,
        onApplyUsdaData = { moisture, fat, itemDesc ->
          onUpdateCommodity(
            commodity.copy(
              moistureContent = moisture,
              fatContent = fat
            )
          )
        },
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Section Header: Required Technical Food Parameters
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Technical Food Properties",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreenPrimary
        )
        TextButton(
          onClick = { showEditDialog = true },
          modifier = Modifier.testTag("edit_properties_button")
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Edit Values", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Technical Properties Card
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // 1. Moisture Content
          PropertyItemRow(
            icon = Icons.Default.WaterDrop,
            name = "Moisture Content",
            value = "${commodity.moistureContent}%",
            subtext = if (commodity.moistureContent >= 80) "High Moisture (Risk of microbial spoilage / mold)" else "Low Moisture (Hygroscopic / Caking risk)",
            iconTint = Color(0xFF0284C7)
          )
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 10.dp))

          // 2. Oil / Fat Content
          PropertyItemRow(
            icon = Icons.Default.Opacity,
            name = "Oil / Fat Content",
            value = "${commodity.fatContent}%",
            subtext = if (commodity.fatContent >= 10) "High Lipid (Oxidation & Rancidity sensitive)" else "Low Lipid (Standard protection)",
            iconTint = Color(0xFFD97706)
          )
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 10.dp))

          // 3. pH Level
          PropertyItemRow(
            icon = Icons.Default.Science,
            name = "pH Level",
            value = "${commodity.phValue}",
            subtext = if (commodity.phValue < 4.6) "High Acid (pH < 4.6, Inherent microbial inhibition)" else "Low Acid (pH ≥ 4.6, Requires strict barrier/hygiene)",
            iconTint = Color(0xFF0D9488)
          )
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 10.dp))

          // 4. Respiration Rate
          PropertyItemRow(
            icon = Icons.Default.Air,
            name = "Respiration Rate",
            value = commodity.respirationRate,
            subtext = "Biological gas exchange rate: O₂ absorption & CO₂ release",
            iconTint = Color(0xFF16A34A)
          )
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 10.dp))

          // 5. Ethylene Sensitivity
          PropertyItemRow(
            icon = Icons.Default.Sensors,
            name = "Ethylene Sensitivity",
            value = commodity.ethyleneSensitivity,
            subtext = "Ripening & senescence response to ambient ethylene C₂H₄",
            iconTint = Color(0xFF7C3AED)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Technical Advisory Card
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PaleSageLight),
        border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            Icons.Default.Lightbulb,
            contentDescription = "Info",
            tint = ForestGreenPrimary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "AI uses these food chemical & physical parameters to calculate the exact transmission rates (OTR, WVTR) and Modified Atmosphere Packaging (MAP) flush requirements.",
            fontSize = 12.sp,
            color = ForestGreenPrimary,
            lineHeight = 17.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun PropertyItemRow(
  icon: ImageVector,
  name: String,
  value: String,
  subtext: String,
  iconTint: Color
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .background(iconTint.copy(alpha = 0.12f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = name,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )
        Text(
          text = subtext,
          fontSize = 10.sp,
          color = TextSecondary,
          lineHeight = 13.sp
        )
      }
    }
    Spacer(modifier = Modifier.width(8.dp))
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = PaleSageLight
    ) {
      Text(
        text = value,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = ForestGreenPrimary,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
      )
    }
  }
}

// -------------------------------------------------------------
// SCREEN 7: STORAGE & TRANSPORTATION INPUT
// -------------------------------------------------------------
@Composable
fun StorageConditionsScreen(
  storageConfig: StorageConfig,
  onUpdateConfig: (StorageConfig) -> Unit,
  onNavigateBack: () -> Unit,
  onNavigateNext: () -> Unit
) {
  val haptics = rememberAppHaptics()
  var unitSystem by remember { mutableStateOf(if (storageConfig.preferredUnitSystem.equals("Imperial", ignoreCase = true)) UnitSystem.IMPERIAL else UnitSystem.METRIC) }
  var storageType by remember { mutableStateOf(storageConfig.storageType) }
  var temp by remember { mutableFloatStateOf(storageConfig.temperatureC.toFloat()) }
  var packageWeightKg by remember { mutableFloatStateOf(storageConfig.packageWeightKg.toFloat()) }
  var humidity by remember { mutableFloatStateOf(storageConfig.relativeHumidityPercent.toFloat()) }
  var transport by remember { mutableStateOf(storageConfig.transportationType) }
  var shelfLife by remember { mutableIntStateOf(storageConfig.desiredShelfLifeDays) }
  var showUnitConverter by remember { mutableStateOf(false) }

  if (showUnitConverter) {
    UnitConverterDialog(
      initialTemperatureC = temp.toDouble(),
      initialWeightKg = packageWeightKg.toDouble(),
      onDismiss = { showUnitConverter = false },
      onApplyTemperature = { newTempC ->
        temp = newTempC.toFloat()
        if (temp <= -10f && storageType != StorageType.FROZEN) storageType = StorageType.FROZEN
        else if (temp > -10f && temp <= 10f && storageType != StorageType.CHILLED) storageType = StorageType.CHILLED
        else if (temp > 10f && storageType != StorageType.AMBIENT) storageType = StorageType.AMBIENT
      },
      onApplyWeight = { newWeightKg ->
        packageWeightKg = newWeightKg.toFloat()
      }
    )
  }

  Scaffold(
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("storage_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ForestGreenPrimary)
          }
          Text(
            "Storage & Logistics",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary
          )
          Spacer(modifier = Modifier.weight(1f))
          FilledTonalButton(
            onClick = { showUnitConverter = true },
            modifier = Modifier.testTag("storage_converter_btn"),
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = PaleSageTint,
              contentColor = ForestGreenPrimary
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Icon(Icons.Default.Calculate, contentDescription = "Unit Converter", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Converter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }

        // Global Measurement System Switcher
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            "Measurement System:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
          )
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = PaleSageLight,
            border = BorderStroke(1.dp, CardBorder)
          ) {
            Row(modifier = Modifier.padding(2.dp)) {
              Surface(
                onClick = { unitSystem = UnitSystem.METRIC },
                shape = RoundedCornerShape(18.dp),
                color = if (unitSystem == UnitSystem.METRIC) ForestGreenPrimary else Color.Transparent,
                modifier = Modifier.testTag("unit_toggle_metric")
              ) {
                Text(
                  "Metric (°C / kg)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (unitSystem == UnitSystem.METRIC) Color.White else ForestGreenPrimary,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
              Surface(
                onClick = { unitSystem = UnitSystem.IMPERIAL },
                shape = RoundedCornerShape(18.dp),
                color = if (unitSystem == UnitSystem.IMPERIAL) ForestGreenPrimary else Color.Transparent,
                modifier = Modifier.testTag("unit_toggle_imperial")
              ) {
                Text(
                  "Imperial (°F / lbs)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (unitSystem == UnitSystem.IMPERIAL) Color.White else ForestGreenPrimary,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }
          }
        }
      }
    },
    bottomBar = {
      Surface(
        color = AppBackground,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
      ) {
        Button(
          onClick = {
            haptics.performFormSubmit()
            onUpdateConfig(
              StorageConfig(
                temperatureC = temp.toDouble(),
                relativeHumidityPercent = humidity.toDouble(),
                transportationType = transport,
                desiredShelfLifeDays = shelfLife,
                storageType = storageType,
                packageWeightKg = packageWeightKg.toDouble(),
                preferredUnitSystem = unitSystem.label
              )
            )
            onNavigateNext()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("storage_next_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Proceed to Priorities", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
        }
      }
    },
    containerColor = AppBackground
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 20.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // 1. REQUIRED: STORAGE TYPE SELECTOR (Ambient, Chilled, or Frozen)
      Text(
        "1. Storage Type (Thermal Classification)",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = ForestGreenPrimary
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        "Select storage state to adapt barrier specifications",
        fontSize = 12.sp,
        color = TextSecondary
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StorageType.values().forEach { type ->
          val isSelected = storageType == type
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) PaleSageLight else Color.White
            ),
            border = BorderStroke(
              width = if (isSelected) 2.dp else 1.dp,
              color = if (isSelected) MintLeafAccent else CardBorder
            ),
            modifier = Modifier
              .weight(1f)
              .clickable {
                storageType = type
                // Auto-tune temperature preset for user convenience
                when (type) {
                  StorageType.AMBIENT -> temp = 22f
                  StorageType.CHILLED -> temp = 4f
                  StorageType.FROZEN -> temp = -18f
                }
              }
              .testTag("storage_type_${type.name.lowercase()}")
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(type.icon, fontSize = 24.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = type.label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ForestGreenPrimary else TextPrimary
              )
              val tempRangeStr = if (unitSystem == UnitSystem.METRIC) {
                type.tempRange
              } else {
                when (type) {
                  StorageType.AMBIENT -> "64°F to 77°F"
                  StorageType.CHILLED -> "32°F to 46°F"
                  StorageType.FROZEN -> "0°F to 14°F"
                }
              }
              Text(
                text = tempRangeStr,
                fontSize = 10.sp,
                color = if (isSelected) ForestGreenPrimary else TextSecondary,
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 2. Storage Temperature Slider with live classification & Metric/Imperial support
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  "Storage Temperature",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = PaleSageTint
                ) {
                  Text(
                    if (unitSystem == UnitSystem.METRIC) "°C" else "°F",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
              Text(
                when {
                  temp <= -10f -> "Frozen Storage State"
                  temp <= 8f -> "Cold Chain / Chilled State"
                  temp <= 25f -> "Ambient Room Temperature"
                  else -> "High Tropical Ambient"
                },
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = PaleSageTint
            ) {
              val formattedTemp = if (unitSystem == UnitSystem.METRIC) {
                "${temp.toInt()}°C (${UnitConverter.celsiusToFahrenheit(temp.toDouble()).roundToInt()}°F)"
              } else {
                "${UnitConverter.celsiusToFahrenheit(temp.toDouble()).roundToInt()}°F (${temp.toInt()}°C)"
              }
              Text(
                formattedTemp,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          if (unitSystem == UnitSystem.METRIC) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Default.AcUnit, contentDescription = "Cold", tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
              Slider(
                value = temp,
                onValueChange = {
                  temp = it
                  if (temp <= -10f && storageType != StorageType.FROZEN) storageType = StorageType.FROZEN
                  else if (temp > -10f && temp <= 10f && storageType != StorageType.CHILLED) storageType = StorageType.CHILLED
                  else if (temp > 10f && storageType != StorageType.AMBIENT) storageType = StorageType.AMBIENT
                },
                valueRange = -20f..35f,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp).testTag("temp_slider_celsius"),
                colors = SliderDefaults.colors(
                  thumbColor = MintLeafAccent,
                  activeTrackColor = MintLeafAccent,
                  inactiveTrackColor = PaleSageTint
                )
              )
              Icon(Icons.Default.WbSunny, contentDescription = "Hot", tint = Color(0xFFEA580C), modifier = Modifier.size(20.dp))
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("-20°C (Frozen)", fontSize = 11.sp, color = TextMuted)
              Text("0°C", fontSize = 11.sp, color = TextMuted)
              Text("35°C (Warm)", fontSize = 11.sp, color = TextMuted)
            }
          } else {
            // Imperial Slider (-4°F to 95°F)
            val currentFahrenheit = UnitConverter.celsiusToFahrenheit(temp.toDouble()).toFloat()
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Default.AcUnit, contentDescription = "Cold", tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
              Slider(
                value = currentFahrenheit.coerceIn(-4f, 95f),
                onValueChange = { fVal ->
                  val cVal = UnitConverter.fahrenheitToCelsius(fVal.toDouble()).toFloat()
                  temp = cVal
                  if (temp <= -10f && storageType != StorageType.FROZEN) storageType = StorageType.FROZEN
                  else if (temp > -10f && temp <= 10f && storageType != StorageType.CHILLED) storageType = StorageType.CHILLED
                  else if (temp > 10f && storageType != StorageType.AMBIENT) storageType = StorageType.AMBIENT
                },
                valueRange = -4f..95f,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp).testTag("temp_slider_fahrenheit"),
                colors = SliderDefaults.colors(
                  thumbColor = MintLeafAccent,
                  activeTrackColor = MintLeafAccent,
                  inactiveTrackColor = PaleSageTint
                )
              )
              Icon(Icons.Default.WbSunny, contentDescription = "Hot", tint = Color(0xFFEA580C), modifier = Modifier.size(20.dp))
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("-4°F (Frozen)", fontSize = 11.sp, color = TextMuted)
              Text("32°F (Freezing)", fontSize = 11.sp, color = TextMuted)
              Text("95°F (Warm)", fontSize = 11.sp, color = TextMuted)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Package & Unit Batch Weight Selector (with Metric / Imperial conversion)
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                "Target Package / Batch Weight",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                "Determines puncture resistance & volume load",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = PaleSageTint
            ) {
              Text(
                UnitConverter.formatWeightWithBoth(packageWeightKg.toDouble()),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Package Weight Preset Chips
          val weightPresets = listOf(
            Triple("250g", 0.25, "8.8 oz"),
            Triple("500g", 0.5, "1.1 lbs"),
            Triple("1 kg", 1.0, "2.2 lbs"),
            Triple("5 kg", 5.0, "11.0 lbs"),
            Triple("25 kg", 25.0, "55.1 lbs")
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            weightPresets.forEach { (label, kgVal, impLabel) ->
              val isSelected = (packageWeightKg - kgVal.toFloat()) in -0.01f..0.01f
              Surface(
                onClick = { packageWeightKg = kgVal.toFloat() },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) ForestGreenPrimary else PaleSageLight,
                border = BorderStroke(1.dp, if (isSelected) ForestGreenPrimary else CardBorder),
                modifier = Modifier.weight(1f)
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp)
                ) {
                  Text(
                    text = if (unitSystem == UnitSystem.METRIC) label else impLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else ForestGreenPrimary,
                    textAlign = TextAlign.Center
                  )
                  Text(
                    text = if (unitSystem == UnitSystem.METRIC) impLabel else label,
                    fontSize = 9.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextSecondary,
                    textAlign = TextAlign.Center
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4. Relative Humidity Slider (0% to 100%)
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                "Relative Humidity (RH)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                if (humidity >= 80f) "High RH (Anti-fog & condensation protection needed)" else "Controlled RH Environment",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = PaleSageTint
            ) {
              Text(
                "${humidity.toInt()}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.WaterDrop, contentDescription = "Dry", tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
            Slider(
              value = humidity,
              onValueChange = { humidity = it },
              valueRange = 0f..100f,
              modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
              colors = SliderDefaults.colors(
                thumbColor = MintLeafAccent,
                activeTrackColor = MintLeafAccent,
                inactiveTrackColor = PaleSageTint
              )
            )
            Icon(Icons.Default.Cloud, contentDescription = "Humid", tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("0% (Dry)", fontSize = 11.sp, color = TextMuted)
            Text("50% (Standard)", fontSize = 11.sp, color = TextMuted)
            Text("100% (Saturated)", fontSize = 11.sp, color = TextMuted)
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 5. Transportation Conditions Selector
      Text(
        "Transportation & Logistics Conditions",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = ForestGreenPrimary
      )

      Spacer(modifier = Modifier.height(10.dp))

      val transportTypes = listOf(
        "Normal / Ambient Transit",
        "Refrigerated Van",
        "Controlled Atmosphere (CA)",
        "Cold Chain Reefer",
        "Export Sea Container"
      )

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        transportTypes.forEach { tType ->
          TransportOptionCard(
            title = tType,
            isSelected = transport.equals(tType, ignoreCase = true),
            modifier = Modifier.fillMaxWidth(),
            onSelect = { transport = tType }
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 6. Desired Shelf Life Counter & Quick Jump Chips
      Text(
        "Desired Target Shelf Life",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = ForestGreenPrimary
      )

      Spacer(modifier = Modifier.height(10.dp))

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            FilledIconButton(
              onClick = { if (shelfLife > 1) shelfLife-- },
              modifier = Modifier.size(44.dp),
              colors = IconButtonDefaults.filledIconButtonColors(containerColor = PaleSageTint),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = ForestGreenPrimary)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "$shelfLife Days",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
              )
              Text(
                text = "approx. ${(shelfLife / 7.0 * 10).roundToInt() / 10.0} Weeks",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }

            FilledIconButton(
              onClick = { shelfLife++ },
              modifier = Modifier.size(44.dp),
              colors = IconButtonDefaults.filledIconButtonColors(containerColor = PaleSageTint),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = "Increase", tint = ForestGreenPrimary)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Quick Preset Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(7, 14, 30, 60, 180).forEach { days ->
              Surface(
                onClick = { shelfLife = days },
                shape = RoundedCornerShape(8.dp),
                color = if (shelfLife == days) ForestGreenPrimary else PaleSageLight,
                border = BorderStroke(1.dp, if (shelfLife == days) ForestGreenPrimary else CardBorder),
                modifier = Modifier.weight(1f)
              ) {
                Text(
                  text = "${days}d",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (shelfLife == days) Color.White else ForestGreenPrimary,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(vertical = 6.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}


@Composable
private fun TransportOptionCard(
  title: String,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onSelect: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) PaleSageLight else Color.White
    ),
    border = BorderStroke(
      width = if (isSelected) 1.5.dp else 1.dp,
      color = if (isSelected) MintLeafAccent else CardBorder
    ),
    modifier = modifier.clickable(onClick = onSelect)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(20.dp)
          .border(
            width = 1.5.dp,
            color = if (isSelected) MintLeafAccent else Color(0xFFD1D5DB),
            shape = CircleShape
          )
          .background(if (isSelected) MintLeafAccent else Color.Transparent, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        if (isSelected) {
          Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
      }
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = if (isSelected) ForestGreenPrimary else TextPrimary
      )
    }
  }
}

// -------------------------------------------------------------
// SCREEN 8: OPTIMIZATION PRIORITIES & FORMAT
// -------------------------------------------------------------
@Composable
fun PackagingRequirementsScreen(
  selectedPriority: PriorityType,
  onSelectPriority: (PriorityType) -> Unit,
  selectedFormat: PackagingFormat,
  onSelectFormat: (PackagingFormat) -> Unit,
  onNavigateBack: () -> Unit,
  onNavigateNext: () -> Unit
) {
  val haptics = rememberAppHaptics()
  Scaffold(
    topBar = {
      IconButton(onClick = onNavigateBack, modifier = Modifier.padding(8.dp).testTag("priorities_back_button")) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ForestGreenPrimary)
      }
    },
    bottomBar = {
      Surface(
        color = AppBackground,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
      ) {
        Button(
          onClick = {
            haptics.performFormSubmit()
            onNavigateNext()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("priorities_next_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Next", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    },
    containerColor = AppBackground
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 20.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Text(
        "Your Priorities",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = ForestGreenPrimary
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        "What matters most to you?",
        fontSize = 14.sp,
        color = TextSecondary
      )

      Spacer(modifier = Modifier.height(20.dp))

      // 3 Priority Cards: Low Cost, Eco Friendly (selected), Max Shelf Life
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        PriorityCard(
          priority = PriorityType.LOW_COST,
          isSelected = selectedPriority == PriorityType.LOW_COST,
          emoji = "₹",
          modifier = Modifier.weight(1f),
          onSelect = { onSelectPriority(PriorityType.LOW_COST) }
        )
        PriorityCard(
          priority = PriorityType.ECO_FRIENDLY,
          isSelected = selectedPriority == PriorityType.ECO_FRIENDLY,
          emoji = "🍃",
          modifier = Modifier.weight(1f),
          onSelect = { onSelectPriority(PriorityType.ECO_FRIENDLY) }
        )
        PriorityCard(
          priority = PriorityType.MAX_SHELF_LIFE,
          isSelected = selectedPriority == PriorityType.MAX_SHELF_LIFE,
          emoji = "📈",
          modifier = Modifier.weight(1f),
          onSelect = { onSelectPriority(PriorityType.MAX_SHELF_LIFE) }
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Preferred Packaging Type Vertical List
      Text(
        "Preferred Packaging Type",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = ForestGreenPrimary
      )

      Spacer(modifier = Modifier.height(12.dp))

      PackagingFormat.entries.forEach { format ->
        val isSelected = format == selectedFormat
        PackagingTypeCard(
          format = format,
          isSelected = isSelected,
          onSelect = { onSelectFormat(format) }
        )
        Spacer(modifier = Modifier.height(10.dp))
      }
    }
  }
}

@Composable
private fun PriorityCard(
  priority: PriorityType,
  isSelected: Boolean,
  emoji: String,
  modifier: Modifier = Modifier,
  onSelect: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) PaleSageLight else Color.White
    ),
    border = BorderStroke(
      width = if (isSelected) 2.dp else 1.dp,
      color = if (isSelected) MintLeafAccent else CardBorder
    ),
    modifier = modifier
      .height(105.dp)
      .clickable(onClick = onSelect)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .background(
            if (isSelected) PaleSageTint else Color(0xFFF3F4F6),
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(text = emoji, fontSize = 18.sp)
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = priority.title,
        fontSize = 12.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = if (isSelected) ForestGreenPrimary else TextPrimary,
        textAlign = TextAlign.Center,
        lineHeight = 14.sp
      )
    }
  }
}

@Composable
private fun PackagingTypeCard(
  format: PackagingFormat,
  isSelected: Boolean,
  onSelect: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) PaleSageLight else Color.White
    ),
    border = BorderStroke(
      width = if (isSelected) 1.5.dp else 1.dp,
      color = if (isSelected) MintLeafAccent else CardBorder
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onSelect)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(22.dp)
          .border(
            width = 1.5.dp,
            color = if (isSelected) MintLeafAccent else Color(0xFFD1D5DB),
            shape = CircleShape
          )
          .background(if (isSelected) MintLeafAccent else Color.Transparent, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        if (isSelected) {
          Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Text(
        text = format.title,
        fontSize = 15.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = if (isSelected) ForestGreenPrimary else TextPrimary
      )
    }
  }
}

@Composable
fun EditPropertiesDialog(
  commodity: CommodityItem,
  onDismiss: () -> Unit,
  onSave: (CommodityItem) -> Unit,
  onOpenUnitConverter: (() -> Unit)? = null
) {
  val haptics = rememberAppHaptics()
  var moistureText by remember { mutableStateOf(commodity.moistureContent.toString()) }
  var fatText by remember { mutableStateOf(commodity.fatContent.toString()) }
  var phText by remember { mutableStateOf(commodity.phValue.toString()) }
  var respiration by remember { mutableStateOf(commodity.respirationRate) }
  var ethylene by remember { mutableStateOf(commodity.ethyleneSensitivity) }

  // Validation Logic for Moisture Content (0% to 100%)
  val moistureVal = moistureText.toDoubleOrNull()
  val moistureError = when {
    moistureText.isBlank() -> "Moisture content is required."
    moistureVal == null -> "Please enter a valid numeric value."
    moistureVal < 0.0 || moistureVal > 100.0 -> "Moisture content must be a percentage between 0% and 100%."
    else -> null
  }

  // Validation Logic for Oil / Fat Content (0% to 100%)
  val fatVal = fatText.toDoubleOrNull()
  val fatError = when {
    fatText.isBlank() -> "Oil/Fat content is required."
    fatVal == null -> "Please enter a valid numeric value."
    fatVal < 0.0 || fatVal > 100.0 -> "Oil/Fat content must be a percentage between 0% and 100%."
    else -> null
  }

  // Validation Logic for pH Level (0.0 to 14.0)
  val phVal = phText.toDoubleOrNull()
  val phError = when {
    phText.isBlank() -> "pH level is required."
    phVal == null -> "Please enter a valid numeric value."
    phVal < 0.0 || phVal > 14.0 -> "pH level must be between 0.0 and 14.0."
    else -> null
  }

  val isFormValid = moistureError == null && fatError == null && phError == null &&
      moistureVal != null && fatVal != null && phVal != null

  val respirationOptions = listOf("Very Low", "Low", "Moderate", "High", "Very High")
  val ethyleneOptions = listOf("Very Low", "Low", "Moderate", "High")

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    containerColor = Color.White,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .background(PaleSageTint, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Tune, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text("Edit Food Properties", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = ForestGreenPrimary)
          Text("${commodity.name} (${commodity.category})", fontSize = 11.sp, color = TextSecondary)
        }
        if (onOpenUnitConverter != null) {
          IconButton(
            onClick = onOpenUnitConverter,
            modifier = Modifier.testTag("dialog_converter_btn")
          ) {
            Icon(Icons.Default.Calculate, contentDescription = "Unit Converter", tint = ForestGreenPrimary)
          }
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Validation Warning Banner (if any errors exist)
        if (!isFormValid) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFEF2F2),
            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.ErrorOutline,
                contentDescription = "Validation Error",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Please resolve the highlighted field errors below to apply changes.",
                color = Color(0xFFB91C1C),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 15.sp
              )
            }
          }
        }

        // USDA FoodData Central Sync & Auto-Population Widget
        UsdaSyncCard(
          commodity = commodity,
          compact = true,
          onApplyUsdaData = { moisture, fat, _ ->
            moistureText = moisture.toString()
            fatText = fat.toString()
          }
        )

        // 1. MOISTURE CONTENT FIELD & SLIDER (0% - 100%)
        Column {
          OutlinedTextField(
            value = moistureText,
            onValueChange = { input ->
              moistureText = input
            },
            label = { Text("Moisture Content (%)", fontSize = 12.sp) },
            placeholder = { Text("e.g. 88.5", color = TextMuted) },
            trailingIcon = {
              if (moistureError != null) {
                Icon(Icons.Default.Error, contentDescription = "Error", tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
              } else {
                Text("%", fontWeight = FontWeight.Bold, color = ForestGreenPrimary, modifier = Modifier.padding(end = 12.dp))
              }
            },
            isError = moistureError != null,
            supportingText = {
              if (moistureError != null) {
                Text(
                  text = "⚠️ $moistureError",
                  color = Color(0xFFDC2626),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              } else {
                Text(
                  text = "Valid percentage range: 0.0% to 100.0%",
                  color = TextSecondary,
                  fontSize = 10.5.sp
                )
              }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("moisture_input_field"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = if (moistureError != null) Color(0xFFDC2626) else MintLeafAccent,
              unfocusedBorderColor = if (moistureError != null) Color(0xFFEF4444) else CardBorder,
              errorBorderColor = Color(0xFFDC2626),
              errorContainerColor = Color(0xFFFEF2F2)
            )
          )

          // Synchronized Slider
          if (moistureVal != null && moistureVal in 0.0..100.0) {
            Slider(
              value = moistureVal.toFloat(),
              onValueChange = { newSliderVal ->
                moistureText = ((newSliderVal * 10).roundToInt() / 10.0).toString()
              },
              valueRange = 0f..100f,
              modifier = Modifier.padding(horizontal = 4.dp),
              colors = SliderDefaults.colors(
                thumbColor = MintLeafAccent,
                activeTrackColor = MintLeafAccent,
                inactiveTrackColor = PaleSageTint
              )
            )
          }
        }

        // 2. OIL / FAT CONTENT FIELD & SLIDER (0% - 100%)
        Column {
          OutlinedTextField(
            value = fatText,
            onValueChange = { input ->
              fatText = input
            },
            label = { Text("Oil / Fat Content (%)", fontSize = 12.sp) },
            placeholder = { Text("e.g. 0.2", color = TextMuted) },
            trailingIcon = {
              if (fatError != null) {
                Icon(Icons.Default.Error, contentDescription = "Error", tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
              } else {
                Text("%", fontWeight = FontWeight.Bold, color = ForestGreenPrimary, modifier = Modifier.padding(end = 12.dp))
              }
            },
            isError = fatError != null,
            supportingText = {
              if (fatError != null) {
                Text(
                  text = "⚠️ $fatError",
                  color = Color(0xFFDC2626),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              } else {
                Text(
                  text = "Valid percentage range: 0.0% to 100.0%",
                  color = TextSecondary,
                  fontSize = 10.5.sp
                )
              }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("fat_input_field"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = if (fatError != null) Color(0xFFDC2626) else MintLeafAccent,
              unfocusedBorderColor = if (fatError != null) Color(0xFFEF4444) else CardBorder,
              errorBorderColor = Color(0xFFDC2626),
              errorContainerColor = Color(0xFFFEF2F2)
            )
          )

          // Synchronized Slider
          if (fatVal != null && fatVal in 0.0..100.0) {
            Slider(
              value = fatVal.toFloat(),
              onValueChange = { newSliderVal ->
                fatText = ((newSliderVal * 10).roundToInt() / 10.0).toString()
              },
              valueRange = 0f..100f,
              modifier = Modifier.padding(horizontal = 4.dp),
              colors = SliderDefaults.colors(
                thumbColor = MintLeafAccent,
                activeTrackColor = MintLeafAccent,
                inactiveTrackColor = PaleSageTint
              )
            )
          }
        }

        // 3. pH LEVEL FIELD & SLIDER (0.0 - 14.0)
        Column {
          OutlinedTextField(
            value = phText,
            onValueChange = { input ->
              phText = input
            },
            label = { Text("pH Level (Acidity scale)", fontSize = 12.sp) },
            placeholder = { Text("e.g. 4.3", color = TextMuted) },
            trailingIcon = {
              if (phError != null) {
                Icon(Icons.Default.Error, contentDescription = "Error", tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
              } else {
                Text("pH", fontWeight = FontWeight.Bold, color = ForestGreenPrimary, modifier = Modifier.padding(end = 12.dp))
              }
            },
            isError = phError != null,
            supportingText = {
              if (phError != null) {
                Text(
                  text = "⚠️ $phError",
                  color = Color(0xFFDC2626),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              } else {
                Text(
                  text = "Scale: 0.0 (High Acid) to 14.0 (Alkaline)",
                  color = TextSecondary,
                  fontSize = 10.5.sp
                )
              }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("ph_input_field"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = if (phError != null) Color(0xFFDC2626) else MintLeafAccent,
              unfocusedBorderColor = if (phError != null) Color(0xFFEF4444) else CardBorder,
              errorBorderColor = Color(0xFFDC2626),
              errorContainerColor = Color(0xFFFEF2F2)
            )
          )

          // Synchronized Slider
          if (phVal != null && phVal in 0.0..14.0) {
            Slider(
              value = phVal.toFloat(),
              onValueChange = { newSliderVal ->
                phText = ((newSliderVal * 10).roundToInt() / 10.0).toString()
              },
              valueRange = 0.0f..14.0f,
              modifier = Modifier.padding(horizontal = 4.dp),
              colors = SliderDefaults.colors(
                thumbColor = MintLeafAccent,
                activeTrackColor = MintLeafAccent,
                inactiveTrackColor = PaleSageTint
              )
            )
          }
        }

        // 4. Respiration Rate Selector Chips
        Column {
          Text("Produce Respiration Rate", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            respirationOptions.forEach { opt ->
              Surface(
                onClick = { respiration = opt },
                shape = RoundedCornerShape(8.dp),
                color = if (respiration.equals(opt, true)) ForestGreenPrimary else PaleSageLight,
                border = BorderStroke(1.dp, if (respiration.equals(opt, true)) ForestGreenPrimary else CardBorder),
                modifier = Modifier.weight(1f)
              ) {
                Text(
                  text = opt,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (respiration.equals(opt, true)) Color.White else ForestGreenPrimary,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp)
                )
              }
            }
          }
        }

        // 5. Ethylene Sensitivity Selector Chips
        Column {
          Text("Ethylene Sensitivity", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            ethyleneOptions.forEach { opt ->
              Surface(
                onClick = { ethylene = opt },
                shape = RoundedCornerShape(8.dp),
                color = if (ethylene.equals(opt, true)) ForestGreenPrimary else PaleSageLight,
                border = BorderStroke(1.dp, if (ethylene.equals(opt, true)) ForestGreenPrimary else CardBorder),
                modifier = Modifier.weight(1f)
              ) {
                Text(
                  text = opt,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (ethylene.equals(opt, true)) Color.White else ForestGreenPrimary,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(vertical = 6.dp)
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (isFormValid && moistureVal != null && fatVal != null && phVal != null) {
            haptics.performSuccess()
            onSave(
              commodity.copy(
                moistureContent = ((moistureVal * 10).roundToInt() / 10.0).coerceIn(0.0, 100.0),
                phValue = ((phVal * 10).roundToInt() / 10.0).coerceIn(0.0, 14.0),
                fatContent = ((fatVal * 10).roundToInt() / 10.0).coerceIn(0.0, 100.0),
                respirationRate = respiration,
                ethyleneSensitivity = ethylene
              )
            )
          } else {
            haptics.performError()
          }
        },
        enabled = isFormValid,
        colors = ButtonDefaults.buttonColors(
          containerColor = ForestGreenPrimary,
          disabledContainerColor = Color(0xFFE5E7EB),
          disabledContentColor = Color(0xFF9CA3AF)
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("apply_properties_button")
      ) {
        Text("Apply Changes", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}

