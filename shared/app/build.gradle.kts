import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.skie)
}

kotlin {
    (this as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryExtension>("androidLibrary") {
        namespace = "pl.rockit.castociasto.app"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
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
            api(projects.shared.infrastructure.database)
            api(libs.koin.core)

            // Core
            api(projects.shared.core.events)

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

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}
