import { useState } from "react";
import { Code2, Plus, Sparkles, Trash2, X } from "lucide-react";
import { SCHEMA_TYPES, useStore } from "../store";
import { Card, CodeBlock, Notice, ScreenHeader, Spinner } from "../components";
import type { SchemaType } from "../types";

function Field({ label, value, onChange, dir = "auto" }: { label: string; value: string; onChange: (v: string) => void; dir?: string }) {
  return (
    <label className="field">
      <span className="field-label">{label}</span>
      <input className="input" dir={dir} value={value} onChange={(e) => onChange(e.target.value)} />
    </label>
  );
}

export function SchemaGeneratorScreen() {
  const s = useStore();
  const [q, setQ] = useState("");
  const [a, setA] = useState("");
  const t = s.selectedSchemaType;
  const isArticleLike = t === "ARTICLE" || t === "HOW_TO" || t === "EVENT" || t === "PERSON";
  const isBusiness = t === "LOCAL_BUSINESS" || t === "ORGANIZATION";

  return (
    <div className="stack">
      <ScreenHeader icon={Code2} color="#F59E0B" title="سازنده اسکیما و کدهای ساختاریافته (Schema JSON-LD)" subtitle="تولید کدهای استاندارد Rich Snippets برای نمایش ستاره‌ها، سوالات و مقالات در نتایج گوگل" />

      <Card className="stack-sm">
        <strong>نوع اسکیما را انتخاب کنید:</strong>
        <div className="chips">
          {(Object.keys(SCHEMA_TYPES) as SchemaType[]).map((k) => (
            <button key={k} className={`chip ${k === t ? "active" : ""}`} onClick={() => s.selectSchemaType(k)}>{SCHEMA_TYPES[k].displayName}</button>
          ))}
        </div>
      </Card>

      <Card className="stack-sm">
        <strong>مشخصات {SCHEMA_TYPES[t].displayName}:</strong>
        {isArticleLike && (
          <>
            <Field label={t === "ARTICLE" ? "عنوان مقاله (Headline)" : "عنوان / نام آیتم"} value={s.schemaArticleTitle} onChange={s.setSchemaArticleTitle} />
            {t === "ARTICLE" && (
              <>
                <Field label="نام نویسنده (Author)" value={s.schemaArticleAuthor} onChange={s.setSchemaArticleAuthor} />
                <Field label="آدرس صفحه مقاله (URL)" value={s.schemaArticleUrl} onChange={s.setSchemaArticleUrl} dir="ltr" />
                <Field label="آدرس تصویر شاخص (Image URL)" value={s.schemaArticleImage} onChange={s.setSchemaArticleImage} dir="ltr" />
              </>
            )}
          </>
        )}
        {isBusiness && (
          <>
            <Field label="نام کسب و کار / برند" value={s.schemaBusinessName} onChange={s.setSchemaBusinessName} />
            <Field label="شماره تماس" value={s.schemaBusinessPhone} onChange={s.setSchemaBusinessPhone} dir="ltr" />
            {t === "LOCAL_BUSINESS" && <Field label="آدرس فیزیکی" value={s.schemaBusinessAddress} onChange={s.setSchemaBusinessAddress} />}
          </>
        )}
        {t === "FAQ_PAGE" && (
          <div className="stack-sm">
            <span className="field-label">پرسش و پاسخ‌های متداول (FAQ):</span>
            {s.schemaFaqList.map((f, i) => (
              <div key={i} className="faq-item">
                <div className="grow">
                  <div><b>Q:</b> {f.question}</div>
                  <div className="small muted"><b>A:</b> {f.answer}</div>
                </div>
                <button className="icon-btn danger" onClick={() => s.removeFaqItem(i)}><X size={16} /></button>
              </div>
            ))}
            <input className="input" placeholder="پرسش جدید" value={q} onChange={(e) => setQ(e.target.value)} />
            <input className="input" placeholder="پاسخ" value={a} onChange={(e) => setA(e.target.value)} />
            <button className="btn btn-ghost" disabled={!q.trim() || !a.trim()} onClick={() => { s.addFaqItem(q.trim(), a.trim()); setQ(""); setA(""); }}>
              <Plus size={16} /> افزودن پرسش و پاسخ
            </button>
          </div>
        )}
        {(t === "PRODUCT" || t === "BREADCRUMB") && <div className="small muted">این نوع اسکیما با داده‌های نمونه آماده تولید می‌شود؛ برای شخصی‌سازی از تولید با هوش مصنوعی استفاده کنید.</div>}
      </Card>

      <Card className="stack-sm ai-card">
        <strong className="row gap-sm"><Sparkles size={18} color="var(--avg)" /> تولید خودکار اسکیما با هوش مصنوعی</strong>
        <span className="small muted">هر متنی یا توضیحی از صفحه خود را وارد کنید تا هوش مصنوعی اسکیما مناسب را استخراج کند:</span>
        <textarea className="input" rows={3} value={s.schemaAiPrompt} onChange={(e) => s.setSchemaAiPrompt(e.target.value)} placeholder="مثال: وبینار رایگان آموزش پیشرفته سئو در تاریخ ۱۵ شهریور..." />
        <div className="row gap-sm wrap">
          <button className="btn btn-amber" disabled={s.isSchemaAiLoading || !s.schemaAiPrompt.trim()} onClick={s.generateAiSchema}>
            {s.isSchemaAiLoading ? <Spinner /> : <Sparkles size={16} />} تولید با هوش مصنوعی
          </button>
          {s.customGeneratedJsonLd && (
            <button className="btn btn-ghost" onClick={() => s.setCustomGeneratedJsonLd(null)}><Trash2 size={15} /> بازگشت به فرم</button>
          )}
        </div>
        {s.geminiEnabled === false && <Notice>کلید GEMINI_API_KEY روی سرور تنظیم نشده؛ خروجی هوش مصنوعی به‌صورت نمونه پیش‌فرض تولید می‌شود.</Notice>}
      </Card>

      <strong>پیش‌نمایش کد استاندارد JSON-LD (آماده کپی و قرار دادن در تگ &lt;head&gt;):</strong>
      <CodeBlock code={s.computedJsonLd} lang="json" filename="schema.html" />
    </div>
  );
}
