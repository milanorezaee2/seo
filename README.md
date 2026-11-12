# SEO Master Pro — سئو مستر پرو

ابزار جامع سئو شامل آنالیز سایت، Core Web Vitals، تحلیل رقبا، ساخت اسکیما، نقشه سایت، بررسی DNS/SSL و robots.txt — با تحلیل هوش مصنوعی Gemini.

این مخزن شامل دو نسخه است:

| نسخه | مسیر | توضیح |
|------|------|-------|
| 📱 اندروید (Kotlin + Jetpack Compose) | `app/` | اپ اصلی |
| 🌐 وب (React + Express) | `web/` | پورت کامل همان امکانات برای اجرا در مرورگر |

## 📱 نسخه اندروید

### دریافت APK آماده (GitHub Actions)
هر push به مخزن، workflow ‏`Build Android APK` را اجرا می‌کند و فایل APK دیباگ را به‌صورت Artifact می‌سازد:

1. به تب **Actions** مخزن بروید → آخرین اجرای *Build Android APK*.
2. از بخش **Artifacts** فایل `seo-master-pro-debug-apk` را دانلود و از حالت zip خارج کنید.
3. فایل `app-debug.apk` را روی گوشی نصب کنید (نصب از منابع ناشناس باید فعال باشد).

برای فعال شدن هوش مصنوعی در APK، در *Settings → Secrets and variables → Actions* یک Secret با نام `GEMINI_API_KEY` بسازید.

### ساخت محلی
نیازمندی‌ها: JDK 17+ (ترجیحاً 21)، Android SDK (platform 36.1)، Gradle 9.3.1

```bash
# یک‌بار: ساخت keystore دیباگ (در .gitignore است)
keytool -genkeypair -keystore debug.keystore -storepass android -alias androiddebugkey \
  -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US"
cp .env.example .env        # و مقدار GEMINI_API_KEY را تنظیم کنید
gradle :app:assembleDebug   # خروجی: app/build/outputs/apk/debug/app-debug.apk
```
یا پروژه را مستقیماً در Android Studio باز کنید.

## 🌐 نسخه وب

```bash
cd web
npm install
cp .env.example .env   # اختیاری: GEMINI_API_KEY
npm run dev            # حالت توسعه روی http://localhost:3000
# یا
npm run build && npm start
```
جزئیات بیشتر در [`web/README.md`](web/README.md).
