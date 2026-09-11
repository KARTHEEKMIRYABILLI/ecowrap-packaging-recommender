package com.example.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PackagingEngine
import com.example.model.CommodityItem
import com.example.model.PackagingMaterial
import com.example.model.StorageConfig
import com.example.model.StorageType
import com.example.model.UserAccount
import com.example.ui.theme.*
import com.example.utils.PdfReportGenerator
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.roundToInt

data class MaterialBarrierProfile(
  val name: String,
  val baseOtr: Double, // cc/m2/24h at 23°C
  val baseWvtr: Double, // g/m2/24h at 38°C 90% RH
  val activationEnergy: Double, // kJ/mol for Arrhenius
  val ecoScore: Double,
  val color: Color,
  val description: String
)

val BENCHMARK_MATERIALS = listOf(
  MaterialBarrierProfile("Micro-Perforated Bio-Film", 4200.0, 140.0, 28.0, 9.6, Color(0xFF10B981), "High breathability for active respirators"),
  MaterialBarrierProfile("PLA / PBAT Bio-Polymer", 1800.0, 90.0, 32.0, 9.2, Color(0xFF059669), "Compostable moderate barrier"),
  MaterialBarrierProfile("LDPE Film (30 µm)", 2600.0, 18.0, 35.0, 7.0, Color(0xFF3B82F6), "Standard moisture barrier, moderate gas permeation"),
  MaterialBarrierProfile("HDPE Film (25 µm)", 1400.0, 8.5, 38.0, 7.8, Color(0xFF6366F1), "Superior moisture barrier with moderate stiffness"),
  MaterialBarrierProfile("PET / EVOH High Barrier", 4.5, 3.2, 45.0, 8.1, Color(0xFF8B5CF6), "Ultra-low O2 & aroma barrier for high-fat/dairy"),
  MaterialBarrierProfile("Metallized BOPP (Met-BOPP)", 18.0, 1.2, 42.0, 6.8, Color(0xFFEC4899), "High light & moisture barrier for snacks/dry foods"),
  MaterialBarrierProfile("Aluminum Foil Laminate", 0.1, 0.05, 52.0, 5.5, Color(0xFFF59E0B), "Hermetic zero-permeation barrier")
)

@Composable
fun EnvironmentalInfographicScreen(
  commodity: CommodityItem,
  material: PackagingMaterial,
  storage: StorageConfig,
  user: UserAccount,
  onNavigateBack: () -> Unit,
  onExportPdf: () -> Unit = {}
) {
  val context = LocalContext.current
  var selectedTemp by remember { mutableFloatStateOf(storage.temperatureC.toFloat()) }
  var selectedRH by remember { mutableFloatStateOf(storage.relativeHumidityPercent.toFloat()) }
  var activeTab by remember { mutableIntStateOf(0) } // 0: Arrhenius OTR, 1: WVTR Moisture, 2: Shelf Life, 3: Risk Radar
  var isAutoDriftActive by remember { mutableStateOf(false) }

  // Auto-drift simulation (simulates diurnal day/night temperature cycles in un-refrigerated transit)
  LaunchedEffect(isAutoDriftActive) {
    if (isAutoDriftActive) {
      var phase = 0
      while (isAutoDriftActive) {
        kotlinx.coroutines.delay(1200L)
        phase = (phase + 1) % 8
        val tempOffsets = listOf(0f, 2.5f, 5.0f, 7.5f, 8.0f, 5.0f, 1.5f, -1.0f)
        val rhOffsets = listOf(0f, -4f, -10f, -15f, -12f, -5f, 2f, 6f)
        selectedTemp = (storage.temperatureC.toFloat() + tempOffsets[phase]).coerceIn(-5f, 45f)
        selectedRH = (storage.relativeHumidityPercent.toFloat() + rhOffsets[phase]).coerceIn(20f, 98f)
      }
    }
  }

  // Dew point approximation (Magnus formula simplified)
  val dewPoint = remember(selectedTemp, selectedRH) {
    selectedTemp - ((100f - selectedRH) / 5f)
  }

  // Condensation Risk evaluation
  val isCondensationRisk = remember(selectedTemp, dewPoint) {
    (selectedTemp - dewPoint) < 2.5f && selectedRH > 80f
  }

  // Respiration multiplier at selectedTemp ($Q_{10} = 2.0$)
  val q10RespirationMultiplier = remember(selectedTemp) {
    2.0.pow((selectedTemp - 10.0) / 10.0).coerceAtLeast(0.2)
  }

  Scaffold(
    topBar = {
      Surface(
        color = Color.White,
        shadowElevation = 2.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("infographic_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ForestGreenPrimary)
          }
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                "Environmental Dynamics",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = ForestGreenPrimary.copy(alpha = 0.1f)
              ) {
                Text(
                  "Interactive",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreenPrimary,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              "Arrhenius Permeation & Moisture Gradient Simulation",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }

          IconButton(
            onClick = onExportPdf,
            modifier = Modifier.testTag("infographic_export_pdf_button")
          ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", tint = ForestGreenPrimary)
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(AppBackground)
        .padding(innerPadding)
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        // Target Commodity Card Header
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          border = BorderStroke(1.dp, CardBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = CircleShape,
              color = PaleSageLight,
              modifier = Modifier.size(44.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(commodity.emoji, fontSize = 22.sp)
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                commodity.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ForestGreenPrimary
              )
              Text(
                "${commodity.category} • Respiration: ${commodity.respirationRate}",
                fontSize = 12.sp,
                color = TextSecondary
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                "Active Material",
                fontSize = 10.sp,
                color = TextSecondary
              )
              Text(
                material.name.take(18) + (if (material.name.length > 18) "..." else ""),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ForestGreenPrimary
              )
            }
          }
        }
      }

      // Environmental Simulation Controls
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, CardBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Environmental Stress Controls", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ForestGreenPrimary)
              }

              // Auto-drift toggle button
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isAutoDriftActive) Color(0xFFDCFCE7) else Color(0xFFF3F4F6),
                border = BorderStroke(1.dp, if (isAutoDriftActive) ForestGreenPrimary else Color.LightGray),
                modifier = Modifier
                  .clickable { isAutoDriftActive = !isAutoDriftActive }
                  .testTag("infographic_drift_toggle")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    if (isAutoDriftActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (isAutoDriftActive) ForestGreenPrimary else TextSecondary,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    if (isAutoDriftActive) "Drift Active" else "Simulate Drift",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isAutoDriftActive) ForestGreenPrimary else TextSecondary
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Temperature Control Slider & Presets
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Storage Temperature", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
              Text(
                "${selectedTemp.roundToInt()}°C (${((selectedTemp * 9/5) + 32).roundToInt()}°F)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                  selectedTemp < 5f -> Color(0xFF2563EB)
                  selectedTemp < 22f -> ForestGreenPrimary
                  selectedTemp < 32f -> Color(0xFFD97706)
                  else -> Color(0xFFDC2626)
                }
              )
            }

            Slider(
              value = selectedTemp,
              onValueChange = {
                selectedTemp = it
                if (isAutoDriftActive) isAutoDriftActive = false
              },
              valueRange = -5f..45f,
              steps = 50,
              colors = SliderDefaults.colors(
                thumbColor = ForestGreenPrimary,
                activeTrackColor = ForestGreenPrimary,
                inactiveTrackColor = Color(0xFFE5E7EB)
              ),
              modifier = Modifier.testTag("temp_slider")
            )

            // Temperature Quick Presets
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              TempPresetChip("❄️ Freezer (-5°C)", selectedTemp == -5f) { selectedTemp = -5f }
              TempPresetChip("🧊 Chilled (4°C)", selectedTemp == 4f) { selectedTemp = 4f }
              TempPresetChip("🍃 Cool (15°C)", selectedTemp == 15f) { selectedTemp = 15f }
              TempPresetChip("🏠 Ambient (24°C)", selectedTemp == 24f) { selectedTemp = 24f }
              TempPresetChip("☀️ Tropical (36°C)", selectedTemp == 36f) { selectedTemp = 36f }
              TempPresetChip("🔥 Extreme (42°C)", selectedTemp == 42f) { selectedTemp = 42f }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Humidity Control Slider & Presets
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Relative Humidity (RH)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
              Text(
                "${selectedRH.roundToInt()}% RH",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                  selectedRH < 40f -> Color(0xFFD97706)
                  selectedRH <= 85f -> ForestGreenPrimary
                  else -> Color(0xFFDC2626)
                }
              )
            }

            Slider(
              value = selectedRH,
              onValueChange = {
                selectedRH = it
                if (isAutoDriftActive) isAutoDriftActive = false
              },
              valueRange = 20f..98f,
              steps = 78,
              colors = SliderDefaults.colors(
                thumbColor = Color(0xFF0284C7),
                activeTrackColor = Color(0xFF0284C7),
                inactiveTrackColor = Color(0xFFE0F2FE)
              ),
              modifier = Modifier.testTag("rh_slider")
            )

            // Humidity Quick Presets
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              RHPresetChip("🏜️ Arid (30%)", selectedRH == 30f) { selectedRH = 30f }
              RHPresetChip("⚖️ Standard (60%)", selectedRH == 60f) { selectedRH = 60f }
              RHPresetChip("🌧️ Humid (85%)", selectedRH == 85f) { selectedRH = 85f }
              RHPresetChip("💦 Saturated (95%)", selectedRH == 95f) { selectedRH = 95f }
            }
          }
        }
      }

      // Infographic Chart Tabs
      item {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color.White,
          border = BorderStroke(1.dp, CardBorder)
        ) {
          TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.Transparent,
            contentColor = ForestGreenPrimary,
            divider = {}
          ) {
            Tab(
              selected = activeTab == 0,
              onClick = { activeTab = 0 },
              text = { Text("OTR (Temp)", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
              selected = activeTab == 1,
              onClick = { activeTab = 1 },
              text = { Text("WVTR (RH)", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
              selected = activeTab == 2,
              onClick = { activeTab = 2 },
              text = { Text("Shelf-Life", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
              selected = activeTab == 3,
              onClick = { activeTab = 3 },
              text = { Text("Risk Radar", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
          }
        }
      }

      // Chart Display Area based on activeTab
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, CardBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            when (activeTab) {
              0 -> ArrheniusOtrChart(currentTemp = selectedTemp)
              1 -> FickianWvtrChart(currentRH = selectedRH)
              2 -> DynamicShelfLifeSimulationView(
                temp = selectedTemp,
                rh = selectedRH,
                commodity = commodity,
                respirationFactor = q10RespirationMultiplier
              )
              3 -> EnvironmentalRiskRadarView(
                temp = selectedTemp,
                rh = selectedRH,
                dewPoint = dewPoint,
                isCondensation = isCondensationRisk,
                commodity = commodity
              )
            }
          }
        }
      }

      // Dynamic Material Leaderboard & Stress Matrix
      item {
        Text(
          "Material Performance Matrix (At ${selectedTemp.roundToInt()}°C, ${selectedRH.roundToInt()}% RH)",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = ForestGreenPrimary
        )
      }

      items(BENCHMARK_MATERIALS) { mat ->
        // Calculate dynamic OTR and WVTR at currently selected Temp & RH
        val tempK = 273.15 + selectedTemp
        val refTempK = 296.15 // 23°C
        val rGas = 8.314 // J/(mol*K)
        // Arrhenius factor
        val arrheniusFactor = exp((mat.activationEnergy * 1000 / rGas) * (1.0 / refTempK - 1.0 / tempK))
        val currentOtr = (mat.baseOtr * arrheniusFactor).coerceAtLeast(0.01)

        // Fickian RH factor: WVTR increases with humidity gradient
        val rhGradientFactor = (selectedRH / 90.0).coerceIn(0.2, 1.25)
        val tempWvtrFactor = 1.8.pow((selectedTemp - 23.0) / 10.0).coerceAtLeast(0.25)
        val currentWvtr = (mat.baseWvtr * rhGradientFactor * tempWvtrFactor).coerceAtLeast(0.01)

        // Calculate dynamic shelf life days
        val baseDays = when (commodity.category.lowercase()) {
          "fruits", "vegetables" -> if (mat.name.contains("Bio") || mat.name.contains("Micro")) 14.0 else 9.0
          "dairy", "meat" -> if (mat.name.contains("PET") || mat.name.contains("Foil")) 21.0 else 5.0
          "dry goods", "grains" -> if (mat.name.contains("BOPP") || mat.name.contains("Foil")) 180.0 else 60.0
          else -> 12.0
        }
        val dynamicShelfLife = (baseDays / (q10RespirationMultiplier * (if (selectedRH > 85) 1.2 else 1.0))).roundToInt().coerceAtLeast(1)

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color.White,
          border = BorderStroke(1.dp, CardBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(mat.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  mat.name,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = ForestGreenPrimary
                )
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = PaleSageLight
              ) {
                Text(
                  "Eco ${mat.ecoScore}/10",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreenPrimary,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              mat.description,
              fontSize = 11.sp,
              color = TextSecondary,
              modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              DynamicStatBadge(
                label = "Live OTR",
                value = if (currentOtr < 1.0) String.format("%.2f", currentOtr) else currentOtr.roundToInt().toString(),
                unit = "cc/m²/day"
              )
              DynamicStatBadge(
                label = "Live WVTR",
                value = if (currentWvtr < 1.0) String.format("%.2f", currentWvtr) else String.format("%.1f", currentWvtr),
                unit = "g/m²/day"
              )
              DynamicStatBadge(
                label = "Est. Shelf-Life",
                value = "$dynamicShelfLife",
                unit = "Days"
              )
            }
          }
        }
      }

      item {
        // Quick Action to generate & share technical report
        Button(
          onClick = onExportPdf,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("download_technical_report_btn")
        ) {
          Icon(Icons.Default.FileDownload, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Download Comprehensive Technical PDF Report", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun DynamicStatBadge(label: String, value: String, unit: String) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = AppBackground,
    border = BorderStroke(1.dp, CardBorder)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(label, fontSize = 10.sp, color = TextSecondary)
      Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
      Text(unit, fontSize = 9.sp, color = TextMuted)
    }
  }
}

@Composable
private fun TempPresetChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = if (isSelected) ForestGreenPrimary else Color(0xFFF3F4F6),
    modifier = Modifier.clickable { onClick() }
  ) {
    Text(
      label,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      color = if (isSelected) Color.White else TextSecondary,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
    )
  }
}

@Composable
private fun RHPresetChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = if (isSelected) Color(0xFF0284C7) else Color(0xFFF3F4F6),
    modifier = Modifier.clickable { onClick() }
  ) {
    Text(
      label,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      color = if (isSelected) Color.White else TextSecondary,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
    )
  }
}

/**
 * Visual Canvas Chart 1: Arrhenius OTR vs Temperature Curve
 */
@Composable
private fun ArrheniusOtrChart(currentTemp: Float) {
  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text("Arrhenius Gas Permeation Curve", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestGreenPrimary)
        Text("OTR vs Temperature (°C) • Activation Energy (Ep)", fontSize = 11.sp, color = TextSecondary)
      }
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFEFF6FF)
      ) {
        Text("ASTM D3985", fontSize = 10.sp, color = Color(0xFF1D4ED8), fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(170.dp)
    ) {
      val w = size.width
      val h = size.height
      val padL = 40f
      val padR = 20f
      val padB = 30f
      val padT = 15f
      val chartW = w - padL - padR
      val chartH = h - padT - padB

      // Draw grid lines
      val gridPaint = androidx.compose.ui.graphics.Color(0xFFE5E7EB)
      for (i in 0..4) {
        val y = padT + (chartH / 4f) * i
        drawLine(gridPaint, Offset(padL, y), Offset(w - padR, y), strokeWidth = 1f)
      }

      // Draw Curves for key materials
      val plotMaterials = listOf(
        BENCHMARK_MATERIALS[0], // Bio-film
        BENCHMARK_MATERIALS[2], // LDPE
        BENCHMARK_MATERIALS[4]  // PET/EVOH
      )

      plotMaterials.forEach { mat ->
        val path = Path()
        val rGas = 8.314
        val refTempK = 296.15

        for (step in 0..30) {
          val t = -5f + (step / 30f) * 50f // -5°C to 45°C
          val tK = 273.15 + t
          val arrhenius = exp((mat.activationEnergy * 1000 / rGas) * (1.0 / refTempK - 1.0 / tK))
          val otr = (mat.baseOtr * arrhenius).coerceIn(0.1, 8000.0)

          // Logarithmic Y scale
          val logOtr = kotlin.math.ln(otr.toFloat() + 1f)
          val maxLog = kotlin.math.ln(8001f)
          val normY = (logOtr / maxLog).coerceIn(0f, 1f)

          val xPos = padL + (step / 30f) * chartW
          val yPos = padT + chartH * (1f - normY)

          if (step == 0) path.moveTo(xPos, yPos) else path.lineTo(xPos, yPos)
        }

        drawPath(
          path = path,
          color = mat.color,
          style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
      }

      // Draw Current Temperature Indicator Vertical Line
      val normTempX = ((currentTemp + 5f) / 50f).coerceIn(0f, 1f)
      val currentX = padL + normTempX * chartW

      drawLine(
        color = Color(0xFFDC2626),
        start = Offset(currentX, padT),
        end = Offset(currentX, padT + chartH),
        strokeWidth = 2.dp.toPx()
      )

      drawCircle(
        color = Color(0xFFDC2626),
        radius = 5.dp.toPx(),
        center = Offset(currentX, padT + chartH * 0.45f)
      )
    }

    // Chart Legend
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      LegendItem(color = BENCHMARK_MATERIALS[0].color, label = "Bio-Film")
      LegendItem(color = BENCHMARK_MATERIALS[2].color, label = "LDPE Film")
      LegendItem(color = BENCHMARK_MATERIALS[4].color, label = "PET High Barrier")
      LegendItem(color = Color(0xFFDC2626), label = "Active Temp (${currentTemp.roundToInt()}°C)")
    }
  }
}

/**
 * Visual Canvas Chart 2: Fickian WVTR vs Relative Humidity Curve
 */
@Composable
private fun FickianWvtrChart(currentRH: Float) {
  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text("Fickian Moisture Vapor Transmission", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestGreenPrimary)
        Text("WVTR Flux vs Relative Humidity (% RH)", fontSize = 11.sp, color = TextSecondary)
      }
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFE0F2FE)
      ) {
        Text("ASTM F1249", fontSize = 10.sp, color = Color(0xFF0369A1), fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(170.dp)
    ) {
      val w = size.width
      val h = size.height
      val padL = 40f
      val padR = 20f
      val padB = 30f
      val padT = 15f
      val chartW = w - padL - padR
      val chartH = h - padT - padB

      // Grid
      val gridPaint = Color(0xFFE5E7EB)
      for (i in 0..4) {
        val y = padT + (chartH / 4f) * i
        drawLine(gridPaint, Offset(padL, y), Offset(w - padR, y), strokeWidth = 1f)
      }

      // Draw WVTR Curves for LDPE, Bio-Film, Met-BOPP
      val materialsToPlot = listOf(
        BENCHMARK_MATERIALS[0], // Bio-film
        BENCHMARK_MATERIALS[2], // LDPE
        BENCHMARK_MATERIALS[5]  // Met-BOPP
      )

      materialsToPlot.forEach { mat ->
        val path = Path()
        for (step in 0..30) {
          val rh = 20f + (step / 30f) * 78f
          val flux = mat.baseWvtr * (rh / 90f).toDouble().pow(1.3)
          val normY = (flux / 160.0).toFloat().coerceIn(0f, 1f)

          val xPos = padL + (step / 30f) * chartW
          val yPos = padT + chartH * (1f - normY)

          if (step == 0) path.moveTo(xPos, yPos) else path.lineTo(xPos, yPos)
        }

        drawPath(
          path = path,
          color = mat.color,
          style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
      }

      // Draw Current RH Indicator Vertical Line
      val normRhX = ((currentRH - 20f) / 78f).coerceIn(0f, 1f)
      val currentX = padL + normRhX * chartW

      drawLine(
        color = Color(0xFF0284C7),
        start = Offset(currentX, padT),
        end = Offset(currentX, padT + chartH),
        strokeWidth = 2.dp.toPx()
      )

      drawCircle(
        color = Color(0xFF0284C7),
        radius = 5.dp.toPx(),
        center = Offset(currentX, padT + chartH * 0.5f)
      )
    }

    // Chart Legend
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      LegendItem(color = BENCHMARK_MATERIALS[0].color, label = "Bio-Film")
      LegendItem(color = BENCHMARK_MATERIALS[2].color, label = "LDPE")
      LegendItem(color = BENCHMARK_MATERIALS[5].color, label = "Met-BOPP")
      LegendItem(color = Color(0xFF0284C7), label = "Active RH (${currentRH.roundToInt()}%)")
    }
  }
}

/**
 * Visual View 3: Shelf-Life Comparative Simulation
 */
@Composable
private fun DynamicShelfLifeSimulationView(
  temp: Float,
  rh: Float,
  commodity: CommodityItem,
  respirationFactor: Double
) {
  Column {
    Text("Projected Shelf-Life Degradation", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestGreenPrimary)
    Text(
      "Estimated retention at ${temp.roundToInt()}°C, ${rh.roundToInt()}% RH (Q₁₀ factor: ${String.format("%.1f", respirationFactor)}x)",
      fontSize = 11.sp,
      color = TextSecondary
    )

    Spacer(modifier = Modifier.height(12.dp))

    BENCHMARK_MATERIALS.take(4).forEach { mat ->
      val baseLife = when (commodity.category.lowercase()) {
        "fruits", "vegetables" -> if (mat.name.contains("Bio")) 18f else 10f
        "dairy", "meat" -> if (mat.name.contains("PET")) 24f else 6f
        else -> 45f
      }
      val simulatedDays = (baseLife / respirationFactor).coerceAtLeast(1.0).toFloat()
      val maxDays = 25f

      Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(mat.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
          Text("${simulatedDays.roundToInt()} Days", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = mat.color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
          progress = { (simulatedDays / maxDays).coerceIn(0.05f, 1f) },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = mat.color,
          trackColor = Color(0xFFF3F4F6)
        )
      }
    }
  }
}

/**
 * Visual View 4: Environmental Risk Radar & Critical Advisories
 */
@Composable
private fun EnvironmentalRiskRadarView(
  temp: Float,
  rh: Float,
  dewPoint: Float,
  isCondensation: Boolean,
  commodity: CommodityItem
) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Text("Dynamic Hazard & Stress Indicators", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestGreenPrimary)

    // Hazard 1: Condensation Risk
    RiskAlertCard(
      title = "Condensation & Fogging Risk",
      level = if (isCondensation) "HIGH RISK" else "LOW RISK",
      description = if (isCondensation)
        "Dew point (${dewPoint.roundToInt()}°C) is close to storage temperature (${temp.roundToInt()}°C). Micro-droplets will accumulate, triggering fungal rot."
      else
        "Sufficient thermal margin (${String.format("%.1f", temp - dewPoint)}°C) above dew point (${dewPoint.roundToInt()}°C). Moisture condensation is inhibited.",
      isAlert = isCondensation,
      icon = Icons.Default.WaterDrop
    )

    // Hazard 2: Respiration Hypoxia / Anaerobic Fermentation
    val isHypoxiaRisk = temp > 28f && commodity.respirationRate.contains("High", ignoreCase = true)
    RiskAlertCard(
      title = "Anaerobic Fermentation / Off-Flavor",
      level = if (isHypoxiaRisk) "CRITICAL WARNING" else "OPTIMAL GAS FLUX",
      description = if (isHypoxiaRisk)
        "Elevated temperature accelerates fruit respiration beyond standard film OTR capacity, risking ethanol accumulation and tissue browning."
      else
        "Gas permeability remains in equilibrium with natural commodity respiration rates.",
      isAlert = isHypoxiaRisk,
      icon = Icons.Default.Air
    )

    // Hazard 3: Desiccation / Weight Loss
    val isDesiccation = rh < 40f
    RiskAlertCard(
      title = "Transpirational Weight Loss / Wilting",
      level = if (isDesiccation) "MODERATE RISK" else "BALANCED RH",
      description = if (isDesiccation)
        "Low ambient RH (<40%) creates a steep vapor pressure deficit, causing skin shriveling and rapid water loss."
      else
        "Ambient humidity preserves cellular turgor and fresh appearance.",
      isAlert = isDesiccation,
      icon = Icons.Default.Thermostat
    )
  }
}

@Composable
private fun RiskAlertCard(
  title: String,
  level: String,
  description: String,
  isAlert: Boolean,
  icon: androidx.compose.ui.graphics.vector.ImageVector
) {
  val bgColor = if (isAlert) Color(0xFFFEF2F2) else Color(0xFFF0FDF4)
  val borderColor = if (isAlert) Color(0xFFFCA5A5) else Color(0xFF86EFAC)
  val textColor = if (isAlert) Color(0xFFB91C1C) else ForestGreenPrimary

  Surface(
    shape = RoundedCornerShape(10.dp),
    color = bgColor,
    border = BorderStroke(1.dp, borderColor)
  ) {
    Row(
      modifier = Modifier.padding(10.dp),
      verticalAlignment = Alignment.Top
    ) {
      Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
          Text(level, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(description, fontSize = 11.sp, color = TextSecondary)
      }
    }
  }
}

@Composable
private fun LegendItem(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(label, fontSize = 10.sp, color = TextSecondary)
  }
}
