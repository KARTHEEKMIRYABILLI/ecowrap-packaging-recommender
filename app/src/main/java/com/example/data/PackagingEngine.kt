package com.example.data

import com.example.model.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * MoFPI Technical Material Recommendation Engine
 * Evaluates food properties, environment parameters, and user priorities
 * to recommend materials specifically from:
 * 1. LDPE (Low-Density Polyethylene)
 * 2. HDPE (High-Density Polyethylene)
 * 3. PET (Polyethylene Terephthalate)
 * 4. Metalized films (Met-PET / Met-BOPP)
 * 5. Aluminum foil laminates (Multi-layer Hermetic Barrier)
 * 6. Biodegradable films (Bio-based / Compostable PLA/PBAT)
 * 7. Breathable / micro-perforated films (MAP Equilibrium Gas Exchange)
 */
object PackagingEngine {

  val DEFAULT_COMMODITIES = listOf(
    CommodityItem("grapes", "Grapes", "Fruits", "🍇", "Perishable Table Grapes", 81.0, 3.8, 0.2, "Moderate", "Low"),
    CommodityItem("strawberry", "Strawberry", "Fruits", "🍓", "Soft Berry", 91.0, 3.5, 0.3, "Very High", "Low"),
    CommodityItem("banana", "Banana", "Fruits", "🍌", "Climacteric Tropical Fruit", 75.0, 5.0, 0.3, "High", "High"),
    CommodityItem("mango", "Mango", "Fruits", "🥭", "Tropical Stone Fruit", 83.5, 4.2, 0.4, "Moderate", "High"),
    CommodityItem("apple", "Apple", "Fruits", "🍎", "Pome Fruit", 85.6, 3.9, 0.2, "Low", "High"),
    CommodityItem("orange", "Orange (Citrus)", "Fruits", "🍊", "Citrus Fruit", 87.0, 3.6, 0.1, "Low", "Low"),
    CommodityItem("papaya", "Papaya", "Fruits", "🍈", "Tropical Climacteric", 88.0, 5.5, 0.3, "High", "High"),
    CommodityItem("guava", "Guava", "Fruits", "🍐", "Tropical Fruit", 86.0, 4.0, 1.0, "High", "High"),
    CommodityItem("tomato", "Tomato", "Vegetables", "🍅", "Solanaceous Vegetable", 94.0, 4.3, 0.2, "High", "Moderate"),
    CommodityItem("potato", "Potato", "Vegetables", "🥔", "Tuber Vegetable", 79.3, 5.8, 0.1, "Low", "Low"),
    CommodityItem("onion", "Onion", "Vegetables", "🧅", "Bulb Vegetable", 89.1, 5.5, 0.1, "Low", "Low"),
    CommodityItem("carrot", "Carrot", "Vegetables", "🥕", "Root Vegetable", 88.3, 6.0, 0.2, "Moderate", "Low"),
    CommodityItem("broccoli", "Broccoli", "Vegetables", "🥦", "Cruciferous Vegetable", 89.0, 6.5, 0.4, "High", "High"),
    CommodityItem("bell_pepper", "Bell Pepper", "Vegetables", "🫑", "Capsicum Vegetable", 92.0, 5.0, 0.2, "Moderate", "Moderate"),
    CommodityItem("spinach", "Spinach", "Vegetables", "🥬", "Leafy Green", 91.4, 6.8, 0.4, "Very High", "High"),
    CommodityItem("cucumber", "Cucumber", "Vegetables", "🥒", "Cucurbit Vegetable", 95.2, 5.7, 0.1, "Moderate", "High"),
    CommodityItem("rice", "Rice", "Grains", "🍚", "Cereal Grain", 13.0, 6.5, 0.6, "Very Low", "Low"),
    CommodityItem("wheat", "Wheat", "Grains", "🌾", "Cereal Grain", 12.5, 6.2, 1.5, "Very Low", "Low"),
    CommodityItem("paneer", "Paneer", "Dairy", "🧊", "Fresh Curd Cheese", 54.0, 6.2, 22.0, "Low", "Low"),
    CommodityItem("poultry", "Poultry Meat", "Meat", "🍗", "White Meat", 74.0, 5.8, 2.6, "Low", "Low"),
    CommodityItem("fish", "Fresh Fish", "Meat", "🐟", "Aquatic Protein", 76.0, 6.4, 1.8, "Moderate", "Low")
  )

  fun evaluateRecommendation(
    commodity: CommodityItem,
    storage: StorageConfig,
    priority: PriorityType,
    format: PackagingFormat
  ): Pair<PackagingMaterial, List<Pair<String, Int>>> {
    val isFreshProduce = commodity.category.equals("Fruits", ignoreCase = true) ||
        commodity.category.equals("Vegetables", ignoreCase = true)
    
    val isHighRespiration = commodity.respirationRate.equals("High", ignoreCase = true) ||
        commodity.respirationRate.equals("Very High", ignoreCase = true) ||
        commodity.respirationRate.equals("Moderate", ignoreCase = true)

    val isHighFatOrOil = commodity.fatContent >= 10.0 || commodity.name.contains("Ghee", true) ||
        commodity.name.contains("Butter", true) || commodity.name.contains("Meat", true) ||
        commodity.name.contains("Mutton", true) || commodity.name.contains("Poultry", true)

    val isHighMoisture = commodity.moistureContent >= 80.0
    val isDryGrain = commodity.category.equals("Grains", ignoreCase = true) || commodity.moistureContent <= 15.0
    val isFrozen = storage.storageType == StorageType.FROZEN || storage.temperatureC <= -5.0
    val isLongShelfLife = storage.desiredShelfLifeDays >= 30

    // Score calculations for 7 materials
    val scores = mutableMapOf<String, Int>()

    // 1. Breathable / micro-perforated films
    var breathableScore = 40
    if (isFreshProduce && isHighRespiration) breathableScore += 48
    if (storage.storageType == StorageType.CHILLED) breathableScore += 6
    if (priority == PriorityType.ECO_FRIENDLY) breathableScore += 4
    if (priority == PriorityType.MAX_SHELF_LIFE && isFreshProduce) breathableScore += 6
    if (!isFreshProduce) breathableScore -= 35
    if (isHighFatOrOil || isDryGrain) breathableScore -= 40
    scores["Breathable / Micro-Perforated Film"] = breathableScore.coerceIn(10, 98)

    // 2. Biodegradable films (PLA / PBAT / Starch)
    var bioScore = 55
    if (priority == PriorityType.ECO_FRIENDLY) bioScore += 35
    if (isFreshProduce && storage.desiredShelfLifeDays <= 12) bioScore += 12
    if (isFrozen) bioScore -= 20
    if (storage.desiredShelfLifeDays > 25) bioScore -= 15
    scores["Biodegradable Film (PLA/PBAT)"] = bioScore.coerceIn(15, 96)

    // 3. LDPE (Low-Density Polyethylene)
    var ldpeScore = 60
    if (priority == PriorityType.LOW_COST) ldpeScore += 26
    if (isHighMoisture && !isHighFatOrOil) ldpeScore += 10
    if (format == PackagingFormat.FLEXIBLE_FILM || format == PackagingFormat.POUCH) ldpeScore += 8
    if (isFrozen) ldpeScore += 5
    if (isFreshProduce && isHighRespiration) ldpeScore -= 12 // needs micro-perforations
    scores["LDPE (Low-Density Polyethylene)"] = ldpeScore.coerceIn(20, 94)

    // 4. HDPE (High-Density Polyethylene)
    var hdpeScore = 58
    if (isDryGrain) hdpeScore += 30
    if (commodity.category.equals("Dairy", ignoreCase = true) || format == PackagingFormat.BOTTLE) hdpeScore += 16
    if (priority == PriorityType.LOW_COST) hdpeScore += 15
    if (isFreshProduce) hdpeScore -= 10
    scores["HDPE (High-Density Polyethylene)"] = hdpeScore.coerceIn(20, 93)

    // 5. PET (Polyethylene Terephthalate)
    var petScore = 62
    if (format == PackagingFormat.TRAY || format == PackagingFormat.BOTTLE) petScore += 22
    if (commodity.category.equals("Dairy", ignoreCase = true) || commodity.category.equals("Meat", ignoreCase = true)) petScore += 18
    if (isFreshProduce && !isHighRespiration) petScore += 10
    if (priority == PriorityType.MAX_SHELF_LIFE) petScore += 10
    scores["PET (Polyethylene Terephthalate)"] = petScore.coerceIn(25, 95)

    // 6. Metalized films (Met-PET / Met-BOPP)
    var metScore = 50
    if (isHighFatOrOil) metScore += 32
    if (isLongShelfLife && !isFreshProduce) metScore += 24
    if (priority == PriorityType.MAX_SHELF_LIFE) metScore += 16
    if (isFreshProduce) metScore -= 30 // Will asphyxiate fresh respiring produce without gas flushing
    scores["Metalized Film (Met-PET / Met-BOPP)"] = metScore.coerceIn(15, 95)

    // 7. Aluminum foil laminates
    var aluScore = 45
    if (isHighFatOrOil && isLongShelfLife) aluScore += 45
    if (isFrozen && (commodity.category.equals("Meat", ignoreCase = true) || commodity.category.equals("Dairy", ignoreCase = true))) aluScore += 35
    if (priority == PriorityType.MAX_SHELF_LIFE && !isFreshProduce) aluScore += 25
    if (priority == PriorityType.LOW_COST) aluScore -= 20
    if (isFreshProduce) aluScore -= 45 // Fresh produce will rapidly spoil without breathability
    scores["Aluminum Foil Laminate"] = aluScore.coerceIn(10, 97)

    // Find top matching material
    val topEntry = scores.maxByOrNull { it.value }
    val topName = topEntry?.key ?: "Breathable / Micro-Perforated Film"
    val topScore = topEntry?.value ?: 92

    // Alternatives list (sorted descending, excluding top)
    val alternativesList = scores.filterKeys { it != topName }
      .toList()
      .sortedByDescending { it.second }
      .map { Pair(it.first, it.second) }

    val topMaterial = buildMaterialProfile(
      name = topName,
      score = topScore,
      commodity = commodity,
      storage = storage,
      priority = priority,
      format = format
    )

    return Pair(topMaterial, alternativesList)
  }

  fun buildMaterialProfile(
    name: String,
    score: Int,
    commodity: CommodityItem,
    storage: StorageConfig,
    priority: PriorityType,
    format: PackagingFormat
  ): PackagingMaterial {
    val isProduce = commodity.category.equals("Fruits", ignoreCase = true) ||
        commodity.category.equals("Vegetables", ignoreCase = true)
    val isRespiring = commodity.respirationRate != "Low" && commodity.respirationRate != "Very Low"

    return when {
      name.contains("Breathable", ignoreCase = true) || name.contains("Micro-Perforated", ignoreCase = true) -> {
        PackagingMaterial(
          name = "Breathable / Micro-Perforated Film",
          materialTypeKey = "BREATHABLE_FILM",
          score = score,
          description = "Engineered Equilibrium MAP film tailored for respiring ${commodity.name}",
          isTopMatch = true,
          isSustainable = true,
          isRecyclable = true,
          recyclingCode = "♶ LDPE #4 / ♷ PP #5 (Recyclable)",
          tags = listOf(
            "Controlled Gas Permeability",
            "Prevents Anaerobic Fermentation",
            "Anti-Fog Surfactant Treated",
            "MoFPI MAP Compliant"
          ),
          expectedShelfLife = (storage.desiredShelfLifeDays * 1.15).coerceAtLeast(14.0).let { (it * 10).roundToInt() / 10.0 },
          ecoScore = 8.8,
          filmThickness = "35 µm",
          costPerKg = "₹ 145 / kg",
          recyclability = 9,
          materialImpact = 8,
          materialUsage = 9,
          carbonImpact = 8,
          biodegradability = 6,
          batchId = "PW-2026-${(100..999).random()}",
          date = "11 Sep 2026",
          whySummary = "The engineered micro-perforations match ${commodity.name}'s ${commodity.respirationRate.lowercase()} respiration rate (${commodity.moistureContent}% moisture), maintaining optimum O₂/CO₂ equilibrium to extend fresh shelf-life without condensation.",
          technicalSpecs = TechnicalPackagingSpecs(
            otrValue = if (isRespiring) "3,800 cc/m²·day·atm" else "1,800 cc/m²·day·atm",
            otrDescription = "Tailored micro-perforations prevent in-pack hypoxia while curbing ethylene production",
            wvtrValue = "9.5 g/m²·day (at 38°C, 90% RH)",
            wvtrDescription = "Integrated internal anti-fog coating eliminates droplet condensation & mold growth",
            filmThickness = "35 µm (18 µm BOPP + 17 µm Metallocene LLDPE Sealant)",
            packagingStructure = "Laser Micro-Perforated Co-ex BOPP/mLLDPE (110 µm pore dia, 320 holes/m²)",
            heatSealRange = "105°C – 128°C (Seal Strength: 17.5 N/15mm)",
            tensileStrength = "MD: 145 MPa / TD: 210 MPa (High Modulus)",
            dartDropImpact = "230 g (ASTM D1709 Standard)",
            punctureResistance = "18.2 N (High puncture resistance against stem punctures)",
            co2TransmissionRate = "14,500 cc/m²·day·atm",
            co2ToO2PermeabilityRatio = "β = 3.82 (Balanced Equilibrium MAP Ratio)"
          ),
          mapSpecs = MapRecommendationSpecs(
            isMapSuitable = true,
            mapSuitabilityLevel = "High Suitability • Active & Passive MAP Recommended",
            suggestedPackagingType = "Micro-perforated MAP Breathable Pouch / Flow-wrap",
            o2Percentage = if (commodity.name.contains("Tomato", true)) "3% – 5%" else "2% – 5%",
            co2Percentage = if (commodity.name.contains("Tomato", true)) "4% – 8%" else "5% – 10%",
            n2Percentage = "87% – 93% (Balance inert filler to prevent pack collapse)",
            targetEquilibriumAtmosphere = "4.0% O₂ + 6.0% CO₂ @ ${storage.temperatureC.toInt()}°C",
            specialAdditives = "Potassium permanganate ethylene scrubber sachet + Dual-sided anti-fog surfactant"
          ),
          safetyAdvisory = "Avoid hermetically sealed non-perforated barriers for ${commodity.name}; lack of O₂ causes rapid anaerobic fermentation, ethanol accumulation, and off-flavors."
        )
      }

      name.contains("Biodegradable", ignoreCase = true) || name.contains("Bio", ignoreCase = true) -> {
        PackagingMaterial(
          name = "Biodegradable Film (PLA / PBAT)",
          materialTypeKey = "BIODEGRADABLE_FILM",
          score = score,
          description = "100% Certified Compostable bio-based polymer blend (EN 13432)",
          isTopMatch = true,
          isSustainable = true,
          isRecyclable = false,
          recyclingCode = "♹ Other (Compostable / Bio-based)",
          tags = listOf(
            "100% Home & Industrial Compostable",
            "Zero Petroleum Microplastics",
            "High Moisture Permeability",
            "Carbon Footprint Reduced by 68%"
          ),
          expectedShelfLife = (storage.desiredShelfLifeDays * 0.95).coerceAtLeast(10.0).let { (it * 10).roundToInt() / 10.0 },
          ecoScore = 9.7,
          filmThickness = "40 µm",
          costPerKg = "₹ 210 / kg",
          recyclability = 6,
          materialImpact = 10,
          materialUsage = 9,
          carbonImpact = 10,
          biodegradability = 10,
          batchId = "PW-2026-${(100..999).random()}",
          date = "11 Sep 2026",
          whySummary = "Bio-derived Polyhydroxybutyrate/PBAT/PLA matrix delivers natural vapor breathability, eliminating synthetic plastics while achieving complete compostability in 90–180 days.",
          technicalSpecs = TechnicalPackagingSpecs(
            otrValue = "650 cc/m²·day·atm",
            otrDescription = "Moderate natural barrier allowing natural respiration for organic produce",
            wvtrValue = "85 g/m²·day (at 38°C, 90% RH)",
            wvtrDescription = "High natural moisture vapor release prevents water buildup inside packaging",
            filmThickness = "40 µm Monolayer Bio-Polymer Blown Film",
            packagingStructure = "Corn Starch Thermoplastic (20%) + PLA (35%) + PBAT (45%) Certified Bio-blend",
            heatSealRange = "95°C – 118°C (Low temperature sealing for energy savings)",
            tensileStrength = "MD: 32 MPa / TD: 28 MPa (Elastic elongation 380%)",
            dartDropImpact = "175 g (ASTM D1709)",
            punctureResistance = "14.1 N",
            co2TransmissionRate = "2,700 cc/m²·day·atm",
            co2ToO2PermeabilityRatio = "β = 4.15 (Permeability favorable for organic greens)"
          ),
          mapSpecs = MapRecommendationSpecs(
            isMapSuitable = isProduce,
            mapSuitabilityLevel = if (isProduce) "Suitable for Short-Medium Transit MAP" else "Moderate MAP Performance",
            suggestedPackagingType = "Compostable Bio-Pouch / Stand-up Bag",
            o2Percentage = "4% – 6%",
            co2Percentage = "6% – 8%",
            n2Percentage = "86% – 90%",
            targetEquilibriumAtmosphere = "5.0% O₂ + 7.0% CO₂ @ ${storage.temperatureC.toInt()}°C",
            specialAdditives = "Bio-based desiccant pad + Natural plant oil antimicrobial coating"
          ),
          safetyAdvisory = "Store bio-film rolls away from direct sunlight and moisture prior to filling. Shelf-life of raw bio-film is 12 months in ambient warehousing."
        )
      }

      name.contains("LDPE", ignoreCase = true) -> {
        PackagingMaterial(
          name = "LDPE (Low-Density Polyethylene)",
          materialTypeKey = "LDPE",
          score = score,
          description = "Cost-effective, highly flexible, 100% recyclable moisture barrier film",
          isTopMatch = true,
          isSustainable = true,
          isRecyclable = true,
          recyclingCode = "♶ LDPE #4 (Widely Recycled)",
          tags = listOf(
            "Superior Moisture Barrier",
            "High Puncture & Tear Resistance",
            "Widely Recyclable Polymer",
            "Most Economical Formulation"
          ),
          expectedShelfLife = (storage.desiredShelfLifeDays * 1.05).coerceAtLeast(12.0).let { (it * 10).roundToInt() / 10.0 },
          ecoScore = 7.8,
          filmThickness = "50 µm",
          costPerKg = "₹ 115 / kg",
          recyclability = 10,
          materialImpact = 7,
          materialUsage = 8,
          carbonImpact = 7,
          biodegradability = 3,
          batchId = "PW-2026-${(100..999).random()}",
          date = "11 Sep 2026",
          whySummary = "LDPE provides outstanding water vapor resistance (WVTR < 16 g/m²·day) and reliable heat sealing at low material costs, making it ideal for standard protective wrapping.",
          technicalSpecs = TechnicalPackagingSpecs(
            otrValue = "2,800 cc/m²·day·atm",
            otrDescription = "Standard semi-permeable rate suitable for general food storage",
            wvtrValue = "14.5 g/m²·day (at 38°C, 90% RH)",
            wvtrDescription = "Reliable moisture containment preventing desiccation of moist food products",
            filmThickness = "50 µm (3-Layer Co-extruded LDPE / LLDPE Blend)",
            packagingStructure = "Co-ex 3-Layer: Virgin LDPE (15µm) / Hexene LLDPE (20µm) / Slip LDPE (15µm)",
            heatSealRange = "112°C – 132°C (Seal Strength: 19.5 N/15mm)",
            tensileStrength = "MD: 26 MPa / TD: 24 MPa (Ultimate Elongation 520%)",
            dartDropImpact = "210 g (ASTM D1709)",
            punctureResistance = "19.8 N (High toughness against external abrasions)",
            co2TransmissionRate = "7,600 cc/m²·day·atm",
            co2ToO2PermeabilityRatio = "β = 2.71"
          ),
          mapSpecs = MapRecommendationSpecs(
            isMapSuitable = isProduce,
            mapSuitabilityLevel = if (isProduce) "Suitable with Macro/Micro Vents" else "General Storage (Non-MAP)",
            suggestedPackagingType = "Side-gusseted LDPE Liner Bag or Pouch",
            o2Percentage = "5% – 8%",
            co2Percentage = "4% – 6%",
            n2Percentage = "86% – 91%",
            targetEquilibriumAtmosphere = "6.0% O₂ + 5.0% CO₂ @ ${storage.temperatureC.toInt()}°C",
            specialAdditives = "Slip additive (Erucamide 600 ppm) + Anti-block silica for high-speed automated packaging"
          ),
          safetyAdvisory = "LDPE melts above 108°C; do not subject to boil-in-bag or microwave retort conditions."
        )
      }

      name.contains("HDPE", ignoreCase = true) -> {
        PackagingMaterial(
          name = "HDPE (High-Density Polyethylene)",
          materialTypeKey = "HDPE",
          score = score,
          description = "Rigid/semi-rigid high barrier against moisture, grease, and chemicals",
          isTopMatch = true,
          isSustainable = true,
          isRecyclable = true,
          recyclingCode = "♴ HDPE #2 (High Value Circular Recyclate)",
          tags = listOf(
            "Ultra-Low Moisture Permeation",
            "High Rigidity & Tensile Strength",
            "100% Recyclable (#2 HDPE)",
            "Grease & Chemical Resistant"
          ),
          expectedShelfLife = (storage.desiredShelfLifeDays * 1.2).coerceAtLeast(18.0).let { (it * 10).roundToInt() / 10.0 },
          ecoScore = 8.1,
          filmThickness = "45 µm",
          costPerKg = "₹ 125 / kg",
          recyclability = 10,
          materialImpact = 8,
          materialUsage = 8,
          carbonImpact = 8,
          biodegradability = 3,
          batchId = "PW-2026-${(100..999).random()}",
          date = "11 Sep 2026",
          whySummary = "High crystalline density creates a superior moisture barrier (WVTR < 6 g/m²·day), preventing caking in dry grains and protecting dairy products against external humidity.",
          technicalSpecs = TechnicalPackagingSpecs(
            otrValue = "1,450 cc/m²·day·atm",
            otrDescription = "Moderate oxygen barrier suitable for dry staples and dairy powders",
            wvtrValue = "5.8 g/m²·day (at 38°C, 90% RH)",
            wvtrDescription = "High-density barrier prevents moisture absorption in hygroscopic commodities",
            filmThickness = "45 µm High Molecular Weight HDPE Film",
            packagingStructure = "High Molecular Weight HMW-HDPE (0.952 g/cm³) Monolayer / Multi-layer",
            heatSealRange = "125°C – 142°C (Seal Strength: 22.0 N/15mm)",
            tensileStrength = "MD: 58 MPa / TD: 44 MPa (High stiffness & crinkle feel)",
            dartDropImpact = "260 g (ASTM D1709)",
            punctureResistance = "24.5 N",
            co2TransmissionRate = "3,900 cc/m²·day·atm",
            co2ToO2PermeabilityRatio = "β = 2.69"
          ),
          mapSpecs = MapRecommendationSpecs(
            isMapSuitable = false,
            mapSuitabilityLevel = "Optimized for Vacuum / Inert Nitrogen Packing of Dry Goods",
            suggestedPackagingType = "Hermetic HDPE Bag-in-box or Rigid Container",
            o2Percentage = "< 1.0% (Residual O₂)",
            co2Percentage = "0% – 5%",
            n2Percentage = "95% – 99% (Pure Nitrogen Flush for grain preservation)",
            targetEquilibriumAtmosphere = "Nitrogen headspace preservation",
            specialAdditives = "Food-grade antistatic masterbatch to prevent powder cling"
          ),
          safetyAdvisory = "HDPE is semi-opaque; choose PET or BOPP if crystal-clear product visibility is required."
        )
      }

      name.contains("PET", ignoreCase = true) -> {
        PackagingMaterial(
          name = "PET (Polyethylene Terephthalate)",
          materialTypeKey = "PET",
          score = score,
          description = "High clarity, mechanical stiffness, and moderate aroma/gas barrier",
          isTopMatch = true,
          isSustainable = true,
          isRecyclable = true,
          recyclingCode = "♳ PET #1 (Globally #1 Most Recycled Plastic)",
          tags = listOf(
            "Crystal Clear Optical Display",
            "Excellent Gas & Aroma Retention",
            "Highest Circular Recycling Rate (rPET)",
            "High Dimensional Stability"
          ),
          expectedShelfLife = (storage.desiredShelfLifeDays * 1.25).coerceAtLeast(20.0).let { (it * 10).roundToInt() / 10.0 },
          ecoScore = 8.3,
          filmThickness = "52 µm",
          costPerKg = "₹ 160 / kg",
          recyclability = 10,
          materialImpact = 8,
          materialUsage = 8,
          carbonImpact = 8,
          biodegradability = 2,
          batchId = "PW-2026-${(100..999).random()}",
          date = "11 Sep 2026",
          whySummary = "Biaxially oriented PET provides superior clarity, aroma containment, and a strong oxygen barrier (OTR ~ 95 cc/m²·day), making it ideal for MAP trays and premium display packaging.",
          technicalSpecs = TechnicalPackagingSpecs(
            otrValue = "95 cc/m²·day·atm",
            otrDescription = "Low oxygen transmission preserving freshness and color retention",
            wvtrValue = "22 g/m²·day (at 38°C, 90% RH)",
            wvtrDescription = "Paired with PE sealant layer to balance moisture protection",
            filmThickness = "52 µm (12 µm BOPET Film + 40 µm PE Sealant Layer)",
            packagingStructure = "BOPET (12 µm) / Solventless PU Adhesive / Metallocene LLDPE (40 µm)",
            heatSealRange = "120°C – 140°C (Seal Strength: 25.0 N/15mm)",
            tensileStrength = "MD: 190 MPa / TD: 215 MPa (Ultra-high tensile rigidity)",
            dartDropImpact = "320 g (ASTM D1709)",
            punctureResistance = "22.4 N",
            co2TransmissionRate = "380 cc/m²·day·atm",
            co2ToO2PermeabilityRatio = "β = 4.0"
          ),
          mapSpecs = MapRecommendationSpecs(
            isMapSuitable = true,
            mapSuitabilityLevel = "High Suitability for Gas-Flushed MAP Trays & Pouches",
            suggestedPackagingType = "Thermoformed PET Tray with Peelable Barrier Lidding",
            o2Percentage = if (commodity.category.equals("Meat", true)) "60% – 70% (High O₂ for red bloom) or < 0.5% (Vacuum)" else "3% – 5%",
            co2Percentage = "20% – 30% (Microbial suppression)",
            n2Percentage = "65% – 75% (Balance)",
            targetEquilibriumAtmosphere = "25% CO₂ + 75% N₂ @ 2°C - 4°C",
            specialAdditives = "Silicone slip coating on outer face + High-clarity anti-fog on inner lidding"
          ),
          safetyAdvisory = "Ensure corona treatment level (>42 dynes/cm) is verified on BOPET surface before printing."
        )
      }

      name.contains("Metalized", ignoreCase = true) || name.contains("Met-PET", ignoreCase = true) -> {
        PackagingMaterial(
          name = "Metalized Film (Met-PET / Met-BOPP)",
          materialTypeKey = "METALIZED_FILM",
          score = score,
          description = "High barrier against light, oxygen, and moisture at economical cost",
          isTopMatch = true,
          isSustainable = false,
          isRecyclable = true,
          recyclingCode = "♷ Other / Composite (Recyclable in Polyolefin stream)",
          tags = listOf(
            "99.9% Light & UV Barrier",
            "High Oxygen Barrier (OTR < 2.0)",
            "Prevents Lipid Oxidation & Rancidity",
            "Cost-Effective Foil Alternative"
          ),
          expectedShelfLife = (storage.desiredShelfLifeDays * 1.5).coerceAtLeast(30.0).let { (it * 10).roundToInt() / 10.0 },
          ecoScore = 7.1,
          filmThickness = "62 µm",
          costPerKg = "₹ 185 / kg",
          recyclability = 7,
          materialImpact = 7,
          materialUsage = 8,
          carbonImpact = 7,
          biodegradability = 2,
          batchId = "PW-2026-${(100..999).random()}",
          date = "11 Sep 2026",
          whySummary = "Vacuum aluminum deposition provides an impenetrable optical and oxygen barrier, preventing photo-degradation and lipid rancidity in oily, fatty, or dry foods.",
          technicalSpecs = TechnicalPackagingSpecs(
            otrValue = "1.8 cc/m²·day·atm",
            otrDescription = "High barrier blocking 99.8% of atmospheric oxygen ingress",
            wvtrValue = "1.1 g/m²·day (at 38°C, 90% RH)",
            wvtrDescription = "High vapor barrier shielding dry and fatty foods from humidity",
            filmThickness = "62 µm 3-Ply Laminate Structure",
            packagingStructure = "Print BOPP (18 µm) / Met-PET (12 µm, OD 2.2) / Cast LLDPE (32 µm)",
            heatSealRange = "118°C – 138°C (Seal Strength: 26.5 N/15mm)",
            tensileStrength = "MD: 135 MPa / TD: 170 MPa",
            dartDropImpact = "280 g (ASTM D1709)",
            punctureResistance = "21.6 N",
            co2TransmissionRate = "7.2 cc/m²·day·atm",
            co2ToO2PermeabilityRatio = "β = 4.0"
          ),
          mapSpecs = MapRecommendationSpecs(
            isMapSuitable = true,
            mapSuitabilityLevel = "High Suitability for Nitrogen-Flushed Snack & Powder MAP",
            suggestedPackagingType = "Center-Seal / 3-Side Seal Nitrogen Flushed Pouch",
            o2Percentage = "< 0.5% (Residual O₂ target)",
            co2Percentage = "0% – 10%",
            n2Percentage = "90% – 99.5% (Inert Nitrogen cushion to protect brittle food)",
            targetEquilibriumAtmosphere = "99.5% N₂ inert cushion",
            specialAdditives = "Optical density (OD 2.2 - 2.5) vacuum metallization layer"
          ),
          safetyAdvisory = "Metalized films cannot be microwaved or inspected with standard metal detectors without specialized calibration."
        )
      }

      else -> { // Aluminum Foil Laminates
        PackagingMaterial(
          name = "Aluminum Foil Laminate",
          materialTypeKey = "ALUMINUM_FOIL",
          score = score,
          description = "Hermetic zero-permeability ultra-barrier for maximum shelf-life and frozen preservation",
          isTopMatch = true,
          isSustainable = false,
          isRecyclable = true,
          recyclingCode = "♷ Multi-material Barrier Composite",
          tags = listOf(
            "Near-Zero OTR (< 0.05 cc)",
            "Total Light & Aroma Lock",
            "Retort & Freeze Resistant",
            "Maximum Extended Shelf Life"
          ),
          expectedShelfLife = (storage.desiredShelfLifeDays * 2.2).coerceAtLeast(60.0).let { (it * 10).roundToInt() / 10.0 },
          ecoScore = 6.4,
          filmThickness = "84 µm",
          costPerKg = "₹ 240 / kg",
          recyclability = 6,
          materialImpact = 6,
          materialUsage = 7,
          carbonImpact = 6,
          biodegradability = 1,
          batchId = "PW-2026-${(100..999).random()}",
          date = "11 Sep 2026",
          whySummary = "Aluminum foil provides an absolute physical barrier to oxygen, moisture, and light, achieving maximum preservation stability for high-fat, aseptic, or frozen products.",
          technicalSpecs = TechnicalPackagingSpecs(
            otrValue = "< 0.05 cc/m²·day·atm (Absolute Hermetic Barrier)",
            otrDescription = "Zero oxygen migration prevents any oxidation over 12+ months",
            wvtrValue = "< 0.05 g/m²·day (at 38°C, 90% RH)",
            wvtrDescription = "Absolute moisture lock preventing freezer burn and dehydration",
            filmThickness = "84 µm 4-Ply High-Barrier Retortable Laminate",
            packagingStructure = "PET (12 µm) / Adhesive / Alu Foil (7 µm) / BOPA (15 µm) / CPP (50 µm)",
            heatSealRange = "145°C – 175°C (Hermetic Seal Strength: 38.0 N/15mm)",
            tensileStrength = "MD: 160 MPa / TD: 155 MPa (High puncture and burst tolerance)",
            dartDropImpact = "450 g (ASTM D1709)",
            punctureResistance = "32.0 N (Maximum resistance to sharp frozen bones or edges)",
            co2TransmissionRate = "< 0.1 cc/m²·day·atm",
            co2ToO2PermeabilityRatio = "β = 1.0 (Zero diffusion barrier)"
          ),
          mapSpecs = MapRecommendationSpecs(
            isMapSuitable = true,
            mapSuitabilityLevel = "High Suitability for Long-Term Aseptic & Vacuum Packaging",
            suggestedPackagingType = "4-Side Sealed Retortable Foil Pouch or Thermoformed Cup",
            o2Percentage = "< 0.1% (Ultra-low residual O₂)",
            co2Percentage = "30% – 50% (High microbial inhibition for meats)",
            n2Percentage = "50% – 70% (Balance)",
            targetEquilibriumAtmosphere = "40% CO₂ + 60% N₂ Vacuum Sealing",
            specialAdditives = "High-purity soft temper aluminum foil (Alloy 8079 / 1235)"
          ),
          safetyAdvisory = "Avoid sharp creasing during folding to prevent foil pinholing; verify pinhole test via ASTM F3018."
        )
      }
    }
  }
}
