package com.example.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Reusable animated linear gradient shimmer brush.
 */
@Composable
fun rememberShimmerBrush(
  shimmerColors: List<Color> = listOf(
    Color(0xFFE2E8F0).copy(alpha = 0.65f),
    Color(0xFFF1F5F9).copy(alpha = 0.95f),
    Color(0xFFFFFFFF).copy(alpha = 0.98f),
    Color(0xFFF1F5F9).copy(alpha = 0.95f),
    Color(0xFFE2E8F0).copy(alpha = 0.65f)
  )
): Brush {
  val transition = rememberInfiniteTransition(label = "shimmer_transition")
  val translateAnim by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1400f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "shimmer_translate"
  )
  return Brush.linearGradient(
    colors = shimmerColors,
    start = Offset(translateAnim - 500f, translateAnim - 500f),
    end = Offset(translateAnim, translateAnim)
  )
}

/**
 * Shimmer Loading Animation for Food Commodity Categories & Product Grid.
 */
@Composable
fun CommodityGridShimmerLoading(
  modifier: Modifier = Modifier,
  count: Int = 6
) {
  val brush = rememberShimmerBrush()

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Subtle loading indicator label
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(MintLeafAccent)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "Fetching verified food biochemical standards...",
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = ForestGreenPrimary
      )
    }

    // Grid of Shimmer Cards (2 columns x 3 rows or 3 columns)
    val rows = count / 3
    for (r in 0 until rows) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        for (c in 0 until 3) {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              // Shimmer Avatar Circle
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .clip(CircleShape)
                  .background(brush)
              )

              Spacer(modifier = Modifier.height(10.dp))

              // Shimmer Commodity Name
              Box(
                modifier = Modifier
                  .width(62.dp)
                  .height(13.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .background(brush)
              )

              Spacer(modifier = Modifier.height(6.dp))

              // Shimmer Variety / Moisture Pill
              Box(
                modifier = Modifier
                  .width(42.dp)
                  .height(10.dp)
                  .clip(RoundedCornerShape(3.dp))
                  .background(brush)
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Shimmer Loading Animation for AI-based Material Recommendations.
 */
@Composable
fun AiRecommendationShimmerLoading(
  modifier: Modifier = Modifier
) {
  val brush = rememberShimmerBrush()

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Status banner
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(PaleSageLight)
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(10.dp)
          .clip(CircleShape)
          .background(MintLeafAccent)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "PackWise AI evaluating OTR/WVTR & barrier metrics...",
        fontSize = 11.5.sp,
        fontWeight = FontWeight.SemiBold,
        color = ForestGreenPrimary
      )
    }

    // Hero AI Recommendation Card Shimmer
    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, CardBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Match Tag Shimmer
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Box(
            modifier = Modifier
              .width(84.dp)
              .height(22.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(brush)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Center 3D Packaging Visual Shimmer Box
        Box(
          modifier = Modifier
            .size(130.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(brush)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Material Name Shimmer
        Box(
          modifier = Modifier
            .width(210.dp)
            .height(22.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(brush)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description Shimmer Lines
        Box(
          modifier = Modifier
            .width(260.dp)
            .height(13.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(brush)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Box(
          modifier = Modifier
            .width(180.dp)
            .height(13.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(brush)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Circular Score Gauge Shimmer
        Box(
          modifier = Modifier
            .size(86.dp)
            .clip(CircleShape)
            .background(brush)
        )
      }
    }

    // Key Parameters 2x2 Shimmer Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.weight(1f)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Box(
            modifier = Modifier
              .width(70.dp)
              .height(11.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(brush)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .width(95.dp)
              .height(18.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(brush)
          )
        }
      }

      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.weight(1f)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Box(
            modifier = Modifier
              .width(70.dp)
              .height(11.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(brush)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .width(95.dp)
              .height(18.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(brush)
          )
        }
      }
    }

    // Grounded Research Card Shimmer
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, CardBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Box(
          modifier = Modifier
            .width(180.dp)
            .height(16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(brush)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(brush)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth(0.65f)
            .height(12.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(brush)
        )
      }
    }
  }
}
