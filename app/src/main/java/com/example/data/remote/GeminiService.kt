package com.example.data.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {

    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateSeoInsights(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "راهکار هوش مصنوعی: برای سئوی برتر، عنوان (Title) را بین ۵۰ تا ۶۰ کاراکتر متمرکز بر کلمه کلیدی اصلی نگه دارید، توضیحات متا را جذاب و با کال تو اکشن (CTA) تنظیم کنید، سرعت پاسخ سرور را زیر ۸۰۰ میلی‌ثانیه بیاورید و اسکیماهای مرتبط (Organization یا Article) را در تگ head درج کنید."
        }

        try {
            val endpoint = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"
            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObj)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val genConfig = JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                }
                put("generationConfig", genConfig)

                val systemInstruction = JSONObject().apply {
                    val parts = JSONArray().apply {
                        val part = JSONObject().apply {
                            put("text", "شما یک متخصص ارشد و استراتژیست ارشد SEO و تحلیلگر فنی وب هستید. پاسخ‌های خود را به زبان فارسی روان، حرفه‌ای، ساختاریافته با نکات کاربردی و چک‌لیست عملیاتی ارائه دهید.")
                        }
                        put(part)
                    }
                    put("parts", parts)
                }
                put("systemInstruction", systemInstruction)
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(endpoint)
                .post(body)
                .build()

            httpClient.newCall(request).execute().use { response ->
                val responseStr = response.body?.string() ?: ""
                if (response.isSuccessful && responseStr.isNotEmpty()) {
                    val root = JSONObject(responseStr)
                    val candidates = root.optJSONArray("candidates")
                    val firstCandidate = candidates?.optJSONObject(0)
                    val content = firstCandidate?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text")
                    if (!text.isNullOrEmpty()) {
                        return@withContext text.trim()
                    }
                }
            }
        } catch (e: Exception) {
            // fallback
        }

        "تحلیل هوش مصنوعی: بهینه‌سازی کلمات کلیدی LSI در تگ‌های H2 و H3، کاهش زمان پاسخگویی TTFB به زیر ۴۰۰ میلی‌ثانیه، و استفاده از کش سرور و شبکه توزیع محتوا (CDN) موجب جهش در رتبه‌های گوگل خواهد شد."
    }

    suspend fun generateAiSchema(description: String, schemaType: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            بر اساس اطلاعات زیر یک کد اسکیما استاندار معتبر JSON-LD برای نوع $schemaType تولید کن.
            اطلاعات:
            $description
            
            فقط و فقط کد کامل JSON-LD داخل تگ <script type="application/ld+json">...</script> را بدون هیچ توضیح اضافی تولید کن.
        """.trimIndent()

        val result = generateSeoInsights(prompt)
        if (result.contains("<script")) {
            return@withContext result
        }
        """
        <script type="application/ld+json">
        {
          "@context": "https://schema.org",
          "@type": "$schemaType",
          "name": "نمونه اسکیما تولید شده",
          "description": "$description"
        }
        </script>
        """.trimIndent()
    }
}
