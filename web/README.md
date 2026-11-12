# SEO Master Pro — Web

Web port of the Android app in `../app`, with the same 7 tools + history:

- **Site Audit** – live HTML crawl (title, meta, canonical, OG, headings, images/alt, links, viewport, word count, HTTPS, TTFB) with the same scoring rules as the app
- **Core Web Vitals** – estimated LCP / INP / CLS / FCP / TTFB / SI from real server latency (same formula as the app)
- **Competitor Analysis** – side-by-side audit + speed comparison
- **Schema Generator** – JSON-LD for Article, LocalBusiness, Organization, Product, FAQ, Breadcrumb… plus AI generation
- **Sitemap** – builder, XML export and live sitemap validator
- **DNS & SSL** – A/AAAA/MX/TXT(SPF/DMARC)/NS/CNAME/SOA, TLS certificate, HTTP headers
- **robots.txt** – presets, editor, Google-style path tester (longest match wins, `*` and `$` supported), live fetch
- **History** – saved in the browser's localStorage (replaces Room)

## Architecture

- `server/` – Express API (Node 22+). Does all outbound fetching, DNS and TLS checks server-side (no CORS issues) and proxies Gemini so the API key never reaches the browser.
- `src/` – React + TypeScript UI (RTL, Persian, dark theme), built with Vite.

## Run

```bash
npm install
npm run dev                 # dev server with Vite middleware on :3000
npm run build && npm start  # production
```

Environment (`web/.env` or repo-root `.env`):

| Variable | Default | |
|---|---|---|
| `GEMINI_API_KEY` | – | Enables real Gemini answers; without it, built-in sample tips are returned (same as the app) |
| `GEMINI_MODEL` | `gemini-3.5-flash` | Model name |
| `PORT` / `HOST` | `3000` / `0.0.0.0` | |

When a target site can't be reached, the server returns the same fallback/sample data as the Android app, and the UI marks it clearly.
