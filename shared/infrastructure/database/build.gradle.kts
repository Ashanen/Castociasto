plugins {
    id("castociasto.kmp.library")
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

android {
    namespace = "pl.rockit.castociasto.infrastructure.database"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.koin.core)
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
