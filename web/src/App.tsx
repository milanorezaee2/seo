import { useEffect, useState } from "react";
import { ArrowLeftRight, BarChart3, Bot, Code2, Gauge, History, Home, LayoutDashboard, Map, Menu, Server, X } from "lucide-react";
import { ALL_TABS, TAB_TITLES, useStore, type AppTab } from "./store";
import { HomeScreen } from "./screens/HomeScreen";
import { SiteAuditScreen } from "./screens/SiteAuditScreen";
import { CoreWebVitalsScreen } from "./screens/CoreWebVitalsScreen";
import { CompetitorScreen } from "./screens/CompetitorScreen";
import { SchemaGeneratorScreen } from "./screens/SchemaGeneratorScreen";
import { SitemapGeneratorScreen } from "./screens/SitemapGeneratorScreen";
import { DnsLookupScreen } from "./screens/DnsLookupScreen";
import { RobotsTxtScreen } from "./screens/RobotsTxtScreen";
import { HistoryScreen } from "./screens/HistoryScreen";

const ICONS: Record<AppTab, typeof Home> = {
  HOME: LayoutDashboard, SITE_AUDIT: BarChart3, CORE_WEB_VITALS: Gauge, COMPETITOR: ArrowLeftRight,
  SCHEMA_GEN: Code2, SITEMAP_GEN: Map, DNS_LOOKUP: Server, ROBOTS_TXT: Bot, HISTORY: History,
};
const BOTTOM_TABS: AppTab[] = ["HOME", "SITE_AUDIT", "CORE_WEB_VITALS", "COMPETITOR", "SCHEMA_GEN"];

function Screen({ tab }: { tab: AppTab }) {
  switch (tab) {
    case "HOME": return <HomeScreen />;
    case "SITE_AUDIT": return <SiteAuditScreen />;
    case "CORE_WEB_VITALS": return <CoreWebVitalsScreen />;
    case "COMPETITOR": return <CompetitorScreen />;
    case "SCHEMA_GEN": return <SchemaGeneratorScreen />;
    case "SITEMAP_GEN": return <SitemapGeneratorScreen />;
    case "DNS_LOOKUP": return <DnsLookupScreen />;
    case "ROBOTS_TXT": return <RobotsTxtScreen />;
    case "HISTORY": return <HistoryScreen />;
  }
}

function NavList({ onPick }: { onPick?: () => void }) {
  const s = useStore();
  return (
    <nav className="nav-list">
      {ALL_TABS.map((t) => {
        const Icon = ICONS[t];
        return (
          <button key={t} className={`nav-item ${s.currentTab === t ? "active" : ""}`} onClick={() => { s.selectTab(t); onPick?.(); }}>
            <Icon size={20} />
            <span>{TAB_TITLES[t]}</span>
          </button>
        );
      })}
    </nav>
  );
}

function Brand() {
  const s = useStore();
  return (
    <div className="brand">
      <div className="brand-title">سئو مستر پرو</div>
      <div className="brand-en">SEO Master Pro Suite</div>
      <div className="brand-desc">جعبه ابزار جامع بهینه‌سازی و سئو سایت</div>
      {s.geminiEnabled !== null && (
        <div className={`ai-status ${s.geminiEnabled ? "on" : ""}`}>
          <i /> {s.geminiEnabled ? "Gemini AI فعال است" : "Gemini: حالت نمونه (بدون کلید)"}
        </div>
      )}
    </div>
  );
}

export function App() {
  const s = useStore();
  const [drawer, setDrawer] = useState(false);

  useEffect(() => {
    window.scrollTo({ top: 0 });
    document.title = s.currentTab === "HOME" ? "سئو مستر پرو | SEO Master Pro" : `${TAB_TITLES[s.currentTab]} | سئو مستر پرو`;
  }, [s.currentTab]);

  return (
    <div className="layout">
      <aside className="sidebar">
        <Brand />
        <NavList />
      </aside>

      {drawer && <div className="scrim" onClick={() => setDrawer(false)} />}
      <aside className={`drawer ${drawer ? "open" : ""}`}>
        <button className="icon-btn drawer-close" onClick={() => setDrawer(false)}><X size={20} /></button>
        <Brand />
        <NavList onPick={() => setDrawer(false)} />
      </aside>

      <div className="main">
        <header className="topbar">
          <button className="icon-btn menu-btn" onClick={() => setDrawer(true)} aria-label="منو ابزارها"><Menu size={22} /></button>
          <div className="topbar-title">
            <strong>{TAB_TITLES[s.currentTab]}</strong>
            {s.currentTab !== "HOME" && <span>SEO Master Pro</span>}
          </div>
          {s.currentTab !== "HOME" ? (
            <button className="icon-btn accent" onClick={() => s.selectTab("HOME")} aria-label="داشبورد"><Home size={21} /></button>
          ) : (
            <button className="icon-btn" onClick={() => s.selectTab("HISTORY")} aria-label="تاریخچه"><History size={21} /></button>
          )}
        </header>

        <main className="content" key={s.currentTab}>
          <Screen tab={s.currentTab} />
        </main>

        <nav className="bottom-nav">
          {BOTTOM_TABS.map((t) => {
            const Icon = ICONS[t];
            return (
              <button key={t} className={s.currentTab === t ? "active" : ""} onClick={() => s.selectTab(t)}>
                <span className="pill"><Icon size={20} /></span>
                <span className="lbl">{TAB_TITLES[t]}</span>
              </button>
            );
          })}
        </nav>
      </div>
    </div>
  );
}
