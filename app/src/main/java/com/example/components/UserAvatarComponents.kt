package com.example.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.PersonaType
import com.example.model.UserAccount
import com.example.ui.theme.*

data class AvatarPreset(
  val id: String,
  val title: String,
  val emoji: String,
  val subtitle: String,
  val backgroundColor: Color
)

val AGRI_AVATAR_PRESETS = listOf(
  AvatarPreset("preset:researcher", "Researcher", "🔬", "Lab Scientist", Color(0xFFE8F5E9)),
  AvatarPreset("preset:farmer", "Farmer", "🌱", "FPO Producer", Color(0xFFF1F8E9)),
  AvatarPreset("preset:food_processor", "Processor", "🏭", "Packaging Plant", Color(0xFFE0F2F1)),
  AvatarPreset("preset:startup", "Innovator", "🚀", "Agri Startup", Color(0xFFE0F7FA)),
  AvatarPreset("preset:biochemist", "Biochemist", "🧪", "Barrier Analysis", Color(0xFFEDE7F6)),
  AvatarPreset("preset:engineer", "Agro Tech", "🚜", "Cold Chain Logistics", Color(0xFFFFF3E0)),
  AvatarPreset("preset:postharvest", "Horticulturist", "🍎", "Produce Specialist", Color(0xFFFFEBEE)),
  AvatarPreset("preset:auditor", "MoFPI Auditor", "📋", "Quality Compliance", Color(0xFFE8EAF6))
)

@Composable
fun UserAvatar(
  user: UserAccount,
  size: Dp = 60.dp,
  showEditBadge: Boolean = false,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val clickModifier = if (onClick != null) {
    Modifier.clickable(onClick = onClick)
  } else {
    Modifier
  }

  Box(
    modifier = modifier
      .size(size)
      .then(clickModifier)
      .testTag("user_avatar_box")
  ) {
    Surface(
      shape = CircleShape,
      color = ForestGreenPrimary,
      border = BorderStroke(1.5.dp, PaleSageTint),
      shadowElevation = 2.dp,
      modifier = Modifier
        .size(size)
        .clip(CircleShape)
    ) {
      val avatarUri = user.avatarUri
      when {
        avatarUri != null && avatarUri.startsWith("preset:") -> {
          val preset = AGRI_AVATAR_PRESETS.find { it.id == avatarUri }
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(preset?.backgroundColor ?: PaleSageTint),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = preset?.emoji ?: "🌱",
              fontSize = (size.value * 0.48f).sp
            )
          }
        }

        !avatarUri.isNullOrBlank() -> {
          AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
              .data(avatarUri)
              .crossfade(true)
              .build(),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxSize()
              .clip(CircleShape),
            fallback = null,
            error = null
          )
        }

        else -> {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(ForestGreenPrimary),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = user.name.take(2).uppercase().ifBlank { "ME" },
              color = Color.White,
              fontSize = (size.value * 0.36f).sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    if (showEditBadge) {
      val badgeSize = (size.value * 0.36f).coerceIn(18f, 26f).dp
      Surface(
        shape = CircleShape,
        color = MintLeafAccent,
        border = BorderStroke(1.5.dp, Color.White),
        shadowElevation = 2.dp,
        modifier = Modifier
          .size(badgeSize)
          .align(Alignment.BottomEnd)
      ) {
        Icon(
          Icons.Default.CameraAlt,
          contentDescription = "Upload Profile Picture",
          tint = Color.White,
          modifier = Modifier
            .padding((badgeSize.value * 0.16f).dp)
            .fillMaxSize()
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileAndAvatarBottomSheet(
  currentUser: UserAccount,
  onDismiss: () -> Unit,
  onSaveProfile: (UserAccount) -> Unit
) {
  val context = LocalContext.current
  var editedName by remember { mutableStateOf(currentUser.name) }
  var editedOrg by remember { mutableStateOf(currentUser.organization) }
  var editedAvatarUri by remember { mutableStateOf(currentUser.avatarUri) }
  var editedRole by remember { mutableStateOf(currentUser.role) }
  var showUploadSuccessBanner by remember { mutableStateOf(false) }

  // Android Photo Picker Launcher (Zero-Permission, Privacy-First)
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      try {
        context.contentResolver.takePersistableUriPermission(
          uri,
          Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
      } catch (_: Exception) {}
      editedAvatarUri = uri.toString()
      showUploadSuccessBanner = true
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = Color.White,
    modifier = Modifier.testTag("edit_profile_avatar_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 22.dp, vertical = 8.dp)
        .padding(bottom = 32.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Profile & Picture Settings",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary
          )
          Text(
            text = "Update your picture, persona role and affiliation",
            fontSize = 12.sp,
            color = TextSecondary
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Center Avatar Preview
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          val previewUser = currentUser.copy(
            name = editedName,
            organization = editedOrg,
            avatarUri = editedAvatarUri,
            role = editedRole
          )

          UserAvatar(
            user = previewUser,
            size = 80.dp,
            showEditBadge = true,
            onClick = {
              photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            }
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Upload Photo Button
            Button(
              onClick = {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
              colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
              modifier = Modifier.testTag("upload_profile_picture_button")
            ) {
              Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Upload Photo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            // Remove Photo if set
            if (editedAvatarUri != null) {
              OutlinedButton(
                onClick = { editedAvatarUri = null },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.testTag("remove_profile_picture_button")
              ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Remove", color = Color(0xFFEF4444), fontSize = 12.sp)
              }
            }
          }
        }
      }

      // Success feedback banner when image chosen
      AnimatedVisibility(visible = showUploadSuccessBanner) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = PaleSageLight,
          border = BorderStroke(1.dp, MintLeafAccent.copy(alpha = 0.5f)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Profile photo selected! Tap Save Changes below.", fontSize = 12.sp, color = ForestGreenPrimary, fontWeight = FontWeight.Medium)
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Presets Section
      Text(
        text = "Or Choose Agricultural & Research Avatar Preset:",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
      )
      Spacer(modifier = Modifier.height(8.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(AGRI_AVATAR_PRESETS) { preset ->
          val isSelected = editedAvatarUri == preset.id
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) PaleSageTint else preset.backgroundColor,
            border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) ForestGreenPrimary else CardBorder),
            modifier = Modifier
              .clickable {
                editedAvatarUri = preset.id
                showUploadSuccessBanner = false
              }
              .testTag("preset_avatar_${preset.id.removePrefix("preset:")}")
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
              Text(preset.emoji, fontSize = 24.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = preset.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ForestGreenPrimary else TextPrimary
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // User Information Fields
      OutlinedTextField(
        value = editedName,
        onValueChange = { editedName = it },
        label = { Text("Full Name", fontSize = 12.sp) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("edit_profile_name_input"),
        shape = RoundedCornerShape(10.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = editedOrg,
        onValueChange = { editedOrg = it },
        label = { Text("Organization / Institution", fontSize = 12.sp) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("edit_profile_org_input"),
        shape = RoundedCornerShape(10.dp)
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onDismiss,
          modifier = Modifier
            .weight(1f)
            .height(48.dp),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Cancel")
        }

        Button(
          onClick = {
            onSaveProfile(
              currentUser.copy(
                name = editedName.ifBlank { currentUser.name },
                organization = editedOrg.ifBlank { currentUser.organization },
                avatarUri = editedAvatarUri,
                role = editedRole
              )
            )
            onDismiss()
          },
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("save_profile_button"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
        ) {
          Text("Save Changes", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
