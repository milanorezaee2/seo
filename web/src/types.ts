// Shared data models — ported 1:1 from app/src/main/java/com/example/data/model

export type IssueSeverity = "HIGH" | "MEDIUM" | "LOW" | "GOOD";

export interface AuditIssue {
  title: string;
  description: string;
  severity: IssueSeverity;
  recommendation: string;
}

export interface SeoAuditResult {
  url: string;
  domain: string;
  overallScore: number;
  title: string | null;
  titleLength: number;
  titleStatus: IssueSeverity;
  metaDescription: string | null;
  metaDescriptionLength: number;
  metaDescriptionStatus: IssueSeverity;
  canonicalUrl: string | null;
  canonicalStatus: IssueSeverity;
  robotsMeta: string | null;
  ogTitle: string | null;
  ogDescription: string | null;
  ogImage: string | null;
  twitterCard: string | null;
  h1List: string[];
  h2List: string[];
  h3List: string[];
  wordCount: number;
  imageCount: number;
  imagesWithoutAlt: number;
  internalLinksCount: number;
  externalLinksCount: number;
  isHttps: boolean;
  httpStatusCode: number;
  responseTimeMs: number;
  charset: string | null;
  hasViewportMeta: boolean;
  hasFavicon: boolean;
  issues: AuditIssue[];
  isFallback?: boolean;
  aiRecommendations?: string | null;
}

export type MetricRating = "GOOD" | "NEEDS_IMPROVEMENT" | "POOR";

export interface VitalMetric {
  name: string;
  acronym: string;
  value: string;
  unit: string;
  rating: MetricRating;
  description: string;
  goodThreshold: string;
  poorThreshold: string;
}

export interface VitalsDiagnostic {
  title: string;
  savings: string;
  category: string;
  suggestion: string;
}

export interface CoreWebVitalsResult {
  url: string;
  performanceScore: number;
  lcp: VitalMetric;
  inp: VitalMetric;
  cls: VitalMetric;
  fcp: VitalMetric;
  ttfb: VitalMetric;
  speedIndex: VitalMetric;
  diagnostics: VitalsDiagnostic[];
  aiOptimizationTips?: string | null;
}

export interface MetricRow {
  title: string;
  myVal: string;
  compVal: string;
  winner: number; // 1 = my site, 2 = competitor, 0 = equal
}

export interface CompetitorComparison {
  myUrl: string;
  competitorUrl: string;
  myScore: number;
  competitorScore: number;
  metricRows: MetricRow[];
  myStrengths: string[];
  competitorStrengths: string[];
  keywordOpportunities: string[];
  aiStrategyBreakdown?: string | null;
}

export interface DnsRecordItem {
  type: string;
  value: string;
  ttl: number;
  priority?: number | null;
  description: string;
}

export interface SslCertificateInfo {
  isSecure: boolean;
  issuer: string;
  subject: string;
  validFrom: string;
  validTo: string;
  daysRemaining: number;
  protocol: string;
}

export interface DnsLookupResult {
  domain: string;
  ipAddresses: string[];
  ipv6Addresses: string[];
  mxRecords: DnsRecordItem[];
  txtRecords: DnsRecordItem[];
  nsRecords: DnsRecordItem[];
  cnameRecords: DnsRecordItem[];
  soaRecord: string | null;
  serverHeaders: Record<string, string>;
  sslInfo: SslCertificateInfo | null;
  responseTimeMs: number;
  dohProvider: string;
}

export interface SitemapUrlEntry {
  id: string;
  loc: string;
  lastmod: string;
  changefreq: string;
  priority: string;
}

export interface SitemapValidationResult {
  url: string;
  isValid: boolean;
  totalUrlsFound: number;
  errors: string[];
  warnings: string[];
  parsedUrls: SitemapUrlEntry[];
}

export type SchemaType =
  | "ARTICLE" | "LOCAL_BUSINESS" | "ORGANIZATION" | "PRODUCT" | "FAQ_PAGE"
  | "BREADCRUMB" | "HOW_TO" | "EVENT" | "PERSON";

export interface FaqItem { question: string; answer: string }

export type RobotsPreset = "WORDPRESS" | "SHOPIFY" | "NEXT_JS" | "ECOMMERCE" | "ALLOW_ALL" | "DISALLOW_ALL";

export interface RobotsRule {
  id: string;
  userAgent: string;
  allows: string[];
  disallows: string[];
  crawlDelay?: number | null;
}

export interface RobotsTestResult {
  path: string;
  userAgent: string;
  isAllowed: boolean;
  matchedRule: string;
  explanation: string;
}

export type ToolType = "AUDIT" | "CWV" | "COMPETITOR" | "SCHEMA" | "SITEMAP" | "DNS" | "ROBOTS";

export interface HistoryEntity {
  id: number;
  toolType: ToolType;
  title: string;
  targetUrl: string;
  summary: string;
  score?: number | null;
  dataPayload?: string;
  timestamp: number;
}
