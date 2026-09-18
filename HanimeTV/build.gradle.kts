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
        // Contourne l'incompatibilité de métadonnées de kotlin-stdlib 2.4.0
        freeCompilerArgs.add("-Xskip-metadata-version-check")
    }
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
        force("org.jetbrains.kotlin:kotlin-stdlib-common:1.9.24")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.24")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24")
    }
    resolutionStrategy.dependencySubstitution {
        substitute(module("com.lagradost:cloudstream3:pre-release"))
            .using(module("com.github.recloudstream.cloudstream:library:-SNAPSHOT"))
    }
}

dependencies {
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