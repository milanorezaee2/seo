import type {
  CoreWebVitalsResult, DnsLookupResult, SeoAuditResult, SitemapValidationResult,
} from "./types";

async function post<T>(url: string, body: unknown): Promise<T> {
  const res = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json() as Promise<T>;
}

export const api = {
  audit: (url: string) => post<SeoAuditResult>("/api/audit", { url }),
  cwv: (url: string) => post<CoreWebVitalsResult>("/api/cwv", { url }),
  dns: (domain: string) => post<DnsLookupResult>("/api/dns", { domain }),
  sitemap: (url: string) => post<SitemapValidationResult>("/api/sitemap", { url }),
  robots: (url: string) => post<{ domain: string; content: string; live: boolean }>("/api/robots", { url }),
  insights: async (prompt: string) => (await post<{ text: string }>("/api/ai/insights", { prompt })).text,
  schema: async (description: string, schemaType: string) =>
    (await post<{ jsonLd: string }>("/api/ai/schema", { description, schemaType })).jsonLd,
  health: async () => (await fetch("/api/health")).json() as Promise<{ ok: boolean; gemini: boolean }>,
};

export function extractDomain(input: string): string {
  let t = input.trim();
  if (!/^https?:\/\//.test(t)) t = `https://${t}`;
  try {
    let h = new URL(t).hostname.toLowerCase();
    if (h.startsWith("www.")) h = h.slice(4);
    return h;
  } catch {
    return input.replace(/^https?:\/\//, "").replace("www.", "").split("/")[0];
  }
}
