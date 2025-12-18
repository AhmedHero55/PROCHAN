# احتفاظ بكلاسات Tachiyomi
-keep class eu.kanade.tachiyomi.** { *; }
-keep class okhttp3.** { *; }
-keep class org.jsoup.** { *; }

# تجنّب حذف أعضاء SManga/SChapter المستخدمة عبر reflection المحتمل
-keepclassmembers class eu.kanade.tachiyomi.source.model.** { *; }
