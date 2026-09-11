package com.example.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationShell(
  selectedTab: Int,
  onSelectTab: (Int) -> Unit,
  // Home parameters
  onSelectCommodity: (String) -> Unit,
  onOpenQuickAction: (String) -> Unit,
  onNavigateToCatalog: () -> Unit,
  // History parameters
  historyItems: List<HistoryAssessmentItem>,
  onSelectHistoryItem: (HistoryAssessmentItem) -> Unit,
  onClearAllHistory: () -> Unit,
  onOpenFeedbackFromHistory: (HistoryAssessmentItem) -> Unit,
  onStartNewRecommendation: () -> Unit,
  // Reports parameters
  commodity: CommodityItem,
  storage: StorageConfig,
  material: PackagingMaterial,
  onShareReport: () -> Unit,
  onDownloadPdf: () -> Unit,
  // Profile parameters
  currentUser: UserAccount,
  onUpdateUser: (UserAccount) -> Unit,
  onLogin: (String, String) -> Boolean,
  onSignUp: (String, String, String, PersonaType) -> Boolean,
  onLogout: () -> Unit,
  notifications: List<AppNotification>,
  onSimulateNotification: (NotificationType) -> Unit,
  onMarkNotificationsRead: () -> Unit,
  feedbacks: List<UserFeedback>,
  onOpenFeedbackDialog: () -> Unit,
  onOpenGuidelinesDialog: () -> Unit,
  onExportAllReports: () -> Unit,
  onGoogleSignIn: () -> Unit = {},
  onSyncToFirestore: () -> Unit = {}
) {
  val unreadNotificationsCount = notifications.count { !it.isRead }

  Scaffold(
    bottomBar = {
      NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("main_navigation_bar")
      ) {
        // Tab 0: Home
        NavigationBarItem(
          icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
          label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          selected = selectedTab == 0,
          onClick = { onSelectTab(0) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ForestGreenPrimary,
            selectedTextColor = ForestGreenPrimary,
            indicatorColor = PaleSageTint
          ),
          modifier = Modifier.testTag("tab_home")
        )

        // Tab 1: History
        NavigationBarItem(
          icon = {
            BadgedBox(
              badge = {
                if (historyItems.isNotEmpty()) {
                  Badge(containerColor = MintLeafAccent, contentColor = Color.White) {
                    Text("${historyItems.size}")
                  }
                }
              }
            ) {
              Icon(Icons.Default.History, contentDescription = "History")
            }
          },
          label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          selected = selectedTab == 1,
          onClick = { onSelectTab(1) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ForestGreenPrimary,
            selectedTextColor = ForestGreenPrimary,
            indicatorColor = PaleSageTint
          ),
          modifier = Modifier.testTag("tab_history")
        )

        // Tab 2: Reports
        NavigationBarItem(
          icon = { Icon(Icons.Default.Description, contentDescription = "Reports") },
          label = { Text("Reports", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          selected = selectedTab == 2,
          onClick = { onSelectTab(2) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ForestGreenPrimary,
            selectedTextColor = ForestGreenPrimary,
            indicatorColor = PaleSageTint
          ),
          modifier = Modifier.testTag("tab_reports")
        )

        // Tab 3: Profile
        NavigationBarItem(
          icon = {
            BadgedBox(
              badge = {
                if (unreadNotificationsCount > 0) {
                  Badge(containerColor = Color(0xFFEF4444), contentColor = Color.White) {
                    Text("$unreadNotificationsCount")
                  }
                }
              }
            ) {
              Icon(Icons.Default.Person, contentDescription = "Profile")
            }
          },
          label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          selected = selectedTab == 3,
          onClick = { onSelectTab(3) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ForestGreenPrimary,
            selectedTextColor = ForestGreenPrimary,
            indicatorColor = PaleSageTint
          ),
          modifier = Modifier.testTag("tab_profile")
        )
      }
    },
    containerColor = AppBackground
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      AnimatedContent(
        targetState = selectedTab,
        transitionSpec = {
          fadeIn() togetherWith fadeOut()
        },
        label = "navTabSwitch"
      ) { tabIndex ->
        when (tabIndex) {
          0 -> HomeContentView(
            onSelectCommodity = onSelectCommodity,
            onOpenQuickAction = onOpenQuickAction,
            onNavigateToCatalog = onNavigateToCatalog,
            currentUser = currentUser,
            onOpenProfile = { onSelectTab(3) }
          )
          1 -> HistoryScreen(
            historyItems = historyItems,
            onSelectHistoryItem = onSelectHistoryItem,
            onClearAll = onClearAllHistory,
            onOpenFeedback = onOpenFeedbackFromHistory,
            onStartNewRecommendation = onStartNewRecommendation
          )
          2 -> ReportsView(
            commodity = commodity,
            storage = storage,
            material = material,
            onShareReport = onShareReport,
            onDownloadPdf = onDownloadPdf
          )
          3 -> ProfileScreen(
            currentUser = currentUser,
            onUpdateUser = onUpdateUser,
            onLogin = onLogin,
            onSignUp = onSignUp,
            onLogout = onLogout,
            notifications = notifications,
            onSimulateNotification = onSimulateNotification,
            onMarkNotificationsRead = onMarkNotificationsRead,
            feedbacks = feedbacks,
            onOpenFeedbackDialog = onOpenFeedbackDialog,
            onOpenGuidelinesDialog = onOpenGuidelinesDialog,
            onExportAllReports = onExportAllReports,
            onGoogleSignIn = onGoogleSignIn,
            onSyncToFirestore = onSyncToFirestore
          )
        }
      }
    }
  }
}
