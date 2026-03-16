package pl.rockit.castociasto.e2e.test

import org.junit.Test
import pl.rockit.castociasto.e2e.base.BaseE2ETest
import pl.rockit.castociasto.e2e.page.ListPage
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * End-to-end tests for the items browsing flow.
 *
 * These tests verify the full user journey:
 * 1. App launches to list screen
 * 2. Items load from API
 * 3. Tapping item navigates to detail with correct content
 * 4. Back navigation returns to list
 *
 * Prerequisites:
 * - Appium server running (`appium`)
 * - Android emulator/device connected
 * - Debug APK built (`./gradlew :androidApp:assembleDebug`)
 * - Internet connection (app fetches from jsonplaceholder.typicode.com)
 */
class ItemsFlowE2ETest : BaseE2ETest() {

    private val listPage: ListPage get() = ListPage(driver)

    @Test
    fun appLaunchesAndShowsListScreen() {
        listPage.waitForItemsToLoad()

        assertTrue(listPage.isDisplayed(), "List screen should be displayed")
        assertEquals("Castociasto", listPage.getTitle())
    }

    @Test
    fun listScreenDisplaysItems() {
        listPage.waitForItemsToLoad()

        val itemCount = listPage.getVisibleItemCount()
        assertTrue(itemCount > 0, "List should display at least one item, got $itemCount")
    }

    @Test
    fun tappingItemShowsDetailWithContent() {
        listPage.waitForItemsToLoad()

        val detailPage = listPage.tapFirstItem()
        detailPage.waitForContent()

        assertTrue(detailPage.isDisplayed(), "Detail screen should be displayed")
        assertTrue(detailPage.getTitle().isNotBlank(), "Detail title should not be blank")
        assertTrue(detailPage.getSubtitle().isNotBlank(), "Detail subtitle should not be blank")
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
    fun successfulLoadDoesNotShowError() {
        listPage.waitForItemsToLoad()

        assertFalse(listPage.isErrorDisplayed(), "Error state should not be shown on successful load")
        assertTrue(listPage.getVisibleItemCount() > 0, "Items should be visible")
    }

    @Test
    fun navigatingToDifferentItemsShowsDifferentContent() {
        listPage.waitForItemsToLoad()

        // Navigate to first item
        val firstDetail = listPage.tapFirstItem()
        firstDetail.waitForContent()
        val firstTitle = firstDetail.getTitle()

        // Go back and navigate to second item
        val returnedList = firstDetail.tapBack()
        returnedList.waitForItemsToLoad()
        val secondDetail = returnedList.tapItemAt(1)
        secondDetail.waitForContent()
        val secondTitle = secondDetail.getTitle()

        assertTrue(firstTitle != secondTitle, "Different items should have different titles")
    }
}
