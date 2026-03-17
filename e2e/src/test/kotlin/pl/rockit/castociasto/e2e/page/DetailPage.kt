package pl.rockit.castociasto.e2e.page

import io.appium.java_client.AppiumBy
import io.appium.java_client.AppiumDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import pl.rockit.castociasto.e2e.config.AppiumConfig
import pl.rockit.castociasto.e2e.config.Platform
import pl.rockit.castociasto.e2e.config.PlatformLocators
import java.time.Duration

/**
 * Page Object for the Detail screen.
 * Uses PlatformLocators for cross-platform element lookup.
 */
class DetailPage(private val driver: AppiumDriver) {

    private val wait = WebDriverWait(driver, Duration.ofSeconds(AppiumConfig.EXPLICIT_WAIT))

    fun waitForContent(): DetailPage {
        wait.until(ExpectedConditions.presenceOfElementLocated(PlatformLocators.byId("detail_title")))
        return this
    }

    fun getTitle(): String {
        return driver.findElement(PlatformLocators.byId("detail_title")).text
    }

    fun getSubtitle(): String {
        return driver.findElement(PlatformLocators.byId("detail_subtitle")).text
    }

    fun tapBack(): ListPage {
        val backLocator = when (Platform.current()) {
            Platform.ANDROID -> AppiumBy.accessibilityId("Back")
            Platform.IOS -> AppiumBy.accessibilityId("BackButton")
        }
        wait.until(ExpectedConditions.elementToBeClickable(backLocator)).click()
        return ListPage(driver)
    }

    fun isDisplayed(): Boolean {
        return try {
            driver.findElement(PlatformLocators.byId("detail_screen")).isDisplayed
        } catch (_: Exception) {
            false
        }
    }
}
