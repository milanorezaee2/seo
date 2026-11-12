import { useState } from "react";
import { Ban, Bot, CheckCircle2, Download, FlaskConical, X } from "lucide-react";
import { ROBOTS_PRESETS, useStore } from "../store";
import { api } from "../api";
import { Badge, Card, CodeBlock, Notice, ScreenHeader, Spinner } from "../components";
import type { RobotsPreset } from "../types";

export function RobotsTxtScreen() {
  const s = useStore();
  const [newPath, setNewPath] = useState("");
  const [liveUrl, setLiveUrl] = useState("google.com");
  const [live, setLive] = useState<{ domain: string; content: string; live: boolean } | null>(null);
  const [liveLoading, setLiveLoading] = useState(false);
  const t = s.robotsTestResult;

  const fetchLive = async () => {
    setLiveLoading(true);
    try { setLive(await api.robots(liveUrl)); } finally { setLiveLoading(false); }
  };

  return (
    <div className="stack">
      <ScreenHeader icon={Bot} color="#EC4899" title="سازنده و آزمایشگر فایل robots.txt" subtitle="تعیین دسترسی خزنده‌های گوگل، بینگ و ربات‌های هوش مصنوعی + تست زنده مسدود بودن آدرس‌ها" />

      <Card className="stack-sm">
        <strong>قالب‌های آماده سیستم‌های مدیریت محتوا:</strong>
        <div className="preset-grid">
          {(Object.keys(ROBOTS_PRESETS) as RobotsPreset[]).map((p) => (
            <button key={p} className={`preset ${p === s.robotsPreset ? "active" : ""}`} onClick={() => s.selectRobotsPreset(p)}>
              <strong>{ROBOTS_PRESETS[p].title}</strong>
              <span className="tiny muted">{ROBOTS_PRESETS[p].description}</span>
            </button>
          ))}
        </div>
      </Card>

      <Card className="stack-sm">
        <strong>قوانین فعلی:</strong>
        {s.robotsRules.map((r, ri) => (
          <div key={r.id} className="rule">
            <div className="small"><b>User-agent:</b> <code dir="ltr">{r.userAgent}</code></div>
            <div className="chips">
              {r.allows.map((a) => <span key={`a${a}`} className="path-chip ok" dir="ltr">Allow: {a}<button onClick={() => s.removeRobotsPath(ri, "allows", a)}><X size={12} /></button></span>)}
              {r.disallows.map((d) => <span key={`d${d}`} className="path-chip bad" dir="ltr">Disallow: {d}<button onClick={() => s.removeRobotsPath(ri, "disallows", d)}><X size={12} /></button></span>)}
            </div>
          </div>
        ))}
        <span className="field-label">افزودن دستور مسدودسازی (Disallow):</span>
        <form className="url-form" onSubmit={(e) => { e.preventDefault(); if (newPath.trim()) { s.addRobotsDisallow(0, newPath.trim()); setNewPath(""); } }}>
          <input className="input" dir="ltr" value={newPath} onChange={(e) => setNewPath(e.target.value)} placeholder="/secret-folder/" />
          <button className="btn btn-pink" disabled={!newPath.trim()}><Ban size={16} /> بستن مسیر</button>
        </form>
      </Card>

      <Card className="stack-sm">
        <strong>تست زنده دسترسی مسیر در robots.txt:</strong>
        <form className="url-form" onSubmit={(e) => { e.preventDefault(); s.testRobotsPath(); }}>
          <input className="input" dir="ltr" value={s.robotsTestPath} onChange={(e) => s.setRobotsTestPath(e.target.value)} placeholder="/wp-admin/settings.php" />
          <button className="btn btn-primary"><FlaskConical size={16} /> تست مسیر</button>
        </form>
        {t && (
          <Notice color={t.isAllowed ? "var(--good)" : "var(--poor)"}>
            <div className="row gap-sm">
              {t.isAllowed ? <CheckCircle2 size={18} /> : <Ban size={18} />}
              <b>{t.isAllowed ? "مجاز (Allowed)" : "مسدود (Blocked)"}</b>
              <Badge color={t.isAllowed ? "var(--good)" : "var(--poor)"}><span dir="ltr">{t.matchedRule}</span></Badge>
            </div>
            <div className="small" style={{ color: "var(--text)" }}>{t.explanation}</div>
            <div className="tiny muted" dir="ltr">{t.userAgent} → {t.path}</div>
          </Notice>
        )}
      </Card>

      <strong>کد کامل robots.txt (قرار دادن در ریشه سایت):</strong>
      <CodeBlock code={s.computedRobotsTxt} lang="robots" filename="robots.txt" />

      <Card className="stack-sm">
        <strong>مشاهده robots.txt زنده یک سایت:</strong>
        <form className="url-form" onSubmit={(e) => { e.preventDefault(); if (liveUrl.trim()) fetchLive(); }}>
          <input className="input" dir="ltr" value={liveUrl} onChange={(e) => setLiveUrl(e.target.value)} placeholder="example.com" />
          <button className="btn btn-ghost" disabled={liveLoading}>{liveLoading ? <Spinner /> : <Download size={16} />} دریافت</button>
        </form>
        {live && !live.live && <Notice>سایت در دسترس نبود؛ قالب پیش‌فرض نمایش داده می‌شود.</Notice>}
      </Card>
      {live && <CodeBlock code={live.content} lang="robots" filename={`${live.domain}-robots.txt`} />}
    </div>
  );
}
