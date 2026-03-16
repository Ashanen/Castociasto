package pl.rockit.castociasto.feature.items.ui

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.feature.items.domain.GetItemUseCase
import pl.rockit.castociasto.foundation.extensions.flowSingle
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadItem action loads item successfully`() = runTest(testDispatcher) {
        val item = Item(id = "1", title = "Bread", subtitle = "Fresh bread", isFavorite = true)
        val viewModel = DetailViewModel(FakeGetItemUseCase(mapOf("1" to item)))

        viewModel.uiState.test {
            // Initial state
            val initial = awaitItem()
            assertNull(initial.item)
            assertFalse(initial.isLoading)

            viewModel.onAction(DetailAction.LoadItem("1"))

            // Loading state
            val loading = awaitItem()
            assertTrue(loading.isLoading)

            // Loaded state
            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            assertEquals("Bread", loaded.item?.title)
            assertTrue(loaded.item?.isFavorite == true)
            assertNull(loaded.error)
        }
    }

    @Test
    fun `LoadItem action sets error when use case fails`() = runTest(testDispatcher) {
        val viewModel = DetailViewModel(
            FakeGetItemUseCase(error = RuntimeException("Not found"))
        )

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.onAction(DetailAction.LoadItem("999"))

            awaitItem() // loading
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Not found", errorState.error)
        }
    }

    @Test
    fun `GoBack sends NavigateBack side effect`() = runTest(testDispatcher) {
        val viewModel = DetailViewModel(FakeGetItemUseCase())

        viewModel.sideEffects.test {
            viewModel.onAction(DetailAction.GoBack)
            val effect = awaitItem()
            assertTrue(effect is DetailSideEffect.NavigateBack)
        }
    }
}

private class FakeGetItemUseCase(
    private val items: Map<String, Item> = emptyMap(),
    private val error: Throwable? = null,
) : GetItemUseCase {
    override fun invoke(id: String): Flow<Item> = flowSingle {
        error?.let { throw it }
        items[id] ?: throw RuntimeException("Item $id not found")
    }
}
