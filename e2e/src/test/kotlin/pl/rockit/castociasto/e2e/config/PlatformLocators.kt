package pl.rockit.castociasto.e2e.config

import io.appium.java_client.AppiumBy
import org.openqa.selenium.By

/**
 * Platform-aware locator factory.
 *
 * Android: Compose testTag -> resource-id -> By.id()
 * iOS: SwiftUI .accessibilityIdentifier -> name -> AppiumBy.accessibilityId()
 */
object PlatformLocators {

    private val platform = Platform.current()

    fun byId(id: String): By = when (platform) {
        Platform.ANDROID -> By.id(id)
        Platform.IOS -> AppiumBy.accessibilityId(id)
    }

    fun byIdPattern(pattern: String): By = when (platform) {
        Platform.ANDROID -> AppiumBy.androidUIAutomator(
            "new UiSelector().resourceIdMatches(\"$pattern\")"
        )
        Platform.IOS -> AppiumBy.iOSNsPredicateString(
            "name MATCHES '$pattern'"
        )
    }
}
