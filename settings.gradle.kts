rootProject.name = "CloudstreamPlugins"

// Only include plugins that currently compile.
// Comment out or remove any plugin that is broken.
val enabled = listOf(
    // "Example",        // broken
    // "Hahomoe",        // broken
    // "Hanime",         // broken
    // "HentaiHaven",    // broken
)

File(rootDir, ".").eachDir { dir ->
    if (enabled.contains(dir.name) && File(dir, "build.gradle.kts").exists()) {
        include(dir.name)
    }
}

fun File.eachDir(block: (File) -> Unit) {
    listFiles()?.filter { it.isDirectory }?.forEach { block(it) }
}