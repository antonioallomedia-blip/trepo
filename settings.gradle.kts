// Configuration du projet CloudstreamPlugins.
// Seuls les plugins listés dans `enabled` seront compilés.

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}

rootProject.name = "CloudstreamPlugins"

// Liste des plugins à compiler. Ajoute le nom exact du dossier ici.
val enabled = listOf<String>(
    "HanimeTV",
)

File(rootDir, ".").eachDir { dir ->
    if (enabled.contains(dir.name) && File(dir, "build.gradle.kts").exists()) {
        include(dir.name)
    }
}

fun File.eachDir(block: (File) -> Unit) {
    listFiles()?.filter { it.isDirectory }?.forEach { block(it) }
}