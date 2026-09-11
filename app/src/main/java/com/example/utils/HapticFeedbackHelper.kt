package com.example.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Provides structured tactile and haptic feedback across key user interactions:
 * - Form submissions & step progression
 * - Successful AI packaging recommendation generation
 * - Parameter adjustments, chips, & dialog actions
 * - Error validation warnings
 */
class AppHapticManager(
  private val context: Context,
  private val composeHaptic: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
  private val vibrator: Vibrator? by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      vibratorManager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  }

  /**
   * Subtle click for standard button taps, chip selectors, and toggles.
   */
  fun performClick() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator?.hasVibrator() == true) {
        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
      } else {
        composeHaptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
      }
    } catch (_: Exception) {
      composeHaptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
  }

  /**
   * Distinctive double-pulse success haptic for major milestones:
   * e.g., Material recommendation generation completion, saving custom parameters.
   */
  fun performSuccess() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator?.hasVibrator() == true) {
        // Double-beat celebration pulse: 35ms buzz, 60ms pause, 65ms buzz
        val timings = longArrayOf(0, 35, 60, 65)
        val amplitudes = intArrayOf(0, 180, 0, 255)
        vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
      } else {
        composeHaptic.performHapticFeedback(HapticFeedbackType.LongPress)
      }
    } catch (_: Exception) {
      composeHaptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }

  /**
   * Solid tactile confirmation feedback on proceeding to the next step or submitting input forms.
   */
  fun performFormSubmit() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator?.hasVibrator() == true) {
        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator?.hasVibrator() == true) {
        vibrator?.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        composeHaptic.performHapticFeedback(HapticFeedbackType.LongPress)
      }
    } catch (_: Exception) {
      composeHaptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }

  /**
   * Light haptic for slider adjustments, micro-interactions, unit toggling.
   */
  fun performTick() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator?.hasVibrator() == true) {
        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
      } else {
        composeHaptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
      }
    } catch (_: Exception) {
      composeHaptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
  }

  /**
   * Alert pulse for invalid form inputs or error states.
   */
  fun performError() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator?.hasVibrator() == true) {
        val timings = longArrayOf(0, 40, 40, 40)
        val amplitudes = intArrayOf(0, 200, 0, 200)
        vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
      } else {
        composeHaptic.performHapticFeedback(HapticFeedbackType.LongPress)
      }
    } catch (_: Exception) {
      composeHaptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }
}

/**
 * Convenient Composable hook to access [AppHapticManager].
 */
@Composable
fun rememberAppHaptics(): AppHapticManager {
  val context = LocalContext.current
  val composeHaptic = LocalHapticFeedback.current
  return remember(context, composeHaptic) {
    AppHapticManager(context.applicationContext, composeHaptic)
  }
}
