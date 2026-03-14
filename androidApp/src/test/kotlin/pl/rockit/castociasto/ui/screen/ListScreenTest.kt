package pl.rockit.castociasto.ui.screen

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.core.items.usecase.GetItemsUseCase
import pl.rockit.castociasto.feature.items.ui.ListViewModel

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [35],
    application = Application::class,
    instrumentedPackages = ["androidx.loader.content"],
)
class ListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `displays items when loaded from use case`() {
        val items = listOf(
            Item(id = "1", title = "Fresh Bread", subtitle = "Sourdough loaf"),
            Item(id = "2", title = "Chocolate Cake", subtitle = "Rich and moist"),
        )
        val viewModel = ListViewModel(GetItemsUseCase { flowOf(items) })

        composeTestRule.setContent {
            ListScreen(
                onNavigateToDetail = {},
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithText("Fresh Bread").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sourdough loaf").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chocolate Cake").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rich and moist").assertIsDisplayed()
    }

    @Test
    fun `displays error message and retry button on failure`() {
        val failingUseCase = GetItemsUseCase {
            flow<List<Item>> { throw RuntimeException("Connection failed") }
        }
        val viewModel = ListViewModel(failingUseCase)

        composeTestRule.setContent {
            ListScreen(
                onNavigateToDetail = {},
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithText("Connection failed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun `displays app title in top bar`() {
        val viewModel = ListViewModel(GetItemsUseCase { flowOf(emptyList()) })

        composeTestRule.setContent {
            ListScreen(
                onNavigateToDetail = {},
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithText("Castociasto").assertIsDisplayed()
    }

    @Test
    fun `item click triggers navigation callback`() {
        var navigatedItemId: String? = null
        val items = listOf(
            Item(id = "42", title = "Clickable Item", subtitle = "Click me"),
        )
        val viewModel = ListViewModel(GetItemsUseCase { flowOf(items) })

        composeTestRule.setContent {
            ListScreen(
                onNavigateToDetail = { navigatedItemId = it },
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithText("Clickable Item").performClick()
        composeTestRule.waitForIdle()

        kotlin.test.assertEquals("42", navigatedItemId)
    }
}
