plugins {
    id("castociasto.kmp.api")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.shared.foundation)
        }
    }
}
