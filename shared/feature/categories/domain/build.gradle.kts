plugins {
    id("castociasto.kmp.domain")
}

android {
    namespace = "pl.rockit.castociasto.feature.categories.domain"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.foundation)
            implementation(projects.shared.core.categories)
        }
    }
}
