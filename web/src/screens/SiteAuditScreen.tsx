import { AlertTriangle, BarChart3, CheckCircle2, Clock, FileText, Image, Link2, Lock, Smartphone, Unlock } from "lucide-react";
import { useStore } from "../store";
import {
  AiBox, Badge, Card, Notice, ScoreMeter, ScreenHeader, SectionTitle, Spinner, StatTile, UrlForm,
  scoreColor, severityColor, severityLabel,
} from "../components";
import type { IssueSeverity } from "../types";

function MetaRow({ label, value, status, extra }: { label: string; value: string | null; status?: IssueSeverity; extra?: string }) {
  return (
    <div className="meta-row">
      <div className="row between">
        <span className="meta-label">{label}</span>
        {status && <Badge color={severityColor(status)}>{severityLabel(status)}{extra ? ` • ${extra}` : ""}</Badge>}
      </div>
      <div className="meta-value" dir="auto">{value || <span className="muted">— یافت نشد —</span>}</div>
    </div>
  );
}

export function SiteAuditScreen() {
  const s = useStore();
  const res = s.auditResult;

  return (
    <div className="stack">
      <ScreenHeader icon={BarChart3} title="آنالیز و عیب‌یابی جامع سایت (Site Audit)" subtitle="بررسی تگ‌های سئو، هدینگ‌ها، متادیتا، تصاویر، لینک‌ها و سرعت" />
      <UrlForm value={s.auditUrl} onChange={s.setAuditUrl} onSubmit={() => s.runAudit(s.auditUrl)} loading={s.isAuditLoading} placeholder="https://example.com" buttonLabel="آنالیز" />

      {s.isAuditLoading && !res && <Card className="center"><Spinner size={28} /><p className="muted">در حال خزش و آنالیز صفحه…</p></Card>}
      {s.auditError && <Notice color="var(--poor)">خطا در آنالیز: {s.auditError}</Notice>}

      {res && (
        <div className={`stack ${s.isAuditLoading ? "dim" : ""}`}>
          {res.isFallback && (
            <Notice>سایت از سمت سرور در دسترس نبود؛ مطابق رفتار اپ اندروید، یک گزارش نمونه (تخمینی) نمایش داده می‌شود.</Notice>
          )}
          <Card className="score-card">
            <ScoreMeter score={res.overallScore} />
            <div className="grow">
              <div className="small muted">امتیاز کلی سئو</div>
              <h3 dir="ltr" className="domain">{res.domain}</h3>
              <div className="row gap-sm wrap">
                <Badge color={scoreColor(res.overallScore)}>{res.overallScore >= 80 ? "عالی" : res.overallScore >= 50 ? "متوسط" : "ضعیف"}</Badge>
                <Badge color="var(--cyan)">HTTP {res.httpStatusCode}</Badge>
                <Badge color={res.isHttps ? "var(--good)" : "var(--poor)"}>{res.isHttps ? "HTTPS امن" : "HTTP ناامن"}</Badge>
                <Badge color="var(--indigo)">{res.issues.length} مورد</Badge>
              </div>
            </div>
          </Card>

          <div className="stat-grid">
            <StatTile icon={Clock} label="زمان پاسخ" value={`${res.responseTimeMs}ms`} color={res.responseTimeMs > 2000 ? "var(--poor)" : "var(--good)"} />
            <StatTile icon={FileText} label="تعداد کلمات" value={res.wordCount} />
            <StatTile icon={Image} label="تصاویر بدون Alt" value={`${res.imagesWithoutAlt}/${res.imageCount}`} color={res.imagesWithoutAlt ? "var(--avg)" : "var(--good)"} />
            <StatTile icon={Link2} label="لینک داخلی / خارجی" value={`${res.internalLinksCount} / ${res.externalLinksCount}`} />
          </div>

          <AiBox title="توصیه‌های هوش مصنوعی Gemini" text={res.aiRecommendations} loading={s.isAuditLoading} />

          <SectionTitle>۱. بررسی تگ‌های اصلی متا و ساختار صفحه</SectionTitle>
          <Card className="stack-sm">
            <MetaRow label="عنوان صفحه (Title)" value={res.title} status={res.titleStatus} extra={`${res.titleLength} کاراکتر`} />
            <MetaRow label="توضیحات متا (Meta Description)" value={res.metaDescription} status={res.metaDescriptionStatus} extra={`${res.metaDescriptionLength} کاراکتر`} />
            <MetaRow label="آدرس Canonical" value={res.canonicalUrl} status={res.canonicalStatus} />
            <MetaRow label="Robots Meta" value={res.robotsMeta ?? "index, follow (پیش‌فرض)"} />
            <MetaRow label="Open Graph Title" value={res.ogTitle} />
            <MetaRow label="Open Graph Image" value={res.ogImage} />
            <MetaRow label="Twitter Card" value={res.twitterCard} />
            <div className="row gap wrap small">
              <span className="row gap-sm"><Smartphone size={15} color={res.hasViewportMeta ? "var(--good)" : "var(--poor)"} /> Viewport: {res.hasViewportMeta ? "دارد" : "ندارد"}</span>
              <span className="row gap-sm">{res.isHttps ? <Lock size={15} color="var(--good)" /> : <Unlock size={15} color="var(--poor)" />} Charset: {res.charset ?? "-"}</span>
              <span className="row gap-sm"><CheckCircle2 size={15} color={res.hasFavicon ? "var(--good)" : "var(--avg)"} /> Favicon: {res.hasFavicon ? "دارد" : "ندارد"}</span>
            </div>
          </Card>

          <SectionTitle>۲. ساختار هدینگ‌ها (Headings Hierarchy)</SectionTitle>
          <Card className="stack-sm">
            <strong>تگ‌های H1 (تعداد: {res.h1List.length})</strong>
            {res.h1List.length ? res.h1List.map((h, i) => <div key={i} className="heading-item h1" dir="auto">• {h}</div>) : <div className="muted small">هیچ H1 یافت نشد</div>}
            <strong>نمونه تگ‌های H2 و H3 (تعداد: {res.h2List.length + res.h3List.length})</strong>
            {res.h2List.map((h, i) => <div key={`2${i}`} className="heading-item" dir="auto">• H2: {h}</div>)}
            {res.h3List.map((h, i) => <div key={`3${i}`} className="heading-item h3" dir="auto">  - H3: {h}</div>)}
          </Card>

          <SectionTitle>۳. تصاویر و لینک‌های صفحه</SectionTitle>
          <div className="stat-grid">
            <StatTile label="کل تصاویر" value={res.imageCount} />
            <StatTile label="تصاویر بدون Alt" value={res.imagesWithoutAlt} color={res.imagesWithoutAlt ? "var(--poor)" : "var(--good)"} />
            <StatTile label="لینک‌های داخلی" value={res.internalLinksCount} color="var(--cyan)" />
            <StatTile label="لینک‌های خارجی" value={res.externalLinksCount} color="var(--indigo)" />
          </div>

          <SectionTitle>۴. لیست خطاها و اقدامات اصلاحی ({res.issues.length} مورد)</SectionTitle>
          {res.issues.length === 0 && <Notice color="var(--good)">هیچ مشکلی یافت نشد — عالی!</Notice>}
          {res.issues.map((it, i) => (
            <Card key={i} className="issue" style={{ borderInlineStartColor: severityColor(it.severity) }}>
              <div className="row between gap">
                <strong className="row gap-sm"><AlertTriangle size={16} color={severityColor(it.severity)} />{it.title}</strong>
                <Badge color={severityColor(it.severity)}>{severityLabel(it.severity)}</Badge>
              </div>
              <p className="muted small">{it.description}</p>
              <div className="fix">✓ راهکار: {it.recommendation}</div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
