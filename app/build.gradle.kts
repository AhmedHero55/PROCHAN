plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ar.prochan"   // ✅ نقلنا الـ namespace هنا بدل الـ Manifest
    compileSdk = 34

    defaultConfig {
        applicationId = "ar.prochan"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // ✅ تفعيل desugaring حتى لا يفشل بناء source-api
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(files("libs/source-api.aar"))

    // ✅ إضافة مكتبة desugar المطلوبة من Google
    coreLibraryDesugaring("com.android.tools:desugar_jdk_lib
