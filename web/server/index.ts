import express from "express";
import path from "node:path";
import fs from "node:fs";
import { fileURLToPath } from "node:url";
import {
  analyzeCoreWebVitals, auditWebsite, fetchAndValidateSitemap, fetchRobotsTxt, performDnsLookup,
} from "./seo.js";
import { generateAiSchema, generateSeoInsights, hasGeminiKey } from "./gemini.js";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
// Project root = nearest parent directory containing package.json (works for tsx and compiled output)
let ROOT = __dirname;
while (!fs.existsSync(path.join(ROOT, "package.json")) && path.dirname(ROOT) !== ROOT) ROOT = path.dirname(ROOT);
const PORT = Number(process.env.PORT) || 3000;
const HOST = process.env.HOST || "0.0.0.0";
const isProd = process.env.NODE_ENV === "production";

// Load .env (web/.env, then repo-root .env) without extra dependencies
for (const p of [path.join(ROOT, ".env"), path.join(ROOT, "..", ".env")]) {
  if (!fs.existsSync(p)) continue;
  for (const line of fs.readFileSync(p, "utf8").split(/\r?\n/)) {
    const m = /^\s*([A-Z0-9_]+)\s*=\s*(.*)\s*$/.exec(line);
    if (m && process.env[m[1]] === undefined) process.env[m[1]] = m[2].replace(/^["']|["']$/g, "");
  }
}

const app = express();
app.use(express.json({ limit: "1mb" }));

type Handler = (body: Record<string, string>) => Promise<unknown>;
const route = (p: string, fn: Handler) =>
  app.post(p, async (req, res) => {
    try {
      res.json(await fn(req.body ?? {}));
    } catch (e) {
      console.error(p, e);
      res.status(500).json({ error: (e as Error).message });
    }
  });

app.get("/api/health", (_req, res) => res.json({ ok: true, gemini: hasGeminiKey() }));
route("/api/audit", (b) => auditWebsite(b.url));
route("/api/cwv", (b) => analyzeCoreWebVitals(b.url));
route("/api/dns", (b) => performDnsLookup(b.domain));
route("/api/robots", (b) => fetchRobotsTxt(b.url));
route("/api/sitemap", (b) => fetchAndValidateSitemap(b.url));
route("/api/ai/insights", async (b) => ({ text: await generateSeoInsights(String(b.prompt ?? "")) }));
route("/api/ai/schema", async (b) => ({ jsonLd: await generateAiSchema(String(b.description ?? ""), String(b.schemaType ?? "Thing")) }));

if (isProd) {
  const dist = path.join(ROOT, "dist");
  app.use(express.static(dist));
  app.get("*", (_req, res) => res.sendFile(path.join(dist, "index.html")));
} else {
  const { createServer } = await import("vite");
  const vite = await createServer({
    root: ROOT,
    server: { middlewareMode: true, allowedHosts: true, hmr: { port: 24678 } },
    appType: "spa",
  });
  app.use(vite.middlewares);
}

app.listen(PORT, HOST, () => {
  console.log(`SEO Master Pro web running on http://${HOST}:${PORT} (${isProd ? "production" : "dev"}) — Gemini: ${hasGeminiKey() ? "enabled" : "fallback tips"}`);
});
