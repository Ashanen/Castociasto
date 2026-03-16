package pl.rockit.castociasto.e2e.page

import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import pl.rockit.castociasto.e2e.config.AppiumConfig
import java.time.Duration

/**
 * Page Object for the Detail screen.
 * All locators use Compose testTag (resource-id) or content-desc.
 */
class DetailPage(private val driver: AndroidDriver) {

    private val wait = WebDriverWait(driver, Duration.ofSeconds(AppiumConfig.EXPLICIT_WAIT))

    fun waitForContent(): DetailPage {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("detail_title")))
        return this
    }

    fun getTitle(): String {
        return driver.findElement(By.id("detail_title")).text
    }

    fun getSubtitle(): String {
        return driver.findElement(By.id("detail_subtitle")).text
    }

    fun tapBack(): ListPage {
        wait.until(
            ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Back"))
        ).click()
        return ListPage(driver)
    }

    fun isDisplayed(): Boolean {
        return try {
            driver.findElement(By.id("detail_screen")).isDisplayed
        } catch (_: Exception) {
            false
        }
    }
}
