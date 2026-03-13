plugins {
    id("castociasto.kmp.api")
}

android {
    namespace = "pl.rockit.castociasto.core.items"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.shared.foundation)
        }
    }
}
