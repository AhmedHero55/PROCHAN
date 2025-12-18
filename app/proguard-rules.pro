-keep class eu.kanade.tachiyomi.** { *; }
-keep class eu.kanade.tachiyomi.source.model.SManga { *; }
-keep class eu.kanade.tachiyomi.source.model.SChapter { *; }
-keep class eu.kanade.tachiyomi.source.model.Page { *; }
-keep class kotlin.** { *; }
-keep class okhttp3.** { *; }
-keep class org.jsoup.** { *; }
-keepclassmembers class * {
    @eu.kanade.tachiyomi.annotations.* <methods>;
}
