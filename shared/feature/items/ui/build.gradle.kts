plugins {
    id("castociasto.kmp.ui")
}

android {
    namespace = "pl.rockit.castociasto.feature.items.ui"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.foundation)
            implementation(projects.shared.feature.items.domain)
            implementation(projects.shared.core.items)
        }
    }
}
