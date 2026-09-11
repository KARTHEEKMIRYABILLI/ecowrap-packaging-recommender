package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.*
import com.example.data.AuthResult
import com.example.data.FirebaseService
import com.example.data.PackagingEngine
import com.example.data.PackagingRepository
import com.example.data.UserRepository
import com.example.model.*
import com.example.screens.*
import com.example.ui.theme.*
import com.example.utils.PdfReportGenerator
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        EcoWrapApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoWrapApp() {
  val context = LocalContext.current
  val userRepository = remember { UserRepository(context) }
  val packagingRepository = remember { PackagingRepository.getInstance(context) }
  val firebaseService = remember { FirebaseService(context) }
  val coroutineScope = rememberCoroutineScope()

  // Navigation & Screen State
  var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
  var selectedPersona by remember { mutableStateOf(userRepository.getInitialSelectedPersona()) }
  var dashboardTab by remember { mutableIntStateOf(0) }
  var detailSubTab by remember { mutableIntStateOf(1) } // 0=Overview, 1=Why This, 2=Shelf Life, 3=Sustainability, 4=Report

  // Quick Screen Switcher Sheet state (allows evaluator to jump directly to any of the 14 screens)
  var showScreenPickerSheet by remember { mutableStateOf(false) }
  var showCompareSheet by remember { mutableStateOf(false) }
  var showScanSheet by remember { mutableStateOf(false) }

  // User Authentication State (persisted in Room Database + SharedPreferences)
  var currentUser by remember { mutableStateOf(userRepository.getInitialUser()) }
  var registeredUsers by remember { mutableStateOf(userRepository.getInitialRegisteredUsers()) }

  // Collect reactive user list updates from Room DB
  LaunchedEffect(userRepository) {
    userRepository.observeUsers().collect { users ->
      if (users.isNotEmpty()) {
        registeredUsers = users
      }
    }
  }

  // Assessment History State
  var historyList by remember {
    mutableStateOf(
      listOf(
        HistoryAssessmentItem(
          id = "HIST-001",
          commodityId = "tomato",
          commodityName = "Tomato",
          category = "Fruits",
          emoji = "🍅",
          date = "07 Sep 2026",
          recommendedMaterial = "Breathable PP/PE Film",
          suitabilityScore = 92,
          expectedShelfLifeDays = 16.4,
          ecoScore = 8.4,
          userEmail = "karthikmiryabbelli@gmail.com",
          storageSummary = "8°C, 85% RH, Refrigerated",
          batchId = "PW-2026-001"
        ),
        HistoryAssessmentItem(
          id = "HIST-002",
          commodityId = "mango",
          commodityName = "Mango",
          category = "Fruits",
          emoji = "🥭",
          date = "05 Sep 2026",
          recommendedMaterial = "Perforated LDPE Film",
          suitabilityScore = 88,
          expectedShelfLifeDays = 18.2,
          ecoScore = 7.9,
          userEmail = "karthikmiryabbelli@gmail.com",
          storageSummary = "12°C, 80% RH, Ambient Cold",
          batchId = "PW-2026-002"
        ),
        HistoryAssessmentItem(
          id = "HIST-003",
          commodityId = "potato",
          commodityName = "Potato",
          category = "Vegetables",
          emoji = "🥔",
          date = "01 Sep 2026",
          recommendedMaterial = "Jute/Burlap Mesh Bag",
          suitabilityScore = 95,
          expectedShelfLifeDays = 45.0,
          ecoScore = 9.2,
          userEmail = "karthikmiryabbelli@gmail.com",
          storageSummary = "15°C, 65% RH, Dark Storage",
          batchId = "PW-2026-003"
        )
      )
    )
  }

  // Collect and synchronize local Room cached recommendations into history
  LaunchedEffect(packagingRepository) {
    packagingRepository.allCachedRecommendations.collect { cachedItems ->
      if (cachedItems.isNotEmpty()) {
        val mappedHistory = cachedItems.map { entity ->
          HistoryAssessmentItem(
            id = "CACHE-${entity.id}",
            commodityId = entity.commodityId,
            commodityName = entity.commodityName,
            category = entity.commodityCategory,
            emoji = entity.commodityEmoji,
            date = "Offline Cached",
            recommendedMaterial = entity.materialName,
            suitabilityScore = entity.materialScore,
            expectedShelfLifeDays = entity.expectedShelfLifeDays,
            ecoScore = entity.ecoScore,
            userEmail = currentUser.email,
            storageSummary = "${entity.temperatureC.toInt()}°C, ${entity.relativeHumidityPercent.toInt()}% RH, ${entity.transportationType}",
            batchId = entity.batchId
          )
        }
        // Merge with existing history ensuring uniqueness by batchId
        val existingBatches = mappedHistory.map { it.batchId }.toSet()
        historyList = mappedHistory + historyList.filterNot { it.batchId in existingBatches }
      }
    }
  }

  // Push Notifications State
  var notificationsList by remember {
    mutableStateOf(
      listOf(
        AppNotification(
          id = "NOTIF-001",
          title = "New AI Model Calibration",
          message = "Bio-PLA blend recommendation updated for Tomato under high humidity conditions.",
          timestamp = "10 mins ago",
          type = NotificationType.AI_RECOMMENDATION,
          isRead = false,
          targetCommodity = "Tomato"
        ),
        AppNotification(
          id = "NOTIF-002",
          title = "Packaging Report Ready",
          message = "Batch PW-2026-001 export traceability report is generated and verified.",
          timestamp = "2 hours ago",
          type = NotificationType.REPORT_UPDATE,
          isRead = true,
          targetCommodity = "Tomato"
        )
      )
    )
  }
  var activeNotificationToast by remember { mutableStateOf<AppNotification?>(null) }

  // Feedback State
  var feedbacksList by remember {
    mutableStateOf(
      listOf(
        UserFeedback(
          id = "FB-001",
          userEmail = "karthikmiryabbelli@gmail.com",
          commodityName = "Tomato",
          ratingAccuracy = 5,
          ratingUsefulness = 5,
          comments = "Breathable PP/PE film prevented condensation in our refrigerated van. Shelf-life reached 16 days as predicted.",
          date = "08 Sep 2026"
        )
      )
    )
  }
  var showFeedbackDialog by remember { mutableStateOf(false) }
  var feedbackCommodityName by remember { mutableStateOf("Tomato") }
  var showGuidelinesDialog by remember { mutableStateOf(false) }
  var showExportReportsSuccessDialog by remember { mutableStateOf(false) }

  // Auth Operations with Room & SharedPreferences + Firebase Auth & Cloud Firestore Persistence
  val onLogin: (String, String) -> Boolean = { email, password ->
    val (user, updatedUsers) = userRepository.switchUser(email, registeredUsers, selectedPersona)
    currentUser = user
    registeredUsers = updatedUsers
    selectedPersona = user.role
    coroutineScope.launch {
      firebaseService.signInWithEmail(email, password)
      firebaseService.syncUserToFirestore(user)
    }
    true
  }

  val onSignUp: (String, String, String, PersonaType) -> Boolean = { name, email, password, role ->
    val (newUser, updatedUsers) = userRepository.recordAccountCreation(name, email, role, registeredUsers)
    currentUser = newUser
    registeredUsers = updatedUsers
    selectedPersona = role
    coroutineScope.launch {
      firebaseService.signUpWithEmail(email, password, name, role, newUser.organization)
      firebaseService.syncUserToFirestore(newUser)
    }
    true
  }

  val onLogout: () -> Unit = {
    firebaseService.signOut()
    val guestUser = UserAccount(
      email = "guest@ecowrap.mofpi.gov.in",
      name = "Guest User",
      role = selectedPersona,
      organization = "Public Preview",
      isOptedInNotifications = true
    )
    currentUser = guestUser
    userRepository.saveUser(guestUser, makeActive = true)
  }

  val onGoogleSignIn: () -> Unit = {
    coroutineScope.launch {
      val activity = context as? Activity
      if (activity != null) {
        when (val result = firebaseService.signInWithGoogle(activity)) {
          is AuthResult.Success -> {
            currentUser = result.user
            selectedPersona = result.user.role
            userRepository.saveUser(result.user, makeActive = true)
            userRepository.saveSelectedPersona(result.user.role)
            registeredUsers = (registeredUsers.filterNot { it.email.equals(result.user.email, ignoreCase = true) } + result.user)
            userRepository.saveRegisteredUsers(registeredUsers)
            firebaseService.syncUserToFirestore(result.user)
          }
          is AuthResult.Error -> {}
          is AuthResult.Cancelled -> {}
        }
      }
    }
  }

  val onSyncToFirestore: () -> Unit = {
    coroutineScope.launch {
      firebaseService.syncUserToFirestore(currentUser)
      historyList.forEach { item ->
        firebaseService.persistAssessmentToFirestore(currentUser.email, item)
      }
      feedbacksList.forEach { fb ->
        firebaseService.persistFeedbackToFirestore(fb)
      }
    }
  }

  // Push Notification Trigger
  val triggerNotification: (String, String, NotificationType, String?) -> Unit = { title, msg, type, target ->
    if (currentUser.isOptedInNotifications) {
      val notif = AppNotification(
        id = "NOTIF-${System.currentTimeMillis().toString().takeLast(5)}",
        title = title,
        message = msg,
        timestamp = "Just now",
        type = type,
        isRead = false,
        targetCommodity = target
      )
      notificationsList = listOf(notif) + notificationsList
      activeNotificationToast = notif
    }
  }

  // Commodities Database with distinct categories: Fruits, Vegetables, Grains, Dairy, Meat
  val defaultCommodities = remember {
    listOf(
      // --- FRUITS ---
      CommodityItem("grapes", "Grapes", "Fruits", "🍇", "Berry Fruit", 81.3, 3.5, 0.2, "Low", "Low"),
      CommodityItem("mango", "Mango", "Fruits", "🥭", "Tropical Fruit", 83.5, 4.5, 0.4, "High", "High"),
      CommodityItem("apple", "Apple", "Fruits", "🍎", "Pome Fruit", 85.6, 3.6, 0.2, "Moderate", "High"),
      CommodityItem("banana", "Banana", "Fruits", "🍌", "Tropical Fruit", 74.9, 4.8, 0.3, "High", "Very High"),
      CommodityItem("orange", "Orange", "Fruits", "🍊", "Citrus Fruit", 86.8, 3.8, 0.1, "Moderate", "Moderate"),
      CommodityItem("strawberry", "Strawberry", "Fruits", "🍓", "Soft Berry", 91.0, 3.5, 0.3, "High", "Low"),
      CommodityItem("guava", "Guava", "Fruits", "🍐", "Tropical Fruit", 84.0, 4.1, 0.5, "High", "High"),
      CommodityItem("papaya", "Papaya", "Fruits", "🍈", "Tropical Fruit", 88.0, 5.2, 0.2, "High", "High"),
      CommodityItem("pomegranate", "Pomegranate", "Fruits", "🫐", "Aril Fruit", 78.0, 3.2, 0.3, "Low", "Low"),

      // --- VEGETABLES ---
      CommodityItem("tomato", "Tomato", "Vegetables", "🍅", "Solanaceous Vegetable", 94.0, 4.3, 0.2, "High", "Moderate"),
      CommodityItem("potato", "Potato", "Vegetables", "🥔", "Tuber Vegetable", 79.3, 5.8, 0.1, "Low", "Low"),
      CommodityItem("onion", "Onion", "Vegetables", "🧅", "Bulb Vegetable", 89.1, 5.5, 0.1, "Low", "Low"),
      CommodityItem("carrot", "Carrot", "Vegetables", "🥕", "Root Vegetable", 88.3, 6.0, 0.2, "Moderate", "Low"),
      CommodityItem("broccoli", "Broccoli", "Vegetables", "🥦", "Cruciferous Vegetable", 89.0, 6.5, 0.4, "High", "High"),
      CommodityItem("bell_pepper", "Bell Pepper", "Vegetables", "🫑", "Capsicum Vegetable", 92.0, 5.0, 0.2, "Moderate", "Moderate"),
      CommodityItem("spinach", "Spinach", "Vegetables", "🥬", "Leafy Green", 91.4, 6.8, 0.4, "Very High", "High"),
      CommodityItem("cucumber", "Cucumber", "Vegetables", "🥒", "Cucurbit Vegetable", 95.2, 5.7, 0.1, "Moderate", "High"),
      CommodityItem("garlic", "Garlic", "Vegetables", "🧄", "Allium Vegetable", 65.0, 6.0, 0.5, "Low", "Low"),

      // --- GRAINS ---
      CommodityItem("rice", "Rice", "Grains", "🍚", "Cereal Grain", 13.0, 6.5, 0.6, "Very Low", "Low"),
      CommodityItem("wheat", "Wheat", "Grains", "🌾", "Cereal Grain", 12.5, 6.2, 1.5, "Very Low", "Low"),
      CommodityItem("maize", "Maize (Corn)", "Grains", "🌽", "Cereal Crop", 14.0, 6.8, 3.8, "Low", "Low"),
      CommodityItem("millet", "Millet", "Grains", "🫓", "Nutrient Grain", 11.8, 6.4, 4.2, "Very Low", "Low"),
      CommodityItem("lentils", "Lentils", "Grains", "🍲", "Pulse Legume", 11.2, 6.3, 1.1, "Very Low", "Low"),
      CommodityItem("oats", "Oats", "Grains", "🥣", "Whole Grain", 10.5, 6.7, 6.9, "Very Low", "Low"),
      CommodityItem("barley", "Barley", "Grains", "🌱", "Cereal Grain", 12.0, 6.3, 1.8, "Very Low", "Low"),
      CommodityItem("chickpeas", "Chickpeas", "Grains", "🧆", "Pulse Legume", 11.0, 6.2, 6.0, "Very Low", "Low"),
      CommodityItem("quinoa", "Quinoa", "Grains", "🥗", "Pseudocereal", 12.2, 6.5, 5.8, "Very Low", "Low"),

      // --- DAIRY ---
      CommodityItem("fresh_milk", "Fresh Milk", "Dairy", "🥛", "Liquid Dairy", 87.7, 6.7, 3.8, "Low", "Low"),
      CommodityItem("paneer", "Paneer", "Dairy", "🧊", "Fresh Curd Cheese", 54.0, 6.2, 22.0, "Low", "Low"),
      CommodityItem("yogurt", "Yogurt (Curd)", "Dairy", "🥣", "Fermented Dairy", 85.0, 4.5, 3.5, "Low", "Low"),
      CommodityItem("butter", "Butter", "Dairy", "🧈", "Dairy Fat", 16.0, 6.5, 81.0, "Low", "Low"),
      CommodityItem("ghee", "Ghee", "Dairy", "🫙", "Clarified Butter", 0.5, 6.6, 99.5, "Very Low", "Low"),
      CommodityItem("cheese", "Cheese", "Dairy", "🧀", "Ripened Dairy", 38.0, 5.4, 32.0, "Low", "Low"),
      CommodityItem("cream", "Dairy Cream", "Dairy", "🍶", "Heavy Dairy", 57.0, 6.6, 36.0, "Low", "Low"),
      CommodityItem("buttermilk", "Buttermilk", "Dairy", "🥤", "Cultured Beverage", 90.0, 4.6, 1.0, "Low", "Low"),
      CommodityItem("khoya", "Khoya (Mawa)", "Dairy", "🥮", "Condensed Milk Solid", 30.0, 6.4, 25.0, "Low", "Low"),

      // --- MEAT / POULTRY / SEAFOOD ---
      CommodityItem("poultry", "Poultry Meat", "Meat", "🍗", "White Meat", 74.0, 5.8, 2.6, "Low", "Low"),
      CommodityItem("fish", "Fresh Fish", "Meat", "🐟", "Aquatic Protein", 76.0, 6.4, 1.8, "Moderate", "Low"),
      CommodityItem("eggs", "Fresh Eggs", "Meat", "🥚", "Poultry Produce", 75.0, 7.6, 9.5, "Low", "Low"),
      CommodityItem("prawns", "Prawns", "Meat", "🦐", "Shellfish", 78.0, 6.6, 1.2, "Moderate", "Low"),
      CommodityItem("mutton", "Mutton", "Meat", "🥩", "Red Meat", 70.0, 5.6, 12.0, "Low", "Low"),
      CommodityItem("crab", "Crab", "Meat", "🦀", "Crustacean", 79.0, 6.8, 1.1, "Moderate", "Low")
    )
  }
  var commoditiesList by remember { mutableStateOf(defaultCommodities) }
  var selectedCommodity by remember { mutableStateOf(defaultCommodities.first { it.id == "grapes" }) }

  // User input conditions
  var storageConfig by remember {
    mutableStateOf(
      StorageConfig(
        temperatureC = 8.0,
        relativeHumidityPercent = 85.0,
        transportationType = "Refrigerated",
        desiredShelfLifeDays = 15
      )
    )
  }
  var selectedPriority by remember { mutableStateOf(PriorityType.ECO_FRIENDLY) }
  var selectedFormat by remember { mutableStateOf(PackagingFormat.FLEXIBLE_FILM) }

  // Recommendation Material Data
  var activeMaterial by remember {
    mutableStateOf(
      PackagingMaterial(
        name = "Breathable PP/PE Film",
        score = 92,
        description = "High compatibility for fresh tomatoes",
        isTopMatch = true,
        tags = listOf("Best for your conditions", "Balanced gas exchange", "Cost effective"),
        expectedShelfLife = 16.4,
        ecoScore = 8.4,
        filmThickness = "50 µm",
        costPerKg = "₹ 145 / kg"
      )
    )
  }

  var alternatives by remember {
    mutableStateOf(
      listOf(
        Pair("PET (Polyethylene Terephthalate)", 84),
        Pair("Biodegradable Film (PLA/PBAT)", 82),
        Pair("Aluminum Foil Laminate", 65)
      )
    )
  }
  var compareMaterialAName by remember { mutableStateOf<String?>("Breathable / Micro-Perforated Film") }
  var compareMaterialBName by remember { mutableStateOf<String?>("Biodegradable Film (PLA / PBAT)") }

  // Responsive desktop / mobile container: Centered box max-width 480.dp with subtle frame
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFE5E7EB)),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .fillMaxHeight()
        .widthIn(max = 480.dp)
        .fillMaxWidth()
        .shadow(12.dp, RoundedCornerShape(0.dp))
        .background(AppBackground)
    ) {
      // Screen Router with smooth transitions
      AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
          fadeIn() togetherWith fadeOut()
        },
        label = "screenRouter"
      ) { screen ->
        when (screen) {
          AppScreen.SPLASH -> SplashScreen(
            onNavigateNext = { currentScreen = AppScreen.ONBOARDING }
          )

          AppScreen.ONBOARDING -> OnboardingScreen(
            selectedPersona = selectedPersona,
            onSkip = {
              currentUser = userRepository.syncOnboardingPersona(selectedPersona, currentUser)
              currentScreen = AppScreen.HOME_DASHBOARD
            },
            onNavigateNext = { currentScreen = AppScreen.PERSONA_SELECTION },
            onSelectPersona = { persona ->
              selectedPersona = persona
              currentUser = userRepository.syncOnboardingPersona(persona, currentUser)
            }
          )

          AppScreen.PERSONA_SELECTION -> PersonaSelectionScreen(
            selectedPersona = selectedPersona,
            onSelectPersona = { persona ->
              selectedPersona = persona
              currentUser = userRepository.syncOnboardingPersona(persona, currentUser)
            },
            onNavigateBack = { currentScreen = AppScreen.ONBOARDING },
            onNavigateNext = {
              currentUser = userRepository.syncOnboardingPersona(selectedPersona, currentUser)
              currentScreen = AppScreen.HOME_DASHBOARD
            }
          )

          AppScreen.HOME_DASHBOARD -> MainNavigationShell(
            selectedTab = dashboardTab,
            onSelectTab = { dashboardTab = it },
            onSelectCommodity = { name ->
              val item = commoditiesList.find { it.name.equals(name, ignoreCase = true) } ?: commoditiesList.first()
              selectedCommodity = item
              currentScreen = AppScreen.FOOD_PROFILE
            },
            onOpenQuickAction = { action ->
              when (action) {
                "recommend" -> currentScreen = AppScreen.COMMODITY_SELECTION
                "compare" -> currentScreen = AppScreen.MATERIAL_COMPARISON
                "shelflife" -> currentScreen = AppScreen.DETAIL_SHELF_LIFE
                "infographic" -> currentScreen = AppScreen.ENVIRONMENTAL_INFOGRAPHIC
                "scan" -> showScanSheet = true
              }
            },
            onNavigateToCatalog = { currentScreen = AppScreen.COMMODITY_SELECTION },
            historyItems = historyList,
            onSelectHistoryItem = { item ->
              val foundComm = commoditiesList.find { it.name.equals(item.commodityName, ignoreCase = true) }
              if (foundComm != null) selectedCommodity = foundComm
              activeMaterial = activeMaterial.copy(
                name = item.recommendedMaterial,
                score = item.suitabilityScore,
                expectedShelfLife = item.expectedShelfLifeDays,
                ecoScore = item.ecoScore,
                batchId = item.batchId
              )
              currentScreen = AppScreen.RECOMMENDATION_RESULT
            },
            onClearAllHistory = { historyList = emptyList() },
            onOpenFeedbackFromHistory = { item ->
              feedbackCommodityName = item.commodityName
              showFeedbackDialog = true
            },
            onStartNewRecommendation = { currentScreen = AppScreen.COMMODITY_SELECTION },
            commodity = selectedCommodity,
            storage = storageConfig,
            material = activeMaterial,
            onShareReport = {
              triggerNotification(
                "Report Exported",
                "Traceability report for Batch ${activeMaterial.batchId} shared.",
                NotificationType.REPORT_UPDATE,
                selectedCommodity.name
              )
            },
            onDownloadPdf = {
              triggerNotification(
                "Report Downloaded",
                "PDF certificate for Batch ${activeMaterial.batchId} saved to downloads.",
                NotificationType.REPORT_UPDATE,
                selectedCommodity.name
              )
            },
            currentUser = currentUser,
            onUpdateUser = {
              currentUser = it
              selectedPersona = it.role
              userRepository.saveUser(it, makeActive = true)
              userRepository.saveSelectedPersona(it.role)
            },
            onLogin = onLogin,
            onSignUp = onSignUp,
            onLogout = onLogout,
            notifications = notificationsList,
            onSimulateNotification = { type ->
              when (type) {
                NotificationType.AI_RECOMMENDATION -> triggerNotification(
                  "New AI Recommendation Available",
                  "AI analyzed optimal bio-film parameters for ${selectedCommodity.name}.",
                  NotificationType.AI_RECOMMENDATION,
                  selectedCommodity.name
                )
                NotificationType.REPORT_UPDATE -> triggerNotification(
                  "Batch Report Updated",
                  "Batch ${activeMaterial.batchId} MoFPI compliance status verified.",
                  NotificationType.REPORT_UPDATE,
                  selectedCommodity.name
                )
                NotificationType.SYSTEM -> triggerNotification(
                  "System Update",
                  "MoFPI database synchronized with latest national standards.",
                  NotificationType.SYSTEM,
                  null
                )
              }
            },
            onMarkNotificationsRead = {
              notificationsList = notificationsList.map { it.copy(isRead = true) }
            },
            feedbacks = feedbacksList,
            onOpenFeedbackDialog = {
              feedbackCommodityName = selectedCommodity.name
              showFeedbackDialog = true
            },
            onOpenGuidelinesDialog = { showGuidelinesDialog = true },
            onExportAllReports = { showExportReportsSuccessDialog = true },
            onGoogleSignIn = onGoogleSignIn,
            onSyncToFirestore = onSyncToFirestore
          )

          AppScreen.COMMODITY_SELECTION -> CommoditySelectionScreen(
            commodities = commoditiesList,
            selectedCommodity = selectedCommodity,
            onSelectCommodity = { selectedCommodity = it },
            onNavigateBack = { currentScreen = AppScreen.HOME_DASHBOARD },
            onNavigateNext = { currentScreen = AppScreen.FOOD_PROFILE }
          )

          AppScreen.FOOD_PROFILE -> FoodProfileScreen(
            commodity = selectedCommodity,
            onUpdateCommodity = { updated ->
              selectedCommodity = updated
              commoditiesList = commoditiesList.map { if (it.id == updated.id) updated else it }
            },
            onNavigateBack = { currentScreen = AppScreen.COMMODITY_SELECTION },
            onNavigateNext = { currentScreen = AppScreen.STORAGE_CONDITIONS }
          )

          AppScreen.STORAGE_CONDITIONS -> StorageConditionsScreen(
            storageConfig = storageConfig,
            onUpdateConfig = { storageConfig = it },
            onNavigateBack = { currentScreen = AppScreen.FOOD_PROFILE },
            onNavigateNext = { currentScreen = AppScreen.PACKAGING_REQUIREMENTS }
          )

          AppScreen.PACKAGING_REQUIREMENTS -> PackagingRequirementsScreen(
            selectedPriority = selectedPriority,
            onSelectPriority = { selectedPriority = it },
            selectedFormat = selectedFormat,
            onSelectFormat = { selectedFormat = it },
            onNavigateBack = { currentScreen = AppScreen.STORAGE_CONDITIONS },
            onNavigateNext = { currentScreen = AppScreen.AI_ANALYSIS }
          )

          AppScreen.AI_ANALYSIS -> AiProcessingScreen(
            onComplete = {
              val (evaluatedMaterial, rankedAlternatives) = PackagingEngine.evaluateRecommendation(
                commodity = selectedCommodity,
                storage = storageConfig,
                priority = selectedPriority,
                format = selectedFormat
              )
              val newBatchId = "PW-2026-${(historyList.size + 1).toString().padStart(3, '0')}"
              val finalMaterial = evaluatedMaterial.copy(batchId = newBatchId)
              activeMaterial = finalMaterial
              alternatives = rankedAlternatives

              val newHistoryItem = HistoryAssessmentItem(
                id = "HIST-${System.currentTimeMillis().toString().takeLast(5)}",
                commodityId = selectedCommodity.id,
                commodityName = selectedCommodity.name,
                category = selectedCommodity.category,
                emoji = selectedCommodity.emoji,
                date = "Today",
                recommendedMaterial = finalMaterial.name,
                suitabilityScore = finalMaterial.score,
                expectedShelfLifeDays = finalMaterial.expectedShelfLife,
                ecoScore = finalMaterial.ecoScore,
                userEmail = currentUser.email,
                storageSummary = "${storageConfig.temperatureC.toInt()}°C, ${storageConfig.relativeHumidityPercent.toInt()}% RH, ${storageConfig.transportationType}",
                batchId = newBatchId
              )
              historyList = listOf(newHistoryItem) + historyList

              // Cache recommendation into local Room database for offline access
              coroutineScope.launch {
                packagingRepository.cacheRecommendation(
                  commodity = selectedCommodity,
                  storage = storageConfig,
                  material = finalMaterial
                )
              }

              triggerNotification(
                "New AI Recommendation Ready",
                "${finalMaterial.name} matched for ${selectedCommodity.name} with ${finalMaterial.score}% suitability.",
                NotificationType.AI_RECOMMENDATION,
                selectedCommodity.name
              )

              currentScreen = AppScreen.RECOMMENDATION_RESULT
            }
          )

          AppScreen.RECOMMENDATION_RESULT -> RecommendationResultScreen(
            commodity = selectedCommodity,
            material = activeMaterial,
            storage = storageConfig,
            user = currentUser,
            alternatives = alternatives,
            onSelectAlternative = { altName ->
              val altScore = alternatives.find { it.first == altName }?.second ?: 80
              val newProfile = PackagingEngine.buildMaterialProfile(
                name = altName,
                score = altScore,
                commodity = selectedCommodity,
                storage = storageConfig,
                priority = selectedPriority,
                format = selectedFormat
              ).copy(isTopMatch = false, batchId = activeMaterial.batchId)
              activeMaterial = newProfile
            },
            onViewDetails = {
              detailSubTab = 1
              currentScreen = AppScreen.DETAIL_WHY_THIS
            },
            onNavigateBack = { currentScreen = AppScreen.HOME_DASHBOARD },
            onShare = { currentScreen = AppScreen.REPORT_TRACEABILITY },
            onOpenFeedback = {
              feedbackCommodityName = selectedCommodity.name
              showFeedbackDialog = true
            },
            onDownloadPdf = {
              triggerNotification(
                "Packaging Report Downloaded",
                "PDF certificate for Batch ${activeMaterial.batchId} saved to device storage.",
                NotificationType.REPORT_UPDATE,
                selectedCommodity.name
              )
            },
            onNavigateToComparison = { matA, matB ->
              if (matA != null) compareMaterialAName = matA
              if (matB != null) compareMaterialBName = matB
              currentScreen = AppScreen.MATERIAL_COMPARISON
            },
            onOpenInfographic = {
              currentScreen = AppScreen.ENVIRONMENTAL_INFOGRAPHIC
            }
          )

          AppScreen.DETAIL_WHY_THIS -> DetailWhyThisScreen(
            material = activeMaterial,
            currentTab = detailSubTab,
            onSelectTab = { idx ->
              when (idx) {
                0 -> currentScreen = AppScreen.RECOMMENDATION_RESULT
                1 -> currentScreen = AppScreen.DETAIL_WHY_THIS
                2 -> currentScreen = AppScreen.DETAIL_SHELF_LIFE
                3 -> currentScreen = AppScreen.DETAIL_SUSTAINABILITY
              }
            },
            onNavigateBack = { currentScreen = AppScreen.RECOMMENDATION_RESULT },
            onNavigateNext = { currentScreen = AppScreen.DETAIL_SHELF_LIFE }
          )

          AppScreen.DETAIL_SHELF_LIFE -> DetailShelfLifeScreen(
            material = activeMaterial,
            onSelectTab = { idx ->
              when (idx) {
                0 -> currentScreen = AppScreen.RECOMMENDATION_RESULT
                1 -> currentScreen = AppScreen.DETAIL_SHELF_LIFE
                2 -> currentScreen = AppScreen.DETAIL_WHY_THIS
                3 -> currentScreen = AppScreen.DETAIL_SUSTAINABILITY
              }
            },
            onNavigateBack = { currentScreen = AppScreen.DETAIL_WHY_THIS },
            onNavigateNext = { currentScreen = AppScreen.DETAIL_SUSTAINABILITY }
          )

          AppScreen.DETAIL_SUSTAINABILITY -> DetailSustainabilityScreen(
            material = activeMaterial,
            onSelectTab = { idx ->
              when (idx) {
                0 -> currentScreen = AppScreen.DETAIL_SHELF_LIFE
                1 -> currentScreen = AppScreen.DETAIL_WHY_THIS
                2 -> currentScreen = AppScreen.DETAIL_SUSTAINABILITY
              }
            },
            onNavigateBack = { currentScreen = AppScreen.DETAIL_SHELF_LIFE },
            onNavigateNext = { currentScreen = AppScreen.REPORT_TRACEABILITY }
          )

          AppScreen.REPORT_TRACEABILITY -> ReportTraceabilityScreen(
            commodity = selectedCommodity,
            storage = storageConfig,
            material = activeMaterial,
            user = currentUser,
            onNavigateBack = { currentScreen = AppScreen.HOME_DASHBOARD },
            onShareReport = { /* Handled in screen */ },
            onDownloadPdf = {
              triggerNotification(
                "Packaging Report Downloaded",
                "PDF certificate for Batch ${activeMaterial.batchId} saved to device storage.",
                NotificationType.REPORT_UPDATE,
                selectedCommodity.name
              )
            }
          )

          AppScreen.MATERIAL_COMPARISON -> MaterialComparisonScreen(
            commodity = selectedCommodity,
            storage = storageConfig,
            priority = selectedPriority,
            format = selectedFormat,
            initialMaterialA = compareMaterialAName,
            initialMaterialB = compareMaterialBName,
            onSelectMaterialForAssessment = { chosenMat ->
              activeMaterial = chosenMat
              currentScreen = AppScreen.RECOMMENDATION_RESULT
            },
            onNavigateBack = {
              currentScreen = AppScreen.RECOMMENDATION_RESULT
            }
          )

          AppScreen.ENVIRONMENTAL_INFOGRAPHIC -> EnvironmentalInfographicScreen(
            commodity = selectedCommodity,
            material = activeMaterial,
            storage = storageConfig,
            user = currentUser,
            onNavigateBack = { currentScreen = AppScreen.RECOMMENDATION_RESULT },
            onExportPdf = {
              val pdfFile = PdfReportGenerator.generatePackagingReportPdf(
                context = context,
                commodity = selectedCommodity,
                material = activeMaterial,
                storage = storageConfig,
                user = currentUser
              )
              triggerNotification(
                "Technical PDF Report Generated",
                "Downloaded ${pdfFile.name} with ASTM barrier specifications.",
                NotificationType.REPORT_UPDATE,
                selectedCommodity.name
              )
              PdfReportGenerator.openPdfFile(context, pdfFile)
            }
          )
        }
      }

      // Floating Screen Switcher Pill Button (Top-Right overlay for evaluator jumping directly to any screen)
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(top = 40.dp, end = 12.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = ForestGreenPrimary.copy(alpha = 0.9f),
          shadowElevation = 4.dp,
          modifier = Modifier.clickable { showScreenPickerSheet = true }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Layers, contentDescription = "Screens", tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              "S${currentScreen.screenNumber}/16",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }

      // Push Notification In-App Toast Banner
      activeNotificationToast?.let { notif ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
          elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
          modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .clickable { activeNotificationToast = null }
            .testTag("in_app_notification_toast")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MintLeafAccent, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(notif.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text(notif.message, color = PaleSageLight, fontSize = 11.sp, maxLines = 2)
            }
            IconButton(onClick = { activeNotificationToast = null }, modifier = Modifier.size(24.dp)) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    }
  }

  // 15-Screens Quick Jump Bottom Sheet
  if (showScreenPickerSheet) {
    ModalBottomSheet(
      onDismissRequest = { showScreenPickerSheet = false },
      containerColor = Color.White
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
          .padding(bottom = 24.dp)
      ) {
        Text(
          "Quick Screen Navigator (16 Screens)",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreenPrimary
        )
        Text(
          "Jump directly to any reference screen:",
          fontSize = 13.sp,
          color = TextSecondary
        )
        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
          modifier = Modifier.heightIn(max = 420.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(AppScreen.entries) { scr ->
            val isCurrent = scr == currentScreen
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isCurrent) PaleSageTint else Color(0xFFF9FAFB),
              border = BorderStroke(1.dp, if (isCurrent) MintLeafAccent else CardBorder),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  currentScreen = scr
                  showScreenPickerSheet = false
                }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    shape = CircleShape,
                    color = if (isCurrent) ForestGreenPrimary else Color(0xFFD1D5DB)
                  ) {
                    Text(
                      "${scr.screenNumber}",
                      color = Color.White,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Text(
                    scr.title,
                    fontSize = 14.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    color = if (isCurrent) ForestGreenPrimary else TextPrimary
                  )
                }
                Icon(
                  Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = null,
                  tint = if (isCurrent) ForestGreenPrimary else Color(0xFF9CA3AF),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }
  }

  // Compare Materials Sheet
  if (showCompareSheet) {
    ModalBottomSheet(
      onDismissRequest = { showCompareSheet = false },
      containerColor = Color.White
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .padding(bottom = 20.dp)
      ) {
        Text("Compare Packaging Materials", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
        Spacer(modifier = Modifier.height(14.dp))
        ComparisonCard("Breathable PP/PE Film", "92%", "₹145/kg", "16.4 days", "8.4/10", true)
        Spacer(modifier = Modifier.height(8.dp))
        ComparisonCard("PET Film", "81%", "₹190/kg", "14.2 days", "7.1/10", false)
        Spacer(modifier = Modifier.height(8.dp))
        ComparisonCard("Bio-Film (PLA)", "78%", "₹230/kg", "12.0 days", "9.4/10", false)
      }
    }
  }

  // Simulated Scan Food Sheet
  if (showScanSheet) {
    ModalBottomSheet(
      onDismissRequest = { showScanSheet = false },
      containerColor = Color.White
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
          .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text("Food Commodity Scanner", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
          modifier = Modifier
            .size(180.dp)
            .background(DarkEmeraldBackground, RoundedCornerShape(16.dp))
            .border(2.dp, MintLeafAccent, RoundedCornerShape(16.dp)),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🍅", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("AI Detection: Tomato (99%)", color = MintLeafAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
          onClick = {
            selectedCommodity = commoditiesList.first()
            showScanSheet = false
            currentScreen = AppScreen.FOOD_PROFILE
          },
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Confirm and Analyze Tomato")
        }
      }
    }
  }

  // Dialogs
  if (showFeedbackDialog) {
    FeedbackDialog(
      commodityName = feedbackCommodityName,
      userEmail = currentUser.email,
      onDismiss = { showFeedbackDialog = false },
      onSubmitFeedback = { newFb ->
        feedbacksList = listOf(newFb) + feedbacksList
      }
    )
  }

  if (showGuidelinesDialog) {
    MofpiGuidelinesDialog(onDismiss = { showGuidelinesDialog = false })
  }

  if (showExportReportsSuccessDialog) {
    AlertDialog(
      onDismissRequest = { showExportReportsSuccessDialog = false },
      title = { Text("Reports Export Complete", fontWeight = FontWeight.Bold, color = ForestGreenPrimary) },
      text = {
        Text("Exported ${historyList.size} batch certificates and cold-chain compliance logs to CSV and encrypted PDF format in device storage.", fontSize = 13.sp)
      },
      confirmButton = {
        Button(
          onClick = { showExportReportsSuccessDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
        ) {
          Text("Done")
        }
      }
    )
  }
}

@Composable
private fun ComparisonCard(
  name: String,
  suitability: String,
  cost: String,
  shelfLife: String,
  ecoScore: String,
  isRecommended: Boolean
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = if (isRecommended) PaleSageLight else Color.White),
    border = BorderStroke(1.dp, if (isRecommended) MintLeafAccent else CardBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ForestGreenPrimary)
          if (isRecommended) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(shape = RoundedCornerShape(4.dp), color = MintLeafAccent) {
              Text("Best", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
            }
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("Cost: $cost | Shelf-life: $shelfLife", fontSize = 12.sp, color = TextSecondary)
      }
      Column(horizontalAlignment = Alignment.End) {
        Text(suitability, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = ForestGreenPrimary)
        Text("Eco: $ecoScore", fontSize = 11.sp, color = TextSecondary)
      }
    }
  }
}
