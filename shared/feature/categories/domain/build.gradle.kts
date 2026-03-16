plugins {
    id("castociasto.kmp.domain")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.foundation)
            implementation(projects.shared.core.categories)
        }
    }
}
