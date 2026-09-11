package com.example.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserFeedback
import com.example.ui.theme.*
import com.example.utils.rememberAppHaptics

/**
 * Inline Feedback Card placed directly at the bottom of the AI Recommendation Result Screen.
 * Allows users to rate prediction accuracy (1-5 stars), pick quick observation tags,
 * and submit qualitative comments to help calibrate AI horticultural preservation models.
 */
@Composable
fun InlineFeedbackCard(
  commodityName: String,
  materialName: String,
  userEmail: String,
  onSubmitFeedback: (UserFeedback) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val haptics = rememberAppHaptics()
  val colors = AppTheme.colors
  val isDark = AppTheme.isDark

  var accuracyRating by remember { mutableIntStateOf(5) }
  var commentsText by remember { mutableStateOf("") }
  var selectedTags by remember { mutableStateOf(setOf<String>()) }
  var isSubmitted by remember { mutableStateOf(false) }

  val tagOptions = listOf(
    "Accurate Shelf-Life",
    "Optimal OTR Barrier",
    "Great Eco-Score",
    "Cost Feasible",
    "Moisture Protected",
    "Needs Thicker Film"
  )

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
    border = BorderStroke(1.dp, if (isSubmitted) MintLeafAccent else colors.cardBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier.testTag("inline_feedback_form_card")
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      if (isSubmitted) {
        // Submitted Success Confirmation View
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .background(colors.paleSageLight, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.CheckCircle,
              contentDescription = "Success",
              tint = if (isDark) MintLeafAccent else ForestGreenPrimary,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              "Feedback Recorded!",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = colors.headerText
            )
            Text(
              "Rated $accuracyRating★ for $commodityName ($materialName). Stored to calibrate future AI packaging models.",
              fontSize = 11.5.sp,
              color = colors.textSecondary,
              lineHeight = 15.sp
            )
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(
          onClick = { isSubmitted = false },
          modifier = Modifier.align(Alignment.End)
        ) {
          Text("Edit Response", fontSize = 12.sp, color = MintLeafAccent, fontWeight = FontWeight.Bold)
        }
      } else {
        // Feedback Form Header
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
            Icon(
              Icons.Default.RateReview,
              contentDescription = null,
              tint = colors.headerText,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              "Rate AI Suggestion Accuracy",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = colors.headerText
            )
            Text(
              "Help MoFPI train better horticultural preservation models",
              fontSize = 11.5.sp,
              color = colors.textSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Star Rating Selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            "Accuracy Rating:",
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
          )
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.paleSageLight
          ) {
            Text(
              getAccuracyLabel(accuracyRating),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.headerText,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Start,
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (i in 1..5) {
            IconButton(
              onClick = {
                accuracyRating = i
                haptics.performTick()
              },
              modifier = Modifier
                .size(38.dp)
                .testTag("inline_feedback_star_$i")
            ) {
              Icon(
                imageVector = if (i <= accuracyRating) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = "Rate $i Stars",
                tint = if (i <= accuracyRating) Color(0xFFF59E0B) else Color(0xFF9CA3AF),
                modifier = Modifier.size(28.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Observation Tags
        Text(
          "Quick Observations:",
          fontSize = 11.5.sp,
          fontWeight = FontWeight.SemiBold,
          color = colors.textSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          tagOptions.take(3).forEach { tag ->
            val isSelected = tag in selectedTags
            FilterChip(
              selected = isSelected,
              onClick = {
                haptics.performTick()
                selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
              },
              label = { Text(tag, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = if (isDark) MintLeafAccent.copy(alpha = 0.3f) else PaleSageLight,
                selectedLabelColor = colors.headerText
              ),
              modifier = Modifier.height(28.dp)
            )
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          tagOptions.drop(3).forEach { tag ->
            val isSelected = tag in selectedTags
            FilterChip(
              selected = isSelected,
              onClick = {
                haptics.performTick()
                selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
              },
              label = { Text(tag, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = if (isDark) MintLeafAccent.copy(alpha = 0.3f) else PaleSageLight,
                selectedLabelColor = colors.headerText
              ),
              modifier = Modifier.height(28.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Qualitative Comments TextField
        OutlinedTextField(
          value = commentsText,
          onValueChange = { commentsText = it },
          placeholder = {
            Text(
              "Share qualitative comments (e.g. OTR barrier suitability, transit condensation observations, sealing notes)...",
              fontSize = 11.5.sp,
              color = colors.textMuted
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .testTag("inline_feedback_comments_field"),
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MintLeafAccent,
            unfocusedBorderColor = colors.cardBorder,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            focusedContainerColor = colors.cardBackground,
            unfocusedContainerColor = colors.cardBackground
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Submit Button
        Button(
          onClick = {
            haptics.performSuccess()
            val combinedComments = buildString {
              if (selectedTags.isNotEmpty()) {
                append("[Tags: ${selectedTags.joinToString(", ")}] ")
              }
              append(commentsText.trim())
            }.trim()

            val feedback = UserFeedback(
              id = "FB-${System.currentTimeMillis().toString().takeLast(6)}",
              userEmail = userEmail,
              commodityName = "$commodityName ($materialName)",
              ratingAccuracy = accuracyRating,
              ratingUsefulness = accuracyRating,
              comments = combinedComments,
              date = "Just now"
            )
            onSubmitFeedback(feedback)
            isSubmitted = true
            Toast.makeText(context, "Feedback submitted! Thank you.", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = if (isDark) MintLeafAccent else ForestGreenPrimary),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("inline_submit_feedback_button")
        ) {
          Icon(Icons.Default.RateReview, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Submit AI Feedback", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }
  }
}

private fun getAccuracyLabel(rating: Int): String {
  return when (rating) {
    1 -> "1★ Needs Recalibration"
    2 -> "2★ Low Accuracy"
    3 -> "3★ Moderately Accurate"
    4 -> "4★ Very Accurate"
    5 -> "5★ Highly Accurate"
    else -> "$rating★"
  }
}

@Composable
fun FeedbackDialog(
  commodityName: String = "Tomato",
  userEmail: String,
  onDismiss: () -> Unit,
  onSubmitFeedback: (UserFeedback) -> Unit
) {
  var accuracyRating by remember { mutableIntStateOf(5) }
  var usefulnessRating by remember { mutableIntStateOf(5) }
  var commentsText by remember { mutableStateOf("") }
  var isSubmitted by remember { mutableStateOf(false) }
  val colors = AppTheme.colors
  val isDark = AppTheme.isDark

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = colors.cardBackground,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .background(colors.paleSageLight, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Star, contentDescription = null, tint = colors.headerText, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text("Rate Recommendation", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.headerText)
          Text("For $commodityName packaging", fontSize = 12.sp, color = colors.textSecondary)
        }
      }
    },
    text = {
      if (isSubmitted) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text("🎉", fontSize = 40.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            "Thank You for Your Feedback!",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = colors.headerText
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            "Your ratings have been stored securely. The MoFPI research panel uses this data to calibrate horticultural packaging models.",
            fontSize = 13.sp,
            color = colors.textSecondary
          )
        }
      } else {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // 1. Accuracy Rating
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Prediction Accuracy", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
              Text(getRatingLabel(accuracyRating), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MintLeafAccent)
            }
            Spacer(modifier = Modifier.height(4.dp))
            StarRatingRow(
              rating = accuracyRating,
              onRatingChanged = { accuracyRating = it },
              testTagPrefix = "accuracy_star"
            )
          }

          // 2. Usefulness Rating
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Practical Usefulness", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
              Text(getRatingLabel(usefulnessRating), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MintLeafAccent)
            }
            Spacer(modifier = Modifier.height(4.dp))
            StarRatingRow(
              rating = usefulnessRating,
              onRatingChanged = { usefulnessRating = it },
              testTagPrefix = "usefulness_star"
            )
          }

          // 3. Comments
          Column {
            Text("Detailed Comments & Observations", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = commentsText,
              onValueChange = { commentsText = it },
              placeholder = {
                Text(
                  "E.g., Film permeability preserved firmness during 12-day transit. Sealing temperature required slight adjustment...",
                  fontSize = 12.sp,
                  color = colors.textMuted
                )
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .testTag("feedback_comments_input"),
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MintLeafAccent,
                unfocusedBorderColor = colors.cardBorder,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary
              )
            )
          }
        }
      }
    },
    confirmButton = {
      if (isSubmitted) {
        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = if (isDark) MintLeafAccent else ForestGreenPrimary)
        ) {
          Text("Done", color = Color.White)
        }
      } else {
        Button(
          onClick = {
            val newFeedback = UserFeedback(
              id = "FB-${System.currentTimeMillis().toString().takeLast(5)}",
              userEmail = userEmail,
              commodityName = commodityName,
              ratingAccuracy = accuracyRating,
              ratingUsefulness = usefulnessRating,
              comments = commentsText.trim(),
              date = "Today"
            )
            onSubmitFeedback(newFeedback)
            isSubmitted = true
          },
          colors = ButtonDefaults.buttonColors(containerColor = if (isDark) MintLeafAccent else ForestGreenPrimary),
          modifier = Modifier.testTag("submit_feedback_button")
        ) {
          Text("Submit Feedback", color = Color.White)
        }
      }
    },
    dismissButton = {
      if (!isSubmitted) {
        OutlinedButton(onClick = onDismiss) {
          Text("Cancel", color = colors.textPrimary)
        }
      }
    }
  )
}

@Composable
private fun StarRatingRow(
  rating: Int,
  onRatingChanged: (Int) -> Unit,
  testTagPrefix: String
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    for (i in 1..5) {
      IconButton(
        onClick = { onRatingChanged(i) },
        modifier = Modifier
          .size(36.dp)
          .testTag("${testTagPrefix}_$i")
      ) {
        Icon(
          imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.StarOutline,
          contentDescription = "Star $i",
          tint = if (i <= rating) Color(0xFFF59E0B) else Color(0xFF9CA3AF),
          modifier = Modifier.size(28.dp)
        )
      }
    }
  }
}

private fun getRatingLabel(rating: Int): String {
  return when (rating) {
    1 -> "1★ Poor"
    2 -> "2★ Fair"
    3 -> "3★ Good"
    4 -> "4★ Very Good"
    5 -> "5★ Excellent"
    else -> "$rating★"
  }
}

@Composable
fun MofpiGuidelinesDialog(
  onDismiss: () -> Unit
) {
  val colors = AppTheme.colors

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = colors.cardBackground,
    title = {
      Text(
        "SIH MoFPI Packaging Guidelines & Compliance",
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = colors.headerText
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text("MoFPI / SIH26236 Standard Protocols", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = colors.headerText)
        Text(
          "1. Modified Atmosphere Packaging (MAP):\n" +
              "• Target O₂ headspace: 2–5% for climacteric fruit (Tomatoes, Mangoes)\n" +
              "• Target CO₂ headspace: 3–7% to inhibit Botrytis cinerea and soft rot fungal pathogens.\n" +
              "• Anaerobic fermentation threshold must NOT drop below 1% O₂.",
          fontSize = 12.sp,
          color = colors.textSecondary
        )
        Text(
          "2. Moisture & Water Vapor Transmission Rate (WVTR):\n" +
              "• High transpiration commodities (>85% moisture) require breathable perforation or bio-coating to prevent condensation fogging and bacterial decay.",
          fontSize = 12.sp,
          color = colors.textSecondary
        )
        Text(
          "3. Environmental Sustainability Directives:\n" +
              "• Plastic Waste Management (PWM) Rules: Minimum 50 µm thickness for virgin polymer films, or certified industrially compostable PLA/PBAT biopolymers conforming to IS/ISO 17088.",
          fontSize = 12.sp,
          color = colors.textSecondary
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
      ) {
        Text("Understood", color = Color.White)
      }
    }
  )
}
