package com.example.data.model

data class DnsRecordItem(
    val type: String, // A, AAAA, MX, TXT, NS, CNAME, SOA
    val value: String,
    val ttl: Int = 300,
    val priority: Int? = null,
    val description: String = ""
)

data class SslCertificateInfo(
    val isSecure: Boolean,
    val issuer: String,
    val subject: String,
    val validFrom: String,
    val validTo: String,
    val daysRemaining: Long,
    val protocol: String
)

data class DnsLookupResult(
    val domain: String,
    val ipAddresses: List<String> = emptyList(),
    val ipv6Addresses: List<String> = emptyList(),
    val mxRecords: List<DnsRecordItem> = emptyList(),
    val txtRecords: List<DnsRecordItem> = emptyList(),
    val nsRecords: List<DnsRecordItem> = emptyList(),
    val cnameRecords: List<DnsRecordItem> = emptyList(),
    val soaRecord: String? = null,
    val serverHeaders: Map<String, String> = emptyMap(),
    val sslInfo: SslCertificateInfo? = null,
    val responseTimeMs: Long = 0,
    val dohProvider: String = "Cloudflare DoH"
)
