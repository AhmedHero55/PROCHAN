# احتفظ بكلاس Prochan كاملاً
-keep class ar.prochan.Prochan { *; }

# Tachiyomi Source API
-keep class eu.kanade.tachiyomi.source.** { *; }
-keep class eu.kanade.tachiyomi.source.model.** { *; }
-keep class eu.kanade.tachiyomi.source.online.ParsedHttpSource { *; }
-keep class eu.kanade.tachiyomi.source.online.HttpSource { *; }

# الشبكات والمحللات
-keep class okhttp3.** { *; }
-keep class org.jsoup.** { *; }

# المكتبات المضافة لحل المفقودات
-keep class io.reactivex.** { *; }
-keep class kotlinx.serialization.** { *; }
-keep class uy.kohesive.injekt.** { *; }

# @Keep
-keep @androidx.annotation.Keep class * { *; }

# الـ constructors العامة
-keepclassmembers class * {
    public <init>(...);
}
