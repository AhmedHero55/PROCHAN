# ✅ احتفظ بكلاس Prochan كاملاً مع كل الدوال
-keep class ar.prochan.Prochan { *; }

# ✅ احتفظ بجميع الكلاسات الخاصة بـ Tachiyomi Source API
-keep class eu.kanade.tachiyomi.source.** { *; }

# ✅ احتفظ بجميع الكلاسات الخاصة بـ Models (SManga, SChapter, Page, FilterList)
-keep class eu.kanade.tachiyomi.source.model.** { *; }

# ✅ احتفظ بجميع الكلاسات الخاصة بـ ParsedHttpSource و HttpSource
-keep class eu.kanade.tachiyomi.source.online.ParsedHttpSource { *; }
-keep class eu.kanade.tachiyomi.source.online.HttpSource { *; }

# ✅ احتفظ بجميع الكلاسات الخاصة بـ okhttp و jsoup
-keep class okhttp3.** { *; }
-keep class org.jsoup.** { *; }

# ✅ لا تحذف أي دوال أو حقول مشروطة بـ @Keep
-keep @androidx.annotation.Keep class * { *; }

# ✅ احتفظ بجميع الـ constructors العامة
-keepclassmembers class * {
    public <init>(...);
}

# ✅ تأكد من عدم حذف أي دوال override (مثل searchMangaRequest و chapterPageParse)
-keepclassmembers class ar.prochan.Prochan {
    public *;
}
