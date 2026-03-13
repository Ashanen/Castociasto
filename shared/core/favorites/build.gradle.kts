plugins {
    id("castociasto.kmp.api")
}

android {
    namespace = "pl.rockit.castociasto.core.favorites"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.shared.foundation)
            api(projects.shared.core.items)
        }
    }
}
