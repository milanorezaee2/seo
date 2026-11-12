package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SyntaxHighlightedCode
import com.example.ui.theme.Emerald500
import com.example.ui.theme.ScoreGood
import com.example.ui.theme.ScorePoor
import com.example.ui.viewmodel.SeoViewModel

@Composable
fun SitemapGeneratorScreen(
    viewModel: SeoViewModel,
    modifier: Modifier = Modifier
) {
    val sitemapEntries by viewModel.sitemapEntries.collectAsStateWithLifecycle()
    val validationResult by viewModel.sitemapValidationResult.collectAsStateWithLifecycle()
    val isValidating by viewModel.isSitemapValidating.collectAsStateWithLifecycle()

    var newUrlInput by remember { mutableStateOf("") }
    var selectedFreq by remember { mutableStateOf("weekly") }
    var selectedPriority by remember { mutableStateOf("0.8") }
    var testSitemapUrl by remember { mutableStateOf("https://example.com/sitemap.xml") }

    val xmlOutput = viewModel.getComputedSitemapXml()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "سازنده و اعتبارسنج نقشه سایت (Sitemap.xml)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "ساخت فایل نقشه سایت بر اساس پروتکل استاندارد sitemaps.org و بررسی آنلاین سلامت sitemap",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // Online Sitemap Validator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("بررسی آنلاین سلامت نقشه سایت (Sitemap Checker):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = testSitemapUrl,
                            onValueChange = { testSitemapUrl = it },
                            placeholder = { Text("https://example.com/sitemap.xml") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.validateRemoteSitemap(testSitemapUrl) },
                            enabled = !isValidating && testSitemapUrl.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                        ) {
                            if (isValidating) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                            } else {
                                Text("بررسی", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }

                    if (validationResult != null) {
                        val res = validationResult!!
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (res.isValid) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (res.isValid) ScoreGood else ScorePoor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (res.isValid) "نقشه سایت معتبر است (${res.totalUrlsFound} آدرس یافت شد)" else "نقشه سایت دارای خطا است",
                                fontWeight = FontWeight.Bold,
                                color = if (res.isValid) ScoreGood else ScorePoor,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Add URL Entry Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("افزودن آدرس جدید به نقشه سایت:", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    OutlinedTextField(
                        value = newUrlInput,
                        onValueChange = { newUrlInput = it },
                        label = { Text("آدرس کامل صفحه (URL)") },
                        placeholder = { Text("https://example.com/page") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedFreq,
                            onValueChange = { selectedFreq = it },
                            label = { Text("فرکانس تغییر") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = selectedPriority,
                            onValueChange = { selectedPriority = it },
                            label = { Text("اولویت (0.1 - 1.0)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (newUrlInput.isNotBlank()) {
                                viewModel.addSitemapEntry(newUrlInput, selectedFreq, selectedPriority)
                                newUrlInput = ""
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("افزودن به نقشه سایت", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Current URLs List
        item {
            Text(
                text = "آدرس‌های تعریف شده (${sitemapEntries.size} صفحه):",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        items(sitemapEntries) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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
                        Text(entry.loc, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "اولویت: ${entry.priority} | تناوب: ${entry.changefreq} | تاریخ: ${entry.lastmod}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { viewModel.removeSitemapEntry(entry.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Code output
        item {
            Text(
                text = "فایل خروجی sitemap.xml (کپی در روت هاست):",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            SyntaxHighlightedCode(code = xmlOutput, language = "XML")
        }
    }
}
