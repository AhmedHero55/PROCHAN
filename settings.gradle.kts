pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // مستودعات إضافية إن كنت تستخدمها لاحقًا:
        // maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "PROCHAN"
include(":app")
