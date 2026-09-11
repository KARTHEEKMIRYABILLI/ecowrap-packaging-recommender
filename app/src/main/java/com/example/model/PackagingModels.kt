package com.example.model

enum class AppScreen(val screenNumber: Int, val title: String) {
  SPLASH(1, "Splash Screen"),
  ONBOARDING(2, "Onboarding"),
  PERSONA_SELECTION(3, "User Type"),
  HOME_DASHBOARD(4, "Home / Dashboard"),
  COMMODITY_SELECTION(5, "Select Commodity"),
  FOOD_PROFILE(6, "Food Profile"),
  STORAGE_CONDITIONS(7, "Storage & Transport"),
  PACKAGING_REQUIREMENTS(8, "Your Priorities"),
  AI_ANALYSIS(9, "AI Processing"),
  RECOMMENDATION_RESULT(10, "Recommendation"),
  DETAIL_WHY_THIS(11, "Why This?"),
  DETAIL_SHELF_LIFE(12, "Shelf-Life"),
  DETAIL_SUSTAINABILITY(13, "Sustainability"),
  REPORT_TRACEABILITY(14, "Packaging Report"),
  MATERIAL_COMPARISON(15, "Compare Materials"),
  ENVIRONMENTAL_INFOGRAPHIC(16, "Environmental Infographic")
}

enum class PersonaType(val title: String, val subtitle: String, val iconType: String) {
  FARMER("Farmer", "Get simple recommendations", "sprout"),
  FOOD_PROCESSOR("Food Processor", "Optimize packaging for production", "factory"),
  STARTUP("Startup", "Innovate with sustainable solutions", "rocket"),
  RESEARCHER("Researcher", "Access advanced analysis tools", "science")
}

enum class PriorityType(val title: String, val iconType: String) {
  LOW_COST("Low Cost", "cost"),
  ECO_FRIENDLY("Eco Friendly", "eco"),
  MAX_SHELF_LIFE("Max Shelf Life", "chart")
}

enum class PackagingFormat(val title: String, val iconType: String) {
  FLEXIBLE_FILM("Flexible Film", "film"),
  TRAY("Tray & Lidding", "tray"),
  POUCH("Stand-up Pouch", "pouch"),
  BOTTLE("Rigid Container", "bottle"),
  BOX("Corrugated Box", "box")
}

enum class StorageType(val label: String, val tempRange: String, val icon: String) {
  AMBIENT("Ambient", "18°C to 25°C", "🌡️"),
  CHILLED("Chilled", "0°C to 8°C", "❄️"),
  FROZEN("Frozen", "-18°C to -10°C", "🧊")
}

data class CommodityItem(
  val id: String,
  val name: String,
  val category: String,
  val emoji: String,
  val subtitle: String = "Fresh Commodity",
  var moistureContent: Double = 94.0,
  var phValue: Double = 4.3,
  var fatContent: Double = 0.2,
  var respirationRate: String = "High",
  var ethyleneSensitivity: String = "Moderate"
)

data class StorageConfig(
  var temperatureC: Double = 8.0,
  var relativeHumidityPercent: Double = 85.0,
  var transportationType: String = "Refrigerated",
  var desiredShelfLifeDays: Int = 15,
  var storageType: StorageType = StorageType.CHILLED,
  var packageWeightKg: Double = 1.0,
  var preferredUnitSystem: String = "Metric"
)

data class FactorWeight(
  val name: String,
  val percentage: Int
)

data class TechnicalPackagingSpecs(
  val otrValue: String = "1,500 cc/m²·day·atm",
  val otrDescription: String = "Controlled gas transmission prevents anaerobic fermentation and off-odors",
  val wvtrValue: String = "1.2 g/m²·day (at 38°C, 90% RH)",
  val wvtrDescription: String = "Prevents moisture condensation & physiological weight loss",
  val filmThickness: String = "45 µm",
  val packagingStructure: String = "3-Layer Co-extruded: LDPE (15µm) / EVOH (10µm) / LLDPE (20µm)",
  val heatSealRange: String = "110°C – 135°C (Seal Strength: 18 N/15mm)",
  val tensileStrength: String = "MD: 38 MPa / TD: 34 MPa",
  val dartDropImpact: String = "210 g (ASTM D1709)",
  val punctureResistance: String = "16.4 N",
  val co2TransmissionRate: String = "5,400 cc/m²·day·atm",
  val co2ToO2PermeabilityRatio: String = "β = 3.6 (Ideal Equilibrium Ratio for Respiration)"
)

data class MapRecommendationSpecs(
  val isMapSuitable: Boolean = true,
  val mapSuitabilityLevel: String = "Highly Recommended for Fresh Produce",
  val suggestedPackagingType: String = "Laser Micro-perforated Breathable Film (120 µm micro-pores)",
  val o2Percentage: String = "3% – 5%",
  val co2Percentage: String = "5% – 8%",
  val n2Percentage: String = "87% – 92% (Balance)",
  val targetEquilibriumAtmosphere: String = "4% O₂ + 6% CO₂ @ 4°C",
  val specialAdditives: String = "Anti-fog surfactant coating + Ethylene Scavenger sachet recommended"
)

data class PackagingMaterial(
  val name: String,
  val materialTypeKey: String = "BREATHABLE_FILM",
  val score: Int,
  val description: String,
  val isTopMatch: Boolean = false,
  val isSustainable: Boolean = true,
  val isRecyclable: Boolean = true,
  val recyclingCode: String = "♶ LDPE #4",
  val tags: List<String>,
  val expectedShelfLife: Double,
  val ecoScore: Double,
  val filmThickness: String = "45 µm",
  val costPerKg: String = "₹ 145 / kg",
  val recyclability: Int = 9,
  val materialImpact: Int = 8,
  val materialUsage: Int = 8,
  val carbonImpact: Int = 7,
  val biodegradability: Int = 6,
  val batchId: String = "PW-2026-001",
  val date: String = "07 Sep 2026",
  val whySummary: String = "The selected film provides an appropriate balance between gas exchange and moisture protection for commodities under specified storage conditions.",
  val technicalSpecs: TechnicalPackagingSpecs = TechnicalPackagingSpecs(),
  val mapSpecs: MapRecommendationSpecs = MapRecommendationSpecs(),
  val factors: List<FactorWeight> = listOf(
    FactorWeight("Respiration rate", 32),
    FactorWeight("Moisture content", 25),
    FactorWeight("Storage temperature", 18),
    FactorWeight("Shelf-life target", 14),
    FactorWeight("Cost", 7),
    FactorWeight("Other factors", 4)
  ),
  val safetyAdvisory: String = "A completely oxygen-impermeable package may cause anaerobic conditions. Therefore a breathable film with controlled gas exchange is preferred."
)

enum class NotificationType(val title: String) {
  AI_RECOMMENDATION("AI Recommendation"),
  REPORT_UPDATE("Report Update"),
  SYSTEM("System Notice")
}

data class AppNotification(
  val id: String,
  val title: String,
  val message: String,
  val timestamp: String,
  val type: NotificationType,
  var isRead: Boolean = false,
  val targetCommodity: String? = null
)

data class UserAccount(
  val email: String,
  val name: String,
  val role: PersonaType = PersonaType.FOOD_PROCESSOR,
  val organization: String = "MoFPI Partner • Active Member",
  val isOptedInNotifications: Boolean = true,
  val defaultPriority: PriorityType = PriorityType.ECO_FRIENDLY,
  val unitMetric: String = "Metric (°C, kg, µm)",
  val isOfflineDbSynced: Boolean = true,
  val memberSince: String = "01 Sep 2026",
  val avatarUri: String? = null
)

data class HistoryAssessmentItem(
  val id: String,
  val commodityId: String,
  val commodityName: String,
  val category: String,
  val emoji: String,
  val date: String,
  val recommendedMaterial: String,
  val suitabilityScore: Int,
  val expectedShelfLifeDays: Double,
  val ecoScore: Double,
  val userEmail: String,
  val storageSummary: String = "8°C, 85% RH, Refrigerated",
  val batchId: String = "PW-2026-001"
)

data class UserFeedback(
  val id: String,
  val userEmail: String,
  val commodityName: String,
  val ratingAccuracy: Int,
  val ratingUsefulness: Int,
  val comments: String,
  val date: String,
  val status: String = "Submitted & Under Review"
)


