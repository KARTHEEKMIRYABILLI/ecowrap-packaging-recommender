package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.PersonaType
import com.example.model.PriorityType
import com.example.model.UserAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserRepository(private val context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("ecowrap_user_prefs", Context.MODE_PRIVATE)
  private val db = AppDatabase.getInstance(context)
  private val userDao = db.userAccountDao()
  private val repositoryScope = CoroutineScope(Dispatchers.IO)

  companion object {
    private const val KEY_USER_EMAIL = "current_user_email"
    private const val KEY_USER_NAME = "current_user_name"
    private const val KEY_USER_ROLE = "current_user_role"
    private const val KEY_USER_ORG = "current_user_org"
    private const val KEY_USER_NOTIF = "current_user_notif"
    private const val KEY_USER_PRIORITY = "current_user_priority"
    private const val KEY_USER_AVATAR = "current_user_avatar"
    private const val KEY_SELECTED_PERSONA = "selected_persona"
    private const val KEY_USERS_RECORDED = "users_recorded_list"
    private const val KEY_DARK_MODE = "is_dark_mode_enabled"

    val DEFAULT_USER = UserAccount(
      email = "karthikmiryabbelli@gmail.com",
      name = "Karthik",
      role = PersonaType.RESEARCHER,
      organization = "Food Tech Research Lab • Active Member",
      isOptedInNotifications = true,
      defaultPriority = PriorityType.ECO_FRIENDLY
    )

    val SEED_USERS = listOf(
      DEFAULT_USER,
      UserAccount(
        email = "farmer.ramesh@gmail.com",
        name = "Ramesh",
        role = PersonaType.FARMER,
        organization = "Farmer Producer Org (FPO)"
      ),
      UserAccount(
        email = "processor.anita@gmail.com",
        name = "Anita",
        role = PersonaType.FOOD_PROCESSOR,
        organization = "Food Processing Unit"
      )
    )
  }

  init {
    // Seed initial users into Room DB if empty
    repositoryScope.launch {
      val existing = userDao.getAllUsers()
      if (existing.isEmpty()) {
        val entities = SEED_USERS.mapIndexed { idx, u ->
          UserAccountEntity.fromUserAccount(u, isLastActive = idx == 0)
        }
        userDao.insertUsers(entities)
      }
    }
  }

  fun getInitialSelectedPersona(): PersonaType {
    val savedPersonaName = prefs.getString(KEY_SELECTED_PERSONA, null)
    if (savedPersonaName != null) {
      try {
        return PersonaType.valueOf(savedPersonaName)
      } catch (_: Exception) {}
    }
    val roleStr = prefs.getString(KEY_USER_ROLE, PersonaType.RESEARCHER.name) ?: PersonaType.RESEARCHER.name
    return try {
      PersonaType.valueOf(roleStr)
    } catch (_: Exception) {
      PersonaType.RESEARCHER
    }
  }

  fun getInitialUser(): UserAccount {
    val email = prefs.getString(KEY_USER_EMAIL, null) ?: return DEFAULT_USER
    val name = prefs.getString(KEY_USER_NAME, "Karthik") ?: "Karthik"
    val roleStr = prefs.getString(KEY_USER_ROLE, PersonaType.RESEARCHER.name) ?: PersonaType.RESEARCHER.name
    val org = prefs.getString(KEY_USER_ORG, "Food Tech Research Lab • Active Member") ?: "Food Tech Research Lab • Active Member"
    val notif = prefs.getBoolean(KEY_USER_NOTIF, true)
    val priorityStr = prefs.getString(KEY_USER_PRIORITY, PriorityType.ECO_FRIENDLY.name) ?: PriorityType.ECO_FRIENDLY.name
    val avatarUri = prefs.getString(KEY_USER_AVATAR, null)

    val role = try {
      PersonaType.valueOf(roleStr)
    } catch (_: Exception) {
      PersonaType.RESEARCHER
    }
    val priority = try {
      PriorityType.valueOf(priorityStr)
    } catch (_: Exception) {
      PriorityType.ECO_FRIENDLY
    }

    return UserAccount(
      email = email,
      name = name,
      role = role,
      organization = org,
      isOptedInNotifications = notif,
      defaultPriority = priority,
      avatarUri = avatarUri
    )
  }

  fun getInitialRegisteredUsers(): List<UserAccount> {
    val usersSerialized = prefs.getString(KEY_USERS_RECORDED, null)
    if (usersSerialized.isNullOrBlank()) {
      return SEED_USERS
    }
    return try {
      usersSerialized.split(";;;").mapNotNull { entry ->
        val parts = entry.split("|||")
        if (parts.size >= 4) {
          val role = try {
            PersonaType.valueOf(parts[2])
          } catch (_: Exception) {
            PersonaType.RESEARCHER
          }
          val avatar = if (parts.size >= 5 && parts[4].isNotBlank()) parts[4] else null
          UserAccount(
            email = parts[0],
            name = parts[1],
            role = role,
            organization = parts[3],
            avatarUri = avatar
          )
        } else null
      }.ifEmpty { SEED_USERS }
    } catch (_: Exception) {
      SEED_USERS
    }
  }

  fun observeUsers(): Flow<List<UserAccount>> {
    return userDao.getAllUsersFlow().map { entities ->
      if (entities.isEmpty()) SEED_USERS else entities.map { it.toUserAccount() }
    }
  }

  fun saveSelectedPersona(persona: PersonaType) {
    prefs.edit()
      .putString(KEY_SELECTED_PERSONA, persona.name)
      .apply()
  }

  fun saveUser(user: UserAccount, makeActive: Boolean = true) {
    // 1. Save synchronously to SharedPreferences for zero-latency restart
    if (makeActive) {
      prefs.edit()
        .putString(KEY_USER_EMAIL, user.email)
        .putString(KEY_USER_NAME, user.name)
        .putString(KEY_USER_ROLE, user.role.name)
        .putString(KEY_USER_ORG, user.organization)
        .putBoolean(KEY_USER_NOTIF, user.isOptedInNotifications)
        .putString(KEY_USER_PRIORITY, user.defaultPriority.name)
        .putString(KEY_USER_AVATAR, user.avatarUri)
        .putString(KEY_SELECTED_PERSONA, user.role.name)
        .apply()
    }

    // 2. Persist to Room Database on IO thread
    repositoryScope.launch {
      val entity = UserAccountEntity.fromUserAccount(user, isLastActive = makeActive)
      userDao.insertUser(entity)
      if (makeActive) {
        userDao.setActiveUser(user.email)
      }
    }
  }

  fun saveRegisteredUsers(users: List<UserAccount>) {
    val serialized = users.joinToString(";;;") {
      "${it.email}|||${it.name}|||${it.role.name}|||${it.organization}|||${it.avatarUri ?: ""}"
    }
    prefs.edit().putString(KEY_USERS_RECORDED, serialized).apply()

    repositoryScope.launch {
      val entities = users.map { UserAccountEntity.fromUserAccount(it) }
      userDao.insertUsers(entities)
    }
  }

  fun recordAccountCreation(
    name: String,
    email: String,
    role: PersonaType,
    existingUsers: List<UserAccount>
  ): Pair<UserAccount, List<UserAccount>> {
    val org = when (role) {
      PersonaType.FARMER -> "Farmer Producer Org (FPO)"
      PersonaType.FOOD_PROCESSOR -> "Food Processing Unit"
      PersonaType.STARTUP -> "Agri-Tech Startup"
      PersonaType.RESEARCHER -> "Food Tech Research Lab • Active Member"
    }
    val newUser = UserAccount(
      email = email,
      name = name,
      role = role,
      organization = org,
      isOptedInNotifications = true
    )

    val updatedUsers = (existingUsers.filterNot { it.email.equals(email, ignoreCase = true) } + newUser)

    saveUser(newUser, makeActive = true)
    saveRegisteredUsers(updatedUsers)
    saveSelectedPersona(role)

    return Pair(newUser, updatedUsers)
  }

  fun switchUser(
    email: String,
    existingUsers: List<UserAccount>,
    fallbackPersona: PersonaType
  ): Pair<UserAccount, List<UserAccount>> {
    val found = existingUsers.find { it.email.equals(email, ignoreCase = true) }
    if (found != null) {
      saveUser(found, makeActive = true)
      saveSelectedPersona(found.role)
      return Pair(found, existingUsers)
    } else {
      val defaultOrg = when (fallbackPersona) {
        PersonaType.FARMER -> "Farmer Producer Org (FPO)"
        PersonaType.FOOD_PROCESSOR -> "Food Processing Unit"
        PersonaType.STARTUP -> "Agri-Tech Startup"
        PersonaType.RESEARCHER -> "Food Tech Research Lab • Active Member"
      }
      val newUser = UserAccount(
        email = email,
        name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
        role = fallbackPersona,
        organization = defaultOrg
      )
      val updatedList = existingUsers + newUser
      saveUser(newUser, makeActive = true)
      saveRegisteredUsers(updatedList)
      saveSelectedPersona(fallbackPersona)
      return Pair(newUser, updatedList)
    }
  }

  fun syncOnboardingPersona(
    persona: PersonaType,
    currentUser: UserAccount
  ): UserAccount {
    val newOrg = when (persona) {
      PersonaType.FARMER -> "Farmer Producer Org (FPO)"
      PersonaType.FOOD_PROCESSOR -> "Food Processing Unit"
      PersonaType.STARTUP -> "Agri-Tech Startup"
      PersonaType.RESEARCHER -> "Food Tech Research Lab • Active Member"
    }
    val updatedUser = currentUser.copy(
      role = persona,
      organization = newOrg
    )
    saveSelectedPersona(persona)
    saveUser(updatedUser, makeActive = true)
    return updatedUser
  }

  fun getDarkMode(): Boolean {
    return prefs.getBoolean(KEY_DARK_MODE, false)
  }

  fun saveDarkMode(isDark: Boolean) {
    prefs.edit().putBoolean(KEY_DARK_MODE, isDark).apply()
  }
}
