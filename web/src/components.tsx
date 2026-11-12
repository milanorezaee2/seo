import { useEffect, useState, type ReactNode } from "react";
import { Check, Copy, Download, Loader2, Sparkles, type LucideIcon } from "lucide-react";
import type { IssueSeverity, MetricRating } from "./types";

export const scoreColor = (s: number) => (s >= 80 ? "var(--good)" : s >= 50 ? "var(--avg)" : "var(--poor)");

export const ratingColor = (r: MetricRating) =>
  r === "GOOD" ? "var(--good)" : r === "NEEDS_IMPROVEMENT" ? "var(--avg)" : "var(--poor)";
export const ratingLabel = (r: MetricRating) =>
  r === "GOOD" ? "خوب" : r === "NEEDS_IMPROVEMENT" ? "نیاز به بهبود" : "ضعیف";

export const severityColor = (s: IssueSeverity) =>
  s === "HIGH" ? "var(--poor)" : s === "MEDIUM" ? "var(--avg)" : s === "LOW" ? "var(--cyan)" : "var(--good)";
export const severityLabel = (s: IssueSeverity) =>
  s === "HIGH" ? "بحرانی" : s === "MEDIUM" ? "متوسط" : s === "LOW" ? "جزئی" : "عالی";

const faDigits = (n: number | string) => String(n).replace(/\d/g, (d) => "۰۱۲۳۴۵۶۷۸۹"[+d]);
export { faDigits };

export function ScoreMeter({ score, size = 132, label }: { score: number; size?: number; label?: string }) {
  const [anim, setAnim] = useState(0);
  useEffect(() => {
    let raf = 0;
    const start = performance.now();
    const from = 0;
    const tick = (t: number) => {
      const p = Math.min(1, (t - start) / 900);
      const eased = 1 - Math.pow(1 - p, 3);
      setAnim(Math.round(from + (score - from) * eased));
      if (p < 1) raf = requestAnimationFrame(tick);
    };
    raf = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(raf);
  }, [score]);
  const stroke = 11;
  const r = (size - stroke) / 2;
  const c = 2 * Math.PI * r;
  const color = scoreColor(score);
  return (
    <div className="meter" style={{ width: size, height: size }}>
      <svg width={size} height={size}>
        <circle cx={size / 2} cy={size / 2} r={r} stroke="var(--border)" strokeWidth={stroke} fill="none" opacity={0.5} />
        <circle
          cx={size / 2} cy={size / 2} r={r} stroke={color} strokeWidth={stroke} fill="none" strokeLinecap="round"
          strokeDasharray={c} strokeDashoffset={c * (1 - anim / 100)}
          transform={`rotate(-90 ${size / 2} ${size / 2})`}
        />
      </svg>
      <div className="meter-center">
        <span className="meter-value" style={{ color, fontSize: size / 3.6 }}>{anim}</span>
        <span className="meter-sub">{label ?? "از ۱۰۰"}</span>
      </div>
    </div>
  );
}

export function Card({ children, className = "", style }: { children: ReactNode; className?: string; style?: React.CSSProperties }) {
  return <div className={`card ${className}`} style={style}>{children}</div>;
}

export function ScreenHeader({ icon: Icon, title, subtitle, color = "var(--emerald)" }: { icon: LucideIcon; title: string; subtitle: string; color?: string }) {
  return (
    <div className="screen-header">
      <div className="icon-box" style={{ background: `color-mix(in srgb, ${color} 18%, transparent)`, color }}>
        <Icon size={24} />
      </div>
      <div>
        <h2>{title}</h2>
        <p>{subtitle}</p>
      </div>
    </div>
  );
}

export function SectionTitle({ children, extra }: { children: ReactNode; extra?: ReactNode }) {
  return (
    <div className="section-title">
      <h3>{children}</h3>
      {extra}
    </div>
  );
}

export function Badge({ color, children }: { color: string; children: ReactNode }) {
  return <span className="badge" style={{ color, background: `color-mix(in srgb, ${color} 15%, transparent)` }}>{children}</span>;
}

export function Spinner({ size = 16 }: { size?: number }) {
  return <Loader2 size={size} className="spin" />;
}

export function UrlForm({
  value, onChange, onSubmit, loading, placeholder, buttonLabel, dir = "ltr",
}: {
  value: string; onChange: (v: string) => void; onSubmit: () => void; loading: boolean;
  placeholder: string; buttonLabel: string; dir?: "ltr" | "rtl";
}) {
  return (
    <form className="url-form" onSubmit={(e) => { e.preventDefault(); if (!loading && value.trim()) onSubmit(); }}>
      <input className="input" dir={dir} value={value} onChange={(e) => onChange(e.target.value)} placeholder={placeholder} spellCheck={false} />
      <button className="btn btn-primary" type="submit" disabled={loading || !value.trim()}>
        {loading ? <Spinner /> : null}
        {buttonLabel}
      </button>
    </form>
  );
}

function renderInline(text: string): ReactNode[] {
  const parts = text.split(/(\*\*[^*]+\*\*|`[^`]+`)/g);
  return parts.map((p, i) => {
    if (p.startsWith("**") && p.endsWith("**")) return <strong key={i}>{p.slice(2, -2)}</strong>;
    if (p.startsWith("`") && p.endsWith("`")) return <code key={i}>{p.slice(1, -1)}</code>;
    return <span key={i}>{p}</span>;
  });
}

/** Minimal markdown renderer for Gemini output (headings, bullets, bold). */
export function RichText({ text }: { text: string }) {
  const lines = text.split(/\r?\n/);
  return (
    <div className="rich">
      {lines.map((raw, i) => {
        const line = raw.trimEnd();
        if (!line.trim()) return <div key={i} className="rich-gap" />;
        const h = /^#{1,6}\s+(.*)$/.exec(line);
        if (h) return <h4 key={i}>{renderInline(h[1])}</h4>;
        const b = /^\s*(?:[-*•]|\d+[.)])\s+(.*)$/.exec(line);
        if (b) return <div key={i} className="rich-li"><span className="dot" />{renderInline(b[1])}</div>;
        return <p key={i}>{renderInline(line)}</p>;
      })}
    </div>
  );
}

export function AiBox({ title, text, loading }: { title: string; text?: string | null; loading?: boolean }) {
  return (
    <Card className="ai-box">
      <div className="ai-head">
        <Sparkles size={18} />
        <span>{title}</span>
      </div>
      {text ? <RichText text={text} /> : loading ? <div className="muted row gap"><Spinner /> در حال دریافت تحلیل هوش مصنوعی…</div> : null}
    </Card>
  );
}

function highlight(code: string, lang: "json" | "xml" | "robots"): string {
  const esc = code.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
  if (lang === "robots") {
    return esc
      .split("\n")
      .map((l) => {
        if (l.trim().startsWith("#")) return `<span class="tk-c">${l}</span>`;
        const m = /^([A-Za-z-]+):(.*)$/.exec(l);
        if (!m) return l;
        const k = m[1].toLowerCase();
        const cls = k === "disallow" ? "tk-bad" : k === "allow" ? "tk-ok" : "tk-k";
        return `<span class="${cls}">${m[1]}:</span><span class="tk-s">${m[2]}</span>`;
      })
      .join("\n");
  }
  if (lang === "xml") {
    return esc.replace(/(&lt;\/?)([\w:-]+)([^&]*?)(\/?&gt;)/g, (_m, a, tag, attrs, b) =>
      `<span class="tk-p">${a}</span><span class="tk-k">${tag}</span>${attrs.replace(/("[^"]*")/g, '<span class="tk-s">$1</span>')}<span class="tk-p">${b}</span>`);
  }
  // json inside <script>
  return esc
    .replace(/(&lt;\/?script[^&]*&gt;)/g, '<span class="tk-p">$1</span>')
    .replace(/("(?:[^"\\]|\\.)*")(\s*:)?/g, (_m, s, colon) => (colon ? `<span class="tk-k">${s}</span>${colon}` : `<span class="tk-s">${s}</span>`))
    .replace(/\b(\d+(?:\.\d+)?)\b(?![^<]*<\/span>)/g, '<span class="tk-n">$1</span>');
}

export function CodeBlock({ code, lang, filename }: { code: string; lang: "json" | "xml" | "robots"; filename?: string }) {
  const [copied, setCopied] = useState(false);
  const copy = async () => {
    try {
      await navigator.clipboard.writeText(code);
    } catch {
      const ta = document.createElement("textarea");
      ta.value = code; document.body.appendChild(ta); ta.select(); document.execCommand("copy"); ta.remove();
    }
    setCopied(true);
    setTimeout(() => setCopied(false), 1600);
  };
  const download = () => {
    const blob = new Blob([code], { type: "text/plain;charset=utf-8" });
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = filename ?? "code.txt";
    a.click();
    URL.revokeObjectURL(a.href);
  };
  return (
    <div className="code">
      <div className="code-head">
        <div className="dots"><i /><i /><i /></div>
        <span className="code-name">{filename ?? "SEO Code"}</span>
        <div className="row gap-sm">
          {filename && <button className="icon-btn" onClick={download} title="دانلود"><Download size={15} /></button>}
          <button className="icon-btn" onClick={copy} title="کپی">{copied ? <Check size={15} color="var(--good)" /> : <Copy size={15} />}</button>
        </div>
      </div>
      <pre dir="ltr"><code dangerouslySetInnerHTML={{ __html: highlight(code, lang) }} /></pre>
    </div>
  );
}

export function StatTile({ label, value, color, icon: Icon }: { label: string; value: ReactNode; color?: string; icon?: LucideIcon }) {
  return (
    <div className="stat">
      {Icon && <Icon size={16} color={color ?? "var(--muted)"} />}
      <span className="stat-value" style={{ color }}>{value}</span>
      <span className="stat-label">{label}</span>
    </div>
  );
}

export function Notice({ children, color = "var(--avg)" }: { children: ReactNode; color?: string }) {
  return <div className="notice" style={{ borderColor: `color-mix(in srgb, ${color} 40%, transparent)`, background: `color-mix(in srgb, ${color} 8%, transparent)`, color }}>{children}</div>;
}
