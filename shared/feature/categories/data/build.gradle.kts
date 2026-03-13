plugins {
    id("castociasto.kmp.data")
}

android {
    namespace = "pl.rockit.castociasto.feature.categories.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core.categories)
        }
    }
}
