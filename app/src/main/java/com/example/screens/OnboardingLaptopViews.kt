package com.example.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// -------------------------------------------------------------
// DATA MODEL FOR ONBOARDING SLIDES
// -------------------------------------------------------------
data class OnboardingSlideData(
  val title: String,
  val subtitle: String,
  val laptopHeader: String,
  val systemStatus: String,
  val chip1Label: String,
  val chip1Icon: ImageVector,
  val chip2Label: String,
  val chip2Icon: ImageVector,
  val activeFeatureIndex: Int,
  val primaryBadgeText: String,
  val primaryBadgeColor: Color
)

val ONBOARDING_SLIDES = listOf(
  // 1. Smart Packaging Intro
  OnboardingSlideData(
    title = "Smart Packaging\nStarts Here",
    subtitle = "AI-powered agricultural packaging platform engineered to reduce post-harvest crop loss across India's supply chains.",
    laptopHeader = "ECO WRAP Platform • Smart Packaging Suite",
    systemStatus = "MoFPI SIH26236 Initiative • Agricultural AI Suite",
    chip1Label = "Smart Packaging",
    chip1Icon = Icons.Default.Inventory2,
    chip2Label = "MoFPI Initiative",
    chip2Icon = Icons.Default.VerifiedUser,
    activeFeatureIndex = -1,
    primaryBadgeText = "Active Smart Pack",
    primaryBadgeColor = ForestGreenPrimary
  ),
  // 2. Real-time Shelf Life Analysis
  OnboardingSlideData(
    title = "Real-Time Shelf\nLife Analysis",
    subtitle = "Simulate thermodynamic decay curves, respiration kinetics, and humidity retention to predict exact freshness windows.",
    laptopHeader = "Shelf Life Engine • Kinetic Decay Analysis",
    systemStatus = "Live Respiration Model | Solanum lycopersicum",
    chip1Label = "Real-Time Forecast",
    chip1Icon = Icons.Default.Timer,
    chip2Label = "Respiration Kinetics",
    chip2Icon = Icons.Default.Sensors,
    activeFeatureIndex = 0,
    primaryBadgeText = "Dynamic Freshness",
    primaryBadgeColor = Color(0xFF0284C7)
  ),
  // 3. Sustainable Material Comparisons
  OnboardingSlideData(
    title = "Sustainable Material\nComparisons",
    subtitle = "Compare barrier properties, degradation rates, and carbon footprints across 100% bio-films, PLA, and cellulose.",
    laptopHeader = "Material Matrix • LCA Sustainability Index",
    systemStatus = "Comparative LCA | Bio-PLA vs Fossil LDPE",
    chip1Label = "Bio-Based Films",
    chip1Icon = Icons.Default.Eco,
    chip2Label = "Zero Microplastics",
    chip2Icon = Icons.Default.Recycling,
    activeFeatureIndex = 1,
    primaryBadgeText = "100% Home Compostable",
    primaryBadgeColor = MintLeafAccent
  ),
  // 4. Cost Optimization Tools
  OnboardingSlideData(
    title = "Cost Optimization\nTools",
    subtitle = "Minimize per-unit packaging expense, transit vibration damage, and spoilage penalties to maximize farmer profit margins.",
    laptopHeader = "Cost Optimizer • Spoilage & ROI Calculator",
    systemStatus = "Financial Optimization | Freight & Per-Unit ROI",
    chip1Label = "Cost Calculator",
    chip1Icon = Icons.Default.AttachMoney,
    chip2Label = "Transit ROI",
    chip2Icon = Icons.Default.LocalShipping,
    activeFeatureIndex = 2,
    primaryBadgeText = "Active ROI Shield",
    primaryBadgeColor = Color(0xFFD97706)
  ),
  // 5. Personalized AI Recommendations for MoFPI users
  OnboardingSlideData(
    title = "Personalized AI\nRecommendations",
    subtitle = "Tailored packaging recipes for farmers, processors, and exporters strictly aligned with MoFPI quality guidelines.",
    laptopHeader = "MoFPI AI Recommendation & Audit Engine",
    systemStatus = "Target Persona: Farmer / Processor / Exporter",
    chip1Label = "Personalized AI",
    chip1Icon = Icons.Default.AutoAwesome,
    chip2Label = "MoFPI Guidelines",
    chip2Icon = Icons.Default.VerifiedUser,
    activeFeatureIndex = 3,
    primaryBadgeText = "MoFPI Verified Recipe",
    primaryBadgeColor = ForestGreenPrimary
  )
)

// -------------------------------------------------------------
// LAPTOP DEVICE MOCKUP CHASSIS
// -------------------------------------------------------------
@Composable
fun LaptopDeviceMockup(
  modifier: Modifier = Modifier,
  headerTitle: String,
  systemStatus: String,
  content: @Composable () -> Unit
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // 1. Laptop Display Lid
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .height(184.dp),
      shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
      color = Color(0xFF1E293B),
      border = BorderStroke(1.dp, Color(0xFF334155)),
      shadowElevation = 4.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(3.dp)
      ) {
        // Laptop Bezel Top with Webcam
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(4.dp)
              .background(Color(0xFF0F172A), CircleShape)
              .border(0.5.dp, Color(0xFF38BDF8), CircleShape)
          )
        }

        // Inner Screen Canvas
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clip(RoundedCornerShape(5.dp)),
          color = Color(0xFF0B132B)
        ) {
          Column(modifier = Modifier.fillMaxSize()) {
            // Window Header Bar
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(horizontal = 6.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Box(modifier = Modifier.size(5.dp).background(Color(0xFFEF4444), CircleShape))
                Box(modifier = Modifier.size(5.dp).background(Color(0xFFF59E0B), CircleShape))
                Box(modifier = Modifier.size(5.dp).background(Color(0xFF10B981), CircleShape))
              }
              Text(
                text = headerTitle,
                color = Color(0xFFE2E8F0),
                fontSize = 8.5.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(4.dp).background(Color(0xFF34D399), CircleShape))
                Spacer(modifier = Modifier.width(2.dp))
                Text("AI LIVE", color = Color(0xFF34D399), fontSize = 7.sp, fontWeight = FontWeight.Bold)
              }
            }

            // Subheader System Status Banner
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = systemStatus,
                color = Color(0xFF94A3B8),
                fontSize = 7.5.sp,
                fontFamily = FontFamily.Monospace
              )
            }

            // Screen Content Body
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
              contentAlignment = Alignment.Center
            ) {
              content()
            }
          }
        }
      }
    }

    // 2. Laptop Hinge
    Box(
      modifier = Modifier
        .width(58.dp)
        .height(3.dp)
        .background(Color(0xFF0F172A), RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
    )

    // 3. Laptop Keyboard Chassis
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .height(24.dp),
      shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
      color = Color(0xFFE2E8F0),
      border = BorderStroke(1.dp, Color(0xFF94A3B8)),
      shadowElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 12.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Keyboard key rows indication
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color(0xFFCBD5E1), RoundedCornerShape(2.dp))
          .padding(horizontal = 4.dp, vertical = 1.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(4) {
              Box(modifier = Modifier.width(8.dp).height(4.dp).background(Color(0xFF94A3B8), RoundedCornerShape(1.dp)))
            }
          }
          // Spacebar
          Box(modifier = Modifier.width(36.dp).height(4.dp).background(Color(0xFF94A3B8), RoundedCornerShape(1.dp)))
          Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(4) {
              Box(modifier = Modifier.width(8.dp).height(4.dp).background(Color(0xFF94A3B8), RoundedCornerShape(1.dp)))
            }
          }
        }

        // Glass Trackpad
        Box(
          modifier = Modifier
            .width(30.dp)
            .height(7.dp)
            .border(0.6.dp, Color(0xFF94A3B8), RoundedCornerShape(1.5.dp))
            .background(Color(0xFFF1F5F9), RoundedCornerShape(1.5.dp))
        )
      }
    }
  }
}

// -------------------------------------------------------------
// SLIDE 1: SMART PACKAGING INTRO (Platform Overview)
// -------------------------------------------------------------
@Composable
fun LaptopSlide0Content() {
  Row(
    modifier = Modifier.fillMaxSize(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {
    // Smart Packaging Package Graphic
    Box(
      modifier = Modifier
        .width(108.dp)
        .height(104.dp)
        .background(
          Brush.verticalGradient(listOf(Color(0xFF064E3B), Color(0xFF022C22))),
          RoundedCornerShape(10.dp)
        )
        .border(1.2.dp, MintLeafAccent, RoundedCornerShape(10.dp))
        .padding(6.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("📦 🌿 ⚡", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = MintLeafAccent.copy(alpha = 0.25f)
        ) {
          Text(
            "Active Smart Pack",
            color = Color(0xFFA7F3D0),
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text("Bio-Barrier + MAP", color = Color(0xFF94A3B8), fontSize = 6.5.sp)
      }
    }

    // Platform Overview Console
    Column(
      modifier = Modifier
        .width(140.dp)
        .background(Color(0xFF1E293B).copy(alpha = 0.85f), RoundedCornerShape(8.dp))
        .border(0.8.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
        .padding(6.dp),
      verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Crop Diagnostics", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("120+ Produce", color = Color(0xFF34D399), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Dynamic MAP Tuning", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("Micro-Pores", color = Color(0xFF38BDF8), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Eco Materials", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("100% Bio-Plastics", color = Color(0xFF4ADE80), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }

      HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("MoFPI Goal", color = Color.White, fontSize = 7.sp)
        Text("Cut 16% Agri Waste", color = Color(0xFF34D399), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

// -------------------------------------------------------------
// SLIDE 2: REAL-TIME SHELF LIFE ANALYSIS (Freshness Kinetics)
// -------------------------------------------------------------
@Composable
fun LaptopSlide1Content() {
  Row(
    modifier = Modifier.fillMaxSize(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {
    // Clamshell Packaging Tray Graphic
    Box(
      modifier = Modifier
        .width(108.dp)
        .height(104.dp)
        .background(
          Brush.verticalGradient(listOf(Color(0xFF1E3A2F), Color(0xFF142B23))),
          RoundedCornerShape(10.dp)
        )
        .border(1.2.dp, MintLeafAccent, RoundedCornerShape(10.dp))
        .padding(6.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🍅 🍅 🍅", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = MintLeafAccent.copy(alpha = 0.25f)
        ) {
          Text(
            "Live Respiration",
            color = Color(0xFFA7F3D0),
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text("PET-G Clamshell", color = Color(0xFF94A3B8), fontSize = 6.5.sp)
      }
    }

    // Diagnostic Readings Console
    Column(
      modifier = Modifier
        .width(140.dp)
        .background(Color(0xFF1E293B).copy(alpha = 0.85f), RoundedCornerShape(8.dp))
        .border(0.8.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
        .padding(6.dp),
      verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Respiration Rate", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("18.2 mg/kg·h", color = Color(0xFF34D399), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Ethylene Sensitivity", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("Moderate", color = Color(0xFFFBBF24), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Moisture Target", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("94.5% RH", color = Color(0xFF38BDF8), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }

      HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Shelf Life Gain", color = Color.White, fontSize = 7.5.sp, fontWeight = FontWeight.SemiBold)
        Text("+280% (19 Days)", color = Color(0xFF4ADE80), fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
      }
    }
  }
}

// -------------------------------------------------------------
// SLIDE 3: SUSTAINABLE MATERIAL COMPARISONS (LCA Matrix)
// -------------------------------------------------------------
@Composable
fun LaptopSlide2Content() {
  Row(
    modifier = Modifier.fillMaxSize(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {
    // Bio-Degradable Film Wrap Graphic
    Box(
      modifier = Modifier
        .width(108.dp)
        .height(104.dp)
        .background(
          Brush.verticalGradient(listOf(Color(0xFF14532D), Color(0xFF052E16))),
          RoundedCornerShape(10.dp)
        )
        .border(1.2.dp, Color(0xFF4ADE80), RoundedCornerShape(10.dp))
        .padding(6.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🥭 🍋 🍎", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = Color(0xFF16A34A).copy(alpha = 0.35f)
        ) {
          Text(
            "100% Bio-Based",
            color = Color(0xFFBBF7D0),
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text("Degrades: 120 Days", color = Color(0xFF86EFAC), fontSize = 6.5.sp)
      }
    }

    // Circular Economy LCA Console
    Column(
      modifier = Modifier
        .width(140.dp)
        .background(Color(0xFF1E293B).copy(alpha = 0.85f), RoundedCornerShape(8.dp))
        .border(0.8.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
        .padding(6.dp),
      verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Sustainability Score", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("9.4 / 10 ★", color = Color(0xFF4ADE80), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }

      // Carbon footprint comparison
      Column {
        Text("Carbon vs Petroleum LDPE", color = Color(0xFF94A3B8), fontSize = 6.5.sp)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("CO₂ Reduction", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
          Text("-82% Saved", color = Color(0xFF34D399), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Microplastic Release", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("0% (Zero)", color = Color(0xFF38BDF8), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }

      HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("Compost Standard", color = Color.White, fontSize = 7.sp)
        Text("ISO 17088 Certified", color = Color(0xFF4ADE80), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

// -------------------------------------------------------------
// SLIDE 4: COST OPTIMIZATION TOOLS (ROI & Spoilage Savings)
// -------------------------------------------------------------
@Composable
fun LaptopSlide3Content() {
  Row(
    modifier = Modifier.fillMaxSize(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {
    // Refrigerated Truck Logistics Graphic
    Box(
      modifier = Modifier
        .width(108.dp)
        .height(104.dp)
        .background(
          Brush.verticalGradient(listOf(Color(0xFF78350F), Color(0xFF451A03))),
          RoundedCornerShape(10.dp)
        )
        .border(1.2.dp, Color(0xFFF59E0B), RoundedCornerShape(10.dp))
        .padding(6.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("❄️ 🚛 💰", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = Color(0xFFD97706).copy(alpha = 0.35f)
        ) {
          Text(
            "Cold Fleet Live",
            color = Color(0xFFFDE68A),
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text("4.2°C Stabilized", color = Color(0xFFFCD34D), fontSize = 6.5.sp)
      }
    }

    // Cost & ROI Console
    Column(
      modifier = Modifier
        .width(140.dp)
        .background(Color(0xFF1E293B).copy(alpha = 0.85f), RoundedCornerShape(8.dp))
        .border(0.8.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
        .padding(6.dp),
      verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Unit Film Cost", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("₹2.40 / kg", color = Color(0xFF34D399), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Transit Spoilage", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("18% ➔ 2.5%", color = Color(0xFF38BDF8), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Shock Damping", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("88% Absorbed", color = Color(0xFFFBBF24), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }

      HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("Net Profit Gain", color = Color.White, fontSize = 7.sp)
        Text("+₹14,800 / Ton", color = Color(0xFF34D399), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

// -------------------------------------------------------------
// SLIDE 5: PERSONALIZED AI RECOMMENDATIONS FOR MoFPI USERS
// -------------------------------------------------------------
@Composable
fun LaptopSlide4Content() {
  Row(
    modifier = Modifier.fillMaxSize(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {
    // Certified Stamp & MoFPI AI Recommendation Graphic
    Box(
      modifier = Modifier
        .width(108.dp)
        .height(104.dp)
        .background(
          Brush.verticalGradient(listOf(Color(0xFF064E3B), Color(0xFF022C22))),
          RoundedCornerShape(10.dp)
        )
        .border(1.2.dp, Color(0xFF10B981), RoundedCornerShape(10.dp))
        .padding(6.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("📄 🛡️ 🔍", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = Color(0xFF059669).copy(alpha = 0.35f)
        ) {
          Text(
            "MoFPI SIH26236",
            color = Color(0xFFA7F3D0),
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text("Batch #B26-SIH", color = Color(0xFF6EE7B7), fontSize = 6.5.sp)
      }
    }

    // Personalized AI MoFPI Recommendation Console
    Column(
      modifier = Modifier
        .width(140.dp)
        .background(Color(0xFF1E293B).copy(alpha = 0.85f), RoundedCornerShape(8.dp))
        .border(0.8.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
        .padding(6.dp),
      verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("User Persona", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("Farmer / Exporter", color = Color(0xFF34D399), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("AI Best Match", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("Micro-PLA-G", color = Color(0xFF38BDF8), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("MoFPI Standard", color = Color(0xFFCBD5E1), fontSize = 7.5.sp)
        Text("IS 9845 Certified", color = Color(0xFFFBBF24), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }

      HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("PDF Traceability", color = Color.White, fontSize = 7.sp)
        Text("Instant Download", color = Color(0xFF34D399), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}
