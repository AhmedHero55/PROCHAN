plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "eu.kanade.tachiyomi.extension.ar.prochan" // عدل الـ package حسب امتدادك
    compileSdk = (project.findProperty("android.compileSdkVersion") as String?)?.toInt() ?: 34

    defaultConfig {
        applicationId = "eu.kanade.tachiyomi.extension.ar.prochan"
        minSdk = (project.findProperty("android.minSdkVersion") as String?)?.toInt() ?: 21
        targetSdk = (project.findProperty("android.targetSdkVersion") as String?)?.toInt() ?: 34

        versionCode = 1
        versionName = "1.0.0"

        // امتدادات Tachiyomi عادة لا تحتاج multiDex، لكن يمكنك تمكينه إذا كبرت التبعيات
        multiDexEnabled = false
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // لمنع أخطاء R8 على Kotlin metadata، احتفظ بنُوى Kotlin:
            // سنضيف قواعد في proguard-rules.pro
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-Xjvm-default=all",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE*",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1"
            )
        }
    }

    lint {
        abortOnError = false
        warningsAsErrors = false
    }
}

dependencies {
    // نواة Tachiyomi (حسب احتياج الامتداد؛ غالبًا تُستخدم APIs من mihon/tachiyomi):
    // إن كنت تعتمد على واجهات الامتدادات المشتركة:
    // implementation("eu.kanade.tachiyomi:extensions-lib:<version>") // إن وجدت لديك مكتبة مشتركة

    // Kotlin stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Coroutines (إن استخدمتها)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // OkHttp/JSoup حسب الامتداد
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jsoup:jsoup:1.17.2")

    // إذا كان لديك AAR محلي مثل source-api.aar:
    // ضع الملف في libs/ وأضفه هكذا:
    implementation(files("libs/source-api.aar"))
}
