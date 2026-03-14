package pl.rockit.castociasto.e2e.test

import org.junit.Assume
import org.junit.Before
import org.junit.Test
import pl.rockit.castociasto.e2e.base.BaseE2ETest
import pl.rockit.castociasto.e2e.page.ListPage
import kotlin.test.assertTrue

/**
 * End-to-end tests for the items browsing flow.
 *
 * These tests verify the full user journey:
 * 1. App launches to list screen
 * 2. Items load from API
 * 3. Tapping item navigates to detail
 * 4. Back navigation returns to list
 *
 * Prerequisites:
 * - Appium server running (`appium`)
 * - Android emulator/device connected
 * - Debug APK built (`./gradlew :androidApp:assembleDebug`)
 * - Internet connection (app fetches from jsonplaceholder.typicode.com)
 */
class ItemsFlowE2ETest : BaseE2ETest() {

    private lateinit var listPage: ListPage

    @Before
    fun checkPreconditions() {
        // Skip tests if Appium server is not available
        try {
            listPage = ListPage(driver)
        } catch (e: Exception) {
            Assume.assumeNoException("Appium server not available, skipping E2E tests", e)
        }
    }

    @Test
    fun appLaunchesAndShowsListScreen() {
        listPage.waitForItemsToLoad()

        assertTrue(listPage.isDisplayed(), "List screen should be displayed")
        assertTrue(
            listPage.getTitle() == "Castociasto",
            "App title should be 'Castociasto'"
        )
    }

    @Test
    fun listScreenDisplaysItems() {
        listPage.waitForItemsToLoad()

        val itemCount = listPage.getVisibleItemCount()
        assertTrue(itemCount > 0, "List should display at least one item")
    }

    @Test
    fun tappingItemNavigatesToDetail() {
        listPage.waitForItemsToLoad()

        val detailPage = listPage.tapFirstItem()
        detailPage.waitForContent()

        assertTrue(detailPage.isDisplayed(), "Detail screen should be displayed")
    }

    @Test
    fun backNavigationReturnsToList() {
        listPage.waitForItemsToLoad()

        val detailPage = listPage.tapFirstItem()
        detailPage.waitForContent()

        val returnedListPage = detailPage.tapBack()
        returnedListPage.waitForItemsToLoad()

        assertTrue(returnedListPage.isDisplayed(), "Should return to list screen after back")
    }

    @Test
    fun fullBrowsingJourney() {
        // Step 1: List loads
        listPage.waitForItemsToLoad()
        assertTrue(listPage.isDisplayed())

        // Step 2: Navigate to detail
        val detailPage = listPage.tapFirstItem()
        detailPage.waitForContent()
        assertTrue(detailPage.isDisplayed())

        // Step 3: Navigate back
        val returnedList = detailPage.tapBack()
        returnedList.waitForItemsToLoad()
        assertTrue(returnedList.isDisplayed())
    }
}
