package com.example.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.utils.TemperatureUnit
import com.example.utils.ThicknessUnit
import com.example.utils.UnitConverter
import com.example.utils.WeightUnit
import com.example.utils.rememberAppHaptics
import kotlin.math.roundToInt

@Composable
fun UnitConverterDialog(
  initialTemperatureC: Double? = null,
  initialWeightKg: Double? = null,
  onDismiss: () -> Unit,
  onApplyTemperature: ((Double) -> Unit)? = null,
  onApplyWeight: ((Double) -> Unit)? = null
) {
  val haptics = rememberAppHaptics()
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Temperature, 1: Weight & Batch, 2: Packaging Film

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color.White,
      tonalElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(vertical = 12.dp)
        .testTag("unit_converter_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .background(PaleSageTint, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Calculate,
                contentDescription = "Unit Converter",
                tint = ForestGreenPrimary,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                "Unit Converter Utility",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = ForestGreenPrimary
              )
              Text(
                "Metric ⇄ Imperial & Packaging Units",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp).testTag("unit_converter_close_btn")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation Tabs (Temperature, Weight, Film Thickness)
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = PaleSageLight,
          contentColor = ForestGreenPrimary,
          indicator = {},
          divider = {},
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .padding(2.dp)
        ) {
          val tabs = listOf("🌡️ Temp", "⚖️ Weight", "🛡️ Film")
          tabs.forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            Tab(
              selected = isSelected,
              onClick = {
                haptics.performTick()
                selectedTab = index
              },
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) Color.White else Color.Transparent)
                .padding(vertical = 8.dp),
              text = {
                Text(
                  text = title,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) ForestGreenPrimary else TextSecondary
                )
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Content with Scroll
        Box(
          modifier = Modifier
            .weight(1f, fill = false)
            .heightIn(max = 380.dp)
            .verticalScroll(rememberScrollState())
        ) {
          when (selectedTab) {
            0 -> TemperatureConverterView(
              initialCelsius = initialTemperatureC ?: 8.0,
              onApply = { cVal ->
                haptics.performSuccess()
                onApplyTemperature?.invoke(cVal)
                onDismiss()
              }
            )
            1 -> WeightConverterView(
              initialKg = initialWeightKg ?: 1.0,
              onApply = { kgVal ->
                haptics.performSuccess()
                onApplyWeight?.invoke(kgVal)
                onDismiss()
              }
            )
            2 -> PackagingFilmConverterView()
          }
        }
      }
    }
  }
}

@Composable
private fun TemperatureConverterView(
  initialCelsius: Double,
  onApply: (Double) -> Unit
) {
  var celsiusText by remember { mutableStateOf(((initialCelsius * 10).roundToInt() / 10.0).toString()) }
  var fahrenheitText by remember {
    val f = UnitConverter.celsiusToFahrenheit(initialCelsius)
    mutableStateOf(((f * 10).roundToInt() / 10.0).toString())
  }
  var kelvinText by remember {
    val k = UnitConverter.celsiusToKelvin(initialCelsius)
    mutableStateOf(((k * 10).roundToInt() / 10.0).toString())
  }

  fun updateFromCelsius(cStr: String) {
    celsiusText = cStr
    val c = cStr.toDoubleOrNull()
    if (c != null) {
      val f = UnitConverter.celsiusToFahrenheit(c)
      val k = UnitConverter.celsiusToKelvin(c)
      fahrenheitText = ((f * 10).roundToInt() / 10.0).toString()
      kelvinText = ((k * 10).roundToInt() / 10.0).toString()
    }
  }

  fun updateFromFahrenheit(fStr: String) {
    fahrenheitText = fStr
    val f = fStr.toDoubleOrNull()
    if (f != null) {
      val c = UnitConverter.fahrenheitToCelsius(f)
      val k = UnitConverter.celsiusToKelvin(c)
      celsiusText = ((c * 10).roundToInt() / 10.0).toString()
      kelvinText = ((k * 10).roundToInt() / 10.0).toString()
    }
  }

  fun updateFromKelvin(kStr: String) {
    kelvinText = kStr
    val k = kStr.toDoubleOrNull()
    if (k != null) {
      val c = UnitConverter.kelvinToCelsius(k)
      val f = UnitConverter.celsiusToFahrenheit(c)
      celsiusText = ((c * 10).roundToInt() / 10.0).toString()
      fahrenheitText = ((f * 10).roundToInt() / 10.0).toString()
    }
  }

  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    // Celsius Input Field
    OutlinedTextField(
      value = celsiusText,
      onValueChange = { updateFromCelsius(it) },
      label = { Text("Celsius (°C) - Metric Standard", fontSize = 12.sp) },
      trailingIcon = {
        Surface(color = PaleSageTint, shape = RoundedCornerShape(6.dp)) {
          Text("°C", fontWeight = FontWeight.Bold, color = ForestGreenPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp)
        }
      },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("converter_celsius_input"),
      shape = RoundedCornerShape(12.dp)
    )

    // Fahrenheit Input Field
    OutlinedTextField(
      value = fahrenheitText,
      onValueChange = { updateFromFahrenheit(it) },
      label = { Text("Fahrenheit (°F) - Imperial Standard", fontSize = 12.sp) },
      trailingIcon = {
        Surface(color = PaleSageTint, shape = RoundedCornerShape(6.dp)) {
          Text("°F", fontWeight = FontWeight.Bold, color = ForestGreenPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp)
        }
      },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("converter_fahrenheit_input"),
      shape = RoundedCornerShape(12.dp)
    )

    // Kelvin Input Field
    OutlinedTextField(
      value = kelvinText,
      onValueChange = { updateFromKelvin(it) },
      label = { Text("Kelvin (K) - Thermodynamic Scale", fontSize = 12.sp) },
      trailingIcon = {
        Surface(color = PaleSageTint, shape = RoundedCornerShape(6.dp)) {
          Text("K", fontWeight = FontWeight.Bold, color = ForestGreenPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp)
        }
      },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp)
    )

    // Food Storage Industry Presets
    Text("Quick Food Storage Presets:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)

    val presets = listOf(
      Triple("Deep Freeze", -18.0, "🧊"),
      Triple("Cold Chain", 4.0, "❄️"),
      Triple("Cool Cellar", 12.0, "🍃"),
      Triple("Ambient Room", 22.0, "🌡️"),
      Triple("Warm Transit", 35.0, "☀️")
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      presets.forEach { (name, cVal, icon) ->
        Surface(
          onClick = { updateFromCelsius(cVal.toString()) },
          shape = RoundedCornerShape(8.dp),
          color = PaleSageLight,
          border = BorderStroke(1.dp, CardBorder),
          modifier = Modifier.weight(1f)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp)
          ) {
            Text(icon, fontSize = 13.sp)
            Text(name, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary, maxLines = 1)
            Text("${cVal.toInt()}°C", fontSize = 9.sp, color = TextSecondary)
          }
        }
      }
    }

    // Apply Button
    val cParsed = celsiusText.toDoubleOrNull()
    Button(
      onClick = { if (cParsed != null) onApply(cParsed) },
      enabled = cParsed != null,
      modifier = Modifier.fillMaxWidth().height(46.dp).testTag("apply_converter_temp_btn"),
      colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
      shape = RoundedCornerShape(12.dp)
    ) {
      Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text("Apply ${celsiusText}°C (${fahrenheitText}°F) to Form", fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun WeightConverterView(
  initialKg: Double,
  onApply: (Double) -> Unit
) {
  var kgText by remember { mutableStateOf(((initialKg * 100).roundToInt() / 100.0).toString()) }
  var gramsText by remember { mutableStateOf(((initialKg * 1000).roundToInt()).toString()) }
  var lbsText by remember {
    val lbs = UnitConverter.kgToLbs(initialKg)
    mutableStateOf(((lbs * 100).roundToInt() / 100.0).toString())
  }
  var ozText by remember {
    val oz = initialKg * UnitConverter.KG_TO_OZ_FACTOR
    mutableStateOf(((oz * 10).roundToInt() / 10.0).toString())
  }

  fun updateFromKg(kgStr: String) {
    kgText = kgStr
    val kg = kgStr.toDoubleOrNull()
    if (kg != null) {
      gramsText = ((kg * 1000).roundToInt()).toString()
      val lbs = UnitConverter.kgToLbs(kg)
      val oz = kg * UnitConverter.KG_TO_OZ_FACTOR
      lbsText = ((lbs * 100).roundToInt() / 100.0).toString()
      ozText = ((oz * 10).roundToInt() / 10.0).toString()
    }
  }

  fun updateFromLbs(lbsStr: String) {
    lbsText = lbsStr
    val lbs = lbsStr.toDoubleOrNull()
    if (lbs != null) {
      val kg = UnitConverter.lbsToKg(lbs)
      kgText = ((kg * 100).roundToInt() / 100.0).toString()
      gramsText = ((kg * 1000).roundToInt()).toString()
      val oz = lbs * 16.0
      ozText = ((oz * 10).roundToInt() / 10.0).toString()
    }
  }

  fun updateFromGrams(gStr: String) {
    gramsText = gStr
    val g = gStr.toDoubleOrNull()
    if (g != null) {
      val kg = g / 1000.0
      kgText = ((kg * 1000).roundToInt() / 1000.0).toString()
      val lbs = UnitConverter.kgToLbs(kg)
      val oz = g * 0.03527396195
      lbsText = ((lbs * 100).roundToInt() / 100.0).toString()
      ozText = ((oz * 10).roundToInt() / 10.0).toString()
    }
  }

  fun updateFromOz(ozStr: String) {
    ozText = ozStr
    val oz = ozStr.toDoubleOrNull()
    if (oz != null) {
      val g = oz / 0.03527396195
      val kg = g / 1000.0
      val lbs = oz / 16.0
      gramsText = ((g * 10).roundToInt() / 10.0).toString()
      kgText = ((kg * 1000).roundToInt() / 1000.0).toString()
      lbsText = ((lbs * 100).roundToInt() / 100.0).toString()
    }
  }

  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    // Kilograms (kg)
    OutlinedTextField(
      value = kgText,
      onValueChange = { updateFromKg(it) },
      label = { Text("Kilograms (kg) - Metric", fontSize = 12.sp) },
      trailingIcon = {
        Surface(color = PaleSageTint, shape = RoundedCornerShape(6.dp)) {
          Text("kg", fontWeight = FontWeight.Bold, color = ForestGreenPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp)
        }
      },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("converter_kg_input"),
      shape = RoundedCornerShape(12.dp)
    )

    // Pounds (lbs)
    OutlinedTextField(
      value = lbsText,
      onValueChange = { updateFromLbs(it) },
      label = { Text("Pounds (lbs) - Imperial", fontSize = 12.sp) },
      trailingIcon = {
        Surface(color = PaleSageTint, shape = RoundedCornerShape(6.dp)) {
          Text("lbs", fontWeight = FontWeight.Bold, color = ForestGreenPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp)
        }
      },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("converter_lbs_input"),
      shape = RoundedCornerShape(12.dp)
    )

    // Grams & Ounces Dual Row
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      OutlinedTextField(
        value = gramsText,
        onValueChange = { updateFromGrams(it) },
        label = { Text("Grams (g)", fontSize = 11.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp)
      )
      OutlinedTextField(
        value = ozText,
        onValueChange = { updateFromOz(it) },
        label = { Text("Ounces (oz)", fontSize = 11.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp)
      )
    }

    // Packaging Batch Size Presets
    Text("Standard Package Sizes:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
    val packPresets = listOf(
      Triple("250g Pouch", 0.25, "8.8 oz"),
      Triple("500g Bag", 0.5, "1.1 lbs"),
      Triple("1kg Pack", 1.0, "2.2 lbs"),
      Triple("5kg Box", 5.0, "11.0 lbs"),
      Triple("25kg Crate", 25.0, "55.1 lbs")
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      packPresets.forEach { (name, kgVal, impSub) ->
        Surface(
          onClick = { updateFromKg(kgVal.toString()) },
          shape = RoundedCornerShape(8.dp),
          color = PaleSageLight,
          border = BorderStroke(1.dp, CardBorder),
          modifier = Modifier.weight(1f)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp)
          ) {
            Text(name, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary, maxLines = 1)
            Text(impSub, fontSize = 8.sp, color = TextSecondary)
          }
        }
      }
    }

    val kgParsed = kgText.toDoubleOrNull()
    Button(
      onClick = { if (kgParsed != null) onApply(kgParsed) },
      enabled = kgParsed != null,
      modifier = Modifier.fillMaxWidth().height(46.dp).testTag("apply_converter_weight_btn"),
      colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
      shape = RoundedCornerShape(12.dp)
    ) {
      Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text("Apply ${kgText} kg (${lbsText} lbs) to Form", fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun PackagingFilmConverterView() {
  var micronsText by remember { mutableStateOf("45.0") }
  var milsText by remember { mutableStateOf("1.77") }
  var gaugeText by remember { mutableStateOf("177.2") }

  var otrMetricText by remember { mutableStateOf("1500.0") }
  var otrImperialText by remember { mutableStateOf("96.8") }

  fun updateFromMicrons(umStr: String) {
    micronsText = umStr
    val um = umStr.toDoubleOrNull()
    if (um != null) {
      val mil = UnitConverter.micronsToMil(um)
      val gauge = UnitConverter.micronsToGauge(um)
      milsText = ((mil * 100).roundToInt() / 100.0).toString()
      gaugeText = ((gauge * 10).roundToInt() / 10.0).toString()
    }
  }

  fun updateFromMils(milStr: String) {
    milsText = milStr
    val mil = milStr.toDoubleOrNull()
    if (mil != null) {
      val um = UnitConverter.milToMicrons(mil)
      val gauge = mil * 100.0
      micronsText = ((um * 10).roundToInt() / 10.0).toString()
      gaugeText = ((gauge * 10).roundToInt() / 10.0).toString()
    }
  }

  fun updateFromOtrMetric(ccStr: String) {
    otrMetricText = ccStr
    val cc = ccStr.toDoubleOrNull()
    if (cc != null) {
      val imp = cc / 15.5
      otrImperialText = ((imp * 10).roundToInt() / 10.0).toString()
    }
  }

  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text("Film Thickness Gauge (µm ⇄ mil ⇄ gauge)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)

    OutlinedTextField(
      value = micronsText,
      onValueChange = { updateFromMicrons(it) },
      label = { Text("Microns (µm) - Global Standard", fontSize = 12.sp) },
      trailingIcon = { Text("µm", fontWeight = FontWeight.Bold, color = ForestGreenPrimary, modifier = Modifier.padding(end = 12.dp)) },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp)
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      OutlinedTextField(
        value = milsText,
        onValueChange = { updateFromMils(it) },
        label = { Text("Mils / Thou (US)", fontSize = 11.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp)
      )
      OutlinedTextField(
        value = gaugeText,
        onValueChange = {
          gaugeText = it
          val ga = it.toDoubleOrNull()
          if (ga != null) {
            val um = UnitConverter.gaugeToMicrons(ga)
            micronsText = ((um * 10).roundToInt() / 10.0).toString()
            milsText = ((ga / 100.0 * 100).roundToInt() / 100.0).toString()
          }
        },
        label = { Text("Gauge (ga)", fontSize = 11.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp)
      )
    }

    HorizontalDivider(color = PaleSageTint, thickness = 1.dp)

    Text("Gas Transmission Barrier Units (OTR)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)

    OutlinedTextField(
      value = otrMetricText,
      onValueChange = { updateFromOtrMetric(it) },
      label = { Text("Metric: cc / m² · day · atm", fontSize = 12.sp) },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp)
    )

    OutlinedTextField(
      value = otrImperialText,
      onValueChange = {
        otrImperialText = it
        val imp = it.toDoubleOrNull()
        if (imp != null) {
          val metric = imp * 15.5
          otrMetricText = ((metric * 10).roundToInt() / 10.0).toString()
        }
      },
      label = { Text("Imperial: cc / 100 in² · day · atm", fontSize = 12.sp) },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp)
    )
  }
}
