import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

android {
    namespace = "com.yourname.hanimetv"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<KotlinJvmCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

// IMPORTANT : ces deux blocs réparent la dépendance cassée du plugin
configurations.all {
    resolutionStrategy.dependencySubstitution {
        // Le plugin CloudStream essaie d'ajouter com.lagradost:cloudstream3:pre-release
        // qui n'existe plus. On le remplace par la vraie bibliothèque.
        substitute(module("com.lagradost:cloudstream3:pre-release"))
            .using(module("com.github.recloudstream.cloudstream:library:-SNAPSHOT"))
    }
}

dependencies {
    // Ajoute la bibliothèque CloudStream manuellement
    implementation("com.github.recloudstream.cloudstream:library:-SNAPSHOT")
}

cloudstream {
    description = "Hanime.tv provider for CloudStream."
    authors = listOf("yourname")
    status = 1
    tvTypes = listOf("Movie")
    requiresResources = false
    language = "en"
    iconUrl = "https://hanime.tv/favicon.ico"
}