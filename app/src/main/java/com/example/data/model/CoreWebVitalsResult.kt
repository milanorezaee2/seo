package com.example.data.model

enum class MetricRating {
    GOOD,
    NEEDS_IMPROVEMENT,
    POOR
}

data class VitalMetric(
    val name: String,
    val acronym: String,
    val value: String,
    val unit: String,
    val rating: MetricRating,
    val description: String,
    val goodThreshold: String,
    val poorThreshold: String
)

data class VitalsDiagnostic(
    val title: String,
    val savings: String,
    val category: String,
    val suggestion: String
)

data class CoreWebVitalsResult(
    val url: String,
    val performanceScore: Int,
    val lcp: VitalMetric, // Largest Contentful Paint
    val inp: VitalMetric, // Interaction to Next Paint
    val cls: VitalMetric, // Cumulative Layout Shift
    val fcp: VitalMetric, // First Contentful Paint
    val ttfb: VitalMetric, // Time to First Byte
    val speedIndex: VitalMetric, // Speed Index
    val diagnostics: List<VitalsDiagnostic>,
    val aiOptimizationTips: String? = null
)
