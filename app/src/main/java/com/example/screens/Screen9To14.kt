package com.example.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.components.*
import com.example.model.*
import com.example.ui.theme.*
import com.example.utils.PdfReportGenerator
import com.example.utils.SocialShareHelper
import com.example.utils.rememberAppHaptics
import java.io.File
import kotlinx.coroutines.delay

// -------------------------------------------------------------
// SCREEN 9: AI PROCESSING ANIMATION ("PackWise AI")
// -------------------------------------------------------------
@Composable
fun AiProcessingScreen(
  onComplete: () -> Unit
) {
  val haptics = rememberAppHaptics()
  var progress by remember { mutableFloatStateOf(0.15f) }
  var completedStepIndex by remember { mutableIntStateOf(0) }

  val checklistItems = listOf(
    "Analyzing food properties",
    "Checking storage conditions",
    "Evaluating OTR/WVTR requirements",
    "Scanning packaging database",
    "Running AI recommendation model",
    "Optimizing cost & sustainability",
    "Generating final results..."
  )

  LaunchedEffect(Unit) {
    while (progress < 1.0f) {
      delay(400)
      progress = (progress + 0.15f).coerceAtMost(1.0f)
      val newStepIndex = (progress * checklistItems.size).toInt().coerceAtMost(checklistItems.size - 1)
      if (newStepIndex != completedStepIndex) {
        completedStepIndex = newStepIndex
        haptics.performTick()
      }
      if (progress >= 1.0f) {
        haptics.performSuccess()
      }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkEmeraldBackground)
      .testTag("ai_processing_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 24.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
      ) {
        // Central Animated Robot Avatar
        AiRobotAvatar(modifier = Modifier.size(100.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "PackWise AI",
          fontSize = 26.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Analyzing your requirements...",
          fontSize = 14.sp,
          color = Color(0xFFA7F3D0)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Vertical Animated Checklist
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        checklistItems.forEachIndexed { index, item ->
          val isDone = index <= completedStepIndex
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .background(
                  if (isDone) MintLeafAccent else Color(0xFF134E2F),
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              if (isDone) {
                Icon(
                  Icons.Default.Check,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
              } else {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF6EE7B7), CircleShape)
                )
              }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
              text = item,
              fontSize = 14.sp,
              fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Normal,
              color = if (isDone) Color.White else Color(0xFF9CA3AF)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Progress Bar & Percentage: "Processing... 70%"
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = MintLeafAccent,
          trackColor = Color(0xFF134E2F)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = if (progress >= 1f) "Completed" else "Processing...",
            fontSize = 12.sp,
            color = Color(0xFFA7F3D0)
          )
          Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }

        if (progress < 1.0f) {
          Spacer(modifier = Modifier.height(10.dp))
          val shimmer = rememberShimmerBrush()
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(34.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF134E2F).copy(alpha = 0.7f))
              .border(1.dp, MintLeafAccent.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
              .padding(horizontal = 10.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(shimmer)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Scanning neural polymer database & moisture curves...",
                fontSize = 11.sp,
                color = Color(0xFFA7F3D0)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Bottom Quote Floating Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3D26).copy(alpha = 0.85f)),
        border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("🌱", fontSize = 20.sp)
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Good packaging keeps food fresh, farmers happy, and the planet green.",
            fontSize = 12.sp,
            color = Color(0xFFD1FAE5),
            lineHeight = 16.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Continue Button
      Button(
        onClick = {
          haptics.performSuccess()
          onComplete()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("ai_processing_continue"),
        colors = ButtonDefaults.buttonColors(containerColor = MintLeafAccent),
        shape = RoundedCornerShape(14.dp)
      ) {
        Text("View Recommendation", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
      }
    }
  }
}

// -------------------------------------------------------------
// SCREEN 10: AI RECOMMENDATION RESULT
// -------------------------------------------------------------
@Composable
fun RecommendationResultScreen(
  commodity: CommodityItem,
  material: PackagingMaterial,
  storage: StorageConfig = StorageConfig(),
  user: UserAccount = UserAccount("karthikmiryabbelli@gmail.com", "Karthik"),
  alternatives: List<Pair<String, Int>>,
  onSelectAlternative: (String) -> Unit,
  onViewDetails: () -> Unit,
  onNavigateBack: () -> Unit,
  onShare: () -> Unit,
  onOpenFeedback: () -> Unit = {},
  onSubmitFeedback: (UserFeedback) -> Unit = {},
  onDownloadPdf: () -> Unit = {},
  onNavigateToComparison: (String?, String?) -> Unit = { _, _ -> },
  onOpenInfographic: () -> Unit = {},
  onToggleDarkMode: () -> Unit = {}
) {
  val context = LocalContext.current
  val haptics = rememberAppHaptics()
  val colors = AppTheme.colors
  val isDark = AppTheme.isDark
  var generatedPdfFile by remember { mutableStateOf<File?>(null) }
  var showPdfSuccessDialog by remember { mutableStateOf(false) }
  var showInAppShareDialog by remember { mutableStateOf(false) }
  var isFetchingAiRecommendation by remember { mutableStateOf(true) }

  // Trigger shimmer loading animation while AI recommendation is fetched and evaluated
  LaunchedEffect(material.name) {
    isFetchingAiRecommendation = true
    delay(450)
    isFetchingAiRecommendation = false
    haptics.performSuccess()
  }

  val triggerPdfDownload = {
    try {
      val file = PdfReportGenerator.generatePackagingReportPdf(
        context = context,
        commodity = commodity,
        material = material,
        storage = storage,
        user = user
      )
      generatedPdfFile = file
      showPdfSuccessDialog = true
      haptics.performSuccess()
      onDownloadPdf()
    } catch (e: Exception) {
      haptics.performError()
      Toast.makeText(context, "Error generating PDF report: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
  }

  Scaffold(
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(colors.background)
          .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("recommendation_back")) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.headerText)
        }
        Text(
          "AI Recommendation",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = colors.headerText
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onToggleDarkMode, modifier = Modifier.testTag("toggle_dark_mode_top_btn")) {
            Icon(
              imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = "Toggle Dark Mode",
              tint = if (isDark) Color(0xFFFBBF24) else ForestGreenPrimary
            )
          }
          IconButton(onClick = triggerPdfDownload, modifier = Modifier.testTag("download_pdf_top_bar_btn")) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = "Download PDF", tint = colors.headerText)
          }
          IconButton(onClick = onOpenFeedback, modifier = Modifier.testTag("rate_top_bar_btn")) {
            Icon(Icons.Default.Star, contentDescription = "Rate", tint = Color(0xFFF59E0B))
          }
          IconButton(
            onClick = {
              haptics.performClick()
              SocialShareHelper.shareReportViaChooser(context, commodity, material, storage)
            },
            modifier = Modifier.testTag("share_top_bar_btn")
          ) {
            Icon(Icons.Default.Share, contentDescription = "Share", tint = colors.headerText)
          }
        }
      }
    },
    containerColor = colors.background
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 20.dp)
        .verticalScroll(rememberScrollState())
    ) {
      if (isFetchingAiRecommendation) {
        AiRecommendationShimmerLoading()
      } else {
        // Hero Card with "Top Match" Chip, 3D Preview, Score Ring
        Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Top Match Chip Row & Recycling/Eco Badges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = PaleSageLight,
              border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.5f))
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("♻️", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = material.recyclingCode,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreenPrimary
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(20.dp),
              color = ForestGreenPrimary
            ) {
              Text(
                "Top Match",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          // Visual Packaging Graphic Container
          Box(
            modifier = Modifier
              .size(130.dp)
              .background(PaleSageLight, RoundedCornerShape(18.dp))
              .border(1.5.dp, MintLeafAccent.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              FoodCommodityVisual(item = commodity, size = 50.dp)
              Spacer(modifier = Modifier.height(4.dp))
              Text(material.filmThickness, fontSize = 10.sp, color = ForestGreenPrimary, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Material Title & Subtitle
          Text(
            text = material.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = material.description,
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Circular Suitability Score Ring (92%)
          SuitabilityScoreRing(
            score = material.score,
            modifier = Modifier.size(120.dp)
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Key Tags
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            material.tags.forEach { tag ->
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .fillMaxWidth()
                  .background(PaleSageLight, RoundedCornerShape(8.dp))
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MintLeafAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(tag, fontSize = 12.sp, color = ForestGreenPrimary, fontWeight = FontWeight.Medium)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Primary CTA Button: "View Details ↓"
          Button(
            onClick = onViewDetails,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("view_details_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("View Full Engineering Analysis ↓", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Primary Direct Download Button
          Button(
            onClick = triggerPdfDownload,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("download_pdf_report_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MintLeafAccent),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Download Official MoFPI Report (PDF)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Rate AI Recommendation Button
          OutlinedButton(
            onClick = onOpenFeedback,
            modifier = Modifier
              .fillMaxWidth()
              .height(42.dp)
              .testTag("rate_recommendation_card_btn"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Rate AI Recommendation", fontSize = 13.sp, color = ForestGreenPrimary, fontWeight = FontWeight.SemiBold)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // -------------------------------------------------------------
      // DETAILED TECHNICAL PACKAGING SPECIFICATIONS (OTR, WVTR, Film, Mechanical)
      // -------------------------------------------------------------
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .background(PaleSageLight, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Shield, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Technical Packaging Specifications", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
              Text("Barrier properties & mechanical strength", fontSize = 11.sp, color = TextSecondary)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Grid: OTR & WVTR
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Surface(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp),
              color = PaleSageLight,
              border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.3f))
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text("Oxygen Barrier (OTR)", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = material.technicalSpecs.otrValue,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreenPrimary
                )
                Text("cc/m²·day·atm", fontSize = 9.sp, color = TextMuted)
              }
            }

            Surface(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp),
              color = PaleSageLight,
              border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.3f))
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text("Moisture Barrier (WVTR)", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = material.technicalSpecs.wvtrValue,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreenPrimary
                )
                Text("g/m²·day (38°C, 90% RH)", fontSize = 9.sp, color = TextMuted)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Specs List: Structure, Sealability, Tensile Strength, Permeability Ratio
          SpecificationRowItem("Structure", material.technicalSpecs.packagingStructure)
          SpecificationRowItem("Film Thickness", material.filmThickness)
          SpecificationRowItem("Heat Seal Range", material.technicalSpecs.heatSealRange)
          SpecificationRowItem("Tensile Strength", material.technicalSpecs.tensileStrength)
          SpecificationRowItem("Dart Drop Impact", material.technicalSpecs.dartDropImpact)
          SpecificationRowItem("Puncture Resistance", material.technicalSpecs.punctureResistance)
          SpecificationRowItem("CO₂ Trans. Rate", material.technicalSpecs.co2TransmissionRate)
          SpecificationRowItem("Permeability Ratio (β)", material.technicalSpecs.co2ToO2PermeabilityRatio)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // -------------------------------------------------------------
      // MAP & RESPIRATION HANDLING FOR FRESH PRODUCE
      // -------------------------------------------------------------
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .background(PaleSageLight, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Air, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Modified Atmosphere Packaging (MAP)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
              Text("Equilibrium gas flush & respiration control", fontSize = 11.sp, color = TextSecondary)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Gas Flush Composition Visual Bar
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF0FDF4),
            border = BorderStroke(1.dp, Color(0xFF86EFAC)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("Recommended Gas Flush Composition", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                GasFlushBadge(
                  label = "O₂ (Oxygen)",
                  value = material.mapSpecs.o2Percentage,
                  color = Color(0xFF2563EB),
                  modifier = Modifier.weight(1f)
                )
                GasFlushBadge(
                  label = "CO₂ (Dioxide)",
                  value = material.mapSpecs.co2Percentage,
                  color = Color(0xFFD97706),
                  modifier = Modifier.weight(1f)
                )
                GasFlushBadge(
                  label = "N₂ (Balance)",
                  value = material.mapSpecs.n2Percentage,
                  color = Color(0xFF15803D),
                  modifier = Modifier.weight(1.2f)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          SpecificationRowItem("MAP Suitability", if (material.mapSpecs.isMapSuitable) "Active / Passive Equilibrium MAP" else "Direct Aerated / Perforated")
          SpecificationRowItem("Produce Respiration", "${commodity.respirationRate} (${material.mapSpecs.mapSuitabilityLevel})")
          SpecificationRowItem("Equilibrium Target", material.mapSpecs.targetEquilibriumAtmosphere)
          SpecificationRowItem("Special Additives", material.mapSpecs.specialAdditives)
          SpecificationRowItem("Packaging Type", material.mapSpecs.suggestedPackagingType)
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Google Search Grounding with gemini-3.5-flash (googleSearch tool)
      GoogleSearchGroundedIntelCard(
        commodity = commodity,
        material = material,
        storageType = "${storage.temperatureC}°C, ${storage.relativeHumidityPercent}% RH, ${storage.transportationType}"
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Environmental Infographic & Fluctuations Simulator CTA Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
        border = BorderStroke(1.dp, Color(0xFF86EFAC)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            haptics.performClick()
            onOpenInfographic()
          }
          .testTag("environmental_infographic_cta_card")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .background(ForestGreenPrimary, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Thermostat,
              contentDescription = "Infographic",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Environmental Dynamics Dashboard",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = ForestGreenPrimary
              ) {
                Text("Interactive", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
              }
            }
            Text(
              text = "Explore live Arrhenius OTR & Fickian WVTR curves against temperature & humidity stress",
              fontSize = 11.5.sp,
              color = TextSecondary,
              lineHeight = 15.sp
            )
          }
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = ForestGreenPrimary,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Side-by-Side Material Comparison CTA Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaleSageLight),
        border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.4f)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            haptics.performClick()
            onNavigateToComparison(material.name, alternatives.firstOrNull()?.first)
          }
          .testTag("compare_materials_cta_card")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .background(ForestGreenPrimary, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.CompareArrows,
              contentDescription = "Compare",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Side-by-Side Comparison",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreenPrimary
            )
            Text(
              text = "Benchmark OTR, WVTR & Sustainability metrics against alternatives",
              fontSize = 11.5.sp,
              color = TextSecondary,
              lineHeight = 15.sp
            )
          }
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = ForestGreenPrimary,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // "See Alternative Options" Section
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "See Alternative Options",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreenPrimary
        )
        TextButton(
          onClick = { onNavigateToComparison(material.name, alternatives.firstOrNull()?.first) }
        ) {
          Text(
            "Compare All",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MintLeafAccent
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
      ) {
        items(alternatives) { alt ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = BorderStroke(1.dp, colors.cardBorder),
            modifier = Modifier
              .width(110.dp)
              .clickable {
                haptics.performClick()
                onSelectAlternative(alt.first)
              }
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(50.dp)
                  .background(colors.paleSageLight, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  when {
                    alt.first.contains("PET") -> "📦"
                    alt.first.contains("Bio") -> "🌿"
                    else -> "🥫"
                  },
                  fontSize = 24.sp
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = alt.first,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${alt.second}%",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (alt.second >= 80) (if (isDark) MintLeafAccent else ForestGreenPrimary) else Color(0xFFEA580C)
              )
            }
          }
        }
      }

      // -------------------------------------------------------------
      // SOCIAL SHARING ACTIONS CARD (Android Share Intent)
      // -------------------------------------------------------------
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth().testTag("social_sharing_section_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(colors.paleSageLight, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Share, contentDescription = null, tint = colors.headerText, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text("Social & Messaging Sharing", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.headerText)
              Text("Share advisory summary & PDF report via external apps", fontSize = 11.5.sp, color = colors.textSecondary)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                haptics.performClick()
                SocialShareHelper.shareReportViaChooser(context, commodity, material, storage)
              },
              colors = ButtonDefaults.buttonColors(containerColor = if (isDark) MintLeafAccent else ForestGreenPrimary),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f).height(42.dp).testTag("share_apps_intent_btn")
            ) {
              Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Share via Apps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            OutlinedButton(
              onClick = {
                showInAppShareDialog = true
              },
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, colors.cardBorder),
              modifier = Modifier.weight(1f).height(42.dp).testTag("share_options_dialog_btn")
            ) {
              Icon(Icons.Default.Tune, contentDescription = null, tint = colors.headerText, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("More Options", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.headerText)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // -------------------------------------------------------------
      // INLINE AI ACCURACY FEEDBACK FORM
      // -------------------------------------------------------------
      InlineFeedbackCard(
        commodityName = commodity.name,
        materialName = material.name,
        userEmail = user.email,
        onSubmitFeedback = { feedback ->
          onSubmitFeedback(feedback)
          haptics.performSuccess()
        },
        modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp)
      )
      }
    }
  }

  if (showPdfSuccessDialog && generatedPdfFile != null) {
    PdfDownloadSuccessDialog(
      file = generatedPdfFile!!,
      commodity = commodity,
      material = material,
      onDismiss = { showPdfSuccessDialog = false }
    )
  }

  if (showInAppShareDialog) {
    InAppShareDialog(
      commodity = commodity,
      material = material,
      storage = storage,
      onDismiss = { showInAppShareDialog = false },
      onDownloadPdf = triggerPdfDownload
    )
  }
}

// -------------------------------------------------------------
// SCREEN 11: DETAIL TAB - "WHY THIS?"
// -------------------------------------------------------------
@Composable
fun DetailWhyThisScreen(
  material: PackagingMaterial,
  currentTab: Int, // 0 = Overview, 1 = Why This, 2 = Shelf Life, 3 = Sustainability, 4 = Report
  onSelectTab: (Int) -> Unit,
  onNavigateBack: () -> Unit,
  onNavigateNext: () -> Unit
) {
  Scaffold(
    topBar = {
      TopAppBarHeader(title = "Detailed Analysis", onNavigateBack = onNavigateBack)
    },
    bottomBar = {
      Surface(
        color = AppBackground,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
      ) {
        Button(
          onClick = onNavigateNext,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("why_this_next_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Next: Shelf Life Prediction", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
      // Segmented Tabs: [Overview], [Why This?], [Properties]
      SegmentedTabRow(
        tabs = listOf("Overview", "Why This?", "Properties"),
        selectedIndex = 1,
        onTabSelected = { idx ->
          if (idx == 0) onSelectTab(0)
          else if (idx == 1) onSelectTab(1)
          else onSelectTab(2)
        }
      )

      Spacer(modifier = Modifier.height(18.dp))

      // "Why This Packaging?" Section
      Text(
        "Why This Packaging?",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = ForestGreenPrimary
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = material.whySummary,
        fontSize = 14.sp,
        color = TextSecondary,
        lineHeight = 20.sp
      )

      Spacer(modifier = Modifier.height(20.dp))

      // "Key Factors Influencing Recommendation" Card
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            "Key Factors Influencing Recommendation",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
          )

          material.factors.forEach { factor ->
            FactorProgressRow(factor = factor)
            Spacer(modifier = Modifier.height(10.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Safety Advisory Note Card with Plus icon
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
            Icons.Default.AddCircle,
            contentDescription = "Advisory",
            tint = ForestGreenPrimary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = material.safetyAdvisory,
            fontSize = 13.sp,
            color = ForestGreenPrimary,
            lineHeight = 18.sp
          )
        }
      }
    }
  }
}

@Composable
private fun FactorProgressRow(factor: FactorWeight) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = factor.name,
      fontSize = 13.sp,
      color = TextPrimary,
      modifier = Modifier.width(130.dp)
    )
    LinearProgressIndicator(
      progress = { factor.percentage / 100f },
      modifier = Modifier
        .weight(1f)
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp)),
      color = ForestGreenPrimary,
      trackColor = Color(0xFFE5E7EB)
    )
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = "${factor.percentage}%",
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      color = ForestGreenPrimary,
      modifier = Modifier.width(36.dp),
      textAlign = TextAlign.End
    )
  }
}

// -------------------------------------------------------------
// SCREEN 12: DETAIL TAB - "SHELF LIFE" PREDICTION
// -------------------------------------------------------------
@Composable
fun DetailShelfLifeScreen(
  material: PackagingMaterial,
  onSelectTab: (Int) -> Unit,
  onNavigateBack: () -> Unit,
  onNavigateNext: () -> Unit
) {
  Scaffold(
    topBar = {
      TopAppBarHeader(title = "Shelf-Life Prediction", onNavigateBack = onNavigateBack)
    },
    bottomBar = {
      Surface(
        color = AppBackground,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
      ) {
        Button(
          onClick = onNavigateNext,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("shelf_life_next_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Next: Sustainability & Cost", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
      // Segmented Tabs: [Overview], [Shelf Life] (Active), [Cost], [Sustainability]
      SegmentedTabRow(
        tabs = listOf("Overview", "Shelf Life", "Cost", "Sustainability"),
        selectedIndex = 1,
        onTabSelected = { idx -> onSelectTab(idx) }
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Main Display: "Predicted Shelf Life: 16.4 Days", Confidence: 87%
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text("Predicted Shelf Life", fontSize = 14.sp, color = TextSecondary)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${material.expectedShelfLife} Days",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ForestGreenPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFEFF6FF)
          ) {
            Text(
              "Confidence: 87%",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF2563EB),
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Interactive Line Chart showing quality degradation over 0, 5, 10, 15, 20 days
          QualityDegradationChart(
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(Modifier.size(8.dp).background(ForestGreenPrimary, CircleShape))
              Spacer(Modifier.width(4.dp))
              Text("Expected Quality", fontSize = 11.sp, color = TextSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(Modifier.size(10.dp, 3.dp).background(MintLeafAccent))
              Spacer(Modifier.width(4.dp))
              Text("Recommended Window", fontSize = 11.sp, color = TextSecondary)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Recommended consumption window banner: "Day 3 - 15"
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PaleSageLight),
        border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            "Recommended consumption window:",
            fontSize = 12.sp,
            color = ForestGreenPrimary
          )
          Text(
            "Day 3 – 15",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------
// SCREEN 13: DETAIL TAB - "SUSTAINABILITY" & ECO SCORE
// -------------------------------------------------------------
@Composable
fun DetailSustainabilityScreen(
  material: PackagingMaterial,
  onSelectTab: (Int) -> Unit,
  onNavigateBack: () -> Unit,
  onNavigateNext: () -> Unit
) {
  Scaffold(
    topBar = {
      TopAppBarHeader(title = "Sustainability & Cost", onNavigateBack = onNavigateBack)
    },
    bottomBar = {
      Surface(
        color = AppBackground,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
      ) {
        Button(
          onClick = onNavigateNext,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("sustainability_next_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("View Final Packaging Report", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
      // Segmented Tabs
      SegmentedTabRow(
        tabs = listOf("Shelf Life", "Cost", "Sustainability"),
        selectedIndex = 2,
        onTabSelected = { idx -> onSelectTab(idx + 1) }
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Main Card: "Packaging Eco Score"
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text("Packaging Eco Score", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

          Spacer(modifier = Modifier.height(14.dp))

          // Circular Score Badge: 8.4 / 10
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .background(MintLeafAccent, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Eco, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(
                text = "${material.ecoScore} / 10",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ForestGreenPrimary
              )
              Text(
                "Environmentally responsible choice",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Parameter Bars: Recyclability, Material impact, Material usage, Carbon impact, Biodegradability
          ScoreParameterRow("Recyclability", material.recyclability)
          Spacer(modifier = Modifier.height(10.dp))
          ScoreParameterRow("Material impact", material.materialImpact)
          Spacer(modifier = Modifier.height(10.dp))
          ScoreParameterRow("Material usage", material.materialUsage)
          Spacer(modifier = Modifier.height(10.dp))
          ScoreParameterRow("Carbon impact", material.carbonImpact)
          Spacer(modifier = Modifier.height(10.dp))
          ScoreParameterRow("Biodegradability", material.biodegradability)
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Environmental Impact Reduction (D3 / Recharts visualizer)
      EnvironmentalImpactVisualizer(
        recommendedMaterialName = material.name,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(18.dp))

      // "Better Alternative" Prompt Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaleSageLight),
        border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🌱", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              "Better Alternative",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreenPrimary
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Switching to Bio-Film could improve sustainability by 12%, but may reduce estimated shelf life by 2 days.",
            fontSize = 13.sp,
            color = ForestGreenPrimary,
            lineHeight = 18.sp
          )
        }
      }
    }
  }
}

@Composable
private fun ScoreParameterRow(name: String, score: Int) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = name,
      fontSize = 13.sp,
      color = TextPrimary,
      modifier = Modifier.width(120.dp)
    )
    LinearProgressIndicator(
      progress = { score / 10f },
      modifier = Modifier
        .weight(1f)
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp)),
      color = MintLeafAccent,
      trackColor = Color(0xFFE5E7EB)
    )
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = "$score/10",
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      color = ForestGreenPrimary,
      modifier = Modifier.width(42.dp),
      textAlign = TextAlign.End
    )
  }
}

// -------------------------------------------------------------
// SCREEN 14: PACKAGING SUMMARY REPORT & TRACEABILITY
// -------------------------------------------------------------
@Composable
fun ReportTraceabilityScreen(
  commodity: CommodityItem,
  storage: StorageConfig,
  material: PackagingMaterial,
  user: UserAccount = UserAccount("karthikmiryabbelli@gmail.com", "Karthik"),
  onNavigateBack: () -> Unit,
  onShareReport: () -> Unit,
  onDownloadPdf: () -> Unit
) {
  val context = LocalContext.current
  var showDownloadSuccess by remember { mutableStateOf(false) }
  var showInAppShareDialog by remember { mutableStateOf(false) }
  var downloadedPdfFile by remember { mutableStateOf<File?>(null) }

  val triggerDownload = {
    try {
      val file = PdfReportGenerator.generatePackagingReportPdf(
        context = context,
        commodity = commodity,
        material = material,
        storage = storage,
        user = user
      )
      downloadedPdfFile = file
      showDownloadSuccess = true
      onDownloadPdf()
    } catch (e: Exception) {
      Toast.makeText(context, "Error generating PDF: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
  }

  Scaffold(
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("report_back")) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ForestGreenPrimary)
        }
        Text(
          "Packaging Report",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreenPrimary
        )
        IconButton(onClick = { showInAppShareDialog = true }) {
          Icon(Icons.Default.Share, contentDescription = "Share", tint = ForestGreenPrimary)
        }
      }
    },
    bottomBar = {
      Surface(
        color = AppBackground,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
      ) {
        Button(
          onClick = triggerDownload,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("download_report_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Download Report (PDF)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
      if (showDownloadSuccess) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = PaleSageTint),
          modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreenPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              "Report PDF downloaded to device storage!",
              fontSize = 13.sp,
              color = ForestGreenPrimary,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Product Header Card: Tomato thumbnail + Packaging Title
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .background(PaleSageLight, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(commodity.emoji, fontSize = 36.sp)
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column {
            Text(
              text = commodity.name,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreenPrimary
            )
            Text(
              text = "Packaging: ${material.name}",
              fontSize = 13.sp,
              color = TextSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Data Table Card
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          ReportTableRow("Expected Shelf Life", "${material.expectedShelfLife} days")
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 6.dp))

          ReportTableRow("Storage Environment", "${storage.storageType.label} (${storage.temperatureC.toInt()}°C, ${storage.relativeHumidityPercent.toInt()}% RH)")
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 6.dp))

          ReportTableRow("Food Moisture & Fat", "${commodity.moistureContent}% Moisture • ${commodity.fatContent}% Fat")
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 6.dp))

          ReportTableRow("Oxygen Barrier (OTR)", material.technicalSpecs.otrValue)
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 6.dp))

          ReportTableRow("Moisture Barrier (WVTR)", material.technicalSpecs.wvtrValue)
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 6.dp))

          ReportTableRow("MAP Gas Flush (O₂/CO₂/N₂)", "${material.mapSpecs.o2Percentage} / ${material.mapSpecs.co2Percentage} / ${material.mapSpecs.n2Percentage}")
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 6.dp))

          ReportTableRow("Packaging Structure", material.technicalSpecs.packagingStructure)
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 6.dp))

          ReportTableRow("Film Thickness", material.filmThickness)
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 6.dp))

          ReportTableRow("Recycling Stream", material.recyclingCode)
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 6.dp))

          ReportTableRow("Estimated Cost", material.costPerKg)
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 6.dp))

          ReportTableRow("Eco Score", "${material.ecoScore} / 10")
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 6.dp))

          ReportTableRow("Batch Reference ID", material.batchId)
          HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 6.dp))

          ReportTableRow("Certification Date", material.date)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Dynamically generated ZXing QR code encoding active batch details into JSON string
      DynamicBatchQrCode(
        batchId = material.batchId,
        commodityName = commodity.name,
        materialName = material.name,
        expectedShelfLifeDays = material.expectedShelfLife.toInt(),
        ecoScore = material.ecoScore.toDouble(),
        date = material.date,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  if (showDownloadSuccess && downloadedPdfFile != null) {
    PdfDownloadSuccessDialog(
      file = downloadedPdfFile!!,
      commodity = commodity,
      material = material,
      onDismiss = { showDownloadSuccess = false }
    )
  }

  if (showInAppShareDialog) {
    InAppShareDialog(
      commodity = commodity,
      material = material,
      storage = storage,
      onDismiss = { showInAppShareDialog = false },
      onDownloadPdf = triggerDownload
    )
  }
}

@Composable
private fun SpecificationRowItem(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 5.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Top
  ) {
    Text(
      text = label,
      fontSize = 12.sp,
      color = TextSecondary,
      modifier = Modifier.width(135.dp)
    )
    Text(
      text = value,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      color = ForestGreenPrimary,
      textAlign = TextAlign.End,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun GasFlushBadge(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = Color.White,
    border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(label, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(2.dp))
      Text(value, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = color)
    }
  }
}

@Composable
private fun ReportTableRow(label: String, value: String) {

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(label, fontSize = 13.sp, color = TextSecondary)
    Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
  }
}

@Composable
private fun TopAppBarHeader(title: String, onNavigateBack: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconButton(onClick = onNavigateBack) {
      Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ForestGreenPrimary)
    }
    Text(
      title,
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      color = ForestGreenPrimary
    )
  }
}

@Composable
private fun SegmentedTabRow(
  tabs: List<String>,
  selectedIndex: Int,
  onTabSelected: (Int) -> Unit
) {
  ScrollableTabRow(
    selectedTabIndex = selectedIndex,
    containerColor = Color.Transparent,
    contentColor = ForestGreenPrimary,
    edgePadding = 0.dp,
    indicator = { tabPositions ->
      if (selectedIndex < tabPositions.size) {
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
          color = ForestGreenPrimary,
          height = 3.dp
        )
      }
    },
    divider = {}
  ) {
    tabs.forEachIndexed { index, title ->
      Tab(
        selected = index == selectedIndex,
        onClick = { onTabSelected(index) },
        text = {
          Text(
            title,
            fontSize = 13.sp,
            fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Medium,
            color = if (index == selectedIndex) ForestGreenPrimary else TextSecondary
          )
        }
      )
    }
  }
}
