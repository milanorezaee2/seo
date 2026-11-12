// Server-side port of app/src/main/java/com/example/data/remote/GeminiService.kt
// The API key stays on the server (env GEMINI_API_KEY) and is never sent to the browser.

const MODEL_NAME = process.env.GEMINI_MODEL || "gemini-3.5-flash";
const BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models";

const SYSTEM_INSTRUCTION =
  "شما یک متخصص ارشد و استراتژیست ارشد SEO و تحلیلگر فنی وب هستید. پاسخ‌های خود را به زبان فارسی روان، حرفه‌ای، ساختاریافته با نکات کاربردی و چک‌لیست عملیاتی ارائه دهید.";

const NO_KEY_TIP =
  "راهکار هوش مصنوعی: برای سئوی برتر، عنوان (Title) را بین ۵۰ تا ۶۰ کاراکتر متمرکز بر کلمه کلیدی اصلی نگه دارید، توضیحات متا را جذاب و با کال تو اکشن (CTA) تنظیم کنید، سرعت پاسخ سرور را زیر ۸۰۰ میلی‌ثانیه بیاورید و اسکیماهای مرتبط (Organization یا Article) را در تگ head درج کنید.";

const ERROR_TIP =
  "تحلیل هوش مصنوعی: بهینه‌سازی کلمات کلیدی LSI در تگ‌های H2 و H3، کاهش زمان پاسخگویی TTFB به زیر ۴۰۰ میلی‌ثانیه، و استفاده از کش سرور و شبکه توزیع محتوا (CDN) موجب جهش در رتبه‌های گوگل خواهد شد.";

export function hasGeminiKey(): boolean {
  const k = process.env.GEMINI_API_KEY;
  return !!k && k !== "MY_GEMINI_API_KEY";
}

export async function generateSeoInsights(prompt: string): Promise<string> {
  if (!hasGeminiKey()) return NO_KEY_TIP;
  try {
    const res = await fetch(`${BASE_URL}/${MODEL_NAME}:generateContent?key=${process.env.GEMINI_API_KEY}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      signal: AbortSignal.timeout(60000),
      body: JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }],
        generationConfig: { temperature: 0.7, topP: 0.95 },
        systemInstruction: { parts: [{ text: SYSTEM_INSTRUCTION }] },
      }),
    });
    if (res.ok) {
      const json = (await res.json()) as { candidates?: { content?: { parts?: { text?: string }[] } }[] };
      const text = json.candidates?.[0]?.content?.parts?.[0]?.text;
      if (text) return text.trim();
    } else {
      console.warn("[gemini] HTTP", res.status, (await res.text()).slice(0, 300));
    }
  } catch (e) {
    console.warn("[gemini] request failed:", (e as Error).message);
  }
  return ERROR_TIP;
}

export async function generateAiSchema(description: string, schemaType: string): Promise<string> {
  const prompt = `بر اساس اطلاعات زیر یک کد اسکیما استاندار معتبر JSON-LD برای نوع ${schemaType} تولید کن.
اطلاعات:
${description}

فقط و فقط کد کامل JSON-LD داخل تگ <script type="application/ld+json">...</script> را بدون هیچ توضیح اضافی تولید کن.`;
  const result = await generateSeoInsights(prompt);
  if (result.includes("<script")) {
    // strip markdown code fences if the model added them
    return result.replace(/^```[a-z]*\s*/i, "").replace(/```\s*$/, "").trim();
  }
  return `<script type="application/ld+json">
${JSON.stringify({ "@context": "https://schema.org", "@type": schemaType, name: "نمونه اسکیما تولید شده", description }, null, 2)}
</script>`;
}
