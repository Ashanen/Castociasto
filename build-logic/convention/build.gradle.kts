import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "pl.rockit.castociasto.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.serializationPlugin)
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "castociasto.kmp.library"
            implementationClass = "CastociastoKmpLibraryPlugin"
        }
        register("kmpApi") {
            id = "castociasto.kmp.api"
            implementationClass = "CastociastoKmpApiPlugin"
        }
        register("kmpDomain") {
            id = "castociasto.kmp.domain"
            implementationClass = "CastociastoKmpDomainPlugin"
        }
        register("kmpData") {
            id = "castociasto.kmp.data"
            implementationClass = "CastociastoKmpDataPlugin"
        }
        register("kmpUi") {
            id = "castociasto.kmp.ui"
            implementationClass = "CastociastoKmpUiPlugin"
        }
        register("kmpInfra") {
            id = "castociasto.kmp.infra"
            implementationClass = "CastociastoKmpInfraPlugin"
        }
    }
}
