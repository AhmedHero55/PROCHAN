# ✅ احتفظ بكلاس Prochan كاملاً مع كل الدوال
-keep class ar.prochan.Prochan { *; }

# ✅ احتفظ بجميع الكلاسات الخاصة بـ Tachiyomi Source API
-keep class eu.kanade.tachiyomi.source.** { *; }
-keep class eu.kanade.tachiyomi.source.model.** { *; }
-keep class eu.kanade.tachiyomi.source.online.ParsedHttpSource { *; }
-keep class eu.kanade.tachiyomi.source.online.HttpSource { *; }

# ✅ احتفظ بجميع الكلاسات الخاصة بـ okhttp و jsoup
-keep class okhttp3.** { *; }
-keep class org.jsoup.** { *; }

# ✅ المكتبات المضافة لحل المفقودات
-keep class io.reactivex.** { *; }
-keep class kotlinx.serialization.** { *; }
-keep class uy.kohesive.injekt.** { *; }

# ✅ لا تحذف أي دوال أو حقول مشروطة بـ @Keep
-keep @androidx.annotation.Keep class * { *; }

# ✅ احتفظ بجميع الـ constructors العامة
-keepclassmembers class * {
    public <init>(...);
}
