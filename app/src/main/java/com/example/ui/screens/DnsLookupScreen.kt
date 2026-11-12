package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.model.DnsRecordItem
import com.example.ui.theme.Emerald500
import com.example.ui.theme.ScoreGood
import com.example.ui.theme.ScorePoor
import com.example.ui.viewmodel.SeoViewModel

@Composable
fun DnsLookupScreen(
    viewModel: SeoViewModel,
    modifier: Modifier = Modifier
) {
    val dnsDomain by viewModel.dnsDomain.collectAsStateWithLifecycle()
    val isDnsLoading by viewModel.isDnsLoading.collectAsStateWithLifecycle()
    val dnsResult by viewModel.dnsResult.collectAsStateWithLifecycle()

    var inputDomain by remember { mutableStateOf(dnsDomain) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "بررسی رکوردهای DNS و گواهی SSL دامنه",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "استعلام زنده رکوردهای A, MX, TXT (SPF/DMARC)، نیم‌سرورها و تاریخ اعتبار SSL",
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
                            value = inputDomain,
                            onValueChange = { inputDomain = it },
                            placeholder = { Text("example.com") },
                            modifier = Modifier.weight(1f).testTag("dns_domain_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (inputDomain.isNotBlank()) {
                                    viewModel.runDnsLookup(inputDomain)
                                }
                            },
                            enabled = !isDnsLoading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7)),
                            modifier = Modifier.testTag("dns_run_btn")
                        ) {
                            if (isDnsLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("بررسی DNS", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        if (dnsResult != null) {
            val res = dnsResult!!

            // SSL Certificate Card
            if (res.sslInfo != null) {
                val ssl = res.sslInfo!!
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Emerald500)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("گواهینامه امنیتی SSL / HTTPS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (ssl.isSecure) ScoreGood.copy(alpha = 0.15f) else ScorePoor.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (ssl.isSecure) "SSL فعال و معتبر" else "SSL نامعتبر",
                                        color = if (ssl.isSecure) ScoreGood else ScorePoor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("صادرکننده: ${ssl.issuer}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("اعتبار تا: ${ssl.validTo} (${ssl.daysRemaining} روز باقی‌مانده)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("الگوریتم رمزنگاری: ${ssl.protocol}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }
                    }
                }
            }

            // A Records (IPs)
            item {
                DnsSectionCard(
                    title = "رکوردهای A (IPv4 Addresses)",
                    records = res.ipAddresses.map { DnsRecordItem("A", it, 300, null, "آدرس سرور میزبان") }
                )
            }

            // MX Records
            if (res.mxRecords.isNotEmpty()) {
                item {
                    DnsSectionCard(
                        title = "رکوردهای MX (Mail Exchange)",
                        records = res.mxRecords
                    )
                }
            }

            // TXT Records (SPF / DMARC)
            if (res.txtRecords.isNotEmpty()) {
                item {
                    DnsSectionCard(
                        title = "رکوردهای متنی TXT (SPF & DMARC)",
                        records = res.txtRecords
                    )
                }
            }

            // NS Records
            if (res.nsRecords.isNotEmpty()) {
                item {
                    DnsSectionCard(
                        title = "نیم‌سرورهای دامنه (NameServers)",
                        records = res.nsRecords
                    )
                }
            }
        }
    }
}

@Composable
private fun DnsSectionCard(title: String, records: List<DnsRecordItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))

            records.forEach { record ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(record.value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        if (record.description.isNotEmpty()) {
                            Text(record.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(record.type, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
