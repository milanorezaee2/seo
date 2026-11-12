package com.example.data.model

data class SitemapUrlEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val loc: String,
    val lastmod: String = "",
    val changefreq: String = "weekly", // always, hourly, daily, weekly, monthly, yearly, never
    val priority: String = "0.8" // 0.0 to 1.0
)

data class SitemapValidationResult(
    val url: String,
    val isValid: Boolean,
    val totalUrlsFound: Int,
    val errors: List<String>,
    val warnings: List<String>,
    val parsedUrls: List<SitemapUrlEntry>
)
