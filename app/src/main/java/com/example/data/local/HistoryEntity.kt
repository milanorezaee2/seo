package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seo_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val toolType: String, // AUDIT, CWV, COMPETITOR, SCHEMA, SITEMAP, DNS, ROBOTS
    val title: String,
    val targetUrl: String,
    val summary: String,
    val score: Int? = null,
    val dataPayload: String = "", // JSON or raw string for restore
    val timestamp: Long = System.currentTimeMillis()
)
