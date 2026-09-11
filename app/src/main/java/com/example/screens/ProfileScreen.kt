package com.example.screens

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.EditProfileAndAvatarBottomSheet
import com.example.components.UserAvatar
import com.example.model.*
import com.example.ui.theme.*

data class PasswordValidationState(
  val hasMinLength: Boolean,
  val hasUppercase: Boolean,
  val hasLowercase: Boolean,
  val hasDigit: Boolean,
  val hasSpecialChar: Boolean
) {
  val isValid: Boolean get() = hasMinLength && hasUppercase && hasLowercase && hasDigit && hasSpecialChar
  val score: Int get() = (if (hasMinLength) 1 else 0) +
    (if (hasUppercase) 1 else 0) +
    (if (hasLowercase) 1 else 0) +
    (if (hasDigit) 1 else 0) +
    (if (hasSpecialChar) 1 else 0)
}

fun validatePassword(password: String): PasswordValidationState {
  val hasMinLength = password.length >= 8
  val hasUppercase = password.any { it.isUpperCase() }
  val hasLowercase = password.any { it.isLowerCase() }
  val hasDigit = password.any { it.isDigit() }
  val specialChars = "@#$%^&*()_+-=[]{};':\"\\|,.<>/?!~`"
  val hasSpecialChar = password.any { it in specialChars }
  return PasswordValidationState(
    hasMinLength = hasMinLength,
    hasUppercase = hasUppercase,
    hasLowercase = hasLowercase,
    hasDigit = hasDigit,
    hasSpecialChar = hasSpecialChar
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
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
  var showAuthDialog by remember { mutableStateOf(false) }
  var isSignUpMode by remember { mutableStateOf(false) }
  var showNotificationsSheet by remember { mutableStateOf(false) }
  var showFeedbackListSheet by remember { mutableStateOf(false) }
  var showHelpDialog by remember { mutableStateOf(false) }
  var showPrivacyDialog by remember { mutableStateOf(false) }
  var showLogoutConfirmDialog by remember { mutableStateOf(false) }
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var accountCreationSuccessMessage by remember { mutableStateOf<String?>(null) }

  val unreadNotificationsCount = notifications.count { !it.isRead }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(AppBackground)
      .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    // Top Bar: "Profile & Settings" + Notification Bell
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Profile & Settings",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = ForestGreenPrimary
      )

      // Notification Bell with Unread Badge
      IconButton(
        onClick = {
          showNotificationsSheet = true
          onMarkNotificationsRead()
        },
        modifier = Modifier.testTag("notification_bell_icon")
      ) {
        BadgedBox(
          badge = {
            if (unreadNotificationsCount > 0) {
              Badge(
                containerColor = Color(0xFFEF4444),
                contentColor = Color.White
              ) {
                Text("$unreadNotificationsCount")
              }
            }
          }
        ) {
          Icon(
            Icons.Outlined.Notifications,
            contentDescription = "Notifications",
            tint = ForestGreenPrimary,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier.weight(1f)
    ) {
      // 1. User Header Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, CardBorder),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("user_profile_header_card")
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Circular Avatar with Edit Badge
            UserAvatar(
              user = currentUser,
              size = 62.dp,
              showEditBadge = true,
              onClick = { showEditProfileDialog = true },
              modifier = Modifier.testTag("profile_avatar_clickable")
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
              modifier = Modifier
                .weight(1f)
                .clickable { showEditProfileDialog = true }
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = currentUser.name,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreenPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Role Badge
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = PaleSageTint,
                  border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.4f))
                ) {
                  val roleIcon = when (currentUser.role) {
                    PersonaType.RESEARCHER -> "🔬 "
                    PersonaType.FARMER -> "🌱 "
                    PersonaType.FOOD_PROCESSOR -> "🏭 "
                    PersonaType.STARTUP -> "🚀 "
                  }
                  Text(
                    text = roleIcon + currentUser.role.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ForestGreenPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = currentUser.email,
                fontSize = 12.sp,
                color = TextSecondary
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = currentUser.organization,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MintLeafAccent
              )
            }

            // Quick Actions: Edit Profile & Switch Account
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              IconButton(
                onClick = { showEditProfileDialog = true },
                modifier = Modifier
                  .size(36.dp)
                  .testTag("edit_profile_icon_button")
              ) {
                Icon(
                  Icons.Default.Edit,
                  contentDescription = "Edit Profile",
                  tint = ForestGreenPrimary,
                  modifier = Modifier.size(20.dp)
                )
              }

              IconButton(
                onClick = { showAuthDialog = true },
                modifier = Modifier
                  .size(36.dp)
                  .testTag("switch_account_icon_button")
              ) {
                Icon(
                  Icons.Outlined.SwitchAccount,
                  contentDescription = "Switch or Create Account",
                  tint = ForestGreenPrimary,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }

      // 2. Packaging Preferences Section
      item {
        SectionTitle("Packaging Preferences")
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, CardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Default Persona Selector
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Default Persona Role", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = PaleSageLight,
                border = BorderStroke(0.8.dp, MintLeafAccent.copy(alpha = 0.5f))
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Icon(Icons.Default.Sync, contentDescription = null, tint = MintLeafAccent, modifier = Modifier.size(11.dp))
                  Spacer(modifier = Modifier.width(3.dp))
                  Text("Synced with Onboarding", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                }
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              PersonaType.entries.forEach { persona ->
                val isSelected = currentUser.role == persona
                val personaDisplay = when (persona) {
                  PersonaType.FARMER -> "Farmer"
                  PersonaType.FOOD_PROCESSOR -> "Processor"
                  PersonaType.STARTUP -> "Startup"
                  PersonaType.RESEARCHER -> "Researcher"
                }
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isSelected) PaleSageTint else Color(0xFFF9FAFB),
                  border = BorderStroke(1.dp, if (isSelected) MintLeafAccent else CardBorder),
                  modifier = Modifier
                    .weight(1f)
                    .clickable {
                      onUpdateUser(
                        currentUser.copy(
                          role = persona,
                          organization = when (persona) {
                            PersonaType.FARMER -> "Farmer Producer Org (FPO)"
                            PersonaType.FOOD_PROCESSOR -> "Food Processing Unit"
                            PersonaType.STARTUP -> "Agri-Tech Startup"
                            PersonaType.RESEARCHER -> "Food Tech Research Lab • Active Member"
                          }
                        )
                      )
                    }
                    .testTag("persona_pref_${persona.name.lowercase()}")
                ) {
                  Text(
                    text = personaDisplay,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) ForestGreenPrimary else TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = CardBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Default Priority Toggle
            Text("Default Optimization Priority", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              PriorityType.entries.forEach { priority ->
                val isSelected = currentUser.defaultPriority == priority
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isSelected) PaleSageTint else Color(0xFFF9FAFB),
                  border = BorderStroke(1.dp, if (isSelected) MintLeafAccent else CardBorder),
                  modifier = Modifier
                    .weight(1f)
                    .clickable { onUpdateUser(currentUser.copy(defaultPriority = priority)) }
                ) {
                  Text(
                    text = priority.title,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) ForestGreenPrimary else TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = CardBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Unit Preference
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Unit Preference", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("Temperature (°C), mass (kg), thickness (µm)", fontSize = 11.sp, color = TextSecondary)
              }
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = PaleSageLight
              ) {
                Text(
                  text = "Metric (SI)",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreenPrimary,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }

      // 3. Push Notifications Section
      item {
        SectionTitle("Push Notifications & Alerts")
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, CardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("Opt-in for Push Notifications", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("Receive AI updates for analyzed commodities and reports", fontSize = 11.sp, color = TextSecondary)
              }
              Switch(
                checked = currentUser.isOptedInNotifications,
                onCheckedChange = { checked ->
                  onUpdateUser(currentUser.copy(isOptedInNotifications = checked))
                },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = ForestGreenPrimary,
                  uncheckedThumbColor = Color.White,
                  uncheckedTrackColor = Color(0xFFD1D5DB)
                ),
                modifier = Modifier.testTag("push_notification_toggle")
              )
            }

            if (currentUser.isOptedInNotifications) {
              Spacer(modifier = Modifier.height(12.dp))
              Text("Notification Simulator (Event Triggers):", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedButton(
                  onClick = { onSimulateNotification(NotificationType.AI_RECOMMENDATION) },
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f),
                  contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                  Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("New AI Match", fontSize = 11.sp, color = ForestGreenPrimary, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                  onClick = { onSimulateNotification(NotificationType.REPORT_UPDATE) },
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f),
                  contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                  Icon(Icons.Outlined.Description, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Report Update", fontSize = 11.sp, color = ForestGreenPrimary, fontWeight = FontWeight.SemiBold)
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              TextButton(
                onClick = { showNotificationsSheet = true },
                modifier = Modifier.align(Alignment.End)
              ) {
                Text("View Notification Center (${notifications.size})", fontSize = 12.sp, color = MintLeafAccent, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // 4. Feedback Feature Section
      item {
        SectionTitle("Recommendation Feedback & Quality")
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, CardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .background(PaleSageLight, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Outlined.RateReview, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text("Rate AI Packaging Accuracy", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("Help MoFPI researchers improve packaging model precision", fontSize = 11.sp, color = TextSecondary)
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = onOpenFeedbackDialog,
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
              ) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Give Feedback", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }

              OutlinedButton(
                onClick = { showFeedbackListSheet = true },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
              ) {
                Text("View Feedback (${feedbacks.size})", fontSize = 12.sp, color = ForestGreenPrimary, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }

      // 5. App & AI Settings
      item {
        SectionTitle("App & AI Database Settings")
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, CardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Offline Packaging Database Toggle
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("Offline Packaging Database", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("Local MoFPI horticultural cache for remote farms", fontSize = 11.sp, color = TextSecondary)
              }
              Switch(
                checked = currentUser.isOfflineDbSynced,
                onCheckedChange = { checked ->
                  onUpdateUser(currentUser.copy(isOfflineDbSynced = checked))
                },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = ForestGreenPrimary
                )
              )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CardBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Export All Reports
            SettingRowItem(
              icon = Icons.Outlined.FileDownload,
              title = "Export All Reports (CSV/PDF)",
              subtitle = "Consolidated batch records & carbon audit logs",
              onClick = onExportAllReports
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CardBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // SIH MoFPI Guidelines & Compliance
            SettingRowItem(
              icon = Icons.Outlined.Gavel,
              title = "SIH MoFPI Guidelines & Compliance",
              subtitle = "Government standards for perishable horticulture",
              onClick = onOpenGuidelinesDialog
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CardBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Offline Local Storage Indicator
            SettingRowItem(
              icon = Icons.Outlined.Storage,
              title = "Offline Local Storage",
              subtitle = "User accounts, persona states & active sessions persisted offline",
              onClick = {
                accountCreationSuccessMessage = "Local Storage Active: Offline database holds all registered user profiles and active persona sessions for offline reliability."
              }
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CardBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Secure Cloud Synchronization
            SettingRowItem(
              icon = Icons.Outlined.CloudSync,
              title = "Secure Cloud Synchronization",
              subtitle = "Encrypted cloud persistence, multi-device backup & sync",
              onClick = {
                onSyncToFirestore()
                accountCreationSuccessMessage = "Cloud Sync Active: Profile, assessments, and feedback are securely synchronized and backed up to the cloud."
              }
            )
          }
        }
      }

      // 6. Account Actions
      item {
        SectionTitle("Account & Legal")
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, CardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            SettingRowItem(
              icon = Icons.AutoMirrored.Outlined.HelpOutline,
              title = "Help & Support",
              subtitle = "FAQs and MoFPI technical helpline",
              onClick = { showHelpDialog = true }
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CardBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            SettingRowItem(
              icon = Icons.Outlined.Policy,
              title = "Privacy Policy",
              subtitle = "Data security & enterprise confidentiality",
              onClick = { showPrivacyDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Out Button styled with subtle red-accent outline
            OutlinedButton(
              onClick = { showLogoutConfirmDialog = true },
              border = BorderStroke(1.2.dp, Color(0xFFEF4444)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("sign_out_button"),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
            ) {
              Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }

  // Edit Profile Dialog
  if (showEditProfileDialog) {
    var editName by remember { mutableStateOf(currentUser.name) }
    var editOrg by remember { mutableStateOf(currentUser.organization) }
    var editRole by remember { mutableStateOf(currentUser.role) }

    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Edit, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Edit Profile Info", fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
        }
      },
      text = {
        Column(
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = editName,
            onValueChange = { editName = it },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = editOrg,
            onValueChange = { editOrg = it },
            label = { Text("Organization / Role Note") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Text(
            text = "Select Persona Role",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            PersonaType.entries.forEach { persona ->
              val isSelected = editRole == persona
              val personaDisplay = when (persona) {
                PersonaType.FARMER -> "🌱 Farmer"
                PersonaType.FOOD_PROCESSOR -> "🏭 Processor"
                PersonaType.STARTUP -> "🚀 Startup"
                PersonaType.RESEARCHER -> "🔬 Researcher"
              }
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) PaleSageTint else Color(0xFFF9FAFB),
                border = BorderStroke(1.dp, if (isSelected) MintLeafAccent else CardBorder),
                modifier = Modifier
                  .weight(1f)
                  .clickable {
                    editRole = persona
                    if (editOrg.isBlank() || editOrg.contains("Unit") || editOrg.contains("Lab") || editOrg.contains("FPO") || editOrg.contains("Startup")) {
                      editOrg = when (persona) {
                        PersonaType.FARMER -> "Farmer Producer Org (FPO)"
                        PersonaType.FOOD_PROCESSOR -> "Food Processing Unit"
                        PersonaType.STARTUP -> "Agri-Tech Startup"
                        PersonaType.RESEARCHER -> "Food Tech Research Lab • Active Member"
                      }
                    }
                  }
                  .testTag("edit_dialog_persona_${persona.name.lowercase()}")
              ) {
                Text(
                  text = personaDisplay,
                  fontSize = 10.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) ForestGreenPrimary else TextSecondary,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(vertical = 8.dp)
                )
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateUser(
              currentUser.copy(
                name = editName.trim().ifBlank { currentUser.name },
                organization = editOrg.trim().ifBlank { currentUser.organization },
                role = editRole
              )
            )
            showEditProfileDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
        ) {
          Text("Save Changes")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") }
      }
    )
  }

  // Auth Dialog (Sign Up / Sign In)
  if (showAuthDialog) {
    var authEmail by remember { mutableStateOf("") }
    var authPassword by remember { mutableStateOf("") }
    var authConfirmPassword by remember { mutableStateOf("") }
    var authName by remember { mutableStateOf("") }
    var authRole by remember { mutableStateOf(currentUser.role) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf("") }

    val passwordState = remember(authPassword) { validatePassword(authPassword) }

    AlertDialog(
      onDismissRequest = { showAuthDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            if (isSignUpMode) Icons.Default.PersonAdd else Icons.Default.Lock,
            contentDescription = null,
            tint = ForestGreenPrimary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isSignUpMode) "Create ECO WRAP Account" else "Sign In to ECO WRAP",
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary,
            fontSize = 18.sp
          )
        }
      },
      text = {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          item {
            Text(
              text = if (isSignUpMode) "Setup your profile and password. 8+ chars with capital, small, numbers, and special chars like @, # are mandatory." else "Enter your credentials to access your saved packaging history.",
              fontSize = 12.sp,
              color = TextSecondary
            )
          }

          if (authError.isNotEmpty()) {
            item {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFEE2E2),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(authError, color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
              }
            }
          }

          // Sign In with Verified Cloud Account Option (Single Sign-On / Cloud Auth)
          item {
            Surface(
              onClick = {
                onGoogleSignIn()
                showAuthDialog = false
              },
              shape = RoundedCornerShape(12.dp),
              color = Color.White,
              border = BorderStroke(1.dp, Color(0xFFDADCE0)),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("cloud_sign_in_button")
            ) {
              Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  Icons.Default.VerifiedUser,
                  contentDescription = "Cloud Sign In",
                  tint = ForestGreenPrimary,
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  "Continue with Verified Cloud Account",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF3C4043)
                )
              }
            }
          }

          item {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
              Text(
                "  OR CONTINUE WITH EMAIL  ",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
              )
              HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
            }
          }

          if (isSignUpMode) {
            item {
              OutlinedTextField(
                value = authName,
                onValueChange = { authName = it },
                label = { Text("Full Name *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("auth_name_input")
              )
            }

            item {
              Text("Select Your Persona Role *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                PersonaType.entries.forEach { persona ->
                  val isSelected = authRole == persona
                  val personaLabel = when (persona) {
                    PersonaType.FARMER -> "🌱 Farmer"
                    PersonaType.FOOD_PROCESSOR -> "🏭 Processor"
                    PersonaType.STARTUP -> "🚀 Startup"
                    PersonaType.RESEARCHER -> "🔬 Researcher"
                  }
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) PaleSageTint else Color(0xFFF9FAFB),
                    border = BorderStroke(1.dp, if (isSelected) MintLeafAccent else CardBorder),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { authRole = persona }
                      .testTag("auth_role_${persona.name.lowercase()}")
                  ) {
                    Text(
                      text = personaLabel,
                      fontSize = 10.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected) ForestGreenPrimary else TextSecondary,
                      textAlign = TextAlign.Center,
                      modifier = Modifier.padding(vertical = 6.dp)
                    )
                  }
                }
              }
            }
          }

          item {
            OutlinedTextField(
              value = authEmail,
              onValueChange = { authEmail = it },
              label = { Text("Email Address *") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("auth_email_input")
            )
          }

          item {
            OutlinedTextField(
              value = authPassword,
              onValueChange = { authPassword = it },
              label = { Text("Password *") },
              visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                  Icon(
                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                  )
                }
              },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("auth_password_input")
            )
          }

          // Password validation rules & strength indicator in Sign Up mode
          if (isSignUpMode) {
            item {
              // Strength bar
              val score = passwordState.score
              val strengthColor = when (score) {
                0, 1 -> Color(0xFFEF4444)
                2, 3 -> Color(0xFFF59E0B)
                4 -> Color(0xFF3B82F6)
                else -> Color(0xFF10B981)
              }
              val strengthLabel = when (score) {
                0, 1 -> "Weak"
                2, 3 -> "Fair"
                4 -> "Good"
                else -> "Strong & Valid"
              }

              Column(modifier = Modifier.padding(vertical = 2.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Password Strength", fontSize = 11.sp, color = TextSecondary)
                  Text(strengthLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = strengthColor)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                  progress = { score / 5f },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                  color = strengthColor,
                  trackColor = Color(0xFFE5E7EB)
                )
              }
            }

            item {
              // Requirements checklist
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text("Password Requirements:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                  PasswordRuleItem(
                    text = "8 digits / characters minimum (Mandatory)",
                    isMet = passwordState.hasMinLength
                  )
                  PasswordRuleItem(
                    text = "Capital letters (A-Z)",
                    isMet = passwordState.hasUppercase
                  )
                  PasswordRuleItem(
                    text = "Small letters (a-z)",
                    isMet = passwordState.hasLowercase
                  )
                  PasswordRuleItem(
                    text = "Few of numbers (0-9)",
                    isMet = passwordState.hasDigit
                  )
                  PasswordRuleItem(
                    text = "Special character like @, #, $, %, etc.",
                    isMet = passwordState.hasSpecialChar
                  )
                }
              }
            }

            item {
              OutlinedTextField(
                value = authConfirmPassword,
                onValueChange = { authConfirmPassword = it },
                label = { Text("Confirm Password *") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                  IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                      if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                      contentDescription = if (confirmPasswordVisible) "Hide Password" else "Show Password"
                    )
                  }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("auth_confirm_password_input")
              )
            }
          }

          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              TextButton(
                onClick = {
                  isSignUpMode = !isSignUpMode
                  authError = ""
                }
              ) {
                Text(
                  if (isSignUpMode) "Already have an account? Sign In" else "New user? Create Account",
                  fontSize = 12.sp,
                  color = MintLeafAccent,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            authError = ""
            if (authEmail.isBlank() || authPassword.isBlank()) {
              authError = "Please fill in email and password"
              return@Button
            }
            if (!authEmail.contains("@") || !authEmail.contains(".")) {
              authError = "Please enter a valid email address"
              return@Button
            }
            if (isSignUpMode) {
              if (authName.isBlank()) {
                authError = "Please enter your full name"
                return@Button
              }
              if (!passwordState.hasMinLength) {
                authError = "Password must be at least 8 digits/characters"
                return@Button
              }
              if (!passwordState.hasUppercase) {
                authError = "Password must contain capital letters (A-Z)"
                return@Button
              }
              if (!passwordState.hasLowercase) {
                authError = "Password must contain small letters (a-z)"
                return@Button
              }
              if (!passwordState.hasDigit) {
                authError = "Password must contain numbers (0-9)"
                return@Button
              }
              if (!passwordState.hasSpecialChar) {
                authError = "Password must contain special characters (like @, #, $)"
                return@Button
              }
              if (authPassword != authConfirmPassword) {
                authError = "Passwords do not match"
                return@Button
              }
              val success = onSignUp(authName.trim(), authEmail.trim(), authPassword.trim(), authRole)
              if (success) {
                showAuthDialog = false
                accountCreationSuccessMessage = "Account for ${authName.trim()} created! Persona role (${authRole.title}) and profile settings have been successfully saved to local Room database."
              } else {
                authError = "Failed to create account"
              }
            } else {
              val success = onLogin(authEmail.trim(), authPassword.trim())
              if (success) {
                showAuthDialog = false
              } else {
                authError = "Invalid credentials"
              }
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
          modifier = Modifier.testTag("auth_submit_button")
        ) {
          Text(if (isSignUpMode) "Create Account" else "Sign In")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showAuthDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Logout Confirmation Dialog
  if (showLogoutConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirmDialog = false },
      title = { Text("Sign Out of ECO WRAP?", fontWeight = FontWeight.Bold, color = ForestGreenPrimary) },
      text = { Text("You can sign back in anytime. Your saved assessments and reports remain secure.", fontSize = 14.sp) },
      confirmButton = {
        Button(
          onClick = {
            onLogout()
            showLogoutConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
        ) {
          Text("Sign Out")
        }
      },
      dismissButton = {
        TextButton(onClick = { showLogoutConfirmDialog = false }) { Text("Cancel") }
      }
    )
  }

  // Notifications Bottom Sheet (Notification Center)
  if (showNotificationsSheet) {
    ModalBottomSheet(
      onDismissRequest = { showNotificationsSheet = false },
      containerColor = Color.White
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
          .padding(bottom = 24.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Notification Center", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
          Text("${notifications.size} updates", fontSize = 12.sp, color = TextSecondary)
        }
        Spacer(modifier = Modifier.height(14.dp))

        if (notifications.isEmpty()) {
          Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("No notifications yet.", color = TextSecondary, fontSize = 13.sp)
          }
        } else {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 400.dp)
          ) {
            items(notifications) { notif ->
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (notif.isRead) Color(0xFFF9FAFB) else PaleSageTint),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Icon(
                    when (notif.type) {
                      NotificationType.AI_RECOMMENDATION -> Icons.Default.AutoAwesome
                      NotificationType.REPORT_UPDATE -> Icons.Default.Description
                      NotificationType.SYSTEM -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = ForestGreenPrimary,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestGreenPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(notif.message, fontSize = 12.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(notif.timestamp, fontSize = 10.sp, color = TextMuted)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Feedback List Bottom Sheet
  if (showFeedbackListSheet) {
    ModalBottomSheet(
      onDismissRequest = { showFeedbackListSheet = false },
      containerColor = Color.White
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
          .padding(bottom = 24.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Submitted Feedback Logs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
          TextButton(onClick = onOpenFeedbackDialog) {
            Text("+ Add Feedback", color = MintLeafAccent, fontWeight = FontWeight.Bold)
          }
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (feedbacks.isEmpty()) {
          Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("No feedback recorded yet. Rate an AI recommendation to see logs here.", color = TextSecondary, fontSize = 13.sp)
          }
        } else {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 400.dp)
          ) {
            items(feedbacks) { fb ->
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(fb.commodityName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ForestGreenPrimary)
                    Text(fb.date, fontSize = 11.sp, color = TextMuted)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Accuracy: ${fb.ratingAccuracy}★  |  Usefulness: ${fb.ratingUsefulness}★", fontSize = 12.sp, color = MintLeafAccent, fontWeight = FontWeight.SemiBold)
                  }
                  if (fb.comments.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("\"${fb.comments}\"", fontSize = 12.sp, color = TextPrimary)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Surface(shape = RoundedCornerShape(4.dp), color = PaleSageTint) {
                    Text(fb.status, fontSize = 10.sp, color = ForestGreenPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Help & Support Dialog
  if (showHelpDialog) {
    AlertDialog(
      onDismissRequest = { showHelpDialog = false },
      title = { Text("MoFPI Help & Support", fontWeight = FontWeight.Bold, color = ForestGreenPrimary) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Ministry of Food Processing Industries (MoFPI) AI Support Line", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          Text("• Email: support.ecowrap@mofpi.gov.in\n• Helpline: 1800-111-WRAP (Mon-Fri 9AM-6PM IST)\n• Portal: www.mofpi.gov.in/sih-ecowrap", fontSize = 12.sp, color = TextSecondary)
          Spacer(modifier = Modifier.height(6.dp))
          Text("Frequently Asked Questions:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          Text("• How is shelf-life calculated?\nShelf life utilizes food respiration Arrhenius kinetics combined with film gas permeability rates (OTR & WVTR).", fontSize = 12.sp, color = TextSecondary)
        }
      },
      confirmButton = {
        Button(onClick = { showHelpDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)) {
          Text("Close")
        }
      }
    )
  }

  // Privacy Policy Dialog
  if (showPrivacyDialog) {
    AlertDialog(
      onDismissRequest = { showPrivacyDialog = false },
      title = { Text("Privacy & Data Governance", fontWeight = FontWeight.Bold, color = ForestGreenPrimary) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("ECO WRAP Enterprise Confidentiality Statement", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          Text("• All food commodity proprietary storage data and batch parameters are processed locally and securely encrypted.\n• Authentication credentials and supply chain traceability logs adhere to Indian Digital Personal Data Protection (DPDP) standards.\n• No trade secrets or cold-chain logistics telemetry are shared with third-party advertising networks.", fontSize = 12.sp, color = TextSecondary)
        }
      },
      confirmButton = {
        Button(onClick = { showPrivacyDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)) {
          Text("Understood")
        }
      }
    )
  }

  // Local Storage Account Creation Confirmation Dialog
  accountCreationSuccessMessage?.let { msg ->
    AlertDialog(
      onDismissRequest = { accountCreationSuccessMessage = null },
      icon = {
        Icon(Icons.Default.CloudDone, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(32.dp))
      },
      title = {
        Text("Local Storage Synced", fontWeight = FontWeight.Bold, color = ForestGreenPrimary, fontSize = 18.sp)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(msg, fontSize = 13.sp, color = TextPrimary, lineHeight = 18.sp)
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = PaleSageLight,
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
              Icon(Icons.Default.Storage, contentDescription = null, tint = MintLeafAccent, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Room DB • table: user_accounts", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { accountCreationSuccessMessage = null },
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
        ) {
          Text("OK")
        }
      }
    )
  }

  // Edit Profile & Avatar Bottom Sheet
  if (showEditProfileDialog) {
    EditProfileAndAvatarBottomSheet(
      currentUser = currentUser,
      onDismiss = { showEditProfileDialog = false },
      onSaveProfile = { updated ->
        onUpdateUser(updated)
      }
    )
  }
}

@Composable
private fun SectionTitle(title: String) {
  Text(
    text = title,
    fontSize = 14.sp,
    fontWeight = FontWeight.Bold,
    color = ForestGreenPrimary,
    modifier = Modifier.padding(vertical = 4.dp)
  )
}

@Composable
private fun SettingRowItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(20.dp))
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
      Text(subtitle, fontSize = 11.sp, color = TextSecondary)
    }
    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(16.dp))
  }
}

@Composable
private fun PasswordRuleItem(
  text: String,
  isMet: Boolean
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)
  ) {
    Icon(
      imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
      contentDescription = null,
      tint = if (isMet) Color(0xFF10B981) else TextMuted,
      modifier = Modifier.size(14.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = text,
      fontSize = 11.sp,
      fontWeight = if (isMet) FontWeight.SemiBold else FontWeight.Normal,
      color = if (isMet) ForestGreenPrimary else TextSecondary
    )
  }
}
