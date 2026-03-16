package pl.rockit.castociasto.e2e.page

import io.appium.java_client.AppiumDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import pl.rockit.castociasto.e2e.config.AppiumConfig
import pl.rockit.castociasto.e2e.config.PlatformLocators
import java.time.Duration

/**
 * Page Object for the List screen.
 * Uses PlatformLocators for cross-platform element lookup.
 */
class ListPage(private val driver: AppiumDriver) {

    private val wait = WebDriverWait(driver, Duration.ofSeconds(AppiumConfig.EXPLICIT_WAIT))

    private val anyItem get() = PlatformLocators.byIdPattern("item_.*")

    fun waitForItemsToLoad(): ListPage {
        wait.until(ExpectedConditions.presenceOfElementLocated(PlatformLocators.byId("items_list")))
        wait.until(ExpectedConditions.presenceOfElementLocated(anyItem))
        return this
    }

    fun getTitle(): String {
        return wait.until(
            ExpectedConditions.presenceOfElementLocated(PlatformLocators.byId("list_title"))
        )!!.text
    }

    fun getVisibleItemCount(): Int {
        return driver.findElements(anyItem).size
    }

    fun tapFirstItem(): DetailPage = tapItemAt(0)

    fun tapItemAt(index: Int): DetailPage {
        val items = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(anyItem))
        items[index].click()
        return DetailPage(driver)
    }

    fun isDisplayed(): Boolean {
        return try {
            driver.findElement(PlatformLocators.byId("list_screen")).isDisplayed
        } catch (_: Exception) {
            false
        }
    }

    fun waitForError(): ListPage {
        wait.until(ExpectedConditions.presenceOfElementLocated(PlatformLocators.byId("list_error")))
        return this
    }

    fun isErrorDisplayed(): Boolean {
        return try {
            driver.findElement(PlatformLocators.byId("list_error")).isDisplayed
        } catch (_: Exception) {
            false
        }
    }

    fun getErrorMessage(): String {
        return wait.until(
            ExpectedConditions.presenceOfElementLocated(PlatformLocators.byId("list_error_message"))
        )!!.text
    }

    fun tapRetry(): ListPage {
        wait.until(
            ExpectedConditions.elementToBeClickable(PlatformLocators.byId("list_retry_button"))
        ).click()
        return this
    }
}
