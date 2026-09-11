package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.PersonaType
import com.example.model.PriorityType
import com.example.model.UserAccount

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
  @PrimaryKey val email: String,
  val name: String,
  val roleName: String,
  val organization: String,
  val isOptedInNotifications: Boolean = true,
  val defaultPriorityName: String = "ECO_FRIENDLY",
  val unitMetric: String = "Metric (°C, kg, µm)",
  val isOfflineDbSynced: Boolean = true,
  val memberSince: String = "01 Sep 2026",
  val isLastActiveUser: Boolean = false,
  val avatarUri: String? = null
) {
  fun toUserAccount(): UserAccount {
    val role = try {
      PersonaType.valueOf(roleName)
    } catch (_: Exception) {
      PersonaType.RESEARCHER
    }
    val priority = try {
      PriorityType.valueOf(defaultPriorityName)
    } catch (_: Exception) {
      PriorityType.ECO_FRIENDLY
    }
    return UserAccount(
      email = email,
      name = name,
      role = role,
      organization = organization,
      isOptedInNotifications = isOptedInNotifications,
      defaultPriority = priority,
      unitMetric = unitMetric,
      isOfflineDbSynced = isOfflineDbSynced,
      memberSince = memberSince,
      avatarUri = avatarUri
    )
  }

  companion object {
    fun fromUserAccount(account: UserAccount, isLastActive: Boolean = false): UserAccountEntity {
      return UserAccountEntity(
        email = account.email,
        name = account.name,
        roleName = account.role.name,
        organization = account.organization,
        isOptedInNotifications = account.isOptedInNotifications,
        defaultPriorityName = account.defaultPriority.name,
        unitMetric = account.unitMetric,
        isOfflineDbSynced = account.isOfflineDbSynced,
        memberSince = account.memberSince,
        isLastActiveUser = isLastActive,
        avatarUri = account.avatarUri
      )
    }
  }
}
