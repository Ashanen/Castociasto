plugins {
    id("castociasto.kmp.domain")
}

android {
    namespace = "pl.rockit.castociasto.feature.favorites.domain"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.foundation)
            implementation(projects.shared.core.items)
            implementation(projects.shared.core.favorites)
        }
    }
}
