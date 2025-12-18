tasks.register("copySourceApiAar") {
    group = "setup"
    description = "Build Mihon source-api AAR and copy to app/libs"

    doLast {
        val mihonDir = File(rootDir, "mihon")
        if (!mihonDir.exists()) {
            println("Cloning Mihon...")
            ProcessBuilder("git", "clone", "--depth", "1", "https://github.com/mihonapp/mihon.git", mihonDir.absolutePath)
                .inheritIO()
                .start()
                .waitFor()
        }

        println("Building source-api AAR...")
        ProcessBuilder("./gradlew", ":source-api:assembleRelease", "--no-daemon")
            .directory(mihonDir)
            .inheritIO()
            .start()
            .waitFor()

        val aarSrc = File(mihonDir, "source-api/build/outputs/aar/source-api-release.aar")
        val libsDir = File(rootDir, "app/libs").apply { mkdirs() }
        val aarDst = File(libsDir, "source-api-release.aar")

        if (aarDst.exists()) {
            println("Replacing old source-api-release.aar...")
            aarDst.delete()
        }

        aarSrc.copyTo(aarDst, overwrite = true)
        println("Copied new source-api-release.aar to app/libs/")
    }
}
