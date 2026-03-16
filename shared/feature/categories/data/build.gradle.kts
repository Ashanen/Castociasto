plugins {
    id("castociasto.kmp.data")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.feature.categories.domain)
            implementation(projects.shared.core.categories)
        }
    }
}
