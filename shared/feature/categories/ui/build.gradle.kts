plugins {
    id("castociasto.kmp.ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.foundation)
            implementation(projects.shared.feature.categories.domain)
            implementation(projects.shared.core.categories)
        }
    }
}
