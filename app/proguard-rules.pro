# ✅ منع حذف كلاس الإضافة الرئيسي
-keep class ar.prochan.Prochan { *; }

# ✅ منع حذف جميع الكلاسات والدوال الخاصة بمكتبة Tachiyomi Source API
-keep class eu.kanade.tachiyomi.source.** { *; }

# ✅ منع حذف جميع الكلاسات الخاصة بالنماذج (SManga, SChapter, Page, FilterList)
-keep class eu.kanade.tachiyomi.source.model.** { *; }

# ✅ منع حذف مكتبات kotlinx.serialization
-keep class kotlinx.serialization.** { *; }

# ✅ منع حذف مكتبات injekt
-keep class uy.kohesive.injekt.** { *; }

# ✅ منع حذف مكتبات RxJava
-keep class rx.** { *; }

# ✅ منع حذف مكتبات okhttp
-keep class okhttp3.** { *; }

# ✅ منع حذف مكتبات jsoup
-keep class org.jsoup.** { *; }

# ✅ منع حذف أي دوال أو كائنات داخل الكلاسات
-keepclassmembers class * {
    <init>(...);
}
