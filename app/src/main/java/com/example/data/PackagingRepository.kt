package com.example.data

import android.content.Context
import com.example.model.CommodityItem
import com.example.model.PackagingMaterial
import com.example.model.StorageConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository for local Room Database operations, caching recommendations and search queries.
 */
class PackagingRepository(private val db: AppDatabase) {

  val allCachedRecommendations: Flow<List<RecommendationCacheEntity>> =
    db.recommendationDao().getAllRecommendations()

  val recentRecommendations: Flow<List<RecommendationCacheEntity>> =
    db.recommendationDao().getRecentRecommendations(20)

  val pinnedRecommendations: Flow<List<RecommendationCacheEntity>> =
    db.recommendationDao().getPinnedRecommendations()

  val cachedRecommendationsCount: Flow<Int> =
    db.recommendationDao().getCount()

  val recentSearches: Flow<List<SearchCacheEntity>> =
    db.searchCacheDao().getRecentSearches(10)

  /**
   * Caches a recommendation assessment into the local Room database for offline access.
   */
  suspend fun cacheRecommendation(
    commodity: CommodityItem,
    storage: StorageConfig,
    material: PackagingMaterial,
    isPinned: Boolean = false
  ): Long = withContext(Dispatchers.IO) {
    val entity = RecommendationCacheEntity.fromModels(commodity, storage, material, isPinned)
    db.recommendationDao().insertRecommendation(entity)
  }

  suspend fun togglePinRecommendation(id: Long, isPinned: Boolean) = withContext(Dispatchers.IO) {
    db.recommendationDao().togglePin(id, isPinned)
  }

  suspend fun deleteCachedRecommendation(id: Long) = withContext(Dispatchers.IO) {
    db.recommendationDao().deleteById(id)
  }

  suspend fun clearAllCachedRecommendations() = withContext(Dispatchers.IO) {
    db.recommendationDao().clearAll()
  }

  /**
   * Caches a search query into Room.
   */
  suspend fun cacheSearch(query: String, category: String, count: Int) = withContext(Dispatchers.IO) {
    if (query.isBlank()) return@withContext
    db.searchCacheDao().insertSearch(
      SearchCacheEntity(
        query = query.trim(),
        category = category,
        resultCount = count
      )
    )
  }

  suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
    db.searchCacheDao().clearAll()
  }

  companion object {
    @Volatile
    private var INSTANCE: PackagingRepository? = null

    fun getInstance(context: Context): PackagingRepository {
      return INSTANCE ?: synchronized(this) {
        val db = AppDatabase.getInstance(context)
        val instance = PackagingRepository(db)
        INSTANCE = instance
        instance
      }
    }
  }
}
