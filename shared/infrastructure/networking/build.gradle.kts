plugins {
    id("castociasto.kmp.infra")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.foundation)
        }
    }
}
