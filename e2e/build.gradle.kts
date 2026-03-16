plugins {
    alias(libs.plugins.kotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    testImplementation(libs.appium.java.client)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.testJunit)
}

tasks.test {
    systemProperty("platform", System.getProperty("platform") ?: "android")
}
