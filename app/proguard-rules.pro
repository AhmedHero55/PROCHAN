# ✅ احتفظ بكلاس Prochan حتى لا يتم حذفه أو تشويهه
-keep class ar.prochan.Prochan { *; }

# ✅ احتفظ بجميع الكلاسات الخاصة بـ Tachiyomi Source API
-keep class eu.kanade.tachiyomi.source.** { *; }

# ✅ احتفظ بجميع الكلاسات الخاصة بـ Models (SManga, SChapter, Page)
-keep class eu.kanade.tachiyomi.source.model.** { *; }

# ✅ احتفظ بجميع الكلاسات الخاصة بـ ParsedHttpSource
-keep class eu.kanade.tachiyomi.source.online.ParsedHttpSource { *; }

# ✅ احتفظ بجميع الكلاسات الخاصة بـ okhttp و jsoup
-keep class okhttp3.** { *; }
-keep class org.jsoup.** { *; }

# ✅ لا تحذف أي دوال أو حقول مشروطة بـ @Keep
-keep @androidx.annotation.Keep class * { *; }

# ✅ احتفظ بجميع الـ constructors العامة
-keepclassmembers class * {
    public <init>(...);
}
