package com.example.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserFeedback
import com.example.ui.theme.*

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

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .background(PaleSageLight, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Star, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text("Rate Recommendation", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
          Text("For $commodityName packaging", fontSize = 12.sp, color = TextSecondary)
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
            color = ForestGreenPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            "Your ratings have been stored securely. The MoFPI research panel uses this data to calibrate horticultural packaging models.",
            fontSize = 13.sp,
            color = TextSecondary
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
              Text("Prediction Accuracy", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
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
              Text("Practical Usefulness", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
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
            Text("Detailed Comments & Observations", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = commentsText,
              onValueChange = { commentsText = it },
              placeholder = {
                Text(
                  "E.g., Film permeability preserved firmness during 12-day transit. Sealing temperature required slight adjustment...",
                  fontSize = 12.sp,
                  color = TextMuted
                )
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .testTag("feedback_comments_input"),
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MintLeafAccent,
                unfocusedBorderColor = CardBorder
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
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
        ) {
          Text("Done")
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
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          modifier = Modifier.testTag("submit_feedback_button")
        ) {
          Text("Submit Feedback")
        }
      }
    },
    dismissButton = {
      if (!isSubmitted) {
        OutlinedButton(onClick = onDismiss) {
          Text("Cancel")
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
          tint = if (i <= rating) Color(0xFFF59E0B) else Color(0xFFD1D5DB),
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
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        "SIH MoFPI Packaging Guidelines & Compliance",
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = ForestGreenPrimary
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text("MoFPI / SIH26236 Standard Protocols", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestGreenPrimary)
        Text(
          "1. Modified Atmosphere Packaging (MAP):\n" +
              "• Target O₂ headspace: 2–5% for climacteric fruit (Tomatoes, Mangoes)\n" +
              "• Target CO₂ headspace: 3–7% to inhibit Botrytis cinerea and soft rot fungal pathogens.\n" +
              "• Anaerobic fermentation threshold must NOT drop below 1% O₂.",
          fontSize = 12.sp,
          color = TextSecondary
        )
        Text(
          "2. Moisture & Water Vapor Transmission Rate (WVTR):\n" +
              "• High transpiration commodities (>85% moisture) require breathable perforation or bio-coating to prevent condensation fogging and bacterial decay.",
          fontSize = 12.sp,
          color = TextSecondary
        )
        Text(
          "3. Environmental Sustainability Directives:\n" +
              "• Plastic Waste Management (PWM) Rules: Minimum 50 µm thickness for virgin polymer films, or certified industrially compostable PLA/PBAT biopolymers conforming to IS/ISO 17088.",
          fontSize = 12.sp,
          color = TextSecondary
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
      ) {
        Text("Understood")
      }
    }
  )
}
