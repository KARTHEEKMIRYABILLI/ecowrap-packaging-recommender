package com.example.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.example.ui.theme.*
import com.example.utils.QrCodeGenerator

@Composable
fun EcoLeafIcon(
  modifier: Modifier = Modifier,
  tint: Color = ForestGreenPrimary,
  secondaryTint: Color = MintLeafAccent
) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height

    // Left leaf
    val leftPath = Path().apply {
      moveTo(w * 0.5f, h * 0.85f)
      cubicTo(w * 0.15f, h * 0.8f, w * 0.05f, h * 0.45f, w * 0.2f, h * 0.15f)
      cubicTo(w * 0.5f, h * 0.15f, w * 0.6f, h * 0.45f, w * 0.5f, h * 0.85f)
      close()
    }
    drawPath(leftPath, color = tint)

    // Right leaf
    val rightPath = Path().apply {
      moveTo(w * 0.5f, h * 0.85f)
      cubicTo(w * 0.85f, h * 0.8f, w * 0.95f, h * 0.45f, w * 0.8f, h * 0.15f)
      cubicTo(w * 0.5f, h * 0.15f, w * 0.4f, h * 0.45f, w * 0.5f, h * 0.85f)
      close()
    }
    drawPath(rightPath, color = secondaryTint)

    // Leaf center vein
    drawLine(
      color = Color.White.copy(alpha = 0.6f),
      start = Offset(w * 0.5f, h * 0.85f),
      end = Offset(w * 0.5f, h * 0.25f),
      strokeWidth = 2.dp.toPx()
    )
  }
}

@Composable
fun SuitabilityScoreRing(
  score: Int,
  modifier: Modifier = Modifier,
  strokeWidth: Dp = 10.dp
) {
  val animatedScore by animateFloatAsState(
    targetValue = score.toFloat(),
    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
    label = "scoreRing"
  )

  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val diameter = size.minDimension
      val radius = (diameter - strokeWidth.toPx()) / 2f
      val center = Offset(size.width / 2f, size.height / 2f)

      // Background ring
      drawCircle(
        color = PaleSageTint,
        radius = radius,
        center = center,
        style = Stroke(width = strokeWidth.toPx())
      )

      // Foreground arc
      val sweepAngle = (animatedScore / 100f) * 360f
      drawArc(
        color = MintLeafAccent,
        startAngle = -90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
      )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "$score%",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = ForestGreenPrimary
      )
      Text(
        text = "Suitability\nScore",
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary,
        textAlign = TextAlign.Center,
        lineHeight = 14.sp
      )
    }
  }
}

@Composable
fun QualityDegradationChart(
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height
    val padLeft = 40.dp.toPx()
    val padBottom = 30.dp.toPx()
    val padTop = 20.dp.toPx()
    val padRight = 20.dp.toPx()

    val chartW = w - padLeft - padRight
    val chartH = h - padBottom - padTop

    // Draw horizontal grid lines (0%, 20%, 40%, 60%, 80%, 100%)
    for (i in 0..5) {
      val y = padTop + chartH * (1f - i / 5f)
      drawLine(
        color = CardBorder,
        start = Offset(padLeft, y),
        end = Offset(w - padRight, y),
        strokeWidth = 1.dp.toPx()
      )
    }

    // Shaded Recommended Consumption Window (Day 3 to Day 15 out of 20)
    val day3X = padLeft + chartW * (3f / 20f)
    val day15X = padLeft + chartW * (15f / 20f)

    drawRoundRect(
      color = MintLeafAccent.copy(alpha = 0.12f),
      topLeft = Offset(day3X, padTop),
      size = Size(day15X - day3X, chartH),
      cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
    )

    // Vertical dashed marker at day 15
    drawLine(
      color = MintLeafAccent.copy(alpha = 0.7f),
      start = Offset(day15X, padTop),
      end = Offset(day15X, padTop + chartH),
      strokeWidth = 1.5.dp.toPx(),
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )

    // Quality curve points: (0 days, 100%), (5 days, 90%), (10 days, 75%), (15 days, 58%), (20 days, 18%)
    val points = listOf(
      Offset(padLeft + chartW * (0f / 20f), padTop + chartH * (1f - 1.0f)),
      Offset(padLeft + chartW * (5f / 20f), padTop + chartH * (1f - 0.90f)),
      Offset(padLeft + chartW * (10f / 20f), padTop + chartH * (1f - 0.74f)),
      Offset(padLeft + chartW * (14.5f / 20f), padTop + chartH * (1f - 0.58f)),
      Offset(padLeft + chartW * (20f / 20f), padTop + chartH * (1f - 0.18f))
    )

    val curvePath = Path().apply {
      moveTo(points.first().x, points.first().y)
      for (i in 0 until points.size - 1) {
        val p0 = points[i]
        val p1 = points[i + 1]
        val cx = (p0.x + p1.x) / 2f
        cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
      }
    }

    drawPath(
      path = curvePath,
      color = ForestGreenPrimary,
      style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )

    // Draw dots at points
    points.forEach { pt ->
      drawCircle(color = MintLeafAccent, radius = 5.dp.toPx(), center = pt)
      drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = pt)
    }
  }
}

@Composable
fun AiRobotAvatar(
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "robotGlow")
  val pulseGlow by infiniteTransition.animateFloat(
    initialValue = 0.8f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow"
  )

  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Glow behind robot
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(MintLeafAccent.copy(alpha = 0.35f * pulseGlow), Color.Transparent),
          center = Offset(w / 2f, h / 2f),
          radius = w * 0.48f * pulseGlow
        ),
        radius = w * 0.48f * pulseGlow,
        center = Offset(w / 2f, h / 2f)
      )

      // Antenna
      drawLine(
        color = MintLeafAccent,
        start = Offset(w * 0.5f, h * 0.28f),
        end = Offset(w * 0.5f, h * 0.15f),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
      )
      drawCircle(
        color = MintLeafAccent,
        radius = 5.dp.toPx(),
        center = Offset(w * 0.5f, h * 0.14f)
      )

      // Robot Head (white/light-grey rounded box)
      drawRoundRect(
        color = Color.White,
        topLeft = Offset(w * 0.28f, h * 0.26f),
        size = Size(w * 0.44f, h * 0.36f),
        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
      )

      // Face Visor Screen (dark mint glass)
      drawRoundRect(
        color = Color(0xFF042213),
        topLeft = Offset(w * 0.32f, h * 0.31f),
        size = Size(w * 0.36f, h * 0.22f),
        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
      )

      // Glowing Eyes
      drawCircle(
        color = MintLeafAccent,
        radius = 4.5.dp.toPx(),
        center = Offset(w * 0.42f, h * 0.41f)
      )
      drawCircle(
        color = MintLeafAccent,
        radius = 4.5.dp.toPx(),
        center = Offset(w * 0.58f, h * 0.41f)
      )

      // Robot Body & Shoulders
      drawRoundRect(
        color = Color.White,
        topLeft = Offset(w * 0.31f, h * 0.64f),
        size = Size(w * 0.38f, h * 0.22f),
        cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
      )
      // Chest badge
      drawCircle(
        color = MintLeafAccent,
        radius = 5.dp.toPx(),
        center = Offset(w * 0.5f, h * 0.74f)
      )
    }
  }
}

@Composable
fun DynamicQrCodeWidget(
  dataText: String,
  modifier: Modifier = Modifier
) {
  val qrBitmap = remember(dataText) {
    QrCodeGenerator.generateQrBitmap(
      content = dataText,
      size = 380,
      darkColor = android.graphics.Color.parseColor("#0F3D26"),
      lightColor = android.graphics.Color.WHITE
    )
  }

  Box(
    modifier = modifier
      .background(Color.White, RoundedCornerShape(16.dp))
      .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
      .padding(14.dp),
    contentAlignment = Alignment.Center
  ) {
    if (qrBitmap != null) {
      Image(
        bitmap = qrBitmap.asImageBitmap(),
        contentDescription = "Dynamic QR Code",
        modifier = Modifier.fillMaxSize()
      )
    } else {
      Canvas(modifier = Modifier.size(160.dp)) {
        val s = size.width
        val cellSize = s / 15f

        fun drawPositionMarker(x: Float, y: Float) {
          drawRect(color = ForestGreenPrimary, topLeft = Offset(x, y), size = Size(cellSize * 5, cellSize * 5))
          drawRect(color = Color.White, topLeft = Offset(x + cellSize, y + cellSize), size = Size(cellSize * 3, cellSize * 3))
          drawRect(color = ForestGreenPrimary, topLeft = Offset(x + cellSize * 1.5f, y + cellSize * 1.5f), size = Size(cellSize * 2, cellSize * 2))
        }

        drawPositionMarker(0f, 0f)
        drawPositionMarker(s - cellSize * 5, 0f)
        drawPositionMarker(0f, s - cellSize * 5)
      }
    }
  }
}
