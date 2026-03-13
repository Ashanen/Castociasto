plugins {
    id("castociasto.kmp.infra")
}

android {
    namespace = "pl.rockit.castociasto.infrastructure.networking"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.foundation)
        }
    }
}
