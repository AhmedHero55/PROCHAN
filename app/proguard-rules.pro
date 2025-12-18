# احتفظ بميتا بيانات Kotlin لتجنب أخطاء R8 على الانعكاس والواجهات
-keep class kotlin.Metadata { *; }
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# احتفظ بواجهات Tachiyomi التي تُستدعى ديناميكياً عبر reflection (حسب ما تستخدمه)
-keep class eu.kanade.tachiyomi.** { *; }

# OkHttp/JSoup في العادة لا تحتاج قواعد خاصة، لكن تجنب حذف الصفوف العامة
-dontwarn org.jsoup.**
-dontwarn okhttp3.**
