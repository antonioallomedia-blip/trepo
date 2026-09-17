rootProject.name = "CloudstreamPlugins"

// Only include plugins that currently compile.
// Leave the list empty for now so the build succeeds.
val enabled = listOf<String>(
    // "Example",
    // "Hahomoe",
    // "Hanime",
    // "HentaiHaven",
)

File(rootDir, ".").eachDir { dir ->
    if (enabled.contains(dir.name) && File(dir, "build.gradle.kts").exists()) {
        include(dir.name)
    }
}

fun File.eachDir(block: (File) -> Unit) {
    listFiles()?.filter { it.isDirectory }?.forEach { block(it) }
}