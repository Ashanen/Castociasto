package pl.rockit.castociasto.ui.screen

import android.app.Application
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import kotlinx.coroutines.flow.flow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.feature.items.domain.GetItemUseCase
import pl.rockit.castociasto.feature.items.ui.DetailViewModel
import pl.rockit.castociasto.foundation.extensions.flowSingle

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [35],
    application = Application::class,
    instrumentedPackages = ["androidx.loader.content"],
)
class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `displays item details after loading`() {
        val item = Item(id = "1", title = "Sourdough Bread", subtitle = "Made with love")
        val useCase = GetItemUseCase { flowSingle { item } }
        val viewModel = DetailViewModel(useCase)

        composeTestRule.setContent {
            DetailScreen(
                itemId = "1",
                onNavigateBack = {},
                viewModel = viewModel,
            )
        }

        // Title appears in both TopAppBar and content
        composeTestRule.onAllNodesWithText("Sourdough Bread").assertCountEquals(2)
        composeTestRule.onNodeWithText("Made with love").assertIsDisplayed()
    }

    @Test
    fun `displays error state when item fails to load`() {
        val useCase = GetItemUseCase { _ ->
            flow<Item> { throw RuntimeException("Item not found") }
        }
        val viewModel = DetailViewModel(useCase)

        composeTestRule.setContent {
            DetailScreen(
                itemId = "999",
                onNavigateBack = {},
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithText("Item not found").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}
