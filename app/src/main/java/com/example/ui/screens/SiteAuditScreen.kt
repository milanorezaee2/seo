package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.IssueSeverity
import com.example.ui.components.AuditItemCard
import com.example.ui.components.SeoScoreMeter
import com.example.ui.components.SeverityBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.SeoViewModel

@Composable
fun SiteAuditScreen(
    viewModel: SeoViewModel,
    modifier: Modifier = Modifier
) {
    val auditUrl by viewModel.auditUrl.collectAsStateWithLifecycle()
    val isAuditLoading by viewModel.isAuditLoading.collectAsStateWithLifecycle()
    val auditResult by viewModel.auditResult.collectAsStateWithLifecycle()

    var inputUrl by remember { mutableStateOf(auditUrl) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Search Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "آنالیز و عیب‌یابی جامع سایت (Site Audit)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "بررسی تگ‌های سئو، هدینگ‌ها، متادیتا، تصاویر، لینک‌ها و سرعت",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputUrl,
                            onValueChange = { inputUrl = it },
                            placeholder = { Text("https://example.com") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("audit_input_field"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (inputUrl.isNotBlank()) {
                                    viewModel.runAudit(inputUrl)
                                }
                            },
                            enabled = !isAuditLoading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                            modifier = Modifier.testTag("audit_run_btn")
                        ) {
                            if (isAuditLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("آنالیز", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }

        if (auditResult != null) {
            val res = auditResult!!

            // Score Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SeoScoreMeter(
                            score = res.overallScore,
                            title = "امتیاز سلامت فنی سئو"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ScoreStatBadge("زمان پاسخ", "${res.responseTimeMs}ms", Emerald500)
                            ScoreStatBadge("وضعیت HTTP", "${res.httpStatusCode}", if (res.httpStatusCode == 200) Emerald500 else ScorePoor)
                            ScoreStatBadge("تعداد کلمات", "${res.wordCount}", Indigo400)
                            ScoreStatBadge("تصاویر", "${res.imageCount}", Cyan400)
                        }
                    }
                }
            }

            // AI Strategic Recommendations
            if (!res.aiRecommendations.isNullOrEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Indigo600.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Indigo400)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "توصیه‌های هوش مصنوعی Gemini",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Indigo400
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = res.aiRecommendations ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                }
            }

            // Section 1: Meta Tags
            item {
                Text(
                    text = "۱. بررسی تگ‌های اصلی متا و ساختار صفحه",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                AuditItemCard(
                    title = "تگ عنوان (Title Tag)",
                    value = res.title ?: "یافت نشد",
                    severity = res.titleStatus,
                    detail = "طول عنوان: ${res.titleLength} کاراکتر (استاندارد: ۵۰ تا ۶۰)"
                )
            }

            item {
                AuditItemCard(
                    title = "توضیحات متا (Meta Description)",
                    value = res.metaDescription ?: "یافت نشد",
                    severity = res.metaDescriptionStatus,
                    detail = "طول توضیحات: ${res.metaDescriptionLength} کاراکتر (استاندارد: ۱۲۰ تا ۱۶۰)"
                )
            }

            item {
                AuditItemCard(
                    title = "آدرس کانونیکال (Canonical URL)",
                    value = res.canonicalUrl ?: "تعریف نشده",
                    severity = res.canonicalStatus,
                    detail = if (res.canonicalUrl != null) "آدرس معتبر ست شده است" else "پیشنهاد می‌شود جهت جلوگیری از محتوای تکراری اضافه شود."
                )
            }

            item {
                AuditItemCard(
                    title = "دستورات متای ربات‌ها (Robots Meta)",
                    value = res.robotsMeta ?: "index, follow (پیش‌فرض)",
                    severity = if (res.robotsMeta?.contains("noindex") == true) IssueSeverity.HIGH else IssueSeverity.GOOD,
                    detail = if (res.robotsMeta?.contains("noindex") == true) "هشدار: صفحه از ایندکس مسدود شده است!" else "قابل دسترسی برای موتورهای جستجو"
                )
            }

            // Section 2: Headings H1, H2, H3
            item {
                Text(
                    text = "۲. ساختار هدینگ‌ها (Headings Hierarchy)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("تگ‌های H1 (تعداد: ${res.h1List.size})", fontWeight = FontWeight.Bold)
                            SeverityBadge(if (res.h1List.size == 1) IssueSeverity.GOOD else if (res.h1List.isEmpty()) IssueSeverity.HIGH else IssueSeverity.LOW)
                        }
                        res.h1List.forEach { h1 ->
                            Text("• $h1", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("نمونه تگ‌های H2 و H3 (تعداد: ${res.h2List.size + res.h3List.size})", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        res.h2List.take(4).forEach { h2 ->
                            Text("• H2: $h2", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        res.h3List.take(3).forEach { h3 ->
                            Text("  - H3: $h3", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                        }
                    }
                }
            }

            // Section 3: Media & Links
            item {
                Text(
                    text = "۳. تصاویر و لینک‌های صفحه",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                AuditItemCard(
                    title = "وضعیت ویژگی Alt تصاویر",
                    value = "کل تصاویر: ${res.imageCount} عدد | تصاویر بدون Alt: ${res.imagesWithoutAlt} عدد",
                    severity = if (res.imagesWithoutAlt == 0) IssueSeverity.GOOD else IssueSeverity.MEDIUM,
                    detail = if (res.imagesWithoutAlt > 0) "${res.imagesWithoutAlt} تصویر فاقد متن جایگزین برای سرچ گوگل هستند." else "همه تصاویر دارای ویژگی Alt هستند."
                )
            }

            item {
                AuditItemCard(
                    title = "لینک‌های داخلی و خارجی",
                    value = "لینک‌های داخلی: ${res.internalLinksCount} | لینک‌های خروجی: ${res.externalLinksCount}",
                    severity = IssueSeverity.GOOD,
                    detail = "ساختار لینک‌سازی داخلی برای توزیع پیج‌رنک"
                )
            }

            // Section 4: All Issues & Action Checklist
            if (res.issues.isNotEmpty()) {
                item {
                    Text(
                        text = "۴. لیست خطاها و اقدامات اصلاحی (${res.issues.size} مورد)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(res.issues) { issue ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(issue.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                SeverityBadge(issue.severity)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(issue.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Emerald500.copy(alpha = 0.1f))
                                    .padding(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = Emerald500, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(issue.recommendation, fontSize = 11.sp, color = Emerald500, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreStatBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = color))
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp))
    }
}
