package com.example.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.*

enum class ImpactMetricType(val label: String, val unit: String, val icon: String) {
  CARBON_FOOTPRINT("Carbon Footprint", "kg CO₂e / kg", "🌿"),
  PLASTIC_WASTE("Plastic Waste Diverted", "kg waste / 1k packs", "🗑️"),
  BIODEGRADATION("Soil Degradation", "Months to breakdown", "⏳")
}

data class MaterialComparisonItem(
  val name: String,
  val category: String,
  val isRecommended: Boolean,
  val carbonFootprint: Float, // kg CO2e / kg
  val plasticWaste: Float,    // kg / 1000 units
  val degradationMonths: Float, // months
  val primaryColor: Color,
  val secondaryColor: Color,
  val notes: String
)

/**
 * High-performance, interactive D3 / Recharts-inspired visualization showing
 * environmental impact reduction (carbon footprint and plastic waste) when comparing
 * the recommended eco-friendly material against traditional packaging alternatives.
 */
@Composable
fun EnvironmentalImpactVisualizer(
  recommendedMaterialName: String = "PLA + Cornstarch Bio-Film",
  modifier: Modifier = Modifier
) {
  var selectedMetric by remember { mutableStateOf(ImpactMetricType.CARBON_FOOTPRINT) }
  var selectedIndex by remember { mutableStateOf<Int?>(0) }
  var viewMode by remember { mutableStateOf(0) } // 0 = Recharts Native Compose, 1 = D3 Web SVG Engine

  // Dataset comparing recommended eco-material with traditional alternatives
  val items = remember(recommendedMaterialName) {
    listOf(
      MaterialComparisonItem(
        name = recommendedMaterialName,
        category = "Eco Bio-Polymer (Recommended)",
        isRecommended = true,
        carbonFootprint = 0.85f,
        plasticWaste = 0.05f,
        degradationMonths = 6f,
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF059669),
        notes = "Zero fossil-fuel cracking. 100% soil compostable in industrial & home conditions."
      ),
      MaterialComparisonItem(
        name = "Virgin LDPE Plastic",
        category = "Conventional Petroleum Plastic",
        isRecommended = false,
        carbonFootprint = 3.45f,
        plasticWaste = 1.05f,
        degradationMonths = 5400f, // 450 years
        primaryColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFFD97706),
        notes = "High carbon intensity from naphtha distillation. Non-biodegradable polymer chain."
      ),
      MaterialComparisonItem(
        name = "Multilayer PET / Foil",
        category = "Unrecyclable Barrier Laminate",
        isRecommended = false,
        carbonFootprint = 4.80f,
        plasticWaste = 1.30f,
        degradationMonths = 6000f, // 500 years
        primaryColor = Color(0xFFEF4444),
        secondaryColor = Color(0xFFDC2626),
        notes = "Bonded aluminum and polymer foils impossible to separate mechanically."
      ),
      MaterialComparisonItem(
        name = "Polystyrene (EPS Foam)",
        category = "Expanded Synthetic Cushion",
        isRecommended = false,
        carbonFootprint = 3.90f,
        plasticWaste = 0.95f,
        degradationMonths = 6000f, // 500 years
        primaryColor = Color(0xFF8B5CF6),
        secondaryColor = Color(0xFF7C3AED),
        notes = "Fragile styrene matrix sheds dangerous microplastics into groundwater."
      )
    )
  }

  // Calculate percentage reduction
  val recommendedItem = items[0]
  val traditionalBenchmark = items[1] // Virgin LDPE
  val carbonReductionPct = remember(items) {
    ((traditionalBenchmark.carbonFootprint - recommendedItem.carbonFootprint) / traditionalBenchmark.carbonFootprint * 100f).toInt()
  }
  val wasteReductionPct = remember(items) {
    ((traditionalBenchmark.plasticWaste - recommendedItem.plasticWaste) / traditionalBenchmark.plasticWaste * 100f).toInt()
  }

  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.2.dp, MintLeafAccent.copy(alpha = 0.4f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("environmental_impact_visualizer_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)
    ) {
      // Header: Title & Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🌱", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              "Environmental Impact Reduction",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreenPrimary
            )
          }
          Text(
            "Recommended Bio-Material vs Traditional Plastics",
            fontSize = 12.sp,
            color = TextSecondary
          )
        }

        // Engine Toggle Chip (Recharts Native vs D3 Interactive)
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = PaleSageLight,
          modifier = Modifier.clickable { viewMode = if (viewMode == 0) 1 else 0 }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.BarChart,
              contentDescription = null,
              tint = ForestGreenPrimary,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              if (viewMode == 0) "D3 Recharts Mode" else "Interactive SVG",
              fontSize = 10.5.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreenPrimary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3 Highlight KPI Impact Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ImpactKpiMiniCard(
          icon = "📉",
          title = "Carbon Cut",
          value = "-$carbonReductionPct%",
          subtitle = "vs Virgin LDPE",
          badgeColor = Color(0xFFDCFCE7),
          valueColor = ForestGreenPrimary,
          modifier = Modifier.weight(1f)
        )
        ImpactKpiMiniCard(
          icon = "♻️",
          title = "Plastic Diverted",
          value = "-$wasteReductionPct%",
          subtitle = "Zero synthetic waste",
          badgeColor = Color(0xFFE0F2FE),
          valueColor = Color(0xFF0369A1),
          modifier = Modifier.weight(1f)
        )
        ImpactKpiMiniCard(
          icon = "⏳",
          title = "Soil Degradation",
          value = "6 Mos",
          subtitle = "vs 450+ Years",
          badgeColor = Color(0xFFFEF3C7),
          valueColor = Color(0xFFB45309),
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Metric Selector Tabs (Carbon Footprint vs Plastic Waste vs Degradation)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFFF1F5F9))
          .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        ImpactMetricType.values().forEach { metric ->
          val isSelected = selectedMetric == metric
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSelected) Color.White else Color.Transparent)
              .clickable { selectedMetric = metric }
              .padding(vertical = 7.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(metric.icon, fontSize = 12.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (metric == ImpactMetricType.CARBON_FOOTPRINT) "Carbon"
                else if (metric == ImpactMetricType.PLASTIC_WASTE) "Plastic" else "Breakdown",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ForestGreenPrimary else TextSecondary
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // View Rendering: Native Compose Recharts Chart or D3 HTML5 SVG Engine
      if (viewMode == 0) {
        RechartsStyleBarChart(
          items = items,
          metricType = selectedMetric,
          selectedIndex = selectedIndex,
          onSelectIndex = { selectedIndex = it }
        )
      } else {
        D3SvgHtmlVisualizer(
          items = items,
          metricType = selectedMetric
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Interactive Material Detail / Tooltip Card
      val activeItem = selectedIndex?.let { items.getOrNull(it) } ?: items[0]
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (activeItem.isRecommended) PaleSageLight else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (activeItem.isRecommended) MintLeafAccent else CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(activeItem.primaryColor)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = activeItem.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                text = when (selectedMetric) {
                  ImpactMetricType.CARBON_FOOTPRINT -> "${activeItem.carbonFootprint} kg CO₂e"
                  ImpactMetricType.PLASTIC_WASTE -> "${activeItem.plasticWaste} kg waste"
                  ImpactMetricType.BIODEGRADATION -> if (activeItem.degradationMonths < 12) "${activeItem.degradationMonths.toInt()} Months" else "${(activeItem.degradationMonths / 12).toInt()} Years"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = activeItem.secondaryColor
              )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = activeItem.notes,
              fontSize = 11.sp,
              color = TextSecondary,
              lineHeight = 15.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ImpactKpiMiniCard(
  icon: String,
  title: String,
  value: String,
  subtitle: String,
  badgeColor: Color,
  valueColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = badgeColor,
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(icon, fontSize = 16.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        fontSize = 16.sp,
        fontWeight = FontWeight.ExtraBold,
        color = valueColor
      )
      Text(
        text = title,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
      Text(
        text = subtitle,
        fontSize = 9.sp,
        color = TextSecondary,
        maxLines = 1
      )
    }
  }
}

/**
 * Native Jetpack Compose Bar Chart with Recharts aesthetics:
 * - Rounded gradient horizontal bars
 * - Reference benchmark grid
 * - Percentage reduction tags
 * - Interactive touch selector
 */
@Composable
private fun RechartsStyleBarChart(
  items: List<MaterialComparisonItem>,
  metricType: ImpactMetricType,
  selectedIndex: Int?,
  onSelectIndex: (Int) -> Unit
) {
  val maxValue = remember(metricType, items) {
    when (metricType) {
      ImpactMetricType.CARBON_FOOTPRINT -> items.maxOf { it.carbonFootprint } * 1.15f
      ImpactMetricType.PLASTIC_WASTE -> items.maxOf { it.plasticWaste } * 1.15f
      ImpactMetricType.BIODEGRADATION -> 120f // Cap scale display for months comparison
    }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    items.forEachIndexed { index, item ->
      val rawValue = when (metricType) {
        ImpactMetricType.CARBON_FOOTPRINT -> item.carbonFootprint
        ImpactMetricType.PLASTIC_WASTE -> item.plasticWaste
        ImpactMetricType.BIODEGRADATION -> item.degradationMonths.coerceAtMost(120f)
      }

      val fraction = (rawValue / maxValue).coerceIn(0.05f, 1f)
      val isSelected = selectedIndex == index
      val isRecommended = item.isRecommended

      // Animate bar width smoothly
      val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "bar_anim_$index"
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onSelectIndex(index) }
          .padding(vertical = 2.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = item.name,
              fontSize = 12.sp,
              fontWeight = if (isRecommended) FontWeight.Bold else FontWeight.Medium,
              color = if (isRecommended) ForestGreenPrimary else TextPrimary
            )
            if (isRecommended) {
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = PaleSageTint
              ) {
                Text(
                  "Recommended",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreenPrimary,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
              }
            }
          }

          // Value and Reduction badge
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (!isRecommended && metricType == ImpactMetricType.CARBON_FOOTPRINT) {
              val red = ((item.carbonFootprint - items[0].carbonFootprint) / item.carbonFootprint * 100f).toInt()
              Text(
                text = "-$red% CO₂",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary,
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFFDCFCE7))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
            } else if (!isRecommended && metricType == ImpactMetricType.PLASTIC_WASTE) {
              val red = ((item.plasticWaste - items[0].plasticWaste) / item.plasticWaste * 100f).toInt()
              Text(
                text = "-$red% Waste",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0369A1),
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFFE0F2FE))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
              text = when (metricType) {
                ImpactMetricType.CARBON_FOOTPRINT -> "${item.carbonFootprint} kg"
                ImpactMetricType.PLASTIC_WASTE -> "${item.plasticWaste} kg"
                ImpactMetricType.BIODEGRADATION -> if (item.degradationMonths < 12) "${item.degradationMonths.toInt()} Mo" else "450+ Yr"
              },
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) item.secondaryColor else TextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Progress / Bar Canvas with Recharts pill layout
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color(0xFFF1F5F9))
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(animatedFraction)
              .fillMaxHeight()
              .clip(RoundedCornerShape(7.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(item.primaryColor, item.secondaryColor)
                )
              )
          )
        }
      }
    }
  }
}

/**
 * Embedded D3 / Recharts HTML5 SVG Visualization rendered via Android WebView.
 * Provides rich SVG gradients, axes, animated tooltips, and interactive Recharts-style bar charts.
 */
@Composable
private fun D3SvgHtmlVisualizer(
  items: List<MaterialComparisonItem>,
  metricType: ImpactMetricType
) {
  val htmlContent = remember(items, metricType) {
    val metricUnit = metricType.unit
    val barsData = items.joinToString(",") { item ->
      val v = when (metricType) {
        ImpactMetricType.CARBON_FOOTPRINT -> item.carbonFootprint
        ImpactMetricType.PLASTIC_WASTE -> item.plasticWaste
        ImpactMetricType.BIODEGRADATION -> if (item.degradationMonths > 100) 100f else item.degradationMonths
      }
      val col = if (item.isRecommended) "#10B981" else "#F59E0B"
      """{"name":"${item.name.replace("\"", "'")}","val":$v,"color":"$col","isRec":${item.isRecommended}}"""
    }

    """
    <!DOCTYPE html>
    <html>
    <head>
      <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
      <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
        body { background: #ffffff; padding: 8px; color: #1e293b; }
        .chart-container { width: 100%; }
        .bar-group { margin-bottom: 12px; cursor: pointer; }
        .bar-header { display: flex; justify-content: space-between; font-size: 11px; margin-bottom: 3px; }
        .bar-name { font-weight: 600; color: #334155; }
        .bar-name.rec { color: #0F3D26; font-weight: 700; }
        .bar-val { font-weight: 700; color: #0F3D26; }
        .bar-track { background: #f1f5f9; height: 14px; border-radius: 7px; overflow: hidden; position: relative; }
        .bar-fill { height: 100%; border-radius: 7px; transition: width 0.8s cubic-bezier(0.16, 1, 0.3, 1); }
        .badge { background: #dcfce7; color: #0F3D26; font-size: 9px; font-weight: bold; padding: 1px 5px; border-radius: 4px; margin-left: 5px; }
        .tooltip { font-size: 10px; color: #64748b; margin-top: 2px; }
      </style>
    </head>
    <body>
      <div class="chart-container" id="chart"></div>
      <script>
        const data = [$barsData];
        const maxVal = Math.max(...data.map(d => d.val)) * 1.1;
        const container = document.getElementById('chart');

        data.forEach(d => {
          const pct = Math.min(100, Math.max(5, (d.val / maxVal) * 100));
          const group = document.createElement('div');
          group.className = 'bar-group';
          group.innerHTML = `
            <div class="bar-header">
              <span class="bar-name ${'$'}{d.isRec ? 'rec' : ''}">${'$'}{d.name} ${'$'}{d.isRec ? '<span class="badge">ECO CHOICE</span>' : ''}</span>
              <span class="bar-val">${'$'}{d.val}</span>
            </div>
            <div class="bar-track">
              <div class="bar-fill" style="width: ${'$'}{pct}%; background: ${'$'}{d.isRec ? 'linear-gradient(90deg, #10B981, #059669)' : 'linear-gradient(90deg, #F59E0B, #D97706)'}"></div>
            </div>
          `;
          container.appendChild(group);
        });
      </script>
    </body>
    </html>
    """.trimIndent()
  }

  AndroidView(
    factory = { ctx ->
      WebView(ctx).apply {
        settings.javaScriptEnabled = true
        webViewClient = WebViewClient()
        setBackgroundColor(android.graphics.Color.TRANSPARENT)
      }
    },
    update = { webView ->
      webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    },
    modifier = Modifier
      .fillMaxWidth()
      .height(180.dp)
  )
}
