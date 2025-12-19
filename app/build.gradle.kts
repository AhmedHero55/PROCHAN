plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ar.prochan"
    compileSdk = 34

    defaultConfig {
        applicationId = "ar.prochan"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // تعطيل الـ minify لحل نهائي وفوري لمشكلة R8 والمفقودات
            isMinifyEnabled = false
            // لو أردت إعادة تفعيل لاحقًا بعد استقرار الاعتمادات، غيّر للسطرين أدناه:
            // isMinifyEnabled = true
            // proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
        }
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/*.kotlin_module"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
        // إبقاءه مفيد عند اختلاف metadata بين Kotlin والـ AAR
        freeCompilerArgs += listOf("-Xskip-metadata-version-check")
    }
}

dependencies {
    // Tachiyomi Source API AAR
    implementation(files("libs/source-api.aar"))

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jsoup:jsoup:1.16.1")

    // الإضافات اللازمة بحسب أخطاء R8 (مفقودات)
    // RxJava 1 (المطلوبة من HttpSource)
    implementation("io.reactivex:rxjava:1.3.8")

    // kotlinx.serialization runtime (مطلوبة لـ Page serializer)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Injekt (مطلوبة من ConfigurableSource)
    implementation("uy.kohesive.injekt:injekt-core:1.3.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
