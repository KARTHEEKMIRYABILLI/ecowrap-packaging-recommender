package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.CommodityItem
import com.example.model.MapRecommendationSpecs
import com.example.model.PackagingMaterial
import com.example.model.StorageConfig
import com.example.model.StorageType
import com.example.model.TechnicalPackagingSpecs

/**
 * Room Database Entity for local offline caching of packaging recommendations.
 * Ensures users can access full technical specifications, OTR, WVTR, and sustainability data offline.
 */
@Entity(tableName = "cached_recommendations")
data class RecommendationCacheEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val commodityId: String,
  val commodityName: String,
  val commodityCategory: String,
  val commodityEmoji: String,
  val respirationRate: String,
  val moistureContent: Double,
  val fatContent: Double,
  val phValue: Double,
  val ethyleneSensitivity: String,
  val temperatureC: Double,
  val relativeHumidityPercent: Double,
  val storageTypeName: String,
  val desiredShelfLifeDays: Int,
  val transportationType: String,
  val materialName: String,
  val materialScore: Int,
  val expectedShelfLifeDays: Double,
  val ecoScore: Double,
  val costPerKg: String,
  val filmThickness: String,
  val recyclingCode: String,
  val description: String,
  val batchId: String,
  val otrValue: String,
  val wvtrValue: String,
  val packagingStructure: String,
  val heatSealRange: String,
  val tensileStrength: String,
  val co2TransmissionRate: String,
  val co2ToO2Ratio: String,
  val mapO2Percent: String,
  val mapCo2Percent: String,
  val mapN2Percent: String,
  val mapEquilibrium: String,
  val safetyAdvisory: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isOfflinePinned: Boolean = false
) {
  fun toCommodityItem(): CommodityItem {
    return CommodityItem(
      id = commodityId,
      name = commodityName,
      category = commodityCategory,
      emoji = commodityEmoji,
      respirationRate = respirationRate,
      moistureContent = moistureContent,
      fatContent = fatContent,
      phValue = phValue,
      ethyleneSensitivity = ethyleneSensitivity
    )
  }

  fun toStorageConfig(): StorageConfig {
    val storageType = try {
      StorageType.valueOf(storageTypeName)
    } catch (_: Exception) {
      StorageType.AMBIENT
    }
    return StorageConfig(
      storageType = storageType,
      temperatureC = temperatureC,
      relativeHumidityPercent = relativeHumidityPercent,
      desiredShelfLifeDays = desiredShelfLifeDays,
      transportationType = transportationType
    )
  }

  fun toPackagingMaterial(): PackagingMaterial {
    return PackagingMaterial(
      name = materialName,
      score = materialScore,
      expectedShelfLife = expectedShelfLifeDays,
      ecoScore = ecoScore,
      costPerKg = costPerKg,
      filmThickness = filmThickness,
      recyclingCode = recyclingCode,
      description = description,
      batchId = batchId,
      tags = listOf("Offline Cached", "Verified ASTM"),
      technicalSpecs = TechnicalPackagingSpecs(
        otrValue = otrValue,
        wvtrValue = wvtrValue,
        packagingStructure = packagingStructure,
        heatSealRange = heatSealRange,
        tensileStrength = tensileStrength,
        co2TransmissionRate = co2TransmissionRate,
        co2ToO2PermeabilityRatio = co2ToO2Ratio
      ),
      mapSpecs = MapRecommendationSpecs(
        o2Percentage = mapO2Percent,
        co2Percentage = mapCo2Percent,
        n2Percentage = mapN2Percent,
        targetEquilibriumAtmosphere = mapEquilibrium
      ),
      safetyAdvisory = safetyAdvisory
    )
  }

  companion object {
    fun fromModels(
      commodity: CommodityItem,
      storage: StorageConfig,
      material: PackagingMaterial,
      isPinned: Boolean = false
    ): RecommendationCacheEntity {
      return RecommendationCacheEntity(
        commodityId = commodity.id,
        commodityName = commodity.name,
        commodityCategory = commodity.category,
        commodityEmoji = commodity.emoji,
        respirationRate = commodity.respirationRate,
        moistureContent = commodity.moistureContent,
        fatContent = commodity.fatContent,
        phValue = commodity.phValue,
        ethyleneSensitivity = commodity.ethyleneSensitivity,
        temperatureC = storage.temperatureC,
        relativeHumidityPercent = storage.relativeHumidityPercent,
        storageTypeName = storage.storageType.name,
        desiredShelfLifeDays = storage.desiredShelfLifeDays,
        transportationType = storage.transportationType,
        materialName = material.name,
        materialScore = material.score,
        expectedShelfLifeDays = material.expectedShelfLife,
        ecoScore = material.ecoScore,
        costPerKg = material.costPerKg,
        filmThickness = material.filmThickness,
        recyclingCode = material.recyclingCode,
        description = material.description,
        batchId = material.batchId,
        otrValue = material.technicalSpecs.otrValue,
        wvtrValue = material.technicalSpecs.wvtrValue,
        packagingStructure = material.technicalSpecs.packagingStructure,
        heatSealRange = material.technicalSpecs.heatSealRange,
        tensileStrength = material.technicalSpecs.tensileStrength,
        co2TransmissionRate = material.technicalSpecs.co2TransmissionRate,
        co2ToO2Ratio = material.technicalSpecs.co2ToO2PermeabilityRatio,
        mapO2Percent = material.mapSpecs.o2Percentage,
        mapCo2Percent = material.mapSpecs.co2Percentage,
        mapN2Percent = material.mapSpecs.n2Percentage,
        mapEquilibrium = material.mapSpecs.targetEquilibriumAtmosphere,
        safetyAdvisory = material.safetyAdvisory,
        isOfflinePinned = isPinned
      )
    }
  }
}
