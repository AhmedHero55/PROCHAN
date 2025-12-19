pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "PROCHAN"
include(":app")   // ✅ تعريف الوحدة الفرعية app
