plugins {
    id("castociasto.kmp.ui")
}

android {
    namespace = "pl.rockit.castociasto.feature.categories.ui"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.foundation)
            implementation(projects.shared.core.categories)
        }
    }
}
