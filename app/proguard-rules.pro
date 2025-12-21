-keep class eu.kanade.tachiyomi.extension.prochan.Prochan { *; }
-keep class eu.kanade.tachiyomi.source.** { *; }
-keep class eu.kanade.tachiyomi.source.model.** { *; }
-keep class kotlinx.serialization.** { *; }
-keep class uy.kohesive.injekt.** { *; }
-keep class rx.** { *; }
-keep class okhttp3.** { *; }
-keep class org.jsoup.** { *; }
-keepclassmembers class * {
    <init>(...);
}
