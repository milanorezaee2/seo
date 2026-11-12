package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.data.remote.NetworkHelper
import com.example.ui.theme.*
import com.example.ui.viewmodel.SeoViewModel

@Composable
fun CompetitorScreen(
    viewModel: SeoViewModel,
    modifier: Modifier = Modifier
) {
    val mySiteUrl by viewModel.mySiteUrl.collectAsStateWithLifecycle()
    val competitorUrl by viewModel.competitorUrl.collectAsStateWithLifecycle()
    val isCompetitorLoading by viewModel.isCompetitorLoading.collectAsStateWithLifecycle()
    val competitorResult by viewModel.competitorResult.collectAsStateWithLifecycle()

    var inputMySite by remember { mutableStateOf(mySiteUrl) }
    var inputCompetitor by remember { mutableStateOf(competitorUrl) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "تحلیل رقبا و مقایسه رو در رو (Competitor Analysis)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "مقایسه امتیاز سئو، حجم محتوا، سرعت، هدینگ‌ها و کشف فرصت‌های رتبه بهتر",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("سایت شما:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Emerald500)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = inputMySite,
                        onValueChange = {
                            inputMySite = it
                            viewModel.setMySiteUrl(it)
                        },
                        placeholder = { Text("https://yoursite.com") },
                        modifier = Modifier.fillMaxWidth().testTag("my_site_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("سایت رقیب:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Indigo400)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = inputCompetitor,
                        onValueChange = {
                            inputCompetitor = it
                            viewModel.setCompetitorUrl(it)
                        },
                        placeholder = { Text("https://competitor.com") },
                        modifier = Modifier.fillMaxWidth().testTag("competitor_site_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (inputMySite.isNotBlank() && inputCompetitor.isNotBlank()) {
                                viewModel.runCompetitorAnalysis()
                            }
                        },
                        enabled = !isCompetitorLoading,
                        modifier = Modifier.fillMaxWidth().testTag("run_competitor_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo500)
                    ) {
                        if (isCompetitorLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CompareArrows, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("شروع مقایسه رو در رو", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        if (competitorResult != null) {
            val res = competitorResult!!

            // Head to Head Score Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // My Site Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Emerald700.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("سایت شما", fontSize = 12.sp, color = Emerald300, fontWeight = FontWeight.Bold)
                            Text(NetworkHelper.extractDomain(res.myUrl), fontSize = 11.sp, maxLines = 1)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${res.myScore}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Emerald500)
                            Text("از ۱۰۰", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Competitor Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Indigo700.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Indigo400.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("سایت رقیب", fontSize = 12.sp, color = Indigo400, fontWeight = FontWeight.Bold)
                            Text(NetworkHelper.extractDomain(res.competitorUrl), fontSize = 11.sp, maxLines = 1)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${res.competitorScore}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Indigo400)
                            Text("از ۱۰۰", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Metric Comparison Rows
            item {
                Text(
                    text = "جدول مقایسه پارامترهای فنی",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(res.metricRows) { row ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(row.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (row.winner == 1) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald500, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text("شما: ${row.myVal}", fontSize = 12.sp, color = if (row.winner == 1) Emerald500 else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = if (row.winner == 1) FontWeight.Bold else FontWeight.Normal)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (row.winner == 2) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Indigo400, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text("رقیب: ${row.compVal}", fontSize = 12.sp, color = if (row.winner == 2) Indigo400 else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = if (row.winner == 2) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }

            // AI SWOT Strategy
            if (!res.aiStrategyBreakdown.isNullOrEmpty()) {
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
                                    text = "استراتژی سبقت از رقیب (تحلیل هوش مصنوعی)",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Indigo400
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = res.aiStrategyBreakdown ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                }
            }

            // Keyword and Content Opportunities
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Emerald500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("فرصت‌های محتوایی و کلمات کلیدی برای پیشی گرفتن", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        res.keywordOpportunities.forEach { opp ->
                            Text("• $opp", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}
