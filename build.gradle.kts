plugins {
    // لا شيء هنا؛ كل شيء داخل :app
}

tasks.register("copySourceApiAar") {
    group = "setup"
    description = "Build Mihon source-api AAR and copy to app/libs"
    doLast {
        val workDir = layout.buildDirectory.asFile.get()
        val mihonDir = File(workDir, "mihon")
        if (!mihonDir.exists()) {
            println("Cloning Mihon...")
            ProcessBuilder("git", "clone", "--depth", "1", "https://github.com/mihonapp/mihon.git", mihonDir.absolutePath)
                .inheritIO().start().waitFor()
        }
        println("Building source-api AAR...")
        ProcessBuilder("./gradlew", ":source-api:assembleRelease", "--no-daemon")
            .directory(mihonDir)
            .inheritIO().start().waitFor()

        val aarSrc = File(mihonDir, "source-api/build/outputs/aar/source-api-release.aar")
        val libsDir = File(projectDir, "app/libs").apply { mkdirs() }
        val aarDst = File(libsDir, "source-api-release.aar")
        aarSrc.copyTo(aarDst, overwrite = true)
        println("Copied AAR to app/libs/source-api-release.aar")
    }
}
