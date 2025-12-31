# حافظ على جميع أصناف الإضافة داخل الباكيج لمنع حذف الكلاس الرئيسي
-keep class eu.kanade.tachiyomi.extension.prochan.** { *; }

# حافظ على واجهات ونماذج Tachiyomi
-keep class eu.kanade.tachiyomi.source.** { *; }
-keep class eu.kanade.tachiyomi.source.model.** { *; }

# مكتبات مستخدمة
-keep class kotlinx.serialization.** { *; }
-keep class uy.kohesive.injekt.** { *; }
-keep class rx.** { *; }
-keep class okhttp3.** { *; }
-keep class org.jsoup.** { *; }

# حافظ على المنشئات في كل الأصناف
-keepclassmembers class * { <init>(...); }

# لا تحذف ميتاداتا Kotlin أو التوقيعات والأنوتيشن، مهمة لعمل الانعكاس والـ ParsedHttpSource
-keep class kotlin.Metadata { *; }
-keepattributes Signature, *Annotation*
