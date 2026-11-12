import { Globe, Lock, Mail, Server, ShieldAlert, FileText, Clock } from "lucide-react";
import { useStore } from "../store";
import { Badge, Card, Notice, ScreenHeader, SectionTitle, Spinner, StatTile, UrlForm } from "../components";
import type { DnsRecordItem } from "../types";

function RecordList({ title, icon: Icon, color, records }: { title: string; icon: typeof Globe; color: string; records: DnsRecordItem[] }) {
  if (!records.length) return null;
  return (
    <Card className="stack-sm">
      <strong className="row gap-sm"><Icon size={17} color={color} /> {title} <Badge color={color}>{records.length}</Badge></strong>
      {records.map((r, i) => (
        <div key={i} className="dns-rec">
          <div className="row between gap">
            <code dir="ltr" className="break">{r.priority != null ? `${r.priority} ` : ""}{r.value}</code>
            <span className="tiny muted" dir="ltr">TTL {r.ttl}</span>
          </div>
          <div className="tiny muted">{r.description}</div>
        </div>
      ))}
    </Card>
  );
}

export function DnsLookupScreen() {
  const s = useStore();
  const r = s.dnsResult;
  const ssl = r?.sslInfo;
  return (
    <div className="stack">
      <ScreenHeader icon={Server} color="#A855F7" title="بررسی رکوردهای DNS و گواهی SSL دامنه" subtitle="استعلام زنده رکوردهای A, MX, TXT (SPF/DMARC)، نیم‌سرورها و تاریخ اعتبار SSL" />
      <UrlForm value={s.dnsDomain} onChange={s.setDnsDomain} onSubmit={() => s.runDnsLookup(s.dnsDomain)} loading={s.isDnsLoading} placeholder="example.com" buttonLabel="بررسی DNS" />
      {s.isDnsLoading && !r && <Card className="center"><Spinner size={28} /><p className="muted">در حال استعلام…</p></Card>}
      {r && (
        <div className={`stack ${s.isDnsLoading ? "dim" : ""}`}>
          <div className="stat-grid">
            <StatTile icon={Globe} label="دامنه" value={<span dir="ltr">{r.domain}</span>} />
            <StatTile icon={Clock} label="زمان استعلام" value={`${r.responseTimeMs}ms`} />
            <StatTile icon={Server} label="منبع" value={<span className="tiny">{r.dohProvider}</span>} />
          </div>
          {r.dohProvider.startsWith("Fallback") && <Notice>DNS از سمت سرور در دسترس نبود؛ مقادیر نمونه نمایش داده می‌شوند.</Notice>}

          {ssl && (
            <Card className="stack-sm" style={{ borderColor: ssl.isSecure ? "color-mix(in srgb, var(--good) 40%, transparent)" : "color-mix(in srgb, var(--poor) 40%, transparent)" }}>
              <strong className="row gap-sm">
                {ssl.isSecure ? <Lock size={18} color="var(--good)" /> : <ShieldAlert size={18} color="var(--poor)" />}
                گواهینامه امنیتی SSL / HTTPS
                <Badge color={ssl.isSecure ? "var(--good)" : "var(--poor)"}>{ssl.isSecure ? "معتبر" : "نامعتبر / نامشخص"}</Badge>
              </strong>
              <div className="small">صادرکننده: <span dir="ltr">{ssl.issuer}</span></div>
              <div className="small">دامنه گواهی: <span dir="ltr">{ssl.subject}</span></div>
              <div className="small">اعتبار تا: {ssl.validTo} ({ssl.daysRemaining} روز باقی‌مانده)</div>
              <div className="small muted">الگوریتم رمزنگاری: <span dir="ltr">{ssl.protocol}</span></div>
            </Card>
          )}

          <SectionTitle>رکوردهای DNS</SectionTitle>
          <RecordList title="رکوردهای A (IPv4)" icon={Globe} color="var(--emerald)" records={r.ipAddresses.map((v) => ({ type: "A", value: v, ttl: 300, description: "آدرس IP نسخه ۴ سرور" }))} />
          <RecordList title="رکوردهای AAAA (IPv6)" icon={Globe} color="var(--cyan)" records={r.ipv6Addresses.map((v) => ({ type: "AAAA", value: v, ttl: 300, description: "آدرس IP نسخه ۶ سرور" }))} />
          <RecordList title="رکوردهای MX (ایمیل)" icon={Mail} color="#F59E0B" records={r.mxRecords} />
          <RecordList title="رکوردهای TXT (SPF / DMARC)" icon={FileText} color="#EC4899" records={r.txtRecords} />
          <RecordList title="نیم‌سرورها (NS)" icon={Server} color="#A855F7" records={r.nsRecords} />
          <RecordList title="رکوردهای CNAME" icon={Globe} color="var(--indigo)" records={r.cnameRecords} />
          {r.soaRecord && (
            <Card className="stack-sm">
              <strong>رکورد SOA</strong>
              <code dir="ltr" className="break small">{r.soaRecord}</code>
            </Card>
          )}

          {Object.keys(r.serverHeaders).length > 0 && (
            <>
              <SectionTitle>هدرهای پاسخ سرور (HTTP Headers)</SectionTitle>
              <Card className="headers" >
                {Object.entries(r.serverHeaders).map(([k, v]) => (
                  <div key={k} className="hdr" dir="ltr"><b>{k}:</b> <span className="break">{v}</span></div>
                ))}
              </Card>
            </>
          )}
        </div>
      )}
    </div>
  );
}
