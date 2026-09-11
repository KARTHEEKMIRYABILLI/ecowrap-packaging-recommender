package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database Entity for caching commodity search queries and lookup results.
 * Enables instant autocomplete and offline searching without network overhead.
 */
@Entity(tableName = "search_cache")
data class SearchCacheEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val query: String,
  val category: String,
  val resultCount: Int,
  val timestamp: Long = System.currentTimeMillis()
)
