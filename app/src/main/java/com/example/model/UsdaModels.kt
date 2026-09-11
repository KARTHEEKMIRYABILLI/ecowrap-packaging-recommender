package com.example.model

/**
 * Data models for the USDA FoodData Central API integration.
 * Enables fetching real-world biochemical nutrient profiles (Moisture/Water %, Total Fat %, Protein, etc.)
 * directly from the USDA Agricultural Research Service FoodData Central repository.
 */
data class UsdaNutrient(
  val nutrientId: Int? = null,
  val nutrientName: String,
  val nutrientNumber: String? = null,
  val unitName: String = "G",
  val value: Double
)

data class UsdaFoodItem(
  val fdcId: Int,
  val description: String,
  val dataType: String = "SR Legacy",
  val foodNutrients: List<UsdaNutrient> = emptyList(),
  val moisturePercent: Double = 0.0,
  val fatPercent: Double = 0.0,
  val proteinGrams: Double = 0.0,
  val carbGrams: Double = 0.0,
  val energyKcal: Double = 0.0,
  val scientificName: String? = null,
  val publicationDate: String? = null,
  val matchedSource: String = "USDA FoodData Central (SR Legacy / Foundation)"
)

sealed class UsdaFetchState {
  object Idle : UsdaFetchState()
  data class Loading(val query: String) : UsdaFetchState()
  data class Success(
    val query: String,
    val foodItem: UsdaFoodItem,
    val candidateItems: List<UsdaFoodItem> = emptyList(),
    val isLiveApi: Boolean,
    val message: String
  ) : UsdaFetchState()
  data class Error(val message: String, val lastQuery: String? = null) : UsdaFetchState()
}
