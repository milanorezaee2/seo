package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
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
import com.example.data.model.SchemaType
import com.example.ui.components.SyntaxHighlightedCode
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo400
import com.example.ui.viewmodel.SeoViewModel

@Composable
fun SchemaGeneratorScreen(
    viewModel: SeoViewModel,
    modifier: Modifier = Modifier
) {
    val selectedType by viewModel.selectedSchemaType.collectAsStateWithLifecycle()
    val articleTitle by viewModel.schemaArticleTitle.collectAsStateWithLifecycle()
    val articleAuthor by viewModel.schemaArticleAuthor.collectAsStateWithLifecycle()
    val articleUrl by viewModel.schemaArticleUrl.collectAsStateWithLifecycle()
    val articleImage by viewModel.schemaArticleImage.collectAsStateWithLifecycle()

    val businessName by viewModel.schemaBusinessName.collectAsStateWithLifecycle()
    val businessPhone by viewModel.schemaBusinessPhone.collectAsStateWithLifecycle()
    val businessAddress by viewModel.schemaBusinessAddress.collectAsStateWithLifecycle()

    val faqList by viewModel.schemaFaqList.collectAsStateWithLifecycle()
    val aiPrompt by viewModel.schemaAiPrompt.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isSchemaAiLoading.collectAsStateWithLifecycle()

    var newQuestion by remember { mutableStateOf("") }
    var newAnswer by remember { mutableStateOf("") }

    val jsonLdCode = viewModel.getComputedJsonLd()

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
                        text = "سازنده اسکیما و کدهای ساختاریافته (Schema JSON-LD)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "تولید کدهای استاندارد Rich Snippets برای نمایش ستاره‌ها، سوالات و مقالات در نتایج گوگل",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // Schema Type Pills (Horizontal Scroll)
        item {
            Text("نوع اسکیما را انتخاب کنید:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SchemaType.values().forEach { type ->
                    val isSelected = type == selectedType
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectSchemaType(type) },
                        label = { Text(type.displayName, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald500,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }
        }

        // Dynamic Form Fields
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("مشخصات ${selectedType.displayName}:", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    when (selectedType) {
                        SchemaType.ARTICLE -> {
                            OutlinedTextField(
                                value = articleTitle,
                                onValueChange = { viewModel.setSchemaArticleTitle(it) },
                                label = { Text("عنوان مقاله (Headline)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = articleAuthor,
                                onValueChange = { viewModel.setSchemaArticleAuthor(it) },
                                label = { Text("نام نویسنده (Author)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = articleUrl,
                                onValueChange = { viewModel.setSchemaArticleUrl(it) },
                                label = { Text("آدرس صفحه مقاله (URL)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = articleImage,
                                onValueChange = { viewModel.setSchemaArticleImage(it) },
                                label = { Text("آدرس تصویر شاخص (Image URL)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        SchemaType.LOCAL_BUSINESS, SchemaType.ORGANIZATION -> {
                            OutlinedTextField(
                                value = businessName,
                                onValueChange = { viewModel.setSchemaBusinessName(it) },
                                label = { Text("نام کسب و کار / برند") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = businessPhone,
                                onValueChange = { viewModel.setSchemaBusinessPhone(it) },
                                label = { Text("شماره تماس") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = businessAddress,
                                onValueChange = { viewModel.setSchemaBusinessAddress(it) },
                                label = { Text("آدرس فیزیکی") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        SchemaType.FAQ_PAGE -> {
                            Text("پرسش و پاسخ‌های متداول (FAQ):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            faqList.forEachIndexed { idx, item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Q: ${item.question}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("A: ${item.answer}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { viewModel.removeFaqItem(idx) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            }

                            OutlinedTextField(
                                value = newQuestion,
                                onValueChange = { newQuestion = it },
                                label = { Text("پرسش جدید") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = newAnswer,
                                onValueChange = { newAnswer = it },
                                label = { Text("پاسخ") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Button(
                                onClick = {
                                    if (newQuestion.isNotBlank() && newAnswer.isNotBlank()) {
                                        viewModel.addFaqItem(newQuestion, newAnswer)
                                        newQuestion = ""
                                        newAnswer = ""
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("افزودن پرسش و پاسخ", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                        else -> {
                            OutlinedTextField(
                                value = businessName,
                                onValueChange = { viewModel.setSchemaBusinessName(it) },
                                label = { Text("عنوان / نام آیتم") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // AI Schema Generator Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Indigo400.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Indigo400)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تولید خودکار اسکیما با هوش مصنوعی", fontWeight = FontWeight.Bold, color = Indigo400, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "هر متنی یا توضیحی از صفحه خود را وارد کنید تا هوش مصنوعی اسکیما مناسب را استخراج کند:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = aiPrompt,
                        onValueChange = { viewModel.setSchemaAiPrompt(it) },
                        placeholder = { Text("مثال: وبینار رایگان آموزش پیشرفته سئو در تاریخ ۱۵ شهریور...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.generateAiSchema() },
                        enabled = !isAiLoading && aiPrompt.isNotBlank(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo400)
                    ) {
                        if (isAiLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("تولید با هوش مصنوعی", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Real-time Generated Code Preview
        item {
            Text(
                text = "پیش‌نمایش کد استاندارد JSON-LD (آماده کپی و قرار دادن در تگ <head>):",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            SyntaxHighlightedCode(
                code = jsonLdCode,
                language = "JSON-LD"
            )
        }
    }
}
