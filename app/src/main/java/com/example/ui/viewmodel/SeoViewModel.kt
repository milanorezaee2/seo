package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.HistoryEntity
import com.example.data.model.*
import com.example.data.remote.GeminiService
import com.example.data.remote.NetworkHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val icon: String) {
    HOME("داشبورد", "dashboard"),
    SITE_AUDIT("آنالیز سایت", "analytics"),
    CORE_WEB_VITALS("Core Web Vitals", "speed"),
    COMPETITOR("تحلیل رقبا", "compare"),
    SCHEMA_GEN("ساخت اسکیما", "code"),
    SITEMAP_GEN("نقشه سایت", "map"),
    DNS_LOOKUP("بررسی DNS", "dns"),
    ROBOTS_TXT("robots.txt", "smart_toy"),
    HISTORY("تاریخچه", "history")
}

class SeoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val historyDao = db.seoHistoryDao()

    val historyList: StateFlow<List<HistoryEntity>> = historyDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active tab
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // 1. Site Audit State
    private val _auditUrl = MutableStateFlow("https://google.com")
    val auditUrl: StateFlow<String> = _auditUrl.asStateFlow()

    private val _isAuditLoading = MutableStateFlow(false)
    val isAuditLoading: StateFlow<Boolean> = _isAuditLoading.asStateFlow()

    private val _auditResult = MutableStateFlow<SeoAuditResult?>(null)
    val auditResult: StateFlow<SeoAuditResult?> = _auditResult.asStateFlow()

    // 2. Core Web Vitals State
    private val _cwvUrl = MutableStateFlow("https://google.com")
    val cwvUrl: StateFlow<String> = _cwvUrl.asStateFlow()

    private val _isCwvLoading = MutableStateFlow(false)
    val isCwvLoading: StateFlow<Boolean> = _isCwvLoading.asStateFlow()

    private val _cwvResult = MutableStateFlow<CoreWebVitalsResult?>(null)
    val cwvResult: StateFlow<CoreWebVitalsResult?> = _cwvResult.asStateFlow()

    // 3. Competitor Analysis State
    private val _mySiteUrl = MutableStateFlow("https://mysite.com")
    val mySiteUrl: StateFlow<String> = _mySiteUrl.asStateFlow()

    private val _competitorUrl = MutableStateFlow("https://competitor.com")
    val competitorUrl: StateFlow<String> = _competitorUrl.asStateFlow()

    private val _isCompetitorLoading = MutableStateFlow(false)
    val isCompetitorLoading: StateFlow<Boolean> = _isCompetitorLoading.asStateFlow()

    private val _competitorResult = MutableStateFlow<CompetitorComparison?>(null)
    val competitorResult: StateFlow<CompetitorComparison?> = _competitorResult.asStateFlow()

    // 4. Schema Generator State
    private val _selectedSchemaType = MutableStateFlow(SchemaType.ARTICLE)
    val selectedSchemaType: StateFlow<SchemaType> = _selectedSchemaType.asStateFlow()

    private val _schemaArticleTitle = MutableStateFlow("راهنمای جامع سئو و بهینه‌سازی سایت در سال ۲۰۲۶")
    val schemaArticleTitle: StateFlow<String> = _schemaArticleTitle.asStateFlow()

    private val _schemaArticleAuthor = MutableStateFlow("علی محمدی")
    val schemaArticleAuthor: StateFlow<String> = _schemaArticleAuthor.asStateFlow()

    private val _schemaArticleUrl = MutableStateFlow("https://example.com/seo-guide")
    val schemaArticleUrl: StateFlow<String> = _schemaArticleUrl.asStateFlow()

    private val _schemaArticleImage = MutableStateFlow("https://example.com/images/seo-banner.jpg")
    val schemaArticleImage: StateFlow<String> = _schemaArticleImage.asStateFlow()

    private val _schemaBusinessName = MutableStateFlow("آژانس دیجیتال مارکتینگ و سئو نوین")
    val schemaBusinessName: StateFlow<String> = _schemaBusinessName.asStateFlow()

    private val _schemaBusinessPhone = MutableStateFlow("+982188888888")
    val schemaBusinessPhone: StateFlow<String> = _schemaBusinessPhone.asStateFlow()

    private val _schemaBusinessAddress = MutableStateFlow("تهران، خیابان ولیعصر، برج سپهر")
    val schemaBusinessAddress: StateFlow<String> = _schemaBusinessAddress.asStateFlow()

    private val _schemaFaqList = MutableStateFlow(
        listOf(
            FaqItem("سئو چقدر زمان می‌برد تا نتیجه دهد؟", "معمولاً بین ۳ تا ۶ ماه زمان نیاز است تا تغییرات پایدار در کلمات کلیدی رقابتی نمایان شوند."),
            FaqItem("آیا سرعت سایت در رتبه‌بندی گوگل موثر است؟", "بله، سرعت سایت و فاکتورهای Core Web Vitals از سیگنال‌های مستقیم رتبه‌بندی الگوریتم تجربه صفحه گوگل هستند.")
        )
    )
    val schemaFaqList: StateFlow<List<FaqItem>> = _schemaFaqList.asStateFlow()

    private val _schemaAiPrompt = MutableStateFlow("")
    val schemaAiPrompt: StateFlow<String> = _schemaAiPrompt.asStateFlow()

    private val _isSchemaAiLoading = MutableStateFlow(false)
    val isSchemaAiLoading: StateFlow<Boolean> = _isSchemaAiLoading.asStateFlow()

    private val _customGeneratedJsonLd = MutableStateFlow<String?>(null)
    val customGeneratedJsonLd: StateFlow<String?> = _customGeneratedJsonLd.asStateFlow()

    // 5. Sitemap Generator State
    private val _sitemapBaseUrl = MutableStateFlow("https://example.com")
    val sitemapBaseUrl: StateFlow<String> = _sitemapBaseUrl.asStateFlow()

    private val _sitemapEntries = MutableStateFlow(
        listOf(
            SitemapUrlEntry(loc = "https://example.com/", changefreq = "daily", priority = "1.0", lastmod = "2026-08-26"),
            SitemapUrlEntry(loc = "https://example.com/blog", changefreq = "daily", priority = "0.9", lastmod = "2026-08-25"),
            SitemapUrlEntry(loc = "https://example.com/services", changefreq = "weekly", priority = "0.8", lastmod = "2026-08-20"),
            SitemapUrlEntry(loc = "https://example.com/about-us", changefreq = "monthly", priority = "0.6", lastmod = "2026-08-15"),
            SitemapUrlEntry(loc = "https://example.com/contact", changefreq = "monthly", priority = "0.5", lastmod = "2026-08-15")
        )
    )
    val sitemapEntries: StateFlow<List<SitemapUrlEntry>> = _sitemapEntries.asStateFlow()

    private val _sitemapValidationResult = MutableStateFlow<SitemapValidationResult?>(null)
    val sitemapValidationResult: StateFlow<SitemapValidationResult?> = _sitemapValidationResult.asStateFlow()

    private val _isSitemapValidating = MutableStateFlow(false)
    val isSitemapValidating: StateFlow<Boolean> = _isSitemapValidating.asStateFlow()

    // 6. DNS Lookup State
    private val _dnsDomain = MutableStateFlow("google.com")
    val dnsDomain: StateFlow<String> = _dnsDomain.asStateFlow()

    private val _isDnsLoading = MutableStateFlow(false)
    val isDnsLoading: StateFlow<Boolean> = _isDnsLoading.asStateFlow()

    private val _dnsResult = MutableStateFlow<DnsLookupResult?>(null)
    val dnsResult: StateFlow<DnsLookupResult?> = _dnsResult.asStateFlow()

    // 7. Robots.txt Generator & Tester State
    private val _robotsPreset = MutableStateFlow(RobotsPreset.WORDPRESS)
    val robotsPreset: StateFlow<RobotsPreset> = _robotsPreset.asStateFlow()

    private val _robotsRules = MutableStateFlow(
        listOf(
            RobotsRule(
                userAgent = "*",
                disallows = listOf("/wp-admin/", "/wp-includes/", "/cart/", "/checkout/", "/search/"),
                allows = listOf("/wp-admin/admin-ajax.php", "/")
            ),
            RobotsRule(
                userAgent = "Googlebot-Image",
                disallows = emptyList(),
                allows = listOf("/wp-content/uploads/")
            )
        )
    )
    val robotsRules: StateFlow<List<RobotsRule>> = _robotsRules.asStateFlow()

    private val _robotsSitemaps = MutableStateFlow(listOf("https://example.com/sitemap_index.xml"))
    val robotsSitemaps: StateFlow<List<String>> = _robotsSitemaps.asStateFlow()

    private val _robotsTestPath = MutableStateFlow("/wp-admin/settings.php")
    val robotsTestPath: StateFlow<String> = _robotsTestPath.asStateFlow()

    private val _robotsTestResult = MutableStateFlow<RobotsTestResult?>(null)
    val robotsTestResult: StateFlow<RobotsTestResult?> = _robotsTestResult.asStateFlow()

    init {
        // Auto-run initial audit sample on startup
        runAudit("https://google.com")
        runDnsLookup("google.com")
        runCoreWebVitals("https://google.com")
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // --- Action 1: Run Website Audit ---
    fun setAuditUrl(url: String) { _auditUrl.value = url }

    fun runAudit(rawUrl: String = _auditUrl.value) {
        viewModelScope.launch {
            _isAuditLoading.value = true
            _auditUrl.value = rawUrl
            try {
                val audit = NetworkHelper.auditWebsite(rawUrl)
                val aiTip = GeminiService.generateSeoInsights(
                    "یک خلاصه استراتژیک سئو ۳ خطی برای این مشخصات سایت تولید کن: دامنه: ${audit.domain}, امتیاز: ${audit.overallScore}, عنوان: ${audit.title}, تعداد خطاهای پیدا شده: ${audit.issues.size}"
                )
                val fullAudit = audit.copy(aiRecommendations = aiTip)
                _auditResult.value = fullAudit

                // Save to Room DB
                historyDao.insertHistory(
                    HistoryEntity(
                        toolType = "AUDIT",
                        title = "آنالیز ${audit.domain}",
                        targetUrl = fullAudit.url,
                        summary = "امتیاز سئو: ${fullAudit.overallScore}/۱۰۰ - ${fullAudit.issues.size} خطا و هشدار",
                        score = fullAudit.overallScore
                    )
                )
            } catch (e: Exception) {
                // handle error
            } finally {
                _isAuditLoading.value = false
            }
        }
    }

    // --- Action 2: Run Core Web Vitals ---
    fun setCwvUrl(url: String) { _cwvUrl.value = url }

    fun runCoreWebVitals(rawUrl: String = _cwvUrl.value) {
        viewModelScope.launch {
            _isCwvLoading.value = true
            _cwvUrl.value = rawUrl
            try {
                val cwv = NetworkHelper.analyzeCoreWebVitals(rawUrl)
                val aiTip = GeminiService.generateSeoInsights(
                    "به عنوان متخصص پرفورمنس وب، ۳ پیشنهاد عملیاتی برای بهبود نمرات Core Web Vitals (LCP: ${cwv.lcp.value}s, INP: ${cwv.inp.value}ms, CLS: ${cwv.cls.value}) برای آدرس $rawUrl ارائه بده."
                )
                val fullCwv = cwv.copy(aiOptimizationTips = aiTip)
                _cwvResult.value = fullCwv

                historyDao.insertHistory(
                    HistoryEntity(
                        toolType = "CWV",
                        title = "تست سرعت ${NetworkHelper.extractDomain(rawUrl)}",
                        targetUrl = fullCwv.url,
                        summary = "امتیاز سرعت: ${fullCwv.performanceScore}/۱۰۰ - LCP: ${fullCwv.lcp.value}s, CLS: ${fullCwv.cls.value}",
                        score = fullCwv.performanceScore
                    )
                )
            } catch (e: Exception) {
                // handle error
            } finally {
                _isCwvLoading.value = false
            }
        }
    }

    // --- Action 3: Competitor Comparison ---
    fun setMySiteUrl(url: String) { _mySiteUrl.value = url }
    fun setCompetitorUrl(url: String) { _competitorUrl.value = url }

    fun runCompetitorAnalysis() {
        viewModelScope.launch {
            _isCompetitorLoading.value = true
            try {
                val myAudit = NetworkHelper.auditWebsite(_mySiteUrl.value)
                val compAudit = NetworkHelper.auditWebsite(_competitorUrl.value)

                val mySpeed = NetworkHelper.analyzeCoreWebVitals(_mySiteUrl.value)
                val compSpeed = NetworkHelper.analyzeCoreWebVitals(_competitorUrl.value)

                val metricRows = listOf(
                    MetricRow("امتیاز کل سئو", "${myAudit.overallScore}/100", "${compAudit.overallScore}/100", if (myAudit.overallScore >= compAudit.overallScore) 1 else 2),
                    MetricRow("طول محتوای متنی", "${myAudit.wordCount} کلمه", "${compAudit.wordCount} کلمه", if (myAudit.wordCount >= compAudit.wordCount) 1 else 2),
                    MetricRow("سرعت بارگذاری LCP", "${mySpeed.lcp.value}s", "${compSpeed.lcp.value}s", if (mySpeed.lcp.value.toDoubleOrNull() ?: 3.0 <= compSpeed.lcp.value.toDoubleOrNull() ?: 3.0) 1 else 2),
                    MetricRow("تعداد تگ‌های H2 و H3", "${myAudit.h2List.size + myAudit.h3List.size}", "${compAudit.h2List.size + compAudit.h3List.size}", if (myAudit.h2List.size >= compAudit.h2List.size) 1 else 2),
                    MetricRow("تصاویر با متن Alt", "${myAudit.imageCount - myAudit.imagesWithoutAlt}/${myAudit.imageCount}", "${compAudit.imageCount - compAudit.imagesWithoutAlt}/${compAudit.imageCount}", if (myAudit.imagesWithoutAlt <= compAudit.imagesWithoutAlt) 1 else 2),
                    MetricRow("زمان پاسخ سرور (TTFB)", "${myAudit.responseTimeMs}ms", "${compAudit.responseTimeMs}ms", if (myAudit.responseTimeMs <= compAudit.responseTimeMs) 1 else 2)
                )

                val myStrengths = mutableListOf<String>()
                val compStrengths = mutableListOf<String>()
                if (myAudit.overallScore > compAudit.overallScore) myStrengths.add("امتیاز فنی بالاتر در چک‌لیست سئو") else compStrengths.add("امتیاز بالاتر ساختار فنی و متا")
                if (myAudit.wordCount > compAudit.wordCount) myStrengths.add("حجم محتوای غنی‌تر و کلمات بیشتر") else compStrengths.add("پوشش عمیق‌تر محتوایی در صفحه")
                if (myAudit.responseTimeMs < compAudit.responseTimeMs) myStrengths.add("پاسخگویی سریع‌تر سرور نسبت به رقیب") else compStrengths.add("زیرساخت سرور و هاستینگ پرسرعت‌تر")

                val keywords = listOf("کلمات کلیدی دم‌دراز مرتبط", "محتوای مقایسه‌ای و بررسی محصول", "صفحات راهنمای گام به گام", "تولید ویدیو و غنی‌سازی FAQ")

                val aiSwot = GeminiService.generateSeoInsights(
                    "تحلیل رقابتی سئو بین دو سایت: سایت ما: ${_mySiteUrl.value} (امتیاز ${myAudit.overallScore}) و رقیب: ${_competitorUrl.value} (امتیاز ${compAudit.overallScore}). استراتژی سبقت از رقیب در ۳ بند شفاف بنویس."
                )

                val result = CompetitorComparison(
                    myUrl = _mySiteUrl.value,
                    competitorUrl = _competitorUrl.value,
                    myScore = myAudit.overallScore,
                    competitorScore = compAudit.overallScore,
                    metricRows = metricRows,
                    myStrengths = myStrengths,
                    competitorStrengths = compStrengths,
                    keywordOpportunities = keywords,
                    aiStrategyBreakdown = aiSwot
                )

                _competitorResult.value = result

                historyDao.insertHistory(
                    HistoryEntity(
                        toolType = "COMPETITOR",
                        title = "مقایسه ${NetworkHelper.extractDomain(_mySiteUrl.value)} با ${NetworkHelper.extractDomain(_competitorUrl.value)}",
                        targetUrl = _mySiteUrl.value,
                        summary = "سایت شما: ${myAudit.overallScore} | رقیب: ${compAudit.overallScore}",
                        score = myAudit.overallScore
                    )
                )
            } catch (e: Exception) {
                // handle error
            } finally {
                _isCompetitorLoading.value = false
            }
        }
    }

    // --- Action 4: Schema Generator ---
    fun selectSchemaType(type: SchemaType) {
        _selectedSchemaType.value = type
        _customGeneratedJsonLd.value = null
    }

    fun setSchemaArticleTitle(v: String) { _schemaArticleTitle.value = v }
    fun setSchemaArticleAuthor(v: String) { _schemaArticleAuthor.value = v }
    fun setSchemaArticleUrl(v: String) { _schemaArticleUrl.value = v }
    fun setSchemaArticleImage(v: String) { _schemaArticleImage.value = v }
    fun setSchemaBusinessName(v: String) { _schemaBusinessName.value = v }
    fun setSchemaBusinessPhone(v: String) { _schemaBusinessPhone.value = v }
    fun setSchemaBusinessAddress(v: String) { _schemaBusinessAddress.value = v }
    fun setSchemaAiPrompt(v: String) { _schemaAiPrompt.value = v }

    fun addFaqItem(q: String, a: String) {
        _schemaFaqList.value = _schemaFaqList.value + FaqItem(q, a)
    }

    fun removeFaqItem(index: Int) {
        _schemaFaqList.value = _schemaFaqList.value.filterIndexed { i, _ -> i != index }
    }

    fun generateAiSchema() {
        if (_schemaAiPrompt.value.isBlank()) return
        viewModelScope.launch {
            _isSchemaAiLoading.value = true
            try {
                val jsonLd = GeminiService.generateAiSchema(_schemaAiPrompt.value, _selectedSchemaType.value.jsonType)
                _customGeneratedJsonLd.value = jsonLd

                historyDao.insertHistory(
                    HistoryEntity(
                        toolType = "SCHEMA",
                        title = "اسکیما هوشمند ${_selectedSchemaType.value.displayName}",
                        targetUrl = "AI Schema",
                        summary = "اسکیما استاندارد JSON-LD تولید شده با هوش مصنوعی",
                        dataPayload = jsonLd
                    )
                )
            } finally {
                _isSchemaAiLoading.value = false
            }
        }
    }

    fun getComputedJsonLd(): String {
        _customGeneratedJsonLd.value?.let { return it }

        return when (_selectedSchemaType.value) {
            SchemaType.ARTICLE -> """
<script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": "Article",
  "headline": "${_schemaArticleTitle.value}",
  "image": "${_schemaArticleImage.value}",
  "author": {
    "@type": "Person",
    "name": "${_schemaArticleAuthor.value}"
  },
  "publisher": {
    "@type": "Organization",
    "name": "سایت سئو مستر",
    "logo": {
      "@type": "ImageObject",
      "url": "https://example.com/logo.png"
    }
  },
  "datePublished": "2026-08-26",
  "dateModified": "2026-08-26",
  "mainEntityOfPage": "${_schemaArticleUrl.value}"
}
</script>""".trimIndent()

            SchemaType.LOCAL_BUSINESS -> """
<script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": "LocalBusiness",
  "name": "${_schemaBusinessName.value}",
  "image": "https://example.com/store-front.jpg",
  "telephone": "${_schemaBusinessPhone.value}",
  "address": {
    "@type": "PostalAddress",
    "streetAddress": "${_schemaBusinessAddress.value}",
    "addressLocality": "تهران",
    "addressCountry": "IR"
  },
  "openingHoursSpecification": {
    "@type": "OpeningHoursSpecification",
    "dayOfWeek": ["Saturday", "Sunday", "Monday", "Tuesday", "Wednesday"],
    "opens": "09:00",
    "closes": "18:00"
  },
  "priceRange": "$$$"
}
</script>""".trimIndent()

            SchemaType.ORGANIZATION -> """
<script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": "Organization",
  "name": "${_schemaBusinessName.value}",
  "url": "https://example.com",
  "logo": "https://example.com/logo.png",
  "contactPoint": {
    "@type": "ContactPoint",
    "telephone": "${_schemaBusinessPhone.value}",
    "contactType": "customer service"
  }
}
</script>""".trimIndent()

            SchemaType.FAQ_PAGE -> {
                val items = _schemaFaqList.value.joinToString(",\n    ") { item ->
                    """
    {
      "@type": "Question",
      "name": "${item.question}",
      "acceptedAnswer": {
        "@type": "Answer",
        "text": "${item.answer}"
      }
    }""".trimIndent()
                }
                """
<script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": "FAQPage",
  "mainEntity": [
    $items
  ]
}
</script>""".trimIndent()
            }

            SchemaType.PRODUCT -> """
<script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": "Product",
  "name": "نرم‌افزار سئو مستر پرو",
  "image": "https://example.com/product.jpg",
  "description": "پکیج جامع ابزارهای بررسی و بهینه‌سازی موتورهای جستجو",
  "brand": {
    "@type": "Brand",
    "name": "SEO Master"
  },
  "offers": {
    "@type": "Offer",
    "url": "https://example.com/pricing",
    "priceCurrency": "IRR",
    "price": "25000000",
    "availability": "https://schema.org/InStock"
  },
  "aggregateRating": {
    "@type": "AggregateRating",
    "ratingValue": "4.9",
    "reviewCount": "128"
  }
}
</script>""".trimIndent()

            SchemaType.BREADCRUMB -> """
<script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": "BreadcrumbList",
  "itemListElement": [
    {
      "@type": "ListItem",
      "position": 1,
      "name": "صفحه اصلی",
      "item": "https://example.com"
    },
    {
      "@type": "ListItem",
      "position": 2,
      "name": "وبلاگ",
      "item": "https://example.com/blog"
    },
    {
      "@type": "ListItem",
      "position": 3,
      "name": "آموزش جامع سئو",
      "item": "https://example.com/blog/seo-guide"
    }
  ]
}
</script>""".trimIndent()

            else -> """
<script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": "${_selectedSchemaType.value.jsonType}",
  "name": "عنوان شاخص",
  "description": "توضیحات پیش‌فرض اسکیما"
}
</script>""".trimIndent()
        }
    }

    // --- Action 5: Sitemap Generator & Validator ---
    fun setSitemapBaseUrl(v: String) { _sitemapBaseUrl.value = v }

    fun addSitemapEntry(loc: String, freq: String = "weekly", priority: String = "0.8") {
        _sitemapEntries.value = _sitemapEntries.value + SitemapUrlEntry(
            loc = loc,
            changefreq = freq,
            priority = priority,
            lastmod = "2026-08-26"
        )
    }

    fun removeSitemapEntry(id: String) {
        _sitemapEntries.value = _sitemapEntries.value.filter { it.id != id }
    }

    fun validateRemoteSitemap(url: String) {
        viewModelScope.launch {
            _isSitemapValidating.value = true
            try {
                val res = NetworkHelper.fetchAndValidateSitemap(url)
                _sitemapValidationResult.value = res
                if (res.parsedUrls.isNotEmpty()) {
                    _sitemapEntries.value = res.parsedUrls
                }

                historyDao.insertHistory(
                    HistoryEntity(
                        toolType = "SITEMAP",
                        title = "بررسی نقشه سایت ${NetworkHelper.extractDomain(url)}",
                        targetUrl = url,
                        summary = "${res.totalUrlsFound} آدرس یافت شد - ${if (res.isValid) "معتبر" else "دارای خطا"}"
                    )
                )
            } finally {
                _isSitemapValidating.value = false
            }
        }
    }

    fun getComputedSitemapXml(): String {
        val urlsXml = _sitemapEntries.value.joinToString("\n") { entry ->
            """  <url>
    <loc>${entry.loc}</loc>
    <lastmod>${entry.lastmod.ifEmpty { "2026-08-26" }}</lastmod>
    <changefreq>${entry.changefreq}</changefreq>
    <priority>${entry.priority}</priority>
  </url>"""
        }

        return """<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
$urlsXml
</urlset>"""
    }

    // --- Action 6: DNS Lookup ---
    fun setDnsDomain(domain: String) { _dnsDomain.value = domain }

    fun runDnsLookup(rawDomain: String = _dnsDomain.value) {
        viewModelScope.launch {
            _isDnsLoading.value = true
            _dnsDomain.value = rawDomain
            try {
                val res = NetworkHelper.performDnsLookup(rawDomain)
                _dnsResult.value = res

                historyDao.insertHistory(
                    HistoryEntity(
                        toolType = "DNS",
                        title = "بررسی DNS دامنه ${res.domain}",
                        targetUrl = res.domain,
                        summary = "IP: ${res.ipAddresses.firstOrNull() ?: "-"} | SSL: ${if (res.sslInfo?.isSecure == true) "فعال" else "نامعتبر"}"
                    )
                )
            } finally {
                _isDnsLoading.value = false
            }
        }
    }

    // --- Action 7: Robots.txt Generator & Tester ---
    fun selectRobotsPreset(preset: RobotsPreset) {
        _robotsPreset.value = preset
        when (preset) {
            RobotsPreset.WORDPRESS -> {
                _robotsRules.value = listOf(
                    RobotsRule(
                        userAgent = "*",
                        disallows = listOf("/wp-admin/", "/wp-includes/", "/cart/", "/checkout/", "/search/"),
                        allows = listOf("/wp-admin/admin-ajax.php", "/wp-content/uploads/")
                    )
                )
                _robotsSitemaps.value = listOf("https://example.com/sitemap_index.xml")
            }
            RobotsPreset.SHOPIFY -> {
                _robotsRules.value = listOf(
                    RobotsRule(
                        userAgent = "*",
                        disallows = listOf("/admin", "/cart", "/orders", "/checkout", "/collections/*+*", "/account"),
                        allows = listOf("/collections", "/products")
                    )
                )
                _robotsSitemaps.value = listOf("https://example.com/sitemap.xml")
            }
            RobotsPreset.NEXT_JS -> {
                _robotsRules.value = listOf(
                    RobotsRule(
                        userAgent = "*",
                        disallows = listOf("/_next/", "/api/", "/admin/"),
                        allows = listOf("/_next/static/", "/")
                    )
                )
                _robotsSitemaps.value = listOf("https://example.com/sitemap.xml")
            }
            RobotsPreset.ECOMMERCE -> {
                _robotsRules.value = listOf(
                    RobotsRule(
                        userAgent = "*",
                        disallows = listOf("/cart/", "/checkout/", "/profile/", "/admin/", "/filter?", "/*?sort="),
                        allows = listOf("/catalog/", "/products/")
                    )
                )
                _robotsSitemaps.value = listOf("https://example.com/sitemap.xml")
            }
            RobotsPreset.ALLOW_ALL -> {
                _robotsRules.value = listOf(
                    RobotsRule(userAgent = "*", disallows = emptyList(), allows = listOf("/"))
                )
                _robotsSitemaps.value = listOf("https://example.com/sitemap.xml")
            }
            RobotsPreset.DISALLOW_ALL -> {
                _robotsRules.value = listOf(
                    RobotsRule(userAgent = "*", disallows = listOf("/"), allows = emptyList())
                )
                _robotsSitemaps.value = emptyList()
            }
        }
    }

    fun addRobotsDisallow(ruleIndex: Int, path: String) {
        val current = _robotsRules.value.toMutableList()
        if (ruleIndex in current.indices) {
            val rule = current[ruleIndex]
            current[ruleIndex] = rule.copy(disallows = rule.disallows + path)
            _robotsRules.value = current
        }
    }

    fun addRobotsAllow(ruleIndex: Int, path: String) {
        val current = _robotsRules.value.toMutableList()
        if (ruleIndex in current.indices) {
            val rule = current[ruleIndex]
            current[ruleIndex] = rule.copy(allows = rule.allows + path)
            _robotsRules.value = current
        }
    }

    fun setRobotsTestPath(p: String) { _robotsTestPath.value = p }

    fun testRobotsPath() {
        val path = _robotsTestPath.value.trim()
        val generatedTxt = getComputedRobotsTxt()

        var isBlocked = false
        var matchedRule = "Default: Allow"

        for (rule in _robotsRules.value) {
            for (disallow in rule.disallows) {
                if (disallow.isNotEmpty() && path.startsWith(disallow.replace("*", ""))) {
                    isBlocked = true
                    matchedRule = "Disallow: $disallow"
                    break
                }
            }
            if (isBlocked) {
                for (allow in rule.allows) {
                    if (allow.isNotEmpty() && path.startsWith(allow.replace("*", ""))) {
                        isBlocked = false
                        matchedRule = "Allow: $allow (Override)"
                        break
                    }
                }
            }
        }

        _robotsTestResult.value = RobotsTestResult(
            path = path,
            userAgent = "Googlebot (*)",
            isAllowed = !isBlocked,
            matchedRule = matchedRule,
            explanation = if (!isBlocked) "ربات‌های جستجو اجازه خزیدن و ایندکس این آدرس را دارند." else "این مسیر توسط قانون $matchedRule برای موتورهای جستجو مسدود شده است."
        )
    }

    fun getComputedRobotsTxt(): String {
        val sb = StringBuilder()
        sb.append("# robots.txt generated by SEO Master Pro\n\n")

        for (rule in _robotsRules.value) {
            sb.append("User-agent: ${rule.userAgent}\n")
            for (allow in rule.allows) {
                sb.append("Allow: $allow\n")
            }
            for (disallow in rule.disallows) {
                sb.append("Disallow: $disallow\n")
            }
            rule.crawlDelay?.let {
                sb.append("Crawl-delay: $it\n")
            }
            sb.append("\n")
        }

        for (sitemap in _robotsSitemaps.value) {
            sb.append("Sitemap: $sitemap\n")
        }

        return sb.toString().trim()
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            historyDao.deleteHistoryById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyDao.clearAllHistory()
        }
    }
}
