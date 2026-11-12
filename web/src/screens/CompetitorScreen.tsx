import { ArrowLeftRight, Trophy } from "lucide-react";
import { useStore } from "../store";
import { AiBox, Card, ScoreMeter, ScreenHeader, SectionTitle, Spinner } from "../components";

export function CompetitorScreen() {
  const s = useStore();
  const res = s.competitorResult;
  return (
    <div className="stack">
      <ScreenHeader icon={ArrowLeftRight} color="var(--indigo)" title="تحلیل رقبا و مقایسه رو در رو (Competitor Analysis)" subtitle="مقایسه امتیاز سئو، حجم محتوا، سرعت، هدینگ‌ها و کشف فرصت‌های رتبه بهتر" />
      <Card className="stack-sm">
        <label className="field-label">سایت شما:</label>
        <input className="input" dir="ltr" value={s.mySiteUrl} onChange={(e) => s.setMySiteUrl(e.target.value)} placeholder="https://yoursite.com" />
        <label className="field-label">سایت رقیب:</label>
        <input className="input" dir="ltr" value={s.competitorUrl} onChange={(e) => s.setCompetitorUrl(e.target.value)} placeholder="https://competitor.com" />
        <button className="btn btn-indigo" disabled={s.isCompetitorLoading} onClick={s.runCompetitorAnalysis}>
          {s.isCompetitorLoading ? <Spinner /> : <ArrowLeftRight size={16} />} شروع مقایسه رو در رو
        </button>
      </Card>

      {res && (
        <div className={`stack ${s.isCompetitorLoading ? "dim" : ""}`}>
          <Card className="versus">
            <div className="vs-side">
              <div className="small muted">سایت شما</div>
              <ScoreMeter score={res.myScore} size={110} />
              <div dir="ltr" className="small ellipsis">{res.myUrl}</div>
            </div>
            <div className="vs-badge">VS</div>
            <div className="vs-side">
              <div className="small muted">سایت رقیب</div>
              <ScoreMeter score={res.competitorScore} size={110} />
              <div dir="ltr" className="small ellipsis">{res.competitorUrl}</div>
            </div>
          </Card>

          <SectionTitle>جدول مقایسه پارامترهای فنی</SectionTitle>
          <Card className="table-card">
            <table className="cmp">
              <thead><tr><th>پارامتر</th><th>سایت شما</th><th>رقیب</th></tr></thead>
              <tbody>
                {res.metricRows.map((r) => (
                  <tr key={r.title}>
                    <td>{r.title}</td>
                    <td className={r.winner === 1 ? "win" : ""}>{r.winner === 1 && <Trophy size={13} />} {r.myVal}</td>
                    <td className={r.winner === 2 ? "win lose-me" : ""}>{r.winner === 2 && <Trophy size={13} />} {r.compVal}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Card>

          <div className="two-col">
            <Card className="stack-sm">
              <strong style={{ color: "var(--good)" }}>نقاط قوت شما</strong>
              {res.myStrengths.length ? res.myStrengths.map((x) => <div key={x} className="small">✓ {x}</div>) : <div className="small muted">—</div>}
            </Card>
            <Card className="stack-sm">
              <strong style={{ color: "var(--poor)" }}>نقاط قوت رقیب</strong>
              {res.competitorStrengths.length ? res.competitorStrengths.map((x) => <div key={x} className="small">⚑ {x}</div>) : <div className="small muted">—</div>}
            </Card>
          </div>

          <Card className="stack-sm">
            <strong>فرصت‌های محتوایی و کلمات کلیدی برای پیشی گرفتن</strong>
            {res.keywordOpportunities.map((o) => <div key={o} className="small">• {o}</div>)}
          </Card>

          <AiBox title="استراتژی سبقت از رقیب (تحلیل هوش مصنوعی)" text={res.aiStrategyBreakdown} loading={s.isCompetitorLoading} />
        </div>
      )}
      {!res && s.isCompetitorLoading && <Card className="center"><Spinner size={28} /><p className="muted">در حال آنالیز هر دو سایت…</p></Card>}
    </div>
  );
}
