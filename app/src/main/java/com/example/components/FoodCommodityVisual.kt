package com.example.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CommodityItem

/**
 * Dedicated Custom Vector Illustration component for Food Commodities.
 * Renders distinct, authentic vector artwork for Grains, Dairy, and Produce,
 * ensuring no two commodities share identical generic visuals.
 */
@Composable
fun FoodCommodityVisual(
  item: CommodityItem,
  modifier: Modifier = Modifier,
  size: Dp = 40.dp
) {
  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    when (item.id) {
      // ----------------- GRAINS -----------------
      "rice" -> RiceVectorGraphic(size = size)
      "wheat" -> WheatVectorGraphic(size = size)
      "barley" -> BarleyVectorGraphic(size = size)
      "millet" -> MilletVectorGraphic(size = size)
      "oats" -> OatsVectorGraphic(size = size)
      "quinoa" -> QuinoaVectorGraphic(size = size)
      "lentils" -> LentilsVectorGraphic(size = size)
      "chickpeas" -> ChickpeasVectorGraphic(size = size)

      // ----------------- DAIRY -----------------
      "fresh_milk" -> FreshMilkVectorGraphic(size = size)
      "paneer" -> PaneerVectorGraphic(size = size)
      "cream" -> DairyCreamVectorGraphic(size = size)
      "buttermilk" -> ButtermilkVectorGraphic(size = size)
      "khoya" -> KhoyaVectorGraphic(size = size)
      "butter" -> ButterVectorGraphic(size = size)
      "ghee" -> GheeVectorGraphic(size = size)
      "cheese" -> CheeseVectorGraphic(size = size)

      // Default & produce fallback to distinct assigned high-res emoji
      else -> {
        Text(text = item.emoji, fontSize = (size.value * 0.72f).sp)
      }
    }
  }
}

// -------------------------------------------------------------
// GRAINS VECTOR GRAPHICS
// -------------------------------------------------------------

@Composable
fun RiceVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Ceramic Bowl
    val bowlPath = Path().apply {
      moveTo(w * 0.15f, h * 0.45f)
      lineTo(w * 0.85f, h * 0.45f)
      cubicTo(w * 0.82f, h * 0.85f, w * 0.65f, h * 0.95f, w * 0.5f, h * 0.95f)
      cubicTo(w * 0.35f, h * 0.95f, w * 0.18f, h * 0.85f, w * 0.15f, h * 0.45f)
      close()
    }
    drawPath(bowlPath, color = Color(0xFFE2E8F0))
    drawPath(bowlPath, color = Color(0xFF0284C7), style = Stroke(width = w * 0.04f))

    // Rice mound (fluffy white)
    drawArc(
      color = Color(0xFFF8FAFC),
      startAngle = 180f,
      sweepAngle = 180f,
      useCenter = true,
      topLeft = Offset(w * 0.16f, h * 0.25f),
      size = Size(w * 0.68f, h * 0.42f)
    )

    // Rice grains texture
    val grainColor = Color(0xFFCBD5E1)
    drawCircle(color = grainColor, radius = w * 0.025f, center = Offset(w * 0.38f, h * 0.36f))
    drawCircle(color = grainColor, radius = w * 0.025f, center = Offset(w * 0.50f, h * 0.32f))
    drawCircle(color = grainColor, radius = w * 0.025f, center = Offset(w * 0.62f, h * 0.37f))

    // Chopsticks
    drawLine(
      color = Color(0xFF92400E),
      start = Offset(w * 0.10f, h * 0.20f),
      end = Offset(w * 0.85f, h * 0.48f),
      strokeWidth = w * 0.035f,
      cap = StrokeCap.Round
    )
    drawLine(
      color = Color(0xFFB45309),
      start = Offset(w * 0.15f, h * 0.15f),
      end = Offset(w * 0.88f, h * 0.42f),
      strokeWidth = w * 0.035f,
      cap = StrokeCap.Round
    )
  }
}

@Composable
fun WheatVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Central golden stem
    drawLine(
      color = Color(0xFFD97706),
      start = Offset(w * 0.5f, h * 0.95f),
      end = Offset(w * 0.5f, h * 0.12f),
      strokeWidth = w * 0.045f,
      cap = StrokeCap.Round
    )

    // Symmetrical golden grain kernels
    val kernelPositions = listOf(
      0.22f to -0.15f, 0.22f to 0.15f,
      0.35f to -0.20f, 0.35f to 0.20f,
      0.48f to -0.22f, 0.48f to 0.22f,
      0.61f to -0.20f, 0.61f to 0.20f,
      0.74f to -0.16f, 0.74f to 0.16f
    )

    for ((yRatio, xOffsetRatio) in kernelPositions) {
      drawOval(
        color = Color(0xFFF59E0B),
        topLeft = Offset(w * (0.5f + xOffsetRatio) - w * 0.07f, h * yRatio - h * 0.06f),
        size = Size(w * 0.14f, h * 0.12f)
      )
    }

    // Top awn / tip
    drawOval(
      color = Color(0xFFD97706),
      topLeft = Offset(w * 0.44f, h * 0.08f),
      size = Size(w * 0.12f, h * 0.15f)
    )
  }
}

@Composable
fun BarleyVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Barley stalk curving slightly to the left
    val stalkPath = Path().apply {
      moveTo(w * 0.6f, h * 0.95f)
      cubicTo(w * 0.55f, h * 0.6f, w * 0.45f, h * 0.4f, w * 0.4f, h * 0.15f)
    }
    drawPath(stalkPath, color = Color(0xFF65A30D), style = Stroke(width = w * 0.04f, cap = StrokeCap.Round))

    // Two-row alternating grain spikes
    val grains = listOf(
      Offset(w * 0.42f, h * 0.25f),
      Offset(w * 0.36f, h * 0.38f),
      Offset(w * 0.46f, h * 0.50f),
      Offset(w * 0.38f, h * 0.62f),
      Offset(w * 0.48f, h * 0.74f)
    )

    for (pos in grains) {
      drawOval(
        color = Color(0xFF84CC16),
        topLeft = Offset(pos.x - w * 0.07f, pos.y - h * 0.06f),
        size = Size(w * 0.15f, h * 0.11f)
      )
    }

    // Characteristic long, elegant barley awns (whiskers)
    drawLine(
      color = Color(0xFF4D7C0F),
      start = Offset(w * 0.40f, h * 0.15f),
      end = Offset(w * 0.20f, h * 0.04f),
      strokeWidth = w * 0.025f,
      cap = StrokeCap.Round
    )
    drawLine(
      color = Color(0xFF4D7C0F),
      start = Offset(w * 0.42f, h * 0.25f),
      end = Offset(w * 0.15f, h * 0.12f),
      strokeWidth = w * 0.025f,
      cap = StrokeCap.Round
    )
    drawLine(
      color = Color(0xFF4D7C0F),
      start = Offset(w * 0.46f, h * 0.35f),
      end = Offset(w * 0.68f, h * 0.18f),
      strokeWidth = w * 0.025f,
      cap = StrokeCap.Round
    )
  }
}

@Composable
fun MilletVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Terracotta earthen bowl / plate
    drawArc(
      color = Color(0xFF9A3412),
      startAngle = 0f,
      sweepAngle = 180f,
      useCenter = true,
      topLeft = Offset(w * 0.12f, h * 0.45f),
      size = Size(w * 0.76f, h * 0.48f)
    )

    // Heap of golden pearl millet seeds
    drawArc(
      color = Color(0xFFD97706),
      startAngle = 180f,
      sweepAngle = 180f,
      useCenter = true,
      topLeft = Offset(w * 0.18f, h * 0.25f),
      size = Size(w * 0.64f, h * 0.42f)
    )

    // Scattered distinct small pearl seeds
    val seedColor = Color(0xFFFEF3C7)
    val seeds = listOf(
      Offset(w * 0.32f, h * 0.36f), Offset(w * 0.42f, h * 0.32f), Offset(w * 0.52f, h * 0.34f),
      Offset(w * 0.62f, h * 0.38f), Offset(w * 0.38f, h * 0.44f), Offset(w * 0.48f, h * 0.42f),
      Offset(w * 0.58f, h * 0.45f), Offset(w * 0.45f, h * 0.50f)
    )
    for (s in seeds) {
      drawCircle(color = seedColor, radius = w * 0.032f, center = s)
    }
  }
}

@Composable
fun OatsVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Blue ceramic oatmeal bowl
    drawArc(
      color = Color(0xFF38BDF8),
      startAngle = 0f,
      sweepAngle = 180f,
      useCenter = true,
      topLeft = Offset(w * 0.15f, h * 0.45f),
      size = Size(w * 0.70f, h * 0.45f)
    )

    // Creamy oat porridge
    drawArc(
      color = Color(0xFFFFFBEB),
      startAngle = 180f,
      sweepAngle = 180f,
      useCenter = true,
      topLeft = Offset(w * 0.18f, h * 0.30f),
      size = Size(w * 0.64f, h * 0.36f)
    )

    // Rolled oat flakes scattered
    val flakeColor = Color(0xFFD97706)
    drawOval(color = flakeColor, topLeft = Offset(w * 0.32f, h * 0.38f), size = Size(w * 0.09f, h * 0.05f))
    drawOval(color = flakeColor, topLeft = Offset(w * 0.46f, h * 0.34f), size = Size(w * 0.10f, h * 0.05f))
    drawOval(color = flakeColor, topLeft = Offset(w * 0.60f, h * 0.39f), size = Size(w * 0.08f, h * 0.05f))

    // Silver Spoon dipping
    drawLine(
      color = Color(0xFF94A3B8),
      start = Offset(w * 0.75f, h * 0.12f),
      end = Offset(w * 0.48f, h * 0.45f),
      strokeWidth = w * 0.04f,
      cap = StrokeCap.Round
    )
  }
}

@Composable
fun QuinoaVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Modern bamboo wooden bowl
    drawArc(
      color = Color(0xFFB45309),
      startAngle = 0f,
      sweepAngle = 180f,
      useCenter = true,
      topLeft = Offset(w * 0.15f, h * 0.45f),
      size = Size(w * 0.70f, h * 0.45f)
    )

    // Multi-color quinoa grain mound
    drawArc(
      color = Color(0xFFFEF3C7),
      startAngle = 180f,
      sweepAngle = 180f,
      useCenter = true,
      topLeft = Offset(w * 0.18f, h * 0.28f),
      size = Size(w * 0.64f, h * 0.38f)
    )

    // Tri-color seeds: ivory, red, and black quinoa rings
    val redQuinoa = Color(0xFF991B1B)
    val blackQuinoa = Color(0xFF1E293B)
    val goldQuinoa = Color(0xFFD97706)

    drawCircle(color = redQuinoa, radius = w * 0.035f, center = Offset(w * 0.35f, h * 0.38f))
    drawCircle(color = blackQuinoa, radius = w * 0.035f, center = Offset(w * 0.50f, h * 0.33f))
    drawCircle(color = redQuinoa, radius = w * 0.035f, center = Offset(w * 0.62f, h * 0.37f))
    drawCircle(color = goldQuinoa, radius = w * 0.035f, center = Offset(w * 0.42f, h * 0.45f))
    drawCircle(color = blackQuinoa, radius = w * 0.035f, center = Offset(w * 0.55f, h * 0.43f))

    // Green herb garnish leaf
    val leafPath = Path().apply {
      moveTo(w * 0.50f, h * 0.32f)
      cubicTo(w * 0.45f, h * 0.18f, w * 0.55f, h * 0.15f, w * 0.52f, h * 0.24f)
      close()
    }
    drawPath(leafPath, color = Color(0xFF16A34A))
  }
}

@Composable
fun LentilsVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Traditional copper dal bowl (katori)
    drawArc(
      color = Color(0xFFC2410C),
      startAngle = 0f,
      sweepAngle = 180f,
      useCenter = true,
      topLeft = Offset(w * 0.12f, h * 0.42f),
      size = Size(w * 0.76f, h * 0.48f)
    )

    // Warm golden-orange lentil soup (dal)
    drawArc(
      color = Color(0xFFF97316),
      startAngle = 180f,
      sweepAngle = 180f,
      useCenter = true,
      topLeft = Offset(w * 0.16f, h * 0.28f),
      size = Size(w * 0.68f, h * 0.36f)
    )

    // Split red & yellow pulses floating with tadka
    drawOval(color = Color(0xFFFDE047), topLeft = Offset(w * 0.30f, h * 0.36f), size = Size(w * 0.08f, h * 0.05f))
    drawOval(color = Color(0xFFEF4444), topLeft = Offset(w * 0.44f, h * 0.32f), size = Size(w * 0.09f, h * 0.05f))
    drawOval(color = Color(0xFFFDE047), topLeft = Offset(w * 0.58f, h * 0.35f), size = Size(w * 0.08f, h * 0.05f))
    drawOval(color = Color(0xFFEF4444), topLeft = Offset(w * 0.38f, h * 0.42f), size = Size(w * 0.08f, h * 0.05f))

    // Cumin seeds (tadka)
    drawLine(color = Color(0xFF451A03), start = Offset(w * 0.48f, h * 0.38f), end = Offset(w * 0.54f, h * 0.41f), strokeWidth = w * 0.02f)
  }
}

@Composable
fun ChickpeasVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Three distinct beige/tan chickpeas with pointed beaks (chana)
    fun drawChickpea(center: Offset, scale: Float) {
      val r = w * 0.16f * scale
      val path = Path().apply {
        // Pointed beak top
        moveTo(center.x, center.y - r * 1.25f)
        cubicTo(center.x + r * 0.9f, center.y - r * 0.6f, center.x + r * 1.1f, center.y + r * 0.8f, center.x, center.y + r)
        cubicTo(center.x - r * 1.1f, center.y + r * 0.8f, center.x - r * 0.9f, center.y - r * 0.6f, center.x, center.y - r * 1.25f)
        close()
      }
      drawPath(path, color = Color(0xFFD4A373))
      drawPath(path, color = Color(0xFF9A7B56), style = Stroke(width = w * 0.025f))

      // Center fold indentation
      drawLine(
        color = Color(0xFF9A7B56),
        start = Offset(center.x, center.y - r * 0.8f),
        end = Offset(center.x, center.y + r * 0.2f),
        strokeWidth = w * 0.02f
      )
    }

    drawChickpea(Offset(w * 0.32f, h * 0.58f), 0.95f)
    drawChickpea(Offset(w * 0.68f, h * 0.58f), 0.95f)
    drawChickpea(Offset(w * 0.50f, h * 0.32f), 1.1f)
  }
}

// -------------------------------------------------------------
// DAIRY VECTOR GRAPHICS
// -------------------------------------------------------------

@Composable
fun FreshMilkVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Glass tumbler
    val glassPath = Path().apply {
      moveTo(w * 0.25f, h * 0.15f)
      lineTo(w * 0.32f, h * 0.88f)
      cubicTo(w * 0.33f, h * 0.92f, w * 0.67f, h * 0.92f, w * 0.68f, h * 0.88f)
      lineTo(w * 0.75f, h * 0.15f)
      close()
    }
    drawPath(glassPath, color = Color(0xFFE0F2FE))
    drawPath(glassPath, color = Color(0xFF38BDF8), style = Stroke(width = w * 0.035f))

    // Milk inside (pure white with soft top meniscus)
    val milkPath = Path().apply {
      moveTo(w * 0.28f, h * 0.32f)
      lineTo(w * 0.33f, h * 0.85f)
      cubicTo(w * 0.34f, h * 0.89f, w * 0.66f, h * 0.89f, w * 0.67f, h * 0.85f)
      lineTo(w * 0.72f, h * 0.32f)
      cubicTo(w * 0.60f, h * 0.36f, w * 0.40f, h * 0.36f, w * 0.28f, h * 0.32f)
      close()
    }
    drawPath(milkPath, color = Color.White)

    // Milk splash drop
    drawCircle(color = Color(0xFF0284C7), radius = w * 0.035f, center = Offset(w * 0.50f, h * 0.18f))
  }
}

@Composable
fun PaneerVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Neat isometric stacked cubes of white fresh cottage cheese (Paneer)
    fun drawCube(topLeft: Offset, cw: Float, ch: Float, depth: Float) {
      // Front face
      drawRoundRect(
        color = Color(0xFFF8FAFC),
        topLeft = topLeft,
        size = Size(cw, ch),
        cornerRadius = CornerRadius(cw * 0.08f)
      )
      drawRoundRect(
        color = Color(0xFFCBD5E1),
        topLeft = topLeft,
        size = Size(cw, ch),
        cornerRadius = CornerRadius(cw * 0.08f),
        style = Stroke(width = w * 0.03f)
      )

      // Top isometric bevel
      val topPath = Path().apply {
        moveTo(topLeft.x, topLeft.y)
        lineTo(topLeft.x + depth, topLeft.y - depth * 0.6f)
        lineTo(topLeft.x + cw + depth, topLeft.y - depth * 0.6f)
        lineTo(topLeft.x + cw, topLeft.y)
        close()
      }
      drawPath(topPath, color = Color.White)
      drawPath(topPath, color = Color(0xFFCBD5E1), style = Stroke(width = w * 0.025f))

      // Right isometric bevel
      val rightPath = Path().apply {
        moveTo(topLeft.x + cw, topLeft.y)
        lineTo(topLeft.x + cw + depth, topLeft.y - depth * 0.6f)
        lineTo(topLeft.x + cw + depth, topLeft.y + ch - depth * 0.6f)
        lineTo(topLeft.x + cw, topLeft.y + ch)
        close()
      }
      drawPath(rightPath, color = Color(0xFFF1F5F9))
      drawPath(rightPath, color = Color(0xFFCBD5E1), style = Stroke(width = w * 0.025f))
    }

    // Two stacked paneer cubes
    drawCube(Offset(w * 0.15f, h * 0.48f), w * 0.38f, h * 0.35f, w * 0.10f)
    drawCube(Offset(w * 0.45f, h * 0.32f), w * 0.38f, h * 0.35f, w * 0.10f)

    // Mint sprig garnish on top
    val mintPath = Path().apply {
      moveTo(w * 0.65f, h * 0.22f)
      cubicTo(w * 0.55f, h * 0.10f, w * 0.70f, h * 0.08f, w * 0.68f, h * 0.18f)
      close()
    }
    drawPath(mintPath, color = Color(0xFF16A34A))
  }
}

@Composable
fun DairyCreamVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // White porcelain cream pitcher / jug
    val jugPath = Path().apply {
      moveTo(w * 0.28f, h * 0.25f)
      // Spout to the right
      lineTo(w * 0.72f, h * 0.20f)
      cubicTo(w * 0.85f, h * 0.45f, w * 0.78f, h * 0.85f, w * 0.50f, h * 0.88f)
      cubicTo(w * 0.25f, h * 0.85f, w * 0.18f, h * 0.50f, w * 0.28f, h * 0.25f)
      close()
    }
    drawPath(jugPath, color = Color(0xFFF8FAFC))
    drawPath(jugPath, color = Color(0xFF94A3B8), style = Stroke(width = w * 0.035f))

    // Handle on the left
    val handlePath = Path().apply {
      moveTo(w * 0.22f, h * 0.35f)
      cubicTo(w * 0.05f, h * 0.42f, w * 0.05f, h * 0.68f, w * 0.25f, h * 0.72f)
    }
    drawPath(handlePath, color = Color(0xFF94A3B8), style = Stroke(width = w * 0.045f, cap = StrokeCap.Round))

    // Flowing velvety cream stream pouring from spout
    val streamPath = Path().apply {
      moveTo(w * 0.72f, h * 0.20f)
      cubicTo(w * 0.85f, h * 0.35f, w * 0.88f, h * 0.65f, w * 0.82f, h * 0.95f)
    }
    drawPath(streamPath, color = Color(0xFFFEF3C7), style = Stroke(width = w * 0.07f, cap = StrokeCap.Round))
    drawPath(streamPath, color = Color(0xFFFDE68A), style = Stroke(width = w * 0.035f, cap = StrokeCap.Round))
  }
}

@Composable
fun ButtermilkVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Traditional ribbed clay glass (Kulhad / Chaas glass)
    val glassPath = Path().apply {
      moveTo(w * 0.25f, h * 0.18f)
      lineTo(w * 0.32f, h * 0.88f)
      cubicTo(w * 0.34f, h * 0.93f, w * 0.66f, h * 0.93f, w * 0.68f, h * 0.88f)
      lineTo(w * 0.75f, h * 0.18f)
      close()
    }
    drawPath(glassPath, color = Color(0xFFD4A373))
    drawPath(glassPath, color = Color(0xFF8B5E34), style = Stroke(width = w * 0.035f))

    // Frothy cultured buttermilk inside
    val buttermilkPath = Path().apply {
      moveTo(w * 0.28f, h * 0.28f)
      lineTo(w * 0.33f, h * 0.85f)
      lineTo(w * 0.67f, h * 0.85f)
      lineTo(w * 0.72f, h * 0.28f)
      close()
    }
    drawPath(buttermilkPath, color = Color(0xFFF8FAFC))

    // Spiced roasted cumin specks (jeera)
    val cuminColor = Color(0xFF78350F)
    drawCircle(color = cuminColor, radius = w * 0.018f, center = Offset(w * 0.40f, h * 0.42f))
    drawCircle(color = cuminColor, radius = w * 0.018f, center = Offset(w * 0.52f, h * 0.36f))
    drawCircle(color = cuminColor, radius = w * 0.018f, center = Offset(w * 0.60f, h * 0.48f))
    drawCircle(color = cuminColor, radius = w * 0.018f, center = Offset(w * 0.46f, h * 0.55f))

    // Fresh green mint sprig peeking from rim
    val sprigPath = Path().apply {
      moveTo(w * 0.50f, h * 0.25f)
      cubicTo(w * 0.40f, h * 0.08f, w * 0.60f, h * 0.05f, w * 0.58f, h * 0.20f)
      close()
    }
    drawPath(sprigPath, color = Color(0xFF15803D))
  }
}

@Composable
fun KhoyaVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Golden-brown caramelized dense milk solids / mawa pedas
    fun drawMawaPeda(center: Offset, r: Float) {
      // Golden caramelized outer body
      drawCircle(
        color = Color(0xFFD97706),
        radius = r,
        center = center
      )
      drawCircle(
        color = Color(0xFFB45309),
        radius = r,
        center = center,
        style = Stroke(width = w * 0.025f)
      )

      // Creamy center indentation
      drawCircle(
        color = Color(0xFFFEF3C7),
        radius = r * 0.45f,
        center = center
      )

      // Green pistachio flake & saffron strand
      drawOval(
        color = Color(0xFF16A34A),
        topLeft = Offset(center.x - r * 0.20f, center.y - r * 0.12f),
        size = Size(r * 0.35f, r * 0.22f)
      )
      drawLine(
        color = Color(0xFFDC2626),
        start = Offset(center.x, center.y - r * 0.18f),
        end = Offset(center.x + r * 0.25f, center.y + r * 0.15f),
        strokeWidth = w * 0.02f
      )
    }

    // Stack of 3 authentic mawa sweets
    drawMawaPeda(Offset(w * 0.32f, h * 0.60f), w * 0.22f)
    drawMawaPeda(Offset(w * 0.68f, h * 0.60f), w * 0.22f)
    drawMawaPeda(Offset(w * 0.50f, h * 0.35f), w * 0.25f)
  }
}

@Composable
fun ButterVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Silver plate
    drawOval(
      color = Color(0xFFE2E8F0),
      topLeft = Offset(w * 0.08f, h * 0.55f),
      size = Size(w * 0.84f, h * 0.35f)
    )

    // Golden butter slab
    val butterPath = Path().apply {
      moveTo(w * 0.22f, h * 0.45f)
      lineTo(w * 0.45f, h * 0.32f)
      lineTo(w * 0.78f, h * 0.42f)
      lineTo(w * 0.78f, h * 0.68f)
      lineTo(w * 0.55f, h * 0.78f)
      lineTo(w * 0.22f, h * 0.68f)
      close()
    }
    drawPath(butterPath, color = Color(0xFFFACC15))
    drawPath(butterPath, color = Color(0xFFCA8A04), style = Stroke(width = w * 0.03f))

    // Top face highlight
    val topPath = Path().apply {
      moveTo(w * 0.22f, h * 0.45f)
      lineTo(w * 0.45f, h * 0.32f)
      lineTo(w * 0.78f, h * 0.42f)
      lineTo(w * 0.55f, h * 0.52f)
      close()
    }
    drawPath(topPath, color = Color(0xFFFEF08A))
  }
}

@Composable
fun GheeVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Glass Jar body
    drawRoundRect(
      color = Color(0xFFE0F2FE),
      topLeft = Offset(w * 0.25f, h * 0.28f),
      size = Size(w * 0.50f, h * 0.62f),
      cornerRadius = CornerRadius(w * 0.08f)
    )
    drawRoundRect(
      color = Color(0xFF38BDF8),
      topLeft = Offset(w * 0.25f, h * 0.28f),
      size = Size(w * 0.50f, h * 0.62f),
      cornerRadius = CornerRadius(w * 0.08f),
      style = Stroke(width = w * 0.035f)
    )

    // Golden liquid Ghee inside
    drawRoundRect(
      color = Color(0xFFF59E0B),
      topLeft = Offset(w * 0.28f, h * 0.42f),
      size = Size(w * 0.44f, h * 0.45f),
      cornerRadius = CornerRadius(w * 0.05f)
    )

    // Jar Golden Lid
    drawRoundRect(
      color = Color(0xFFD97706),
      topLeft = Offset(w * 0.22f, h * 0.18f),
      size = Size(w * 0.56f, h * 0.12f),
      cornerRadius = CornerRadius(w * 0.04f)
    )
  }
}

@Composable
fun CheeseVectorGraphic(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Triangular wedge of aged swiss cheese
    val cheesePath = Path().apply {
      moveTo(w * 0.15f, h * 0.65f)
      lineTo(w * 0.85f, h * 0.35f)
      lineTo(w * 0.85f, h * 0.65f)
      cubicTo(w * 0.60f, h * 0.85f, w * 0.35f, h * 0.85f, w * 0.15f, h * 0.65f)
      close()
    }
    drawPath(cheesePath, color = Color(0xFFF59E0B))
    drawPath(cheesePath, color = Color(0xFFD97706), style = Stroke(width = w * 0.035f))

    // Top face
    val topFace = Path().apply {
      moveTo(w * 0.15f, h * 0.65f)
      lineTo(w * 0.85f, h * 0.35f)
      lineTo(w * 0.55f, h * 0.20f)
      close()
    }
    drawPath(topFace, color = Color(0xFFFCD34D))

    // Characteristic cheese eye holes
    val holeColor = Color(0xFFD97706)
    drawCircle(color = holeColor, radius = w * 0.05f, center = Offset(w * 0.45f, h * 0.65f))
    drawCircle(color = holeColor, radius = w * 0.04f, center = Offset(w * 0.68f, h * 0.55f))
    drawCircle(color = holeColor, radius = w * 0.035f, center = Offset(w * 0.58f, h * 0.72f))
  }
}
