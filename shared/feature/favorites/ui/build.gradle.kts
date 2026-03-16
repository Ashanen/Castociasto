plugins {
    id("castociasto.kmp.ui")
}

android {
    namespace = "pl.rockit.castociasto.feature.favorites.ui"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.foundation)
            implementation(projects.shared.feature.favorites.domain)
            implementation(projects.shared.core.items)
            implementation(projects.shared.core.favorites)
        }
    }
}
