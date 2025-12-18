# Keep Tachiyomi/Mihon API classes to prevent Kotlin metadata rewrite issues
-keep class eu.kanade.tachiyomi.** { *; }
-keep class eu.kanade.tachiyomi.source.model.** { *; }

# Keep Kotlin stdlib types (metadata safety)
-keep class kotlin.** { *; }

# Keep OkHttp and Jsoup
-keep class okhttp3.** { *; }
-keep class org.jsoup.** { *; }
