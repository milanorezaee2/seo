import { Gauge, Zap } from "lucide-react";
import { useStore } from "../store";
import { AiBox, Badge, Card, ScoreMeter, ScreenHeader, SectionTitle, Spinner, UrlForm, ratingColor, ratingLabel } from "../components";
import type { VitalMetric } from "../types";

function VitalCard({ m }: { m: VitalMetric }) {
  const c = ratingColor(m.rating);
  return (
    <Card className="vital">
      <div className="row between">
        <div>
          <strong className="acr" style={{ color: c }}>{m.acronym}</strong>
          <div className="small muted">{m.name}</div>
        </div>
        <Badge color={c}>{ratingLabel(m.rating)}</Badge>
      </div>
      <div className="vital-value" style={{ color: c }} dir="ltr">{m.value} <small>{m.unit}</small></div>
      <div className="bar"><span style={{ background: c, width: m.rating === "GOOD" ? "33%" : m.rating === "NEEDS_IMPROVEMENT" ? "66%" : "100%" }} /></div>
      <p className="small muted">{m.description}</p>
      <div className="row between tiny">
        <span style={{ color: "var(--good)" }}>محدوده ایده‌آل: {m.goodThreshold}</span>
        <span style={{ color: "var(--poor)" }}>محدوده ضعیف: {m.poorThreshold}</span>
      </div>
    </Card>
  );
}

export function CoreWebVitalsScreen() {
  const s = useStore();
  const r = s.cwvResult;
  return (
    <div className="stack">
      <ScreenHeader icon={Gauge} color="var(--cyan)" title="ارزیابی Core Web Vitals و سرعت صفحه" subtitle="بررسی معیارهای رسمی گوگل برای تجربه کاربری و سرعت بارگذاری" />
      <UrlForm value={s.cwvUrl} onChange={s.setCwvUrl} onSubmit={() => s.runCoreWebVitals(s.cwvUrl)} loading={s.isCwvLoading} placeholder="https://example.com" buttonLabel="تست سرعت" />
      {s.isCwvLoading && !r && <Card className="center"><Spinner size={28} /><p className="muted">در حال اندازه‌گیری…</p></Card>}
      {r && (
        <div className={`stack ${s.isCwvLoading ? "dim" : ""}`}>
          <Card className="score-card">
            <ScoreMeter score={r.performanceScore} label="Performance" />
            <div className="grow">
              <div className="small muted">امتیاز عملکرد (Performance Score)</div>
              <h3 dir="ltr" className="domain">{r.url}</h3>
              <p className="small muted">
                معیارها بر اساس زمان پاسخ واقعی سرور (TTFB: {r.ttfb.value}ms) تخمین زده شده‌اند — همانند منطق اپ اندروید.
              </p>
            </div>
          </Card>
          <SectionTitle>شاخص‌های حیاتی تجربه کاربری (CWV Metrics)</SectionTitle>
          <div className="vital-grid">
            {[r.lcp, r.inp, r.cls, r.fcp, r.ttfb, r.speedIndex].map((m) => <VitalCard key={m.acronym} m={m} />)}
          </div>
          <AiBox title="استراتژی بهینه‌سازی سرعت (هوش مصنوعی)" text={r.aiOptimizationTips} loading={s.isCwvLoading} />
          <SectionTitle>فرصت‌های افزایش سرعت و کاهش حجم (Diagnostics)</SectionTitle>
          {r.diagnostics.map((d, i) => (
            <Card key={i} className="stack-sm">
              <div className="row between gap">
                <strong className="row gap-sm"><Zap size={16} color="var(--avg)" />{d.title}</strong>
                <Badge color="var(--cyan)">{d.category}</Badge>
              </div>
              <div className="small" style={{ color: "var(--good)" }}>صرفه‌جویی: {d.savings}</div>
              <p className="small muted">{d.suggestion}</p>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
