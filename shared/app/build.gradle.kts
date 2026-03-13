import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.skie)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true

            export(projects.shared.foundation)
            export(projects.shared.core.items)
            export(projects.shared.core.categories)
            export(projects.shared.core.favorites)
            export(projects.shared.feature.items.ui)
            export(projects.shared.feature.categories.ui)
            export(projects.shared.feature.favorites.ui)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.shared.foundation)
            api(projects.shared.infrastructure.networking)
            api(libs.koin.core)

            // Items
            api(projects.shared.core.items)
            api(projects.shared.feature.items.domain)
            api(projects.shared.feature.items.data)
            api(projects.shared.feature.items.ui)

            // Categories
            api(projects.shared.core.categories)
            api(projects.shared.feature.categories.domain)
            api(projects.shared.feature.categories.data)
            api(projects.shared.feature.categories.ui)

            // Favorites
            api(projects.shared.core.favorites)
            api(projects.shared.feature.favorites.domain)
            api(projects.shared.feature.favorites.data)
            api(projects.shared.feature.favorites.ui)
        }
    }
}

android {
    namespace = "pl.rockit.castociasto.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
