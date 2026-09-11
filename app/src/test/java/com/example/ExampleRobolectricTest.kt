package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.PersonaType
import com.example.model.UserAccount
import com.example.screens.validatePassword
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ECO WRAP", appName)
  }

  @Test
  fun `password validation enforces 8 chars uppercase lowercase number and special chars`() {
    // Too short (< 8 chars)
    val shortPass = validatePassword("Ab1@")
    assertFalse(shortPass.hasMinLength)
    assertFalse(shortPass.isValid)

    // Only numbers and letters, no special characters
    val noSpecial = validatePassword("Password123")
    assertTrue(noSpecial.hasMinLength)
    assertTrue(noSpecial.hasUppercase)
    assertTrue(noSpecial.hasLowercase)
    assertTrue(noSpecial.hasDigit)
    assertFalse(noSpecial.hasSpecialChar)
    assertFalse(noSpecial.isValid)

    // Only lowercase and special, no uppercase or digit
    val noUpperOrDigit = validatePassword("password@#")
    assertFalse(noUpperOrDigit.hasUppercase)
    assertFalse(noUpperOrDigit.hasDigit)
    assertFalse(noUpperOrDigit.isValid)

    // Only uppercase, lowercase, numbers, no special
    val noSpecial2 = validatePassword("AgriFood2026")
    assertFalse(noSpecial2.hasSpecialChar)
    assertFalse(noSpecial2.isValid)

    // Meets all 5 criteria: 8+ length, uppercase, lowercase, digit, special character (@, #)
    val validPass1 = validatePassword("EcoWrap@2026")
    assertTrue(validPass1.hasMinLength)
    assertTrue(validPass1.hasUppercase)
    assertTrue(validPass1.hasLowercase)
    assertTrue(validPass1.hasDigit)
    assertTrue(validPass1.hasSpecialChar)
    assertTrue(validPass1.isValid)
    assertEquals(5, validPass1.score)

    val validPass2 = validatePassword("Researcher#99")
    assertTrue(validPass2.isValid)
  }

  @Test
  fun `researcher role displays correctly on UserAccount`() {
    val researcherUser = UserAccount(
      email = "karthikmiryabbelli@gmail.com",
      name = "Karthik",
      role = PersonaType.RESEARCHER,
      organization = "Food Tech Research Lab • Active Member"
    )
    assertEquals(PersonaType.RESEARCHER, researcherUser.role)
    assertEquals("Researcher", researcherUser.role.title)
    assertEquals("Food Tech Research Lab • Active Member", researcherUser.organization)
  }

  @Test
  fun `user repository correctly syncs onboarding persona to profile`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = com.example.data.UserRepository(context)

    val initialUser = repository.getInitialUser()
    val syncedUser = repository.syncOnboardingPersona(PersonaType.FARMER, initialUser)

    assertEquals(PersonaType.FARMER, syncedUser.role)
    assertEquals("Farmer Producer Org (FPO)", syncedUser.organization)
    assertEquals(PersonaType.FARMER, repository.getInitialSelectedPersona())
  }

  @Test
  fun `account creation persists to local storage repository`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = com.example.data.UserRepository(context)

    val (newUser, userList) = repository.recordAccountCreation(
      name = "Dr. Shreya",
      email = "shreya.scientist@lab.in",
      role = PersonaType.RESEARCHER,
      existingUsers = repository.getInitialRegisteredUsers()
    )

    assertEquals("Dr. Shreya", newUser.name)
    assertEquals("shreya.scientist@lab.in", newUser.email)
    assertEquals(PersonaType.RESEARCHER, newUser.role)
    assertTrue(userList.any { it.email == "shreya.scientist@lab.in" })

    val activeUser = repository.getInitialUser()
    assertEquals("shreya.scientist@lab.in", activeUser.email)
    assertEquals(PersonaType.RESEARCHER, activeUser.role)
  }
}
