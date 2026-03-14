package pl.rockit.castociasto.e2e.page

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import pl.rockit.castociasto.e2e.config.AppiumConfig
import java.time.Duration

/**
 * Page Object for the Detail screen.
 * Encapsulates all locators and interactions for the item detail view.
 */
class DetailPage(private val driver: AndroidDriver) {

    private val wait = WebDriverWait(driver, Duration.ofSeconds(AppiumConfig.EXPLICIT_WAIT))

    fun waitForContent(): DetailPage {
        wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("android.widget.TextView"))
        )
        return this
    }

    fun getTitle(): String {
        val elements = driver.findElements(By.className("android.widget.TextView"))
        // The first large text element is the title in the content area
        return elements.firstOrNull()?.text ?: ""
    }

    fun getSubtitle(): String {
        val elements = driver.findElements(By.className("android.widget.TextView"))
        // Subtitle is typically the second text element after the title
        return elements.getOrNull(1)?.text ?: ""
    }

    fun hasText(text: String): Boolean {
        return try {
            wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.TextView[contains(@text, '$text')]")
                )
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    fun tapBack(): ListPage {
        // Navigate back using the back button in the top bar
        val backButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.ImageButton[@content-desc='Back']")
            )
        )
        backButton.click()
        return ListPage(driver)
    }

    fun navigateBackWithSystemButton(): ListPage {
        driver.navigate().back()
        return ListPage(driver)
    }

    fun isDisplayed(): Boolean {
        return try {
            val elements = driver.findElements(By.className("android.widget.TextView"))
            elements.size > 1 // Detail screen has title + subtitle
        } catch (e: Exception) {
            false
        }
    }
}
