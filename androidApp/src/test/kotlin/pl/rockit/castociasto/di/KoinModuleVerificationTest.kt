package pl.rockit.castociasto.di

import android.app.Application
import io.ktor.client.engine.HttpClientEngine
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.verify.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Verifies that all Koin modules are correctly configured.
 * This catches missing dependency declarations at test time
 * rather than at runtime.
 *
 * Platform-specific types (like HttpClientEngine) are provided
 * as extraTypes since they're resolved at runtime per platform.
 */
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [35],
    application = Application::class,
)
class KoinModuleVerificationTest : KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `all koin modules verify successfully`() {
        // Combine all modules into one for cross-module dependency resolution
        val combined = module {
            includes(appModules)
        }
        combined.verify(
            extraTypes = listOf(
                HttpClientEngine::class,
            ),
        )
    }
}
