plugins {
    id("castociasto.kmp.library")
}

android {
    namespace = "pl.rockit.castociasto.foundation"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            api(libs.androidx.lifecycle.viewmodelCompose)
        }
    }
}
