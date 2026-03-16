plugins {
    id("castociasto.kmp.ui")
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
