package com.example.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.EcoLeafIcon
import com.example.components.EcoWrapStartupHeroCard
import com.example.components.UserAvatar
import com.example.model.*
import com.example.ui.theme.*

// -------------------------------------------------------------
// SCREEN 1: SPLASH SCREEN ("ECO WRAP")
// -------------------------------------------------------------
@Composable
fun SplashScreen(
  onNavigateNext: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(AppBackground)
      .testTag("splash_screen")
  ) {
    // Decorative subtle leafy backgrounds in corners
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .offset(x = 40.dp, y = (-30).dp)
        .size(160.dp)
        .clip(CircleShape)
        .background(PaleSageTint.copy(alpha = 0.5f))
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Header
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
      ) {
        EcoLeafIcon(modifier = Modifier.size(72.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "ECO WRAP",
          fontSize = 36.sp,
          fontWeight = FontWeight.ExtraBold,
          color = ForestGreenPrimary,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Smart Packaging\nfor a Sustainable Tomorrow",
          fontSize = 17.sp,
          fontWeight = FontWeight.Medium,
          color = ForestGreenPrimary,
          textAlign = TextAlign.Center,
          lineHeight = 22.sp
        )
      }

      // Central Hero Illustration Card with Startup Artwork
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
      ) {
        EcoWrapStartupHeroCard()
      }

      // 3 Circular Badge Highlights
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        SplashBadgeItem(icon = Icons.Default.Shield, label = "Protect\nFood")
        SplashBadgeItem(icon = Icons.Default.Autorenew, label = "Reduce\nWaste")
        SplashBadgeItem(icon = Icons.Default.Eco, label = "Greener\nFuture")
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Bottom Quote & CTA
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "\"Better Packaging\nHealthier Food\nBrighter Tomorrow\"",
          fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold,
          color = ForestGreenPrimary,
          textAlign = TextAlign.Center,
          lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onNavigateNext,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("splash_start_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text(
            "Get Started",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Forward",
            tint = Color.White
          )
        }
      }
    }
  }
}

@Composable
private fun SplashBadgeItem(icon: ImageVector, label: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Box(
      modifier = Modifier
        .size(60.dp)
        .background(PaleSageTint, CircleShape)
        .border(2.dp, MintLeafAccent.copy(alpha = 0.6f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = ForestGreenPrimary,
        modifier = Modifier.size(28.dp)
      )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = ForestGreenPrimary,
      textAlign = TextAlign.Center,
      lineHeight = 15.sp
    )
  }
}

// -------------------------------------------------------------
// SCREEN 2: ONBOARDING / VALUE CAROUSEL (Interactive & Auto-Shifting)
// -------------------------------------------------------------
@Composable
fun OnboardingScreen(
  selectedPersona: PersonaType = PersonaType.RESEARCHER,
  onSkip: () -> Unit,
  onNavigateNext: () -> Unit,
  onSelectPersona: (PersonaType) -> Unit = {}
) {
  var activePage by remember { mutableIntStateOf(0) }
  val currentSlide = ONBOARDING_SLIDES[activePage]

  Scaffold(
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(AppBackground)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("🌱", fontSize = 18.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            "EcoWrap",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary
          )
        }

        // Skip action
        TextButton(
          onClick = onSkip,
          modifier = Modifier.testTag("onboarding_skip_button"),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            "Skip",
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    },
    containerColor = AppBackground
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 18.dp, vertical = 6.dp)
        .pointerInput(activePage) {
          var totalDragX = 0f
          detectHorizontalDragGestures(
            onDragStart = { totalDragX = 0f },
            onDragEnd = {
              if (totalDragX < -40f) {
                // Swipe Left -> Next
                activePage = (activePage + 1) % ONBOARDING_SLIDES.size
              } else if (totalDragX > 40f) {
                // Swipe Right -> Prev
                activePage = if (activePage > 0) activePage - 1 else ONBOARDING_SLIDES.size - 1
              }
            },
            onHorizontalDrag = { _, dragAmount ->
              totalDragX += dragAmount
            }
          )
        },
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Titles with smooth animated transition
      AnimatedContent(
        targetState = currentSlide,
        transitionSpec = {
          fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(150))
        },
        label = "onboarding_title_transition"
      ) { slide ->
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = slide.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = slide.subtitle,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Normal,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
          )
        }
      }

      // Center Graphic: Laptop Device Mockup with slide-specific screens
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .background(Color.White)
          .border(1.dp, CardBorder, RoundedCornerShape(22.dp))
          .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          // The Laptop Mockup with Animated Content
          AnimatedContent(
            targetState = activePage,
            transitionSpec = {
              fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(150))
            },
            label = "laptop_content_transition"
          ) { page ->
            LaptopDeviceMockup(
              modifier = Modifier.fillMaxWidth(),
              headerTitle = currentSlide.laptopHeader,
              systemStatus = currentSlide.systemStatus
            ) {
              when (page) {
                0 -> LaptopSlide0Content()
                1 -> LaptopSlide1Content()
                2 -> LaptopSlide2Content()
                3 -> LaptopSlide3Content()
                4 -> LaptopSlide4Content()
                else -> LaptopSlide0Content()
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Dynamic Feature Chips for this slide (interactive tap)
          AnimatedContent(
            targetState = currentSlide,
            transitionSpec = {
              fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(120))
            },
            label = "chips_transition"
          ) { slide ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly
            ) {
              AssistChip(
                onClick = {},
                label = { Text(slide.chip1Label, fontSize = 11.sp, color = ForestGreenPrimary, fontWeight = FontWeight.Medium) },
                leadingIcon = {
                  Icon(slide.chip1Icon, null, tint = MintLeafAccent, modifier = Modifier.size(14.dp))
                },
                colors = AssistChipDefaults.assistChipColors(containerColor = PaleSageLight),
                border = AssistChipDefaults.assistChipBorder(borderColor = Color(0xFFD1E7DD), enabled = true)
              )
              AssistChip(
                onClick = {},
                label = { Text(slide.chip2Label, fontSize = 11.sp, color = ForestGreenPrimary, fontWeight = FontWeight.Medium) },
                leadingIcon = {
                  Icon(slide.chip2Icon, null, tint = MintLeafAccent, modifier = Modifier.size(14.dp))
                },
                colors = AssistChipDefaults.assistChipColors(containerColor = PaleSageLight),
                border = AssistChipDefaults.assistChipBorder(borderColor = Color(0xFFD1E7DD), enabled = true)
              )
            }
          }
        }
      }

      // Interactive Slide Controller: Previous / Next Buttons + 5-dot page indicator + CTA Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Prev / Next quick navigators
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          IconButton(
            onClick = {
              activePage = if (activePage > 0) activePage - 1 else ONBOARDING_SLIDES.size - 1
            },
            modifier = Modifier
              .size(36.dp)
              .background(PaleSageLight, CircleShape)
              .testTag("onboarding_prev_button")
          ) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Previous Slide",
              tint = ForestGreenPrimary,
              modifier = Modifier.size(16.dp)
            )
          }

          // 5 Interactive Dots
          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp)
          ) {
            for (i in ONBOARDING_SLIDES.indices) {
              Box(
                modifier = Modifier
                  .height(8.dp)
                  .width(if (i == activePage) 22.dp else 8.dp)
                  .clip(CircleShape)
                  .background(if (i == activePage) ForestGreenPrimary else Color(0xFFD1D5DB))
                  .clickable {
                    activePage = i
                  }
              )
            }
          }

          IconButton(
            onClick = {
              activePage = (activePage + 1) % ONBOARDING_SLIDES.size
            },
            modifier = Modifier
              .size(36.dp)
              .background(PaleSageLight, CircleShape)
              .testTag("onboarding_next_dot_button")
          ) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "Next Slide",
              tint = ForestGreenPrimary,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        // Circular Green Forward Button or Expanded Get Started Button
        if (activePage < ONBOARDING_SLIDES.size - 1) {
          IconButton(
            onClick = { activePage++ },
            modifier = Modifier
              .size(48.dp)
              .shadow(4.dp, CircleShape)
              .background(ForestGreenPrimary, CircleShape)
              .testTag("onboarding_next_fab")
          ) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "Next Slide",
              tint = Color.White
            )
          }
        } else {
          Button(
            onClick = onNavigateNext,
            modifier = Modifier
              .height(46.dp)
              .shadow(4.dp, RoundedCornerShape(24.dp))
              .testTag("onboarding_get_started_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
            shape = RoundedCornerShape(24.dp)
          ) {
            Text(
              "Get Started",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 13.5.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      // Bottom Feature Navigation Tags with interactive 1-click jump & active highlighting
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White, RoundedCornerShape(16.dp))
          .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
          .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        FeatureTagItem(
          icon = Icons.Outlined.Timer,
          label = "Predict\nShelf Life",
          isSelected = activePage == 1,
          onClick = { activePage = 1 }
        )
        FeatureTagItem(
          icon = Icons.AutoMirrored.Outlined.CompareArrows,
          label = "Compare\nMaterials",
          isSelected = activePage == 2,
          onClick = { activePage = 2 }
        )
        FeatureTagItem(
          icon = Icons.Outlined.AttachMoney,
          label = "Optimize\nCost",
          isSelected = activePage == 3,
          onClick = { activePage = 3 }
        )
        FeatureTagItem(
          icon = Icons.Outlined.AutoAwesome,
          label = "MoFPI\nAI Assist",
          isSelected = activePage == 4,
          onClick = { activePage = 4 }
        )
      }
    }
  }
}

@Composable
private fun FeatureTagItem(
  icon: ImageVector,
  label: String,
  isSelected: Boolean = false,
  onClick: () -> Unit = {}
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .width(76.dp)
      .clip(RoundedCornerShape(10.dp))
      .background(if (isSelected) PaleSageLight else Color.Transparent)
      .clickable(onClick = onClick)
      .padding(vertical = 6.dp, horizontal = 2.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = if (isSelected) ForestGreenPrimary else Color(0xFF6B7280),
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
      text = label,
      fontSize = 9.5.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      color = if (isSelected) ForestGreenPrimary else TextSecondary,
      textAlign = TextAlign.Center,
      lineHeight = 12.sp
    )
  }
}

// -------------------------------------------------------------
// SCREEN 3: PERSONA SELECTION ("Who are you?")
// -------------------------------------------------------------
@Composable
fun PersonaSelectionScreen(
  selectedPersona: PersonaType,
  onSelectPersona: (PersonaType) -> Unit,
  onNavigateBack: () -> Unit,
  onNavigateNext: () -> Unit
) {
  Scaffold(
    topBar = {
      IconButton(
        onClick = onNavigateBack,
        modifier = Modifier
          .padding(8.dp)
          .testTag("persona_back_button")
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ForestGreenPrimary)
      }
    },
    bottomBar = {
      Surface(
        color = AppBackground,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
      ) {
        Button(
          onClick = onNavigateNext,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("persona_next_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Save Persona & Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    },
    containerColor = AppBackground
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 24.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Text(
        text = "Who are you?",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = ForestGreenPrimary
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Choose your profile to get a personalized experience.",
        fontSize = 14.sp,
        color = TextSecondary
      )

      Spacer(modifier = Modifier.height(16.dp))

      // State syncing indicator
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = PaleSageLight,
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Icon(Icons.Default.CloudDone, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Your selected persona automatically syncs with your profile settings and local offline database.",
            fontSize = 11.5.sp,
            color = ForestGreenPrimary,
            fontWeight = FontWeight.Medium,
            lineHeight = 15.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      PersonaType.entries.forEach { persona ->
        val isSelected = persona == selectedPersona
        PersonaCard(
          persona = persona,
          isSelected = isSelected,
          onSelect = { onSelectPersona(persona) }
        )
        Spacer(modifier = Modifier.height(14.dp))
      }
    }
  }
}

@Composable
private fun PersonaCard(
  persona: PersonaType,
  isSelected: Boolean,
  onSelect: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) PaleSageLight else CardSurface
    ),
    border = BorderStroke(
      width = if (isSelected) 2.dp else 1.dp,
      color = if (isSelected) MintLeafAccent else CardBorder
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onSelect)
      .testTag("persona_card_${persona.name.lowercase()}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Custom Badge Icon
      Box(
        modifier = Modifier
          .size(48.dp)
          .background(
            if (isSelected) PaleSageTint else Color(0xFFF3F4F6),
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        val emoji = when (persona) {
          PersonaType.FARMER -> "🌱"
          PersonaType.FOOD_PROCESSOR -> "🏭"
          PersonaType.STARTUP -> "🚀"
          PersonaType.RESEARCHER -> "🔬"
        }
        Text(text = emoji, fontSize = 24.sp)
      }

      Spacer(modifier = Modifier.width(16.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = persona.title,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreenPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = persona.subtitle,
          fontSize = 13.sp,
          color = TextSecondary
        )
      }

      RadioButton(
        selected = isSelected,
        onClick = onSelect,
        colors = RadioButtonDefaults.colors(
          selectedColor = ForestGreenPrimary,
          unselectedColor = Color(0xFFD1D5DB)
        )
      )
    }
  }
}

// -------------------------------------------------------------
// SCREEN 4: HOME DASHBOARD
// -------------------------------------------------------------
@Composable
fun HomeDashboardScreen(
  onSelectCommodity: (String) -> Unit,
  onOpenQuickAction: (String) -> Unit,
  onNavigateToCatalog: () -> Unit,
  selectedTab: Int,
  onSelectTab: (Int) -> Unit,
  currentUser: UserAccount = UserAccount(email = "karthikmiryabbelli@gmail.com", name = "Karthik", role = PersonaType.RESEARCHER),
  onOpenProfile: () -> Unit = { onSelectTab(3) }
) {
  Scaffold(
    bottomBar = {
      NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
      ) {
        NavigationBarItem(
          icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
          label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          selected = selectedTab == 0,
          onClick = { onSelectTab(0) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ForestGreenPrimary,
            selectedTextColor = ForestGreenPrimary,
            indicatorColor = PaleSageTint
          )
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.History, contentDescription = "History") },
          label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          selected = selectedTab == 1,
          onClick = { onSelectTab(1) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ForestGreenPrimary,
            selectedTextColor = ForestGreenPrimary,
            indicatorColor = PaleSageTint
          )
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.Description, contentDescription = "Reports") },
          label = { Text("Reports", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          selected = selectedTab == 2,
          onClick = { onSelectTab(2) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ForestGreenPrimary,
            selectedTextColor = ForestGreenPrimary,
            indicatorColor = PaleSageTint
          )
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
          label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          selected = selectedTab == 3,
          onClick = { onSelectTab(3) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ForestGreenPrimary,
            selectedTextColor = ForestGreenPrimary,
            indicatorColor = PaleSageTint
          )
        )
      }
    },
    containerColor = AppBackground
  ) { padding ->
    Box(modifier = Modifier.padding(padding)) {
      HomeContentView(
        onSelectCommodity = onSelectCommodity,
        onOpenQuickAction = onOpenQuickAction,
        onNavigateToCatalog = onNavigateToCatalog,
        currentUser = currentUser,
        onOpenProfile = onOpenProfile
      )
    }
  }
}

@Composable
fun HomeContentView(
  onSelectCommodity: (String) -> Unit,
  onOpenQuickAction: (String) -> Unit,
  onNavigateToCatalog: () -> Unit,
  currentUser: UserAccount = UserAccount(email = "karthikmiryabbelli@gmail.com", name = "Karthik", role = PersonaType.RESEARCHER),
  onOpenProfile: () -> Unit = {}
) {
  var searchQuery by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    // Header with User Info & Clickable Profile Avatar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        val firstName = currentUser.name.split(" ").firstOrNull()?.ifBlank { "User" } ?: "User"
        Text(
          text = "Good Morning, $firstName!",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreenPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          val roleIcon = when (currentUser.role) {
            PersonaType.RESEARCHER -> "🔬 "
            PersonaType.FARMER -> "🌱 "
            PersonaType.FOOD_PROCESSOR -> "🏭 "
            PersonaType.STARTUP -> "🚀 "
          }
          Text(
            text = roleIcon + currentUser.role.title + " • Eco Wrap",
            fontSize = 13.sp,
            color = TextSecondary
          )
        }
      }

      // Interactive Profile Avatar in Top Right
      UserAvatar(
        user = currentUser,
        size = 46.dp,
        onClick = onOpenProfile,
        modifier = Modifier.testTag("home_top_profile_avatar")
      )
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Search food commodity...", color = TextMuted, fontSize = 14.sp) },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
      trailingIcon = {
        Box(
          modifier = Modifier
            .size(36.dp)
            .background(ForestGreenPrimary, CircleShape)
            .clickable { onNavigateToCatalog() },
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .shadow(2.dp, RoundedCornerShape(14.dp)),
      shape = RoundedCornerShape(14.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = MintLeafAccent,
        unfocusedBorderColor = CardBorder
      ),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(22.dp))

    // Popular Commodities Section
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Popular Commodities",
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = ForestGreenPrimary
      )
      TextButton(onClick = onNavigateToCatalog) {
        Text("See all", color = MintLeafAccent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Horizontal Slider: Tomato, Mango, Apple, Potato
    val popularItems = listOf(
      Pair("Tomato", "🍅"),
      Pair("Mango", "🥭"),
      Pair("Apple", "🍎"),
      Pair("Potato", "🥔")
    )
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(popularItems) { item ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, CardBorder),
          modifier = Modifier
            .width(90.dp)
            .clickable { onSelectCommodity(item.first) }
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = item.second, fontSize = 38.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = item.first,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = ForestGreenPrimary
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Gradient Green Promo Card: "AI powered. Sustainable tomorrow."
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(
          Brush.horizontalGradient(
            colors = listOf(ForestGreenPrimary, MintLeafAccent)
          )
        )
        .padding(16.dp),
      contentAlignment = Alignment.CenterStart
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .background(Color.White.copy(alpha = 0.2f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text("🌱", fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = "AI powered. Sustainable tomorrow.",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }

    Spacer(modifier = Modifier.height(22.dp))

    // 2x2 Quick Actions Grid
    Text(
      text = "Quick Actions",
      fontSize = 17.sp,
      fontWeight = FontWeight.Bold,
      color = ForestGreenPrimary
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      QuickActionCard(
        title = "Recommend\nPackaging",
        icon = Icons.Default.Inventory2,
        modifier = Modifier.weight(1f),
        onClick = { onOpenQuickAction("recommend") }
      )
      QuickActionCard(
        title = "Compare\nMaterials",
        icon = Icons.AutoMirrored.Filled.CompareArrows,
        modifier = Modifier.weight(1f),
        onClick = { onOpenQuickAction("compare") }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      QuickActionCard(
        title = "Shelf Life\nPrediction",
        icon = Icons.Default.AccessTime,
        modifier = Modifier.weight(1f),
        onClick = { onOpenQuickAction("shelflife") }
      )
      QuickActionCard(
        title = "Environmental\nInfographic",
        icon = Icons.Default.Thermostat,
        modifier = Modifier.weight(1f),
        onClick = { onOpenQuickAction("infographic") }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    QuickActionCard(
      title = "Identify Food Commodity (AI Vision Scan)",
      icon = Icons.Default.QrCodeScanner,
      modifier = Modifier.fillMaxWidth(),
      onClick = { onOpenQuickAction("scan") }
    )

    Spacer(modifier = Modifier.height(80.dp))
  }
}

@Composable
private fun QuickActionCard(
  title: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, CardBorder),
    modifier = modifier
      .clickable(onClick = onClick)
      .height(100.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = Color(0xFF4F46E5),
          modifier = Modifier.size(24.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary,
        lineHeight = 17.sp
      )
    }
  }
}
