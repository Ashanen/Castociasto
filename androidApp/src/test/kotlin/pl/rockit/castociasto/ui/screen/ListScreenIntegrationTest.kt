package pl.rockit.castociasto.ui.screen

import android.app.Application
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import pl.rockit.castociasto.feature.items.ui.ListViewModel
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
class ListScreenIntegrationTest {

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
    fun `displays items loaded through real use cases from fake repository`() {
        val items = listOf(
            Item(id = "1", title = "Banana Bread", subtitle = "Ripe bananas"),
            Item(id = "2", title = "Apple Pie", subtitle = "Granny Smith"),
        )
        fakeItemRepository.items = items

        val viewModel: ListViewModel = get(ListViewModel::class.java)

        composeTestRule.setContent {
            ListScreen(
                onNavigateToDetail = {},
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithText("Apple Pie").assertIsDisplayed()
        composeTestRule.onNodeWithText("Granny Smith").assertIsDisplayed()
        composeTestRule.onNodeWithText("Banana Bread").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ripe bananas").assertIsDisplayed()
    }

    @Test
    fun `displays error when repository throws`() {
        fakeItemRepository.shouldThrow = RuntimeException("Network error")

        val viewModel: ListViewModel = get(ListViewModel::class.java)

        composeTestRule.setContent {
            ListScreen(
                onNavigateToDetail = {},
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun `item click triggers navigation`() {
        var navigatedItemId: String? = null
        val items = listOf(
            Item(id = "77", title = "Ciabatta", subtitle = "Italian classic"),
        )
        fakeItemRepository.items = items

        val viewModel: ListViewModel = get(ListViewModel::class.java)

        composeTestRule.setContent {
            ListScreen(
                onNavigateToDetail = { navigatedItemId = it },
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithText("Ciabatta").performClick()
        composeTestRule.waitForIdle()

        kotlin.test.assertEquals("77", navigatedItemId)
    }
}
