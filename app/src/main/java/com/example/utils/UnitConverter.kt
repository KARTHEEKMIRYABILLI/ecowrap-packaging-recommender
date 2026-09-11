package com.example.utils

import kotlin.math.roundToInt

enum class UnitSystem(val label: String, val tempUnit: String, val weightUnit: String) {
  METRIC("Metric", "°C", "kg / g"),
  IMPERIAL("Imperial", "°F", "lbs / oz")
}

enum class TemperatureUnit(val symbol: String, val label: String) {
  CELSIUS("°C", "Celsius"),
  FAHRENHEIT("°F", "Fahrenheit"),
  KELVIN("K", "Kelvin")
}

enum class WeightUnit(val symbol: String, val label: String) {
  KILOGRAM("kg", "Kilograms"),
  GRAM("g", "Grams"),
  POUND("lbs", "Pounds"),
  OUNCE("oz", "Ounces"),
  METRIC_TON("MT", "Metric Tonnes")
}

enum class ThicknessUnit(val symbol: String, val label: String) {
  MICRON("µm", "Microns (µm)"),
  MIL("mil", "Mils (thou)"),
  GAUGE("ga", "Gauge")
}

object UnitConverter {

  // --- Temperature Conversions ---
  fun celsiusToFahrenheit(c: Double): Double = (c * 9.0 / 5.0) + 32.0

  fun fahrenheitToCelsius(f: Double): Double = (f - 32.0) * 5.0 / 9.0

  fun celsiusToKelvin(c: Double): Double = c + 273.15

  fun kelvinToCelsius(k: Double): Double = k - 273.15

  fun fahrenheitToKelvin(f: Double): Double = celsiusToKelvin(fahrenheitToCelsius(f))

  fun kelvinToFahrenheit(k: Double): Double = celsiusToFahrenheit(kelvinToCelsius(k))

  fun convertTemperature(value: Double, from: TemperatureUnit, to: TemperatureUnit): Double {
    if (from == to) return value
    val celsius = when (from) {
      TemperatureUnit.CELSIUS -> value
      TemperatureUnit.FAHRENHEIT -> fahrenheitToCelsius(value)
      TemperatureUnit.KELVIN -> kelvinToCelsius(value)
    }
    return when (to) {
      TemperatureUnit.CELSIUS -> celsius
      TemperatureUnit.FAHRENHEIT -> celsiusToFahrenheit(celsius)
      TemperatureUnit.KELVIN -> celsiusToKelvin(celsius)
    }
  }

  // --- Weight & Mass Conversions ---
  const val KG_TO_LBS_FACTOR = 2.2046226218
  const val KG_TO_OZ_FACTOR = 35.27396195
  const val LBS_TO_OZ_FACTOR = 16.0

  fun kgToLbs(kg: Double): Double = kg * KG_TO_LBS_FACTOR

  fun lbsToKg(lbs: Double): Double = lbs / KG_TO_LBS_FACTOR

  fun kgToGrams(kg: Double): Double = kg * 1000.0

  fun gramsToKg(g: Double): Double = g / 1000.0

  fun gramsToOz(g: Double): Double = g * 0.03527396195

  fun ozToGrams(oz: Double): Double = oz / 0.03527396195

  fun lbsToOz(lbs: Double): Double = lbs * LBS_TO_OZ_FACTOR

  fun ozToLbs(oz: Double): Double = oz / LBS_TO_OZ_FACTOR

  fun convertWeight(value: Double, from: WeightUnit, to: WeightUnit): Double {
    if (from == to) return value
    // Normalize to kg first
    val kg = when (from) {
      WeightUnit.KILOGRAM -> value
      WeightUnit.GRAM -> gramsToKg(value)
      WeightUnit.POUND -> lbsToKg(value)
      WeightUnit.OUNCE -> gramsToKg(ozToGrams(value))
      WeightUnit.METRIC_TON -> value * 1000.0
    }
    return when (to) {
      WeightUnit.KILOGRAM -> kg
      WeightUnit.GRAM -> kgToGrams(kg)
      WeightUnit.POUND -> kgToLbs(kg)
      WeightUnit.OUNCE -> kg * KG_TO_OZ_FACTOR
      WeightUnit.METRIC_TON -> kg / 1000.0
    }
  }

  // --- Packaging Film Thickness ---
  fun micronsToMil(um: Double): Double = um / 25.4

  fun milToMicrons(mil: Double): Double = mil * 25.4

  fun micronsToGauge(um: Double): Double = (um / 25.4) * 100.0

  fun gaugeToMicrons(gauge: Double): Double = (gauge / 100.0) * 25.4

  fun convertThickness(value: Double, from: ThicknessUnit, to: ThicknessUnit): Double {
    if (from == to) return value
    val microns = when (from) {
      ThicknessUnit.MICRON -> value
      ThicknessUnit.MIL -> milToMicrons(value)
      ThicknessUnit.GAUGE -> gaugeToMicrons(value)
    }
    return when (to) {
      ThicknessUnit.MICRON -> microns
      ThicknessUnit.MIL -> micronsToMil(microns)
      ThicknessUnit.GAUGE -> micronsToGauge(microns)
    }
  }

  // --- Formatting Helpers ---
  fun formatTemp(celsius: Double, system: UnitSystem): String {
    return if (system == UnitSystem.METRIC) {
      "${((celsius * 10).roundToInt() / 10.0)}°C"
    } else {
      val fahrenheit = celsiusToFahrenheit(celsius)
      "${((fahrenheit * 10).roundToInt() / 10.0)}°F"
    }
  }

  fun formatTempWithBoth(celsius: Double): String {
    val f = celsiusToFahrenheit(celsius)
    val cRounded = (celsius * 10).roundToInt() / 10.0
    val fRounded = (f * 10).roundToInt() / 10.0
    return "$cRounded°C / $fRounded°F"
  }

  fun formatWeight(kg: Double, system: UnitSystem): String {
    return if (system == UnitSystem.METRIC) {
      if (kg < 1.0) {
        "${(kg * 1000).roundToInt()} g"
      } else {
        "${((kg * 100).roundToInt() / 100.0)} kg"
      }
    } else {
      val lbs = kgToLbs(kg)
      if (lbs < 1.0) {
        val oz = lbs * 16.0
        "${((oz * 10).roundToInt() / 10.0)} oz"
      } else {
        "${((lbs * 100).roundToInt() / 100.0)} lbs"
      }
    }
  }

  fun formatWeightWithBoth(kg: Double): String {
    val lbs = kgToLbs(kg)
    val kgFormatted = if (kg < 1.0) "${(kg * 1000).roundToInt()}g" else "${((kg * 10).roundToInt() / 10.0)}kg"
    val lbsFormatted = if (lbs < 1.0) "${((lbs * 16 * 10).roundToInt() / 10.0)}oz" else "${((lbs * 10).roundToInt() / 10.0)}lbs"
    return "$kgFormatted ($lbsFormatted)"
  }
}
