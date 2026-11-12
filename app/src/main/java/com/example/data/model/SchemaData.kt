package com.example.data.model

enum class SchemaType(val displayName: String, val jsonType: String, val iconName: String) {
    ARTICLE("مقاله (Article)", "Article", "article"),
    LOCAL_BUSINESS("کسب و کار محلی (LocalBusiness)", "LocalBusiness", "store"),
    ORGANIZATION("سازمان یا شرکت (Organization)", "Organization", "corporate"),
    PRODUCT("محصول و قیمت (Product)", "Product", "shopping_cart"),
    FAQ_PAGE("سوالات متداول (FAQPage)", "FAQPage", "quiz"),
    BREADCRUMB("مسیر راهنما (BreadcrumbList)", "BreadcrumbList", "alt_route"),
    HOW_TO("آموزش مرحله‌ای (HowTo)", "HowTo", "build"),
    EVENT("رویداد و وبینار (Event)", "Event", "event"),
    PERSON("شخص یا متخصص (Person)", "Person", "person")
}

data class FaqItem(
    val question: String,
    val answer: String
)

data class BreadcrumbItem(
    val name: String,
    val url: String
)

data class HowToStep(
    val title: String,
    val text: String
)
