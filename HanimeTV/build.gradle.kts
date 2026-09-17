import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.yourname.hanimetv"
    compileSdk = 35

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

// Aucun bloc dependencies ici : le plugin CloudStream les ajoute automatiquement.

cloudstream {
    description = "Hanime.tv provider for CloudStream."
    authors = listOf("yourname")
    status = 1
    tvTypes = listOf("Movie")
    requiresResources = false
    language = "en"
    iconUrl = "https://hanime.tv/favicon.ico"
}