package com.example.data.model

data class MetricRow(
    val title: String,
    val myVal: String,
    val compVal: String,
    val winner: Int // 1 if my site is better, 2 if competitor is better, 0 if equal
)

data class CompetitorComparison(
    val myUrl: String,
    val competitorUrl: String,
    val myScore: Int,
    val competitorScore: Int,
    val metricRows: List<MetricRow>,
    val myStrengths: List<String>,
    val competitorStrengths: List<String>,
    val keywordOpportunities: List<String>,
    val aiStrategyBreakdown: String? = null
)
