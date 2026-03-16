plugins {
    id("castociasto.kmp.domain")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.foundation)
            implementation(projects.shared.core.items)
            implementation(projects.shared.core.favorites)
        }
    }
}
