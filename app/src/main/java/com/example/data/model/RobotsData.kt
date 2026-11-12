package com.example.data.model

enum class RobotsPreset(val title: String, val description: String) {
    WORDPRESS("وردپرس (WordPress)", "مسیرهای استاندارد wp-admin, wp-includes و پیوند سitemap"),
    SHOPIFY("شاپیفای (Shopify)", "مسیرهای فیلتر کاتالوگ، سبد خرید، اکانت و تسویه‌حساب"),
    NEXT_JS("نکست جی‌اس (Next.js)", "قوانین بهینه‌سازی فایل‌های _next، api و استاتیک"),
    ECOMMERCE("فروشگاه اینترنتی جامع", "بستن سرچ داخلی، فیلترها، کوپن، سبد خرید و صفحات کاربر"),
    ALLOW_ALL("دسترسی کامل (Allow All)", "اجازه ایندکس و بررسی تمام صفحات برای همه ربات‌ها"),
    DISALLOW_ALL("مسدودسازی کامل (Staging / Private)", "جلوگیری از ایندکس کل سایت توسط تمام موتورهای جستجو")
}

data class RobotsRule(
    val id: String = java.util.UUID.randomUUID().toString(),
    val userAgent: String = "*",
    val allows: List<String> = emptyList(),
    val disallows: List<String> = emptyList(),
    val crawlDelay: Int? = null
)

data class RobotsTxtConfig(
    val rules: List<RobotsRule> = listOf(RobotsRule()),
    val sitemapUrls: List<String> = emptyList(),
    val host: String = "",
    val cleanParams: List<String> = emptyList()
)

data class RobotsTestResult(
    val path: String,
    val userAgent: String,
    val isAllowed: Boolean,
    val matchedRule: String,
    val explanation: String
)
