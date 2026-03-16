plugins {
    id("castociasto.kmp.data")
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
