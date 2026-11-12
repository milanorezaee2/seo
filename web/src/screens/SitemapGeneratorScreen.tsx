import { useState } from "react";
import { CheckCircle2, Map, Plus, Search, Trash2, XCircle } from "lucide-react";
import { useStore } from "../store";
import { Badge, Card, CodeBlock, Notice, ScreenHeader, Spinner } from "../components";

const FREQS = ["always", "hourly", "daily", "weekly", "monthly", "yearly", "never"];

export function SitemapGeneratorScreen() {
  const s = useStore();
  const [checkUrl, setCheckUrl] = useState("https://example.com/sitemap.xml");
  const [loc, setLoc] = useState("");
  const [freq, setFreq] = useState("weekly");
  const [priority, setPriority] = useState("0.8");
  const v = s.sitemapValidationResult;

  return (
    <div className="stack">
      <ScreenHeader icon={Map} color="#38BDF8" title="سازنده و اعتبارسنج نقشه سایت (Sitemap.xml)" subtitle="ساخت فایل نقشه سایت بر اساس پروتکل استاندارد sitemaps.org و بررسی آنلاین سلامت sitemap" />

      <Card className="stack-sm">
        <strong>بررسی آنلاین سلامت نقشه سایت (Sitemap Checker):</strong>
        <form className="url-form" onSubmit={(e) => { e.preventDefault(); if (checkUrl.trim()) s.validateRemoteSitemap(checkUrl); }}>
          <input className="input" dir="ltr" value={checkUrl} onChange={(e) => setCheckUrl(e.target.value)} placeholder="https://example.com/sitemap.xml" />
          <button className="btn btn-primary" disabled={s.isSitemapValidating}>{s.isSitemapValidating ? <Spinner /> : <Search size={16} />} بررسی</button>
        </form>
        {v && (
          <div className="stack-sm">
            <div className="row gap-sm wrap">
              {v.isValid ? <Badge color="var(--good)"><CheckCircle2 size={13} /> معتبر</Badge> : <Badge color="var(--poor)"><XCircle size={13} /> دارای خطا</Badge>}
              <Badge color="var(--cyan)">{v.totalUrlsFound} آدرس</Badge>
              <span className="small muted" dir="ltr">{v.url}</span>
            </div>
            {v.errors.map((e) => <Notice key={e} color="var(--poor)">{e}</Notice>)}
            {v.warnings.map((w) => <Notice key={w}>{w}</Notice>)}
          </div>
        )}
      </Card>

      <Card className="stack-sm">
        <strong>افزودن آدرس جدید به نقشه سایت:</strong>
        <input className="input" dir="ltr" value={loc} onChange={(e) => setLoc(e.target.value)} placeholder="https://example.com/page" />
        <div className="row gap-sm">
          <label className="field grow">
            <span className="field-label">فرکانس تغییر</span>
            <select className="input" value={freq} onChange={(e) => setFreq(e.target.value)}>{FREQS.map((f) => <option key={f}>{f}</option>)}</select>
          </label>
          <label className="field grow">
            <span className="field-label">اولویت (0.1 - 1.0)</span>
            <select className="input" value={priority} onChange={(e) => setPriority(e.target.value)}>
              {["1.0", "0.9", "0.8", "0.7", "0.6", "0.5", "0.4", "0.3", "0.2", "0.1"].map((p) => <option key={p}>{p}</option>)}
            </select>
          </label>
        </div>
        <button className="btn btn-ghost" disabled={!loc.trim()} onClick={() => { s.addSitemapEntry(loc.trim(), freq, priority); setLoc(""); }}>
          <Plus size={16} /> افزودن به نقشه سایت
        </button>
      </Card>

      <Card className="stack-sm">
        <strong>آدرس‌های تعریف شده ({s.sitemapEntries.length} صفحه):</strong>
        <div className="entry-list">
          {s.sitemapEntries.map((e) => (
            <div key={e.id} className="entry">
              <div className="grow">
                <div dir="ltr" className="ellipsis">{e.loc}</div>
                <div className="tiny muted" dir="ltr">{e.changefreq} • priority {e.priority}{e.lastmod ? ` • ${e.lastmod}` : ""}</div>
              </div>
              <button className="icon-btn danger" onClick={() => s.removeSitemapEntry(e.id)}><Trash2 size={15} /></button>
            </div>
          ))}
        </div>
      </Card>

      <strong>فایل خروجی sitemap.xml (کپی در روت هاست):</strong>
      <CodeBlock code={s.computedSitemapXml} lang="xml" filename="sitemap.xml" />
    </div>
  );
}
