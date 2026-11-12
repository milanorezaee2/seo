package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.SmartToy
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
import com.example.data.model.RobotsPreset
import com.example.ui.components.SyntaxHighlightedCode
import com.example.ui.theme.Emerald500
import com.example.ui.theme.ScoreGood
import com.example.ui.theme.ScorePoor
import com.example.ui.viewmodel.SeoViewModel

@Composable
fun RobotsTxtScreen(
    viewModel: SeoViewModel,
    modifier: Modifier = Modifier
) {
    val selectedPreset by viewModel.robotsPreset.collectAsStateWithLifecycle()
    val testPath by viewModel.robotsTestPath.collectAsStateWithLifecycle()
    val testResult by viewModel.robotsTestResult.collectAsStateWithLifecycle()

    var customDisallowInput by remember { mutableStateOf("") }
    var customAllowInput by remember { mutableStateOf("") }

    val robotsTxtCode = viewModel.getComputedRobotsTxt()

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
                        text = "سازنده و آزمایشگر فایل robots.txt",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "تعیین دسترسی خزنده‌های گوگل، بینگ و ربات‌های هوش مصنوعی + تست زنده مسدود بودن آدرس‌ها",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // CMS Presets
        item {
            Text("قالب‌های آماده سیستم‌های مدیریت محتوا:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RobotsPreset.values().forEach { preset ->
                    val isSelected = preset == selectedPreset
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectRobotsPreset(preset) },
                        label = { Text(preset.title, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald500,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }
        }

        // Live URL Path Tester
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("تست زنده دسترسی مسیر در robots.txt:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = testPath,
                            onValueChange = { viewModel.setRobotsTestPath(it) },
                            placeholder = { Text("/wp-admin/settings.php") },
                            modifier = Modifier.weight(1f).testTag("robots_test_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.testRobotsPath() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                            modifier = Modifier.testTag("robots_test_btn")
                        ) {
                            Text("تست مسیر", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    if (testResult != null) {
                        val res = testResult!!
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (res.isAllowed) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (res.isAllowed) ScoreGood else ScorePoor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (res.isAllowed) "دسترسی مجاز است (Allowed)" else "دسترسی مسدود است (Blocked)",
                                fontWeight = FontWeight.Bold,
                                color = if (res.isAllowed) ScoreGood else ScorePoor,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = res.explanation,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Add Custom Rule
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("افزودن دستور مسدودسازی (Disallow):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customDisallowInput,
                            onValueChange = { customDisallowInput = it },
                            placeholder = { Text("/secret-folder/") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (customDisallowInput.isNotBlank()) {
                                    viewModel.addRobotsDisallow(0, customDisallowInput)
                                    customDisallowInput = ""
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("بستن مسیر", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Code output
        item {
            Text(
                text = "کد کامل robots.txt (قرار دادن در ریشه سایت):",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            SyntaxHighlightedCode(code = robotsTxtCode, language = "Plain Text")
        }
    }
}
