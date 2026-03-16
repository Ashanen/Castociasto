plugins {
    id("castociasto.kmp.data")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "pl.rockit.castociasto.feature.items.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.feature.items.domain)
            implementation(projects.shared.core.items)
            implementation(projects.shared.infrastructure.networking)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
    }
}
