plugins {
    id("castociasto.kmp.data")
}

android {
    namespace = "pl.rockit.castociasto.feature.favorites.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.feature.favorites.domain)
            implementation(projects.shared.core.favorites)
            implementation(projects.shared.core.events)
        }
    }
}
