import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Infrastructure module plugin.
 * Provides: coroutines + Koin + Ktor + serialization.
 */
class CastociastoKmpInfraPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("castociasto.kmp.library")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    implementation(libs.findLibrary("kotlinx-coroutines-core").get())
                    implementation(libs.findLibrary("kotlinx-serialization-json").get())
                    implementation(libs.findLibrary("koin-core").get())
                    implementation(libs.findLibrary("ktor-client-core").get())
                    implementation(libs.findLibrary("ktor-client-content-negotiation").get())
                    implementation(libs.findLibrary("ktor-serialization-kotlinx-json").get())
                    implementation(libs.findLibrary("ktor-client-logging").get())
                }

                sourceSets.androidMain.dependencies {
                    implementation(libs.findLibrary("ktor-client-okhttp").get())
                }

                sourceSets.iosMain.dependencies {
                    implementation(libs.findLibrary("ktor-client-darwin").get())
                }
            }
        }
    }
}
