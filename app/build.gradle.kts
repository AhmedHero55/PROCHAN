plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "ar.prochan"
    compileSdk = 35

    defaultConfig {
        applicationId = "ar.prochan"
        minSdk = 21
        targetSdk = 35

        versionCode = (providers.gradleProperty("VERSION_CODE").get().toInt())
        versionName = providers.gradleProperty("VERSION_NAME").get()

        // Tachiyomi extensions لا تحتاج نشاطات UI
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                file("proguard-rules.pro")
            )
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
        freeCompilerArgs += listOf("-Xjvm-default=all")
    }

    packaging {
        resources.excludes += setOf("META-INF/*")
    }
}

dependencies {
    // Tachiyomi source-api AAR محليًا
    implementation(files("libs/source-api-release.aar"))

    // OkHttp + Jsoup (عادةً مضمّنة داخل source-api، احتياطًا إن لزم)
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Kotlin stdlib
    implementation(kotlin("stdlib"))
}
