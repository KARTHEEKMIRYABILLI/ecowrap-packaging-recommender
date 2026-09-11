package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationDao {

  @Query("SELECT * FROM cached_recommendations ORDER BY timestamp DESC")
  fun getAllRecommendations(): Flow<List<RecommendationCacheEntity>>

  @Query("SELECT * FROM cached_recommendations ORDER BY timestamp DESC LIMIT :limit")
  fun getRecentRecommendations(limit: Int = 15): Flow<List<RecommendationCacheEntity>>

  @Query("SELECT * FROM cached_recommendations WHERE isOfflinePinned = 1 ORDER BY timestamp DESC")
  fun getPinnedRecommendations(): Flow<List<RecommendationCacheEntity>>

  @Query("SELECT * FROM cached_recommendations WHERE commodityName LIKE '%' || :query || '%' OR materialName LIKE '%' || :query || '%' ORDER BY timestamp DESC")
  fun searchCachedRecommendations(query: String): Flow<List<RecommendationCacheEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecommendation(entity: RecommendationCacheEntity): Long

  @Query("UPDATE cached_recommendations SET isOfflinePinned = :isPinned WHERE id = :id")
  suspend fun togglePin(id: Long, isPinned: Boolean)

  @Query("DELETE FROM cached_recommendations WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("DELETE FROM cached_recommendations")
  suspend fun clearAll()

  @Query("SELECT COUNT(*) FROM cached_recommendations")
  fun getCount(): Flow<Int>
}
