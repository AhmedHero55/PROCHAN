# ✅ احفظ جميع أصناف Tachiyomi API
-keep class eu.kanade.tachiyomi.** { *; }

# ✅ احفظ موديلات المصدر (SManga, SChapter, Page)
-keep class eu.kanade.tachiyomi.source.model.SManga { *; }
-keep class eu.kanade.tachiyomi.source.model.SChapter { *; }
-keep class eu.kanade.tachiyomi.source.model.Page { *; }

# ✅ احفظ مكتبات Kotlin
-keep class kotlin.** { *; }

# ✅ احفظ مكتبات OkHttp
-keep class okhttp3.** { *; }

# ✅ احفظ مكتبات Jsoup
-keep class org.jsoup.** { *; }

# ✅ تجنب حذف أي كائنات مستخدمة في الـ extension
-keepclassmembers class * {
    @eu.kanade.tachiyomi.annotations.* <methods>;
}
