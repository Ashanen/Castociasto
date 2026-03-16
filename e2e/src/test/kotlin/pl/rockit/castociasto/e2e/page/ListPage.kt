package pl.rockit.castociasto.e2e.page

import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import pl.rockit.castociasto.e2e.config.AppiumConfig
import java.time.Duration

/**
 * Page Object for the List screen.
 * All locators use Compose testTag (mapped to resource-id).
 */
class ListPage(private val driver: AndroidDriver) {

    private val wait = WebDriverWait(driver, Duration.ofSeconds(AppiumConfig.EXPLICIT_WAIT))

    private val anyItem = AppiumBy.androidUIAutomator(
        "new UiSelector().resourceIdMatches(\"item_.*\")"
    )

    fun waitForItemsToLoad(): ListPage {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("items_list")))
        wait.until(ExpectedConditions.presenceOfElementLocated(anyItem))
        return this
    }

    fun getTitle(): String {
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.id("list_title")))!!.text
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
            driver.findElement(By.id("list_screen")).isDisplayed
        } catch (_: Exception) {
            false
        }
    }

    fun waitForError(): ListPage {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("list_error")))
        return this
    }

    fun isErrorDisplayed(): Boolean {
        return try {
            driver.findElement(By.id("list_error")).isDisplayed
        } catch (_: Exception) {
            false
        }
    }

    fun getErrorMessage(): String {
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.id("list_error_message")))!!.text
    }

    fun tapRetry(): ListPage {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("list_retry_button"))).click()
        return this
    }
}
