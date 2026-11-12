package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.SeoViewModel

data class ToolCardItem(
    val tab: AppTab,
    val title: String,
    val enTitle: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val tag: String
)

@Composable
fun HomeScreen(
    viewModel: SeoViewModel,
    modifier: Modifier = Modifier
) {
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    var quickUrlInput by remember { mutableStateOf("https://") }

    val tools = listOf(
        ToolCardItem(
            tab = AppTab.SITE_AUDIT,
            title = "آنالیز سایت",
            enTitle = "Site Audit & SEO Check",
            description = "بررسی تگ‌های متا، هدینگ‌ها، تصاویر، محتوا و خطاهای فنی سئو",
            icon = Icons.Default.Analytics,
            accentColor = Emerald500,
            tag = "ابزار جامع"
        ),
        ToolCardItem(
            tab = AppTab.CORE_WEB_VITALS,
            title = "امتیاز Core Web Vitals",
            enTitle = "Page Speed & Vitals",
            description = "محاسبه فاکتورهای LCP، INP، CLS، TTFB و راهکارهای افزایش سرعت",
            icon = Icons.Default.Speed,
            accentColor = Cyan400,
            tag = "سرعت و پرفورمنس"
        ),
        ToolCardItem(
            tab = AppTab.COMPETITOR,
            title = "ابزار تحلیل رقبا",
            enTitle = "Competitor Analysis",
            description = "مقایسه رو در روی دو سایت، کشف شکاف کلمات کلیدی و تحلیل SWOT",
            icon = Icons.Default.CompareArrows,
            accentColor = Indigo400,
            tag = "مقایسه هوشمند"
        ),
        ToolCardItem(
            tab = AppTab.SCHEMA_GEN,
            title = "ساخت اسکیما",
            enTitle = "Schema Markup Generator",
            description = "تولید استانداردهای JSON-LD برای مقالات، محصولات، سوالات متداول و مشاغل",
            icon = Icons.Default.Code,
            accentColor = Color(0xFFF59E0B),
            tag = "ریچ اسنیپت"
        ),
        ToolCardItem(
            tab = AppTab.SITEMAP_GEN,
            title = "ساخت نقشه سایت",
            enTitle = "Sitemap.xml Generator",
            description = "تولید فایل XML نقشه سایت با اولویت‌بندی، تاریخ و اعتبارسنجی آنلاین",
            icon = Icons.Default.Map,
            accentColor = Color(0xFF38BDF8),
            tag = "ایندکس گوگل"
        ),
        ToolCardItem(
            tab = AppTab.DNS_LOOKUP,
            title = "بررسی DNS دامنه",
            enTitle = "DNS & SSL Inspector",
            description = "بررسی رکوردهای A, MX, TXT (SPF/DMARC)، گواهی SSL و نیم‌سرورها",
            icon = Icons.Default.Dns,
            accentColor = Color(0xFFA855F7),
            tag = "امنیت و شبکه"
        ),
        ToolCardItem(
            tab = AppTab.ROBOTS_TXT,
            title = "ساخت robots.txt",
            enTitle = "Robots.txt Builder & Tester",
            description = "تولید و تست آنلاین فایل قوانین دسترسی ربات‌های جستجو و هوش مصنوعی",
            icon = Icons.Default.SmartToy,
            accentColor = Color(0xFFEC4899),
            tag = "کنترل خزنده‌ها"
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Emerald700.copy(alpha = 0.4f),
                                    Cyan600.copy(alpha = 0.2f),
                                    DarkCard
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "جعبه ابزار سئو حرفه‌ای",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "SEO Master Pro Suite • AI-Powered",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Emerald300,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Emerald500.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Emerald500,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "آنالیز جامع وب‌سایت، تست Core Web Vitals، تحلیل رقبا، تولید Schema و بررسی فنی زیرساخت سئو با قدرت هوش مصنوعی Gemini",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Scan Box
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = quickUrlInput,
                                onValueChange = { quickUrlInput = it },
                                placeholder = { Text("https://yourwebsite.com", fontSize = 13.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quick_url_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                textStyle = MaterialTheme.typography.bodySmall,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Emerald500,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (quickUrlInput.isNotBlank()) {
                                        viewModel.runAudit(quickUrlInput)
                                        viewModel.selectTab(AppTab.SITE_AUDIT)
                                    }
                                },
                                modifier = Modifier.testTag("quick_audit_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                            ) {
                                Text(
                                    text = "آنالیز سریع",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ابزارهای ۷ گانه سئو",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "۷ ابزار تخصصی",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Emerald500,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // 7 SEO Tools Cards
        items(tools) { tool ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.selectTab(tool.tab) }
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        RoundedCornerShape(16.dp)
                    )
                    .testTag("tool_card_${tool.tab.name}"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(tool.accentColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = tool.title,
                            tint = tool.accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = tool.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(tool.accentColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tool.tag,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = tool.accentColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Text(
                            text = tool.enTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = tool.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            maxLines = 2
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "رفتن به ابزار",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Recent History Preview
        if (historyList.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "گزارش‌های اخیر",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    TextButton(onClick = { viewModel.selectTab(AppTab.HISTORY) }) {
                        Text("مشاهده همه", color = Emerald500, fontSize = 12.sp)
                    }
                }
            }

            items(historyList.take(3)) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = item.summary,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        if (item.score != null) {
                            val scoreColor = if (item.score >= 80) ScoreGood else if (item.score >= 50) ScoreAverage else ScorePoor
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(scoreColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${item.score}/100",
                                    color = scoreColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
