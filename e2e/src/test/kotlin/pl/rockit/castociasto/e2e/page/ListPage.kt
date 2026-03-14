package pl.rockit.castociasto.e2e.page

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import pl.rockit.castociasto.e2e.config.AppiumConfig
import java.time.Duration

/**
 * Page Object for the List screen.
 * Encapsulates all locators and interactions for the items list.
 */
class ListPage(private val driver: AndroidDriver) {

    private val wait = WebDriverWait(driver, Duration.ofSeconds(AppiumConfig.EXPLICIT_WAIT))

    fun waitForItemsToLoad(): ListPage {
        wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.TextView"))
        )
        return this
    }

    fun getTitle(): String {
        val titleElement = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//android.widget.TextView[@text='Castociasto']")
            )
        )
        return titleElement?.text ?: ""
    }

    fun getItemTitles(): List<String> {
        val elements = driver.findElements(By.className("android.widget.TextView"))
        // Filter out the app title and other non-item texts
        return elements
            .map { it.text }
            .filter { it.isNotBlank() && it != "Castociasto" }
    }

    fun getVisibleItemCount(): Int {
        return getItemTitles().size
    }

    fun tapItem(title: String): DetailPage {
        val item = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.TextView[@text='$title']")
            )
        )
        item.click()
        return DetailPage(driver)
    }

    fun tapFirstItem(): DetailPage {
        val items = driver.findElements(By.className("android.widget.TextView"))
        val firstItem = items.firstOrNull { it.text.isNotBlank() && it.text != "Castociasto" }
            ?: throw NoSuchElementException("No items found in list")
        firstItem.click()
        return DetailPage(driver)
    }

    fun isDisplayed(): Boolean {
        return try {
            driver.findElement(By.xpath("//android.widget.TextView[@text='Castociasto']"))
                .isDisplayed
        } catch (e: Exception) {
            false
        }
    }
}
