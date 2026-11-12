import { ArrowLeftRight, BarChart3, Bot, Code2, Gauge, History, Map, Server, Trash2 } from "lucide-react";
import { useStore, type AppTab } from "../store";
import { Card, ScreenHeader, scoreColor } from "../components";
import type { ToolType } from "../types";

const META: Record<ToolType, { icon: typeof History; color: string; label: string; tab: AppTab }> = {
  AUDIT: { icon: BarChart3, color: "#10B981", label: "آنالیز سایت", tab: "SITE_AUDIT" },
  CWV: { icon: Gauge, color: "#22D3EE", label: "Core Web Vitals", tab: "CORE_WEB_VITALS" },
  COMPETITOR: { icon: ArrowLeftRight, color: "#818CF8", label: "تحلیل رقبا", tab: "COMPETITOR" },
  SCHEMA: { icon: Code2, color: "#F59E0B", label: "اسکیما", tab: "SCHEMA_GEN" },
  SITEMAP: { icon: Map, color: "#38BDF8", label: "نقشه سایت", tab: "SITEMAP_GEN" },
  DNS: { icon: Server, color: "#A855F7", label: "DNS", tab: "DNS_LOOKUP" },
  ROBOTS: { icon: Bot, color: "#EC4899", label: "robots.txt", tab: "ROBOTS_TXT" },
};

export function HistoryScreen() {
  const s = useStore();
  return (
    <div className="stack">
      <ScreenHeader icon={History} title="تاریخچه بررسی‌ها و گزارش‌ها" subtitle="تمام آنالیزهای ذخیره شده در حافظه محلی مرورگر (LocalStorage)" />
      {s.history.length > 0 && (
        <div className="row end">
          <button className="btn btn-danger-ghost" onClick={() => { if (confirm("همه تاریخچه پاک شود؟")) s.clearAllHistory(); }}>
            <Trash2 size={15} /> پاک کردن همه
          </button>
        </div>
      )}
      {s.history.length === 0 && (
        <Card className="center">
          <History size={42} className="muted" />
          <p className="muted">هنوز هیچ آنالیزی ذخیره نشده است.</p>
        </Card>
      )}
      {s.history.map((h) => {
        const m = META[h.toolType];
        return (
          <Card key={h.id} className="history-item">
            <button className="icon-box sm" style={{ background: `${m.color}2e`, color: m.color }} onClick={() => s.selectTab(m.tab)} title={m.label}>
              <m.icon size={20} />
            </button>
            <div className="grow">
              <strong>{h.title}</strong>
              <div className="small muted">{h.summary}</div>
              <div className="tiny muted">{new Date(h.timestamp).toLocaleString("fa-IR")} • <span dir="ltr">{h.targetUrl}</span></div>
            </div>
            {h.score != null && <span className="score-circle" style={{ color: scoreColor(h.score), borderColor: scoreColor(h.score) }}>{h.score}</span>}
            <button className="icon-btn danger" onClick={() => s.deleteHistoryItem(h.id)}><Trash2 size={16} /></button>
          </Card>
        );
      })}
    </div>
  );
}
