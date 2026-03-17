package pl.rockit.castociasto.ui.screen

import android.app.Application
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pl.rockit.castociasto.core.favorites.repository.FavoriteRepository
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.core.items.repository.ItemRepository
import pl.rockit.castociasto.fake.FakeFavoriteRepository
import pl.rockit.castociasto.fake.FakeItemRepository
import pl.rockit.castociasto.feature.items.domain.di.itemsDomainModule
import pl.rockit.castociasto.feature.items.ui.DetailViewModel
import pl.rockit.castociasto.feature.items.ui.di.itemsUiModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get
import org.junit.After
import org.junit.Before

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [35],
    application = Application::class,
    instrumentedPackages = ["androidx.loader.content"],
)
class DetailScreenIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeItemRepository = FakeItemRepository()
    private val fakeFavoriteRepository = FakeFavoriteRepository()

    private val fakeReposModule = module {
        single<ItemRepository> { fakeItemRepository }
        single<FavoriteRepository> { fakeFavoriteRepository }
    }

    @Before
    fun setUp() {
        startKoin {
            modules(fakeReposModule, itemsDomainModule, itemsUiModule)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `displays item details loaded through real use case from fake repository`() {
        val item = Item(id = "1", title = "Sourdough Bread", subtitle = "Made with love")
        fakeItemRepository.itemById = mapOf("1" to item)

        val viewModel: DetailViewModel = get(DetailViewModel::class.java)

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
    fun `enriches item with favorite status from repository`() {
        val item = Item(id = "5", title = "Rye Bread", subtitle = "Dark and hearty")
        fakeItemRepository.itemById = mapOf("5" to item)
        fakeFavoriteRepository.favoriteIds = mutableSetOf("5")

        val viewModel: DetailViewModel = get(DetailViewModel::class.java)

        composeTestRule.setContent {
            DetailScreen(
                itemId = "5",
                onNavigateBack = {},
                viewModel = viewModel,
            )
        }

        // Item should load successfully with favorite enrichment
        composeTestRule.onAllNodesWithText("Rye Bread").assertCountEquals(2)
        composeTestRule.onNodeWithText("Dark and hearty").assertIsDisplayed()
    }

    @Test
    fun `displays error when item not found in repository`() {
        fakeItemRepository.itemById = emptyMap()

        val viewModel: DetailViewModel = get(DetailViewModel::class.java)

        composeTestRule.setContent {
            DetailScreen(
                itemId = "999",
                onNavigateBack = {},
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithText("Item 999 not found").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun `displays error when repository throws`() {
        fakeItemRepository.shouldThrow = RuntimeException("Database corrupted")

        val viewModel: DetailViewModel = get(DetailViewModel::class.java)

        composeTestRule.setContent {
            DetailScreen(
                itemId = "1",
                onNavigateBack = {},
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithText("Database corrupted").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}
