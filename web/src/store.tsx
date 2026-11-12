// Web port of app/src/main/java/com/example/ui/viewmodel/SeoViewModel.kt
// History is persisted in localStorage (replaces the Room database).
import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState, type ReactNode } from "react";
import { api, extractDomain } from "./api";
import type {
  CompetitorComparison, CoreWebVitalsResult, DnsLookupResult, FaqItem, HistoryEntity, MetricRow,
  RobotsPreset, RobotsRule, RobotsTestResult, SchemaType, SeoAuditResult, SitemapUrlEntry,
  SitemapValidationResult,
} from "./types";

export type AppTab =
  | "HOME" | "SITE_AUDIT" | "CORE_WEB_VITALS" | "COMPETITOR" | "SCHEMA_GEN"
  | "SITEMAP_GEN" | "DNS_LOOKUP" | "ROBOTS_TXT" | "HISTORY";

export const TAB_TITLES: Record<AppTab, string> = {
  HOME: "داشبورد",
  SITE_AUDIT: "آنالیز سایت",
  CORE_WEB_VITALS: "Core Web Vitals",
  COMPETITOR: "تحلیل رقبا",
  SCHEMA_GEN: "ساخت اسکیما",
  SITEMAP_GEN: "نقشه سایت",
  DNS_LOOKUP: "بررسی DNS",
  ROBOTS_TXT: "robots.txt",
  HISTORY: "تاریخچه",
};
export const ALL_TABS = Object.keys(TAB_TITLES) as AppTab[];

export const SCHEMA_TYPES: Record<SchemaType, { displayName: string; jsonType: string }> = {
  ARTICLE: { displayName: "مقاله (Article)", jsonType: "Article" },
  LOCAL_BUSINESS: { displayName: "کسب و کار محلی (LocalBusiness)", jsonType: "LocalBusiness" },
  ORGANIZATION: { displayName: "سازمان یا شرکت (Organization)", jsonType: "Organization" },
  PRODUCT: { displayName: "محصول و قیمت (Product)", jsonType: "Product" },
  FAQ_PAGE: { displayName: "سوالات متداول (FAQPage)", jsonType: "FAQPage" },
  BREADCRUMB: { displayName: "مسیر راهنما (BreadcrumbList)", jsonType: "BreadcrumbList" },
  HOW_TO: { displayName: "آموزش مرحله‌ای (HowTo)", jsonType: "HowTo" },
  EVENT: { displayName: "رویداد و وبینار (Event)", jsonType: "Event" },
  PERSON: { displayName: "شخص یا متخصص (Person)", jsonType: "Person" },
};

export const ROBOTS_PRESETS: Record<RobotsPreset, { title: string; description: string }> = {
  WORDPRESS: { title: "وردپرس (WordPress)", description: "مسیرهای استاندارد wp-admin, wp-includes و پیوند sitemap" },
  SHOPIFY: { title: "شاپیفای (Shopify)", description: "مسیرهای فیلتر کاتالوگ، سبد خرید، اکانت و تسویه‌حساب" },
  NEXT_JS: { title: "نکست جی‌اس (Next.js)", description: "قوانین بهینه‌سازی فایل‌های _next، api و استاتیک" },
  ECOMMERCE: { title: "فروشگاه اینترنتی جامع", description: "بستن سرچ داخلی، فیلترها، کوپن، سبد خرید و صفحات کاربر" },
  ALLOW_ALL: { title: "دسترسی کامل (Allow All)", description: "اجازه ایندکس و بررسی تمام صفحات برای همه ربات‌ها" },
  DISALLOW_ALL: { title: "مسدودسازی کامل (Staging / Private)", description: "جلوگیری از ایندکس کل سایت توسط تمام موتورهای جستجو" },
};

const uid = () => (crypto.randomUUID ? crypto.randomUUID() : Math.random().toString(36).slice(2));
const today = () => new Date().toISOString().slice(0, 10);
const rule = (userAgent: string, disallows: string[], allows: string[]): RobotsRule => ({ id: uid(), userAgent, disallows, allows });
const entry = (loc: string, changefreq: string, priority: string, lastmod: string): SitemapUrlEntry => ({ id: uid(), loc, changefreq, priority, lastmod });

const HISTORY_KEY = "seo_master_history_v1";
function loadHistory(): HistoryEntity[] {
  try {
    return JSON.parse(localStorage.getItem(HISTORY_KEY) || "[]");
  } catch {
    return [];
  }
}

function useStoreState() {
  const [currentTab, setCurrentTab] = useState<AppTab>("HOME");
  const [history, setHistory] = useState<HistoryEntity[]>(loadHistory);
  const [geminiEnabled, setGeminiEnabled] = useState<boolean | null>(null);

  useEffect(() => {
    localStorage.setItem(HISTORY_KEY, JSON.stringify(history.slice(0, 200)));
  }, [history]);

  const addHistory = useCallback((h: Omit<HistoryEntity, "id" | "timestamp">) => {
    setHistory((prev) => [{ ...h, id: Date.now() + Math.random(), timestamp: Date.now() }, ...prev]);
  }, []);
  const deleteHistoryItem = (id: number) => setHistory((p) => p.filter((x) => x.id !== id));
  const clearAllHistory = () => setHistory([]);

  // 1. Site audit
  const [auditUrl, setAuditUrl] = useState("https://google.com");
  const [isAuditLoading, setAuditLoading] = useState(false);
  const [auditResult, setAuditResult] = useState<SeoAuditResult | null>(null);
  const [auditError, setAuditError] = useState<string | null>(null);

  const runAudit = useCallback(async (rawUrl: string, save = true) => {
    setAuditLoading(true);
    setAuditUrl(rawUrl);
    setAuditError(null);
    try {
      const audit = await api.audit(rawUrl);
      setAuditResult(audit);
      const aiTip = await api.insights(
        `یک خلاصه استراتژیک سئو ۳ خطی برای این مشخصات سایت تولید کن: دامنه: ${audit.domain}, امتیاز: ${audit.overallScore}, عنوان: ${audit.title}, تعداد خطاهای پیدا شده: ${audit.issues.length}`,
      );
      setAuditResult({ ...audit, aiRecommendations: aiTip });
      if (save) addHistory({
        toolType: "AUDIT", title: `آنالیز ${audit.domain}`, targetUrl: audit.url,
        summary: `امتیاز سئو: ${audit.overallScore}/۱۰۰ - ${audit.issues.length} خطا و هشدار`, score: audit.overallScore,
      });
    } catch (e) {
      setAuditError((e as Error).message);
    } finally {
      setAuditLoading(false);
    }
  }, [addHistory]);

  // 2. Core Web Vitals
  const [cwvUrl, setCwvUrl] = useState("https://google.com");
  const [isCwvLoading, setCwvLoading] = useState(false);
  const [cwvResult, setCwvResult] = useState<CoreWebVitalsResult | null>(null);

  const runCoreWebVitals = useCallback(async (rawUrl: string, save = true) => {
    setCwvLoading(true);
    setCwvUrl(rawUrl);
    try {
      const cwv = await api.cwv(rawUrl);
      setCwvResult(cwv);
      const aiTip = await api.insights(
        `به عنوان متخصص پرفورمنس وب، ۳ پیشنهاد عملیاتی برای بهبود نمرات Core Web Vitals (LCP: ${cwv.lcp.value}s, INP: ${cwv.inp.value}ms, CLS: ${cwv.cls.value}) برای آدرس ${rawUrl} ارائه بده.`,
      );
      setCwvResult({ ...cwv, aiOptimizationTips: aiTip });
      if (save) addHistory({
        toolType: "CWV", title: `تست سرعت ${extractDomain(rawUrl)}`, targetUrl: cwv.url,
        summary: `امتیاز سرعت: ${cwv.performanceScore}/۱۰۰ - LCP: ${cwv.lcp.value}s, CLS: ${cwv.cls.value}`, score: cwv.performanceScore,
      });
    } catch { /* ignore */ } finally {
      setCwvLoading(false);
    }
  }, [addHistory]);

  // 3. Competitor analysis
  const [mySiteUrl, setMySiteUrl] = useState("https://mysite.com");
  const [competitorUrl, setCompetitorUrl] = useState("https://competitor.com");
  const [isCompetitorLoading, setCompetitorLoading] = useState(false);
  const [competitorResult, setCompetitorResult] = useState<CompetitorComparison | null>(null);

  const runCompetitorAnalysis = async () => {
    setCompetitorLoading(true);
    try {
      const [myAudit, compAudit, mySpeed, compSpeed] = await Promise.all([
        api.audit(mySiteUrl), api.audit(competitorUrl), api.cwv(mySiteUrl), api.cwv(competitorUrl),
      ]);
      const num = (s: string) => (Number.isFinite(parseFloat(s)) ? parseFloat(s) : 3.0);
      const metricRows: MetricRow[] = [
        { title: "امتیاز کل سئو", myVal: `${myAudit.overallScore}/100`, compVal: `${compAudit.overallScore}/100`, winner: myAudit.overallScore >= compAudit.overallScore ? 1 : 2 },
        { title: "طول محتوای متنی", myVal: `${myAudit.wordCount} کلمه`, compVal: `${compAudit.wordCount} کلمه`, winner: myAudit.wordCount >= compAudit.wordCount ? 1 : 2 },
        { title: "سرعت بارگذاری LCP", myVal: `${mySpeed.lcp.value}s`, compVal: `${compSpeed.lcp.value}s`, winner: num(mySpeed.lcp.value) <= num(compSpeed.lcp.value) ? 1 : 2 },
        { title: "تعداد تگ‌های H2 و H3", myVal: `${myAudit.h2List.length + myAudit.h3List.length}`, compVal: `${compAudit.h2List.length + compAudit.h3List.length}`, winner: myAudit.h2List.length >= compAudit.h2List.length ? 1 : 2 },
        { title: "تصاویر با متن Alt", myVal: `${myAudit.imageCount - myAudit.imagesWithoutAlt}/${myAudit.imageCount}`, compVal: `${compAudit.imageCount - compAudit.imagesWithoutAlt}/${compAudit.imageCount}`, winner: myAudit.imagesWithoutAlt <= compAudit.imagesWithoutAlt ? 1 : 2 },
        { title: "زمان پاسخ سرور (TTFB)", myVal: `${myAudit.responseTimeMs}ms`, compVal: `${compAudit.responseTimeMs}ms`, winner: myAudit.responseTimeMs <= compAudit.responseTimeMs ? 1 : 2 },
      ];
      const myStrengths: string[] = [];
      const compStrengths: string[] = [];
      if (myAudit.overallScore > compAudit.overallScore) myStrengths.push("امتیاز فنی بالاتر در چک‌لیست سئو"); else compStrengths.push("امتیاز بالاتر ساختار فنی و متا");
      if (myAudit.wordCount > compAudit.wordCount) myStrengths.push("حجم محتوای غنی‌تر و کلمات بیشتر"); else compStrengths.push("پوشش عمیق‌تر محتوایی در صفحه");
      if (myAudit.responseTimeMs < compAudit.responseTimeMs) myStrengths.push("پاسخگویی سریع‌تر سرور نسبت به رقیب"); else compStrengths.push("زیرساخت سرور و هاستینگ پرسرعت‌تر");

      const base: CompetitorComparison = {
        myUrl: mySiteUrl, competitorUrl, myScore: myAudit.overallScore, competitorScore: compAudit.overallScore,
        metricRows, myStrengths, competitorStrengths: compStrengths,
        keywordOpportunities: ["کلمات کلیدی دم‌دراز مرتبط", "محتوای مقایسه‌ای و بررسی محصول", "صفحات راهنمای گام به گام", "تولید ویدیو و غنی‌سازی FAQ"],
      };
      setCompetitorResult(base);
      const aiSwot = await api.insights(
        `تحلیل رقابتی سئو بین دو سایت: سایت ما: ${mySiteUrl} (امتیاز ${myAudit.overallScore}) و رقیب: ${competitorUrl} (امتیاز ${compAudit.overallScore}). استراتژی سبقت از رقیب در ۳ بند شفاف بنویس.`,
      );
      setCompetitorResult({ ...base, aiStrategyBreakdown: aiSwot });
      addHistory({
        toolType: "COMPETITOR", title: `مقایسه ${extractDomain(mySiteUrl)} با ${extractDomain(competitorUrl)}`,
        targetUrl: mySiteUrl, summary: `سایت شما: ${myAudit.overallScore} | رقیب: ${compAudit.overallScore}`, score: myAudit.overallScore,
      });
    } catch { /* ignore */ } finally {
      setCompetitorLoading(false);
    }
  };

  // 4. Schema generator
  const [selectedSchemaType, setSelectedSchemaType] = useState<SchemaType>("ARTICLE");
  const [schemaArticleTitle, setSchemaArticleTitle] = useState("راهنمای جامع سئو و بهینه‌سازی سایت در سال ۲۰۲۶");
  const [schemaArticleAuthor, setSchemaArticleAuthor] = useState("علی محمدی");
  const [schemaArticleUrl, setSchemaArticleUrl] = useState("https://example.com/seo-guide");
  const [schemaArticleImage, setSchemaArticleImage] = useState("https://example.com/images/seo-banner.jpg");
  const [schemaBusinessName, setSchemaBusinessName] = useState("آژانس دیجیتال مارکتینگ و سئو نوین");
  const [schemaBusinessPhone, setSchemaBusinessPhone] = useState("+982188888888");
  const [schemaBusinessAddress, setSchemaBusinessAddress] = useState("تهران، خیابان ولیعصر، برج سپهر");
  const [schemaFaqList, setSchemaFaqList] = useState<FaqItem[]>([
    { question: "سئو چقدر زمان می‌برد تا نتیجه دهد؟", answer: "معمولاً بین ۳ تا ۶ ماه زمان نیاز است تا تغییرات پایدار در کلمات کلیدی رقابتی نمایان شوند." },
    { question: "آیا سرعت سایت در رتبه‌بندی گوگل موثر است؟", answer: "بله، سرعت سایت و فاکتورهای Core Web Vitals از سیگنال‌های مستقیم رتبه‌بندی الگوریتم تجربه صفحه گوگل هستند." },
  ]);
  const [schemaAiPrompt, setSchemaAiPrompt] = useState("");
  const [isSchemaAiLoading, setSchemaAiLoading] = useState(false);
  const [customGeneratedJsonLd, setCustomGeneratedJsonLd] = useState<string | null>(null);

  const selectSchemaType = (t: SchemaType) => { setSelectedSchemaType(t); setCustomGeneratedJsonLd(null); };
  const addFaqItem = (question: string, answer: string) => setSchemaFaqList((l) => [...l, { question, answer }]);
  const removeFaqItem = (i: number) => setSchemaFaqList((l) => l.filter((_, idx) => idx !== i));

  const generateAiSchema = async () => {
    if (!schemaAiPrompt.trim()) return;
    setSchemaAiLoading(true);
    try {
      const jsonLd = await api.schema(schemaAiPrompt, SCHEMA_TYPES[selectedSchemaType].jsonType);
      setCustomGeneratedJsonLd(jsonLd);
      addHistory({
        toolType: "SCHEMA", title: `اسکیما هوشمند ${SCHEMA_TYPES[selectedSchemaType].displayName}`,
        targetUrl: "AI Schema", summary: "اسکیما استاندارد JSON-LD تولید شده با هوش مصنوعی", dataPayload: jsonLd,
      });
    } catch { /* ignore */ } finally {
      setSchemaAiLoading(false);
    }
  };

  const computedJsonLd = useMemo(() => {
    if (customGeneratedJsonLd) return customGeneratedJsonLd;
    const ctx = "https://schema.org";
    let obj: Record<string, unknown>;
    switch (selectedSchemaType) {
      case "ARTICLE":
        obj = {
          "@context": ctx, "@type": "Article", headline: schemaArticleTitle, image: schemaArticleImage,
          author: { "@type": "Person", name: schemaArticleAuthor },
          publisher: { "@type": "Organization", name: "سایت سئو مستر", logo: { "@type": "ImageObject", url: "https://example.com/logo.png" } },
          datePublished: today(), dateModified: today(), mainEntityOfPage: schemaArticleUrl,
        };
        break;
      case "LOCAL_BUSINESS":
        obj = {
          "@context": ctx, "@type": "LocalBusiness", name: schemaBusinessName, image: "https://example.com/store-front.jpg",
          telephone: schemaBusinessPhone,
          address: { "@type": "PostalAddress", streetAddress: schemaBusinessAddress, addressLocality: "تهران", addressCountry: "IR" },
          openingHoursSpecification: { "@type": "OpeningHoursSpecification", dayOfWeek: ["Saturday", "Sunday", "Monday", "Tuesday", "Wednesday"], opens: "09:00", closes: "18:00" },
          priceRange: "$$$",
        };
        break;
      case "ORGANIZATION":
        obj = {
          "@context": ctx, "@type": "Organization", name: schemaBusinessName, url: "https://example.com", logo: "https://example.com/logo.png",
          contactPoint: { "@type": "ContactPoint", telephone: schemaBusinessPhone, contactType: "customer service" },
        };
        break;
      case "FAQ_PAGE":
        obj = {
          "@context": ctx, "@type": "FAQPage",
          mainEntity: schemaFaqList.map((f) => ({ "@type": "Question", name: f.question, acceptedAnswer: { "@type": "Answer", text: f.answer } })),
        };
        break;
      case "PRODUCT":
        obj = {
          "@context": ctx, "@type": "Product", name: "نرم‌افزار سئو مستر پرو", image: "https://example.com/product.jpg",
          description: "پکیج جامع ابزارهای بررسی و بهینه‌سازی موتورهای جستجو", brand: { "@type": "Brand", name: "SEO Master" },
          offers: { "@type": "Offer", url: "https://example.com/pricing", priceCurrency: "IRR", price: "25000000", availability: "https://schema.org/InStock" },
          aggregateRating: { "@type": "AggregateRating", ratingValue: "4.9", reviewCount: "128" },
        };
        break;
      case "BREADCRUMB":
        obj = {
          "@context": ctx, "@type": "BreadcrumbList",
          itemListElement: [
            { "@type": "ListItem", position: 1, name: "صفحه اصلی", item: "https://example.com" },
            { "@type": "ListItem", position: 2, name: "وبلاگ", item: "https://example.com/blog" },
            { "@type": "ListItem", position: 3, name: "آموزش جامع سئو", item: "https://example.com/blog/seo-guide" },
          ],
        };
        break;
      default:
        obj = { "@context": ctx, "@type": SCHEMA_TYPES[selectedSchemaType].jsonType, name: schemaArticleTitle || "عنوان شاخص", description: "توضیحات پیش‌فرض اسکیما" };
    }
    return `<script type="application/ld+json">\n${JSON.stringify(obj, null, 2)}\n</script>`;
  }, [customGeneratedJsonLd, selectedSchemaType, schemaArticleTitle, schemaArticleAuthor, schemaArticleUrl, schemaArticleImage, schemaBusinessName, schemaBusinessPhone, schemaBusinessAddress, schemaFaqList]);

  // 5. Sitemap
  const [sitemapEntries, setSitemapEntries] = useState<SitemapUrlEntry[]>([
    entry("https://example.com/", "daily", "1.0", "2026-08-26"),
    entry("https://example.com/blog", "daily", "0.9", "2026-08-25"),
    entry("https://example.com/services", "weekly", "0.8", "2026-08-20"),
    entry("https://example.com/about-us", "monthly", "0.6", "2026-08-15"),
    entry("https://example.com/contact", "monthly", "0.5", "2026-08-15"),
  ]);
  const [sitemapValidationResult, setSitemapValidationResult] = useState<SitemapValidationResult | null>(null);
  const [isSitemapValidating, setSitemapValidating] = useState(false);

  const addSitemapEntry = (loc: string, freq = "weekly", priority = "0.8") =>
    setSitemapEntries((l) => [...l, entry(loc, freq, priority, today())]);
  const removeSitemapEntry = (id: string) => setSitemapEntries((l) => l.filter((e) => e.id !== id));
  const validateRemoteSitemap = async (url: string) => {
    setSitemapValidating(true);
    try {
      const res = await api.sitemap(url);
      setSitemapValidationResult(res);
      if (res.parsedUrls.length) setSitemapEntries(res.parsedUrls);
      addHistory({
        toolType: "SITEMAP", title: `بررسی نقشه سایت ${extractDomain(url)}`, targetUrl: url,
        summary: `${res.totalUrlsFound} آدرس یافت شد - ${res.isValid ? "معتبر" : "دارای خطا"}`,
      });
    } catch { /* ignore */ } finally {
      setSitemapValidating(false);
    }
  };
  const computedSitemapXml = useMemo(() => {
    const esc = (s: string) => s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
    const urls = sitemapEntries.map((e) => `  <url>
    <loc>${esc(e.loc)}</loc>
    <lastmod>${e.lastmod || today()}</lastmod>
    <changefreq>${e.changefreq}</changefreq>
    <priority>${e.priority}</priority>
  </url>`).join("\n");
    return `<?xml version="1.0" encoding="UTF-8"?>\n<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n${urls}\n</urlset>`;
  }, [sitemapEntries]);

  // 6. DNS
  const [dnsDomain, setDnsDomain] = useState("google.com");
  const [isDnsLoading, setDnsLoading] = useState(false);
  const [dnsResult, setDnsResult] = useState<DnsLookupResult | null>(null);
  const runDnsLookup = useCallback(async (raw: string, save = true) => {
    setDnsLoading(true);
    setDnsDomain(raw);
    try {
      const res = await api.dns(raw);
      setDnsResult(res);
      if (save) addHistory({
        toolType: "DNS", title: `بررسی DNS دامنه ${res.domain}`, targetUrl: res.domain,
        summary: `IP: ${res.ipAddresses[0] ?? "-"} | SSL: ${res.sslInfo?.isSecure ? "فعال" : "نامعتبر"}`,
      });
    } catch { /* ignore */ } finally {
      setDnsLoading(false);
    }
  }, [addHistory]);

  // 7. robots.txt
  const [robotsPreset, setRobotsPreset] = useState<RobotsPreset>("WORDPRESS");
  const [robotsRules, setRobotsRules] = useState<RobotsRule[]>([
    rule("*", ["/wp-admin/", "/wp-includes/", "/cart/", "/checkout/", "/search/"], ["/wp-admin/admin-ajax.php", "/"]),
    rule("Googlebot-Image", [], ["/wp-content/uploads/"]),
  ]);
  const [robotsSitemaps, setRobotsSitemaps] = useState<string[]>(["https://example.com/sitemap_index.xml"]);
  const [robotsTestPath, setRobotsTestPath] = useState("/wp-admin/settings.php");
  const [robotsTestResult, setRobotsTestResult] = useState<RobotsTestResult | null>(null);

  const selectRobotsPreset = (p: RobotsPreset) => {
    setRobotsPreset(p);
    setRobotsTestResult(null);
    switch (p) {
      case "WORDPRESS":
        setRobotsRules([rule("*", ["/wp-admin/", "/wp-includes/", "/cart/", "/checkout/", "/search/"], ["/wp-admin/admin-ajax.php", "/wp-content/uploads/"])]);
        setRobotsSitemaps(["https://example.com/sitemap_index.xml"]); break;
      case "SHOPIFY":
        setRobotsRules([rule("*", ["/admin", "/cart", "/orders", "/checkout", "/collections/*+*", "/account"], ["/collections", "/products"])]);
        setRobotsSitemaps(["https://example.com/sitemap.xml"]); break;
      case "NEXT_JS":
        setRobotsRules([rule("*", ["/_next/", "/api/", "/admin/"], ["/_next/static/", "/"])]);
        setRobotsSitemaps(["https://example.com/sitemap.xml"]); break;
      case "ECOMMERCE":
        setRobotsRules([rule("*", ["/cart/", "/checkout/", "/profile/", "/admin/", "/filter?", "/*?sort="], ["/catalog/", "/products/"])]);
        setRobotsSitemaps(["https://example.com/sitemap.xml"]); break;
      case "ALLOW_ALL":
        setRobotsRules([rule("*", [], ["/"])]);
        setRobotsSitemaps(["https://example.com/sitemap.xml"]); break;
      case "DISALLOW_ALL":
        setRobotsRules([rule("*", ["/"], [])]);
        setRobotsSitemaps([]); break;
    }
  };
  const addRobotsDisallow = (ruleIndex: number, path: string) =>
    setRobotsRules((rs) => rs.map((r, i) => (i === ruleIndex ? { ...r, disallows: [...r.disallows, path] } : r)));
  const removeRobotsPath = (ruleIndex: number, kind: "allows" | "disallows", path: string) =>
    setRobotsRules((rs) => rs.map((r, i) => (i === ruleIndex ? { ...r, [kind]: r[kind].filter((x) => x !== path) } : r)));

  // Google robots.txt semantics: the most specific (longest) matching rule wins; Allow wins ties.
  // Supports "*" wildcards and "$" end anchors.
  const testRobotsPath = () => {
    const path = robotsTestPath.trim() || "/";
    const toRegex = (pattern: string) => {
      const anchored = pattern.endsWith("$");
      const body = (anchored ? pattern.slice(0, -1) : pattern)
        .split("*").map((x) => x.replace(/[.+?^${}()|[\]\\]/g, "\\$&")).join(".*");
      return new RegExp(`^${body}${anchored ? "$" : ""}`);
    };
    const group = robotsRules.find((r) => r.userAgent.toLowerCase() === "googlebot") ?? robotsRules.find((r) => r.userAgent === "*");
    let best: { kind: "Allow" | "Disallow"; pattern: string } | null = null;
    for (const [kind, list] of [["Allow", group?.allows ?? []], ["Disallow", group?.disallows ?? []]] as const) {
      for (const pattern of list) {
        if (!pattern || !toRegex(pattern).test(path)) continue;
        if (!best || pattern.length > best.pattern.length || (pattern.length === best.pattern.length && kind === "Allow")) {
          best = { kind, pattern };
        }
      }
    }
    const isBlocked = best?.kind === "Disallow";
    const matchedRule = best ? `${best.kind}: ${best.pattern}` : "Default: Allow";
    setRobotsTestResult({
      path, userAgent: `Googlebot (${group?.userAgent ?? "*"})`, isAllowed: !isBlocked, matchedRule,
      explanation: !isBlocked ? "ربات‌های جستجو اجازه خزیدن و ایندکس این آدرس را دارند." : `این مسیر توسط قانون ${matchedRule} برای موتورهای جستجو مسدود شده است.`,
    });
  };

  const computedRobotsTxt = useMemo(() => {
    let s = "# robots.txt generated by SEO Master Pro\n\n";
    for (const r of robotsRules) {
      s += `User-agent: ${r.userAgent}\n`;
      for (const a of r.allows) s += `Allow: ${a}\n`;
      for (const d of r.disallows) s += `Disallow: ${d}\n`;
      if (r.crawlDelay != null) s += `Crawl-delay: ${r.crawlDelay}\n`;
      s += "\n";
    }
    for (const sm of robotsSitemaps) s += `Sitemap: ${sm}\n`;
    return s.trim();
  }, [robotsRules, robotsSitemaps]);

  // Init: like the Android ViewModel, auto-run sample analyses on startup (without polluting history)
  const didInit = useRef(false);
  useEffect(() => {
    if (didInit.current) return;
    didInit.current = true;
    api.health().then((h) => setGeminiEnabled(h.gemini)).catch(() => setGeminiEnabled(false));
    runAudit("https://google.com", false);
    runDnsLookup("google.com", false);
    runCoreWebVitals("https://google.com", false);
  }, [runAudit, runDnsLookup, runCoreWebVitals]);

  return {
    currentTab, selectTab: setCurrentTab, history, deleteHistoryItem, clearAllHistory, geminiEnabled,
    auditUrl, setAuditUrl, isAuditLoading, auditResult, auditError, runAudit,
    cwvUrl, setCwvUrl, isCwvLoading, cwvResult, runCoreWebVitals,
    mySiteUrl, setMySiteUrl, competitorUrl, setCompetitorUrl, isCompetitorLoading, competitorResult, runCompetitorAnalysis,
    selectedSchemaType, selectSchemaType, schemaArticleTitle, setSchemaArticleTitle, schemaArticleAuthor, setSchemaArticleAuthor,
    schemaArticleUrl, setSchemaArticleUrl, schemaArticleImage, setSchemaArticleImage, schemaBusinessName, setSchemaBusinessName,
    schemaBusinessPhone, setSchemaBusinessPhone, schemaBusinessAddress, setSchemaBusinessAddress, schemaFaqList, addFaqItem, removeFaqItem,
    schemaAiPrompt, setSchemaAiPrompt, isSchemaAiLoading, generateAiSchema, computedJsonLd, customGeneratedJsonLd, setCustomGeneratedJsonLd,
    sitemapEntries, addSitemapEntry, removeSitemapEntry, sitemapValidationResult, isSitemapValidating, validateRemoteSitemap, computedSitemapXml,
    dnsDomain, setDnsDomain, isDnsLoading, dnsResult, runDnsLookup,
    robotsPreset, selectRobotsPreset, robotsRules, addRobotsDisallow, removeRobotsPath, robotsSitemaps, robotsTestPath, setRobotsTestPath,
    robotsTestResult, testRobotsPath, computedRobotsTxt,
  };
}

export type Store = ReturnType<typeof useStoreState>;
const Ctx = createContext<Store | null>(null);

export function StoreProvider({ children }: { children: ReactNode }) {
  const store = useStoreState();
  return <Ctx.Provider value={store}>{children}</Ctx.Provider>;
}

export function useStore(): Store {
  const s = useContext(Ctx);
  if (!s) throw new Error("useStore outside provider");
  return s;
}
