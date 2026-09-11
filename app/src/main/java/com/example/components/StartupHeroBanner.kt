package com.example.components

import android.graphics.BitmapFactory
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Startup Hero Banner:
 * - Automatically checks if an original image file (e.g. ecowrap_startup.png, startup.png, image.png)
 *   has been uploaded to res/drawable or app assets.
 * - Otherwise renders the full-fidelity EcoWrap sunrise agricultural artwork with glowing 3D sprout cube.
 */
@Composable
fun EcoWrapStartupHeroCard(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val customBitmap = remember {
    var bmp: android.graphics.Bitmap? = null
    // Try to load from assets
    val candidateAssetNames = listOf(
      "ecowrap_startup.png",
      "ecowrap_startup.jpg",
      "startup.png",
      "startup.jpg",
      "image.png",
      "image.jpg",
      "hero.png"
    )
    for (name in candidateAssetNames) {
      try {
        context.assets.open(name).use { input ->
          bmp = BitmapFactory.decodeStream(input)
        }
        if (bmp != null) break
      } catch (_: Exception) {}
    }
    // Try to load from res/drawable dynamically
    if (bmp == null) {
      val candidateResNames = listOf("ecowrap_startup", "startup_image", "img_startup", "image")
      for (resName in candidateResNames) {
        val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
        if (resId != 0) {
          try {
            bmp = BitmapFactory.decodeResource(context.resources, resId)
            if (bmp != null) break
          } catch (_: Exception) {}
        }
      }
    }
    bmp
  }

  if (customBitmap != null) {
    Card(
      modifier = modifier
        .fillMaxWidth()
        .shadow(16.dp, RoundedCornerShape(24.dp))
        .testTag("startup_hero_card"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2418)),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2E7D32).copy(alpha = 0.6f))
    ) {
      Image(
        bitmap = customBitmap.asImageBitmap(),
        contentDescription = "EcoWrap Startup Official Image",
        modifier = Modifier
          .fillMaxWidth()
          .height(310.dp)
          .clip(RoundedCornerShape(24.dp)),
        contentScale = ContentScale.Crop
      )
    }
    return
  }

  // Gentle pulsing glow for the eco cube
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.6f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glowAlpha"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .shadow(16.dp, RoundedCornerShape(24.dp))
      .testTag("startup_hero_card"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2418)),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2E7D32).copy(alpha = 0.6f))
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(310.dp)
        .clip(RoundedCornerShape(24.dp))
    ) {
      // 1. Background Sunrise & Agricultural Fields Canvas
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Sky & Sunrise Glow
        val skyGradient = Brush.verticalGradient(
          0.0f to Color(0xFF1E3A2B),
          0.35f to Color(0xFF7C6D39),
          0.55f to Color(0xFFD4A359),
          0.70f to Color(0xFF335C3D),
          1.0f to Color(0xFF132A1C)
        )
        drawRect(brush = skyGradient)

        // Sunrise Sun rays and disk
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xFFFFF7D6).copy(alpha = 0.9f),
              Color(0xFFFFD54F).copy(alpha = 0.6f),
              Color(0xFFFFB74D).copy(alpha = 0.2f),
              Color.Transparent
            ),
            center = Offset(w * 0.72f, h * 0.28f),
            radius = w * 0.45f
          ),
          center = Offset(w * 0.72f, h * 0.28f),
          radius = w * 0.45f
        )

        // Distant rolling misty hills
        val hillPath1 = Path().apply {
          moveTo(0f, h * 0.50f)
          cubicTo(w * 0.25f, h * 0.42f, w * 0.55f, h * 0.48f, w, h * 0.44f)
          lineTo(w, h * 0.75f)
          lineTo(0f, h * 0.75f)
          close()
        }
        drawPath(hillPath1, color = Color(0xFF3D6B42).copy(alpha = 0.75f))

        val hillPath2 = Path().apply {
          moveTo(0f, h * 0.56f)
          cubicTo(w * 0.35f, h * 0.51f, w * 0.70f, h * 0.59f, w, h * 0.52f)
          lineTo(w, h * 0.78f)
          lineTo(0f, h * 0.78f)
          close()
        }
        drawPath(hillPath2, color = Color(0xFF234B29).copy(alpha = 0.9f))

        // Rich rustic wooden tabletop in foreground
        val woodTableGradient = Brush.verticalGradient(
          0.0f to Color(0xFF4E3629),
          0.2f to Color(0xFF3E2723),
          0.7f to Color(0xFF271712),
          1.0f to Color(0xFF1B0E0A)
        )
        drawRect(
          brush = woodTableGradient,
          topLeft = Offset(0f, h * 0.76f),
          size = androidx.compose.ui.geometry.Size(w, h * 0.24f)
        )

        // Wood grain subtle lines
        drawLine(
          color = Color(0xFF5D4037).copy(alpha = 0.6f),
          start = Offset(0f, h * 0.765f),
          end = Offset(w, h * 0.765f),
          strokeWidth = 2f
        )
        drawLine(
          color = Color(0xFF2A1810).copy(alpha = 0.8f),
          start = Offset(0f, h * 0.84f),
          end = Offset(w, h * 0.84f),
          strokeWidth = 1.5f
        )
      }

      // Dark translucent vignette overlay for superior text contrast
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              0.0f to Color(0x73000000),
              0.45f to Color(0x8C000000),
              0.75f to Color(0x99000000),
              1.0f to Color(0xCC000000)
            )
          )
      )

      // 2. Main Content Overlay (Logo, Title, Subtitle, Tagline, Produce)
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // TOP: Glowing Eco Sprout Cube & Brand Title
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(top = 4.dp)
        ) {
          // 3D Glowing Green Eco Cube with Sprout
          Box(
            modifier = Modifier
              .size(54.dp),
            contentAlignment = Alignment.Center
          ) {
            // Glow aura
            Box(
              modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFF22C55E).copy(alpha = 0.28f * glowAlpha))
            )

            // Isometric Cube & Sprout Drawing
            Canvas(modifier = Modifier.size(44.dp)) {
              val cw = size.width
              val ch = size.height

              // Top face of isometric cube
              val topFace = Path().apply {
                moveTo(cw * 0.5f, ch * 0.22f)
                lineTo(cw * 0.82f, ch * 0.38f)
                lineTo(cw * 0.5f, ch * 0.54f)
                lineTo(cw * 0.18f, ch * 0.38f)
                close()
              }
              drawPath(topFace, color = Color(0xFFDCFCE7).copy(alpha = 0.85f))
              drawPath(topFace, color = Color(0xFF22C55E), style = Stroke(width = 2.dp.toPx()))

              // Left face
              val leftFace = Path().apply {
                moveTo(cw * 0.18f, ch * 0.38f)
                lineTo(cw * 0.5f, ch * 0.54f)
                lineTo(cw * 0.5f, ch * 0.88f)
                lineTo(cw * 0.18f, ch * 0.72f)
                close()
              }
              drawPath(leftFace, color = Color(0xFF15803D).copy(alpha = 0.8f))
              drawPath(leftFace, color = Color(0xFF22C55E), style = Stroke(width = 2.dp.toPx()))

              // Right face
              val rightFace = Path().apply {
                moveTo(cw * 0.5f, ch * 0.54f)
                lineTo(cw * 0.82f, ch * 0.38f)
                lineTo(cw * 0.82f, ch * 0.72f)
                lineTo(cw * 0.5f, ch * 0.88f)
                close()
              }
              drawPath(rightFace, color = Color(0xFF166534).copy(alpha = 0.85f))
              drawPath(rightFace, color = Color(0xFF4ADE80), style = Stroke(width = 2.dp.toPx()))

              // Green leaf sprout rising from cube center
              val sproutPath = Path().apply {
                moveTo(cw * 0.5f, ch * 0.52f)
                cubicTo(cw * 0.45f, ch * 0.30f, cw * 0.32f, ch * 0.20f, cw * 0.5f, ch * 0.05f)
                cubicTo(cw * 0.68f, ch * 0.20f, cw * 0.55f, ch * 0.30f, cw * 0.5f, ch * 0.52f)
                close()
              }
              drawPath(sproutPath, color = Color(0xFF4ADE80))
              drawPath(sproutPath, color = Color(0xFFFFFFFF), style = Stroke(width = 1.2.dp.toPx()))

              // Right branchlet
              val branchPath = Path().apply {
                moveTo(cw * 0.5f, ch * 0.35f)
                cubicTo(cw * 0.65f, ch * 0.32f, cw * 0.75f, ch * 0.26f, cw * 0.78f, ch * 0.20f)
              }
              drawPath(branchPath, color = Color(0xFF86EFAC), style = Stroke(width = 2.dp.toPx()))
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          // Brand Title: EcoWrap
          Column {
            Text(
              text = "EcoWrap",
              fontSize = 32.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFF34D399),
              letterSpacing = 0.5.sp,
              style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                  color = Color(0xFF064E3B),
                  offset = Offset(2f, 2f),
                  blurRadius = 6f
                )
              )
            )
          }
        }

        // MIDDLE: Full Headline Description
        Column(
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
          Text(
            text = "AI-Based Intelligent Food Packaging Material Recommendation System for Food Commodities",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF9FAFB),
            lineHeight = 21.sp,
            textAlign = TextAlign.Start,
            style = androidx.compose.ui.text.TextStyle(
              shadow = androidx.compose.ui.graphics.Shadow(
                color = Color.Black,
                offset = Offset(1.5f, 1.5f),
                blurRadius = 4f
              )
            )
          )
        }

        // BOTTOM: Tagline + Visual Harvest Basket of Commodities
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          // Motto: Right Food • Right Package • A Greener Future
          Column(modifier = Modifier.weight(1f)) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0x66000000),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4ADE80).copy(alpha = 0.4f))
            ) {
              Text(
                text = "Right Food • Right Package • A Greener Future",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFE5E7EB),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Foreground Commodity Icons (Basket with Rice, Tomatoes, Grains, Vegetables)
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0x8A1B2E21),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.6f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("🌾", fontSize = 18.sp)
              Text("🍅", fontSize = 18.sp)
              Text("🍚", fontSize = 18.sp)
              Text("🥬", fontSize = 18.sp)
            }
          }
        }
      }
    }
  }
}
