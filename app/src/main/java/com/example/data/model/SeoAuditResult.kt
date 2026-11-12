package com.example.data.model

enum class IssueSeverity {
    HIGH, MEDIUM, LOW, GOOD
}

data class AuditIssue(
    val title: String,
    val description: String,
    val severity: IssueSeverity,
    val recommendation: String
)

data class SeoAuditResult(
    val url: String,
    val domain: String,
    val overallScore: Int,
    val title: String? = null,
    val titleLength: Int = 0,
    val titleStatus: IssueSeverity = IssueSeverity.GOOD,
    val metaDescription: String? = null,
    val metaDescriptionLength: Int = 0,
    val metaDescriptionStatus: IssueSeverity = IssueSeverity.GOOD,
    val canonicalUrl: String? = null,
    val canonicalStatus: IssueSeverity = IssueSeverity.GOOD,
    val robotsMeta: String? = null,
    val ogTitle: String? = null,
    val ogDescription: String? = null,
    val ogImage: String? = null,
    val twitterCard: String? = null,
    val h1List: List<String> = emptyList(),
    val h2List: List<String> = emptyList(),
    val h3List: List<String> = emptyList(),
    val wordCount: Int = 0,
    val imageCount: Int = 0,
    val imagesWithoutAlt: Int = 0,
    val internalLinksCount: Int = 0,
    val externalLinksCount: Int = 0,
    val isHttps: Boolean = true,
    val httpStatusCode: Int = 200,
    val responseTimeMs: Long = 0,
    val charset: String? = null,
    val hasViewportMeta: Boolean = true,
    val hasFavicon: Boolean = true,
    val issues: List<AuditIssue> = emptyList(),
    val aiRecommendations: String? = null
)
