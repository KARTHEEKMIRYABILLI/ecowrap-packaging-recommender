package com.example.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PackagingEngine
import com.example.model.*
import com.example.ui.theme.*
import com.example.utils.rememberAppHaptics

/**
 * Side-by-Side Packaging Material Comparison Screen
 * Allows users to compare any two packaging materials across:
 * - OTR (Oxygen Transmission Rate) & Gas Permeability
 * - WVTR (Water Vapor Transmission Rate) & Moisture Protection
 * - Sustainability Metrics (EcoScore, Recyclability, Biodegradability, Carbon Impact)
 * - Commercial & Physical Performance (Shelf Life, Cost/kg, Tensile Strength, MAP Suitability)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialComparisonScreen(
  commodity: CommodityItem,
  storage: StorageConfig,
  priority: PriorityType = PriorityType.ECO_FRIENDLY,
  format: PackagingFormat = PackagingFormat.FLEXIBLE_FILM,
  initialMaterialA: String? = null,
  initialMaterialB: String? = null,
  onSelectMaterialForAssessment: (PackagingMaterial) -> Unit = {},
  onNavigateBack: () -> Unit = {}
) {
  val haptics = rememberAppHaptics()

  val availableMaterials = remember {
    listOf(
      "Breathable / Micro-Perforated Film",
      "Biodegradable Film (PLA / PBAT)",
      "LDPE (Low-Density Polyethylene)",
      "HDPE (High-Density Polyethylene)",
      "PET (Polyethylene Terephthalate)",
      "Metalized Film (Met-PET / Met-BOPP)",
      "Aluminum Foil Laminate"
    )
  }

  var materialAName by remember {
    mutableStateOf(
      initialMaterialA ?: availableMaterials[0]
    )
  }

  var materialBName by remember {
    mutableStateOf(
      initialMaterialB ?: availableMaterials[1]
    )
  }

  var showMaterialADropdown by remember { mutableStateOf(false) }
  var showMaterialBDropdown by remember { mutableStateOf(false) }
  var selectedTabFilter by remember { mutableStateOf(0) } // 0: All, 1: Barrier (OTR/WVTR), 2: Sustainability, 3: Specs & Cost
  var swapRotationAngle by remember { mutableStateOf(0f) }

  // Build reactive material profiles based on active commodity & storage conditions
  val materialA = remember(materialAName, commodity, storage, priority, format) {
    PackagingEngine.buildMaterialProfile(
      name = materialAName,
      score = 90,
      commodity = commodity,
      storage = storage,
      priority = priority,
      format = format
    )
  }

  val materialB = remember(materialBName, commodity, storage, priority, format) {
    PackagingEngine.buildMaterialProfile(
      name = materialBName,
      score = 85,
      commodity = commodity,
      storage = storage,
      priority = priority,
      format = format
    )
  }

  val swapMaterials = {
    haptics.performTick()
    swapRotationAngle += 180f
    val temp = materialAName
    materialAName = materialBName
    materialBName = temp
  }

  val animatedRotation by animateFloatAsState(
    targetValue = swapRotationAngle,
    animationSpec = tween(durationMillis = 350),
    label = "swapRotation"
  )

  Scaffold(
    topBar = {
      Surface(
        color = Color.White,
        shadowElevation = 2.dp
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            IconButton(
              onClick = onNavigateBack,
              modifier = Modifier.testTag("material_comparison_back_btn")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = ForestGreenPrimary
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "Material Comparison",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
              )
              Text(
                text = "OTR • WVTR • Sustainability Benchmarking",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }

            IconButton(
              onClick = swapMaterials,
              modifier = Modifier.testTag("swap_materials_top_btn")
            ) {
              Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Swap",
                tint = ForestGreenPrimary,
                modifier = Modifier.rotate(animatedRotation)
              )
            }
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
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      // 1. Commodity Context Header Banner
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = PaleSageLight,
        border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(commodity.emoji, fontSize = 26.sp)
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Evaluating for ${commodity.name} (${commodity.category})",
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreenPrimary
            )
            Text(
              text = "Moisture: ${commodity.moistureContent}% • Temp: ${storage.temperatureC}°C • Target: ${storage.desiredShelfLifeDays} Days",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Preset Pair Quick Jump Chips
      Text(
        text = "Quick Comparison Presets",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondary
      )
      Spacer(modifier = Modifier.height(6.dp))
      val presets = listOf(
        Pair("Breathable vs Bio-Film", Pair("Breathable / Micro-Perforated Film", "Biodegradable Film (PLA / PBAT)")),
        Pair("LDPE vs HDPE", Pair("LDPE (Low-Density Polyethylene)", "HDPE (High-Density Polyethylene)")),
        Pair("PET vs Aluminum Foil", Pair("PET (Polyethylene Terephthalate)", "Aluminum Foil Laminate")),
        Pair("Bio-Film vs Metalized", Pair("Biodegradable Film (PLA / PBAT)", "Metalized Film (Met-PET / Met-BOPP)"))
      )
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(presets) { preset ->
          val isSelected = (materialAName == preset.second.first && materialBName == preset.second.second) ||
              (materialAName == preset.second.second && materialBName == preset.second.first)
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) ForestGreenPrimary else Color.White,
            border = BorderStroke(1.dp, if (isSelected) ForestGreenPrimary else CardBorder),
            modifier = Modifier.clickable {
              haptics.performTick()
              materialAName = preset.second.first
              materialBName = preset.second.second
            }
          ) {
            Text(
              text = preset.first,
              fontSize = 11.5.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else TextPrimary,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Side-by-Side Material Selectors (Material A vs Material B)
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Material A Selector Box
        MaterialSelectorCard(
          materialName = materialAName,
          tag = "Material A",
          badgeColor = Color(0xFF047857), // Emerald Green
          accentColor = PaleSageTint,
          availableMaterials = availableMaterials,
          onSelect = { materialAName = it },
          modifier = Modifier.weight(1f),
          isExpanded = showMaterialADropdown,
          onToggleExpand = { showMaterialADropdown = !showMaterialADropdown }
        )

        // Center Swap / VS Badge
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(horizontal = 6.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 3.dp,
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier
              .size(36.dp)
              .clickable { swapMaterials() }
              .testTag("swap_materials_center_btn")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Swap",
                tint = ForestGreenPrimary,
                modifier = Modifier
                  .size(20.dp)
                  .rotate(animatedRotation)
              )
            }
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "VS",
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextSecondary
          )
        }

        // Material B Selector Box
        MaterialSelectorCard(
          materialName = materialBName,
          tag = "Material B",
          badgeColor = Color(0xFF0284C7), // Sky Blue
          accentColor = Color(0xFFE0F2FE),
          availableMaterials = availableMaterials,
          onSelect = { materialBName = it },
          modifier = Modifier.weight(1f),
          isExpanded = showMaterialBDropdown,
          onToggleExpand = { showMaterialBDropdown = !showMaterialBDropdown }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4. Quick Winner Strengths Matrix Bar
      QuickWinnersMatrixCard(
        materialA = materialA,
        materialB = materialB
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 5. Category Tab Filter
      val tabTitles = listOf("All Metrics", "OTR & WVTR", "Sustainability", "Specs & Cost")
      TabRow(
        selectedTabIndex = selectedTabFilter,
        containerColor = Color.White,
        contentColor = ForestGreenPrimary,
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
      ) {
        tabTitles.forEachIndexed { index, title ->
          Tab(
            selected = selectedTabFilter == index,
            onClick = {
              haptics.performTick()
              selectedTabFilter = index
            },
            text = {
              Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = if (selectedTabFilter == index) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 6. Detailed Comparison Content Blocks
      when (selectedTabFilter) {
        0 -> { // All Metrics
          OtrComparisonSection(materialA, materialB, commodity)
          Spacer(modifier = Modifier.height(16.dp))
          WvtrComparisonSection(materialA, materialB, commodity)
          Spacer(modifier = Modifier.height(16.dp))
          SustainabilityComparisonSection(materialA, materialB)
          Spacer(modifier = Modifier.height(16.dp))
          PhysicalAndCostComparisonSection(materialA, materialB, storage)
        }
        1 -> { // OTR & WVTR Gas/Moisture Barrier
          OtrComparisonSection(materialA, materialB, commodity)
          Spacer(modifier = Modifier.height(16.dp))
          WvtrComparisonSection(materialA, materialB, commodity)
        }
        2 -> { // Sustainability
          SustainabilityComparisonSection(materialA, materialB)
        }
        3 -> { // Specs & Cost
          PhysicalAndCostComparisonSection(materialA, materialB, storage)
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 7. Comparative AI Verdict Card
      ComparisonVerdictCard(
        materialA = materialA,
        materialB = materialB,
        commodity = commodity,
        storage = storage
      )

      Spacer(modifier = Modifier.height(20.dp))

      // 8. Action Buttons to Apply Material to Current Assessment
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = {
            haptics.performSuccess()
            onSelectMaterialForAssessment(materialA)
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("apply_material_a_btn")
        ) {
          Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Use Material A",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
          )
        }

        Button(
          onClick = {
            haptics.performSuccess()
            onSelectMaterialForAssessment(materialB)
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("apply_material_b_btn")
        ) {
          Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Use Material B",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
          )
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

// -------------------------------------------------------------
// SUB-COMPONENTS
// -------------------------------------------------------------

@Composable
private fun MaterialSelectorCard(
  materialName: String,
  tag: String,
  badgeColor: Color,
  accentColor: Color,
  availableMaterials: List<String>,
  onSelect: (String) -> Unit,
  modifier: Modifier = Modifier,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit
) {
  val haptics = rememberAppHaptics()

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.5.dp, badgeColor.copy(alpha = 0.4f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = accentColor
        ) {
          Text(
            text = tag,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = badgeColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Icon(
          imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
          contentDescription = "Select",
          tint = badgeColor,
          modifier = Modifier
            .size(20.dp)
            .clickable { onToggleExpand() }
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = materialName,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        maxLines = 2,
        minLines = 2,
        lineHeight = 16.sp,
        modifier = Modifier.clickable { onToggleExpand() }
      )

      DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onToggleExpand,
        modifier = Modifier
          .widthIn(max = 240.dp)
          .background(Color.White)
      ) {
        availableMaterials.forEach { mat ->
          val isSelected = mat == materialName
          DropdownMenuItem(
            text = {
              Text(
                text = mat,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) badgeColor else TextPrimary
              )
            },
            onClick = {
              haptics.performTick()
              onSelect(mat)
              onToggleExpand()
            },
            leadingIcon = {
              if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = badgeColor, modifier = Modifier.size(16.dp))
              }
            }
          )
        }
      }
    }
  }
}

@Composable
private fun QuickWinnersMatrixCard(
  materialA: PackagingMaterial,
  materialB: PackagingMaterial
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, CardBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Relative Strengths & Category Leaders",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreenPrimary
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Breathability Leader
        val isAWinnerOtr = parseOtrNumeric(materialA.technicalSpecs.otrValue) > parseOtrNumeric(materialB.technicalSpecs.otrValue)
        WinnerChip(
          title = "Gas Permeability",
          winner = if (isAWinnerOtr) "Material A" else "Material B",
          color = if (isAWinnerOtr) Color(0xFF047857) else Color(0xFF0284C7),
          icon = "💨",
          modifier = Modifier.weight(1f)
        )

        // Moisture Barrier Leader
        val isAWinnerWvtr = parseWvtrNumeric(materialA.technicalSpecs.wvtrValue) < parseWvtrNumeric(materialB.technicalSpecs.wvtrValue)
        WinnerChip(
          title = "Moisture Seal",
          winner = if (isAWinnerWvtr) "Material A" else "Material B",
          color = if (isAWinnerWvtr) Color(0xFF047857) else Color(0xFF0284C7),
          icon = "💧",
          modifier = Modifier.weight(1f)
        )

        // Sustainability Leader
        val isAWinnerEco = materialA.ecoScore >= materialB.ecoScore
        WinnerChip(
          title = "Sustainability",
          winner = if (isAWinnerEco) "Material A" else "Material B",
          color = if (isAWinnerEco) Color(0xFF047857) else Color(0xFF0284C7),
          icon = "🌿",
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun WinnerChip(
  title: String,
  winner: String,
  color: Color,
  icon: String,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = color.copy(alpha = 0.08f),
    border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(icon, fontSize = 16.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = title,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary,
        textAlign = TextAlign.Center,
        maxLines = 1
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = winner,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        textAlign = TextAlign.Center
      )
    }
  }
}

// -------------------------------------------------------------
// SECTION 1: OTR (Oxygen Transmission Rate)
// -------------------------------------------------------------
@Composable
private fun OtrComparisonSection(
  materialA: PackagingMaterial,
  materialB: PackagingMaterial,
  commodity: CommodityItem
) {
  ComparisonSectionCard(
    title = "Oxygen Transmission Rate (OTR)",
    subtitle = "cc / m² · day · atm (ASTM D3985 standard)",
    icon = Icons.Outlined.Air,
    headerColor = Color(0xFF059669)
  ) {
    // Side by Side Metric Display
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Metric A
      SideValueBlock(
        label = "Material A (OTR)",
        value = materialA.technicalSpecs.otrValue,
        description = materialA.technicalSpecs.otrDescription,
        badge = if (materialA.name.contains("Breathable", true)) "High Permeability" else if (materialA.name.contains("Foil", true)) "Zero Hermetic" else "Controlled Barrier",
        color = Color(0xFF047857),
        modifier = Modifier.weight(1f)
      )

      // Metric B
      SideValueBlock(
        label = "Material B (OTR)",
        value = materialB.technicalSpecs.otrValue,
        description = materialB.technicalSpecs.otrDescription,
        badge = if (materialB.name.contains("Breathable", true)) "High Permeability" else if (materialB.name.contains("Foil", true)) "Zero Hermetic" else "Controlled Barrier",
        color = Color(0xFF0284C7),
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // CO2 transmission & selectivity ratio
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF9FAFB),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.weight(1f)
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text("CO₂ Transmission Rate:", fontSize = 10.sp, color = TextSecondary)
          Text(materialA.technicalSpecs.co2TransmissionRate, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          Spacer(modifier = Modifier.height(4.dp))
          Text("Gas Selectivity:", fontSize = 10.sp, color = TextSecondary)
          Text(materialA.technicalSpecs.co2ToO2PermeabilityRatio, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF047857))
        }
      }

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF9FAFB),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.weight(1f)
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text("CO₂ Transmission Rate:", fontSize = 10.sp, color = TextSecondary)
          Text(materialB.technicalSpecs.co2TransmissionRate, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          Spacer(modifier = Modifier.height(4.dp))
          Text("Gas Selectivity:", fontSize = 10.sp, color = TextSecondary)
          Text(materialB.technicalSpecs.co2ToO2PermeabilityRatio, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0284C7))
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Contextual produce impact notice
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = Color(0xFFFEF3C7).copy(alpha = 0.6f),
      border = BorderStroke(1.dp, Color(0xFFFDE68A)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("💡", fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "For ${commodity.name} (${commodity.respirationRate.lowercase()} respiration rate), adequate OTR prevents in-pack hypoxia and ethanol fermentation, while ultra-low OTR prevents lipid rancidity in processed foods.",
          fontSize = 11.sp,
          color = Color(0xFF92400E),
          lineHeight = 14.sp
        )
      }
    }
  }
}

// -------------------------------------------------------------
// SECTION 2: WVTR (Water Vapor Transmission Rate)
// -------------------------------------------------------------
@Composable
private fun WvtrComparisonSection(
  materialA: PackagingMaterial,
  materialB: PackagingMaterial,
  commodity: CommodityItem
) {
  ComparisonSectionCard(
    title = "Water Vapor Transmission Rate (WVTR)",
    subtitle = "g / m² · day @ 38°C, 90% RH (ASTM F1249)",
    icon = Icons.Outlined.WaterDrop,
    headerColor = Color(0xFF0284C7)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      SideValueBlock(
        label = "Material A (WVTR)",
        value = materialA.technicalSpecs.wvtrValue,
        description = materialA.technicalSpecs.wvtrDescription,
        badge = if (materialA.technicalSpecs.wvtrValue.contains("< 1.0") || materialA.technicalSpecs.wvtrValue.contains("< 0.05")) "Hermetic Barrier" else "Moisture Permeable",
        color = Color(0xFF047857),
        modifier = Modifier.weight(1f)
      )

      SideValueBlock(
        label = "Material B (WVTR)",
        value = materialB.technicalSpecs.wvtrValue,
        description = materialB.technicalSpecs.wvtrDescription,
        badge = if (materialB.technicalSpecs.wvtrValue.contains("< 1.0") || materialB.technicalSpecs.wvtrValue.contains("< 0.05")) "Hermetic Barrier" else "Moisture Permeable",
        color = Color(0xFF0284C7),
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Anti-fog & condensation evaluation
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = Color(0xFFE0F2FE).copy(alpha = 0.5f),
      border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Moisture Containment: A balanced WVTR prevents physiological weight loss in fresh produce (${commodity.moistureContent}% water) while preventing water droplet pooling.",
          fontSize = 11.sp,
          color = Color(0xFF0369A1),
          lineHeight = 14.sp
        )
      }
    }
  }
}

// -------------------------------------------------------------
// SECTION 3: Sustainability Metrics
// -------------------------------------------------------------
@Composable
private fun SustainabilityComparisonSection(
  materialA: PackagingMaterial,
  materialB: PackagingMaterial
) {
  ComparisonSectionCard(
    title = "Sustainability & Life Cycle Metrics",
    subtitle = "Circular Economy & Environmental Impact",
    icon = Icons.Outlined.Eco,
    headerColor = Color(0xFF16A34A)
  ) {
    // Side by Side EcoScore & Circular Code
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Eco Card A
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF0FDF4),
        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
        modifier = Modifier.weight(1f)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text("Material A EcoScore", fontSize = 10.5.sp, color = TextSecondary)
          Text(
            text = "${materialA.ecoScore} / 10",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF047857)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(materialA.recyclingCode, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
          Spacer(modifier = Modifier.height(6.dp))
          SustainabilityProgressBar("Recyclability", materialA.recyclability, 10, Color(0xFF047857))
          SustainabilityProgressBar("Biodegradability", materialA.biodegradability, 10, Color(0xFF16A34A))
          SustainabilityProgressBar("Carbon Score", materialA.carbonImpact, 10, Color(0xFF059669))
        }
      }

      // Eco Card B
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF0F9FF),
        border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
        modifier = Modifier.weight(1f)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text("Material B EcoScore", fontSize = 10.5.sp, color = TextSecondary)
          Text(
            text = "${materialB.ecoScore} / 10",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0284C7)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(materialB.recyclingCode, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
          Spacer(modifier = Modifier.height(6.dp))
          SustainabilityProgressBar("Recyclability", materialB.recyclability, 10, Color(0xFF0284C7))
          SustainabilityProgressBar("Biodegradability", materialB.biodegradability, 10, Color(0xFF0EA5E9))
          SustainabilityProgressBar("Carbon Score", materialB.carbonImpact, 10, Color(0xFF0369A1))
        }
      }
    }
  }
}

@Composable
private fun SustainabilityProgressBar(label: String, value: Int, max: Int, color: Color) {
  Column(modifier = Modifier.padding(vertical = 3.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(label, fontSize = 9.5.sp, color = TextSecondary)
      Text("$value/$max", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = color)
    }
    Spacer(modifier = Modifier.height(2.dp))
    LinearProgressIndicator(
      progress = { value.toFloat() / max },
      modifier = Modifier
        .fillMaxWidth()
        .height(4.dp)
        .clip(RoundedCornerShape(2.dp)),
      color = color,
      trackColor = color.copy(alpha = 0.15f),
    )
  }
}

// -------------------------------------------------------------
// SECTION 4: Physical Specs, Shelf Life & Cost
// -------------------------------------------------------------
@Composable
private fun PhysicalAndCostComparisonSection(
  materialA: PackagingMaterial,
  materialB: PackagingMaterial,
  storage: StorageConfig
) {
  ComparisonSectionCard(
    title = "Commercial & Engineering Specifications",
    subtitle = "Thickness, Mechanical Strength & Unit Economics",
    icon = Icons.Outlined.Build,
    headerColor = Color(0xFF6366F1)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      SpecComparisonRow("Estimated Shelf Life", "${materialA.expectedShelfLife} days", "${materialB.expectedShelfLife} days", isAHighlight = materialA.expectedShelfLife >= materialB.expectedShelfLife)
      SpecComparisonRow("Raw Material Cost", materialA.costPerKg, materialB.costPerKg, isAHighlight = parseCostNumeric(materialA.costPerKg) <= parseCostNumeric(materialB.costPerKg))
      SpecComparisonRow("Film Thickness", materialA.filmThickness, materialB.filmThickness)
      SpecComparisonRow("Heat Seal Range", materialA.technicalSpecs.heatSealRange, materialB.technicalSpecs.heatSealRange)
      SpecComparisonRow("Tensile Strength", materialA.technicalSpecs.tensileStrength, materialB.technicalSpecs.tensileStrength)
      SpecComparisonRow("Puncture Resistance", materialA.technicalSpecs.punctureResistance, materialB.technicalSpecs.punctureResistance)
      SpecComparisonRow("MAP Gas Mixture", materialA.mapSpecs.targetEquilibriumAtmosphere, materialB.mapSpecs.targetEquilibriumAtmosphere)
    }
  }
}

@Composable
private fun SpecComparisonRow(
  label: String,
  valueA: String,
  valueB: String,
  isAHighlight: Boolean? = null
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = Color(0xFFF9FAFB),
    border = BorderStroke(1.dp, CardBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = valueA,
          fontSize = 11.5.sp,
          fontWeight = if (isAHighlight == true) FontWeight.Bold else FontWeight.Normal,
          color = if (isAHighlight == true) Color(0xFF047857) else TextPrimary,
          modifier = Modifier.weight(1f)
        )
        Text(
          text = valueB,
          fontSize = 11.5.sp,
          fontWeight = if (isAHighlight == false) FontWeight.Bold else FontWeight.Normal,
          color = if (isAHighlight == false) Color(0xFF0284C7) else TextPrimary,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

// -------------------------------------------------------------
// VERDICT CARD
// -------------------------------------------------------------
@Composable
private fun ComparisonVerdictCard(
  materialA: PackagingMaterial,
  materialB: PackagingMaterial,
  commodity: CommodityItem,
  storage: StorageConfig
) {
  val isProduce = commodity.category.equals("Fruits", true) || commodity.category.equals("Vegetables", true)

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = PaleSageTint
    ),
    border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.5f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "AI Comparative Recommendation",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreenPrimary
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = when {
          isProduce && (materialA.name.contains("Breathable", true) || materialB.name.contains("Breathable", true)) -> {
            "For ${commodity.name}, micro-perforated breathable film provides the vital gas equilibrium needed to avoid anaerobic decay. However, if zero-waste composting is your primary mandate, the biodegradable PLA/PBAT blend offers an eco-friendly alternative with moderate shelf life."
          }
          materialA.ecoScore > materialB.ecoScore && materialA.costPerKg < materialB.costPerKg -> {
            "${materialA.name} provides both superior environmental sustainability (EcoScore ${materialA.ecoScore}) and lower material costs (${materialA.costPerKg}) compared to ${materialB.name}."
          }
          else -> {
            "Select ${materialA.name} for ${materialA.tags.firstOrNull() ?: "its balanced profile"}, or choose ${materialB.name} if you require ${materialB.tags.firstOrNull() ?: "extended barrier protection"}."
          }
        },
        fontSize = 12.sp,
        color = TextPrimary,
        lineHeight = 16.sp
      )
    }
  }
}

// -------------------------------------------------------------
// REUSABLE HELPER BLOCKS
// -------------------------------------------------------------
@Composable
private fun ComparisonSectionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  headerColor: Color,
  content: @Composable ColumnScope.() -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, CardBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .background(headerColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(icon, contentDescription = null, tint = headerColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          Text(subtitle, fontSize = 10.5.sp, color = TextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      content()
    }
  }
}

@Composable
private fun SideValueBlock(
  label: String,
  value: String,
  description: String,
  badge: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = color.copy(alpha = 0.05f),
    border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = color.copy(alpha = 0.15f)
        ) {
          Text(
            text = badge,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.ExtraBold,
        color = color,
        maxLines = 2
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = description,
        fontSize = 9.5.sp,
        color = TextSecondary,
        lineHeight = 12.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

// -------------------------------------------------------------
// UTILITY PARSERS FOR NUMERIC COMPARISONS
// -------------------------------------------------------------
private fun parseOtrNumeric(otrStr: String): Double {
  val clean = otrStr.replace(",", "").replace("<", "").replace(">", "").trim()
  val match = Regex("""([0-9.]+)""").find(clean)
  return match?.groupValues?.get(1)?.toDoubleOrNull() ?: 100.0
}

private fun parseWvtrNumeric(wvtrStr: String): Double {
  val clean = wvtrStr.replace(",", "").replace("<", "").replace(">", "").trim()
  val match = Regex("""([0-9.]+)""").find(clean)
  return match?.groupValues?.get(1)?.toDoubleOrNull() ?: 10.0
}

private fun parseCostNumeric(costStr: String): Double {
  val match = Regex("""([0-9.]+)""").find(costStr)
  return match?.groupValues?.get(1)?.toDoubleOrNull() ?: 150.0
}
