import { useState } from "react";
import { Activity, ArrowLeftRight, BarChart3, ChevronLeft, Code2, Gauge, Map, Search, Server, Bot } from "lucide-react";
import { useStore, type AppTab } from "../store";
import { Badge, scoreColor } from "../components";

export const TOOLS: { tab: AppTab; title: string; enTitle: string; description: string; icon: typeof Search; color: string; tag: string }[] = [
  { tab: "SITE_AUDIT", title: "آنالیز سایت", enTitle: "Site Audit & SEO Check", description: "بررسی تگ‌های متا، هدینگ‌ها، تصاویر، محتوا و خطاهای فنی سئو", icon: BarChart3, color: "#10B981", tag: "ابزار جامع" },
  { tab: "CORE_WEB_VITALS", title: "امتیاز Core Web Vitals", enTitle: "Page Speed & Vitals", description: "محاسبه فاکتورهای LCP، INP، CLS، TTFB و راهکارهای افزایش سرعت", icon: Gauge, color: "#22D3EE", tag: "سرعت و پرفورمنس" },
  { tab: "COMPETITOR", title: "ابزار تحلیل رقبا", enTitle: "Competitor Analysis", description: "مقایسه رو در روی دو سایت، کشف شکاف کلمات کلیدی و تحلیل SWOT", icon: ArrowLeftRight, color: "#818CF8", tag: "مقایسه هوشمند" },
  { tab: "SCHEMA_GEN", title: "ساخت اسکیما", enTitle: "Schema Markup Generator", description: "تولید استانداردهای JSON-LD برای مقالات، محصولات، سوالات متداول و مشاغل", icon: Code2, color: "#F59E0B", tag: "ریچ اسنیپت" },
  { tab: "SITEMAP_GEN", title: "ساخت نقشه سایت", enTitle: "Sitemap.xml Generator", description: "تولید فایل XML نقشه سایت با اولویت‌بندی، تاریخ و اعتبارسنجی آنلاین", icon: Map, color: "#38BDF8", tag: "ایندکس گوگل" },
  { tab: "DNS_LOOKUP", title: "بررسی DNS دامنه", enTitle: "DNS & SSL Inspector", description: "بررسی رکوردهای A, MX, TXT (SPF/DMARC)، گواهی SSL و نیم‌سرورها", icon: Server, color: "#A855F7", tag: "امنیت و شبکه" },
  { tab: "ROBOTS_TXT", title: "ساخت robots.txt", enTitle: "Robots.txt Builder & Tester", description: "تولید و تست آنلاین فایل قوانین دسترسی ربات‌های جستجو و هوش مصنوعی", icon: Bot, color: "#EC4899", tag: "کنترل خزنده‌ها" },
];

export function HomeScreen() {
  const s = useStore();
  const [quickUrl, setQuickUrl] = useState("https://");

  return (
    <div className="stack">
      <div className="hero">
        <div className="row between">
          <div>
            <h1>جعبه ابزار سئو حرفه‌ای</h1>
            <div className="hero-sub">SEO Master Pro Suite • AI-Powered</div>
          </div>
          <div className="hero-icon"><Search size={24} /></div>
        </div>
        <p className="muted">
          آنالیز جامع وب‌سایت، تست Core Web Vitals، تحلیل رقبا، تولید Schema و بررسی فنی زیرساخت سئو با قدرت هوش مصنوعی Gemini
        </p>
        <form
          className="url-form"
          onSubmit={(e) => {
            e.preventDefault();
            if (quickUrl.trim() && quickUrl.trim() !== "https://") {
              s.runAudit(quickUrl);
              s.selectTab("SITE_AUDIT");
            }
          }}
        >
          <input className="input" dir="ltr" value={quickUrl} onChange={(e) => setQuickUrl(e.target.value)} placeholder="https://yourwebsite.com" />
          <button className="btn btn-primary" type="submit">آنالیز سریع</button>
        </form>
      </div>

      <div className="section-title">
        <h3>ابزارهای ۷ گانه سئو</h3>
        <span className="accent-label">۷ ابزار تخصصی</span>
      </div>

      <div className="tool-grid">
        {TOOLS.map((t) => (
          <button key={t.tab} className="tool-card" onClick={() => s.selectTab(t.tab)}>
            <div className="icon-box" style={{ background: `${t.color}2e`, color: t.color }}><t.icon size={26} /></div>
            <div className="tool-body">
              <div className="row gap-sm wrap">
                <strong>{t.title}</strong>
                <Badge color={t.color}>{t.tag}</Badge>
              </div>
              <div className="en">{t.enTitle}</div>
              <div className="desc">{t.description}</div>
            </div>
            <ChevronLeft size={22} className="muted" />
          </button>
        ))}
      </div>

      {s.history.length > 0 && (
        <>
          <div className="section-title">
            <h3>گزارش‌های اخیر</h3>
            <button className="link-btn" onClick={() => s.selectTab("HISTORY")}>مشاهده همه</button>
          </div>
          {s.history.slice(0, 3).map((h) => (
            <div key={h.id} className="history-mini">
              <Activity size={18} className="muted" />
              <div className="grow">
                <strong>{h.title}</strong>
                <div className="small muted">{h.summary}</div>
              </div>
              {h.score != null && (
                <span className="score-pill" style={{ color: scoreColor(h.score), background: `color-mix(in srgb, ${scoreColor(h.score)} 15%, transparent)` }}>
                  {h.score}/100
                </span>
              )}
            </div>
          ))}
        </>
      )}
    </div>
  );
}
