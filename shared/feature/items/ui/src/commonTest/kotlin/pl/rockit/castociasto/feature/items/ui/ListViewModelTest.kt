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
import pl.rockit.castociasto.feature.items.domain.GetItemsUseCase
import pl.rockit.castociasto.foundation.extensions.flowSingle
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ListViewModelTest {

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
    fun `initial state triggers loading and emits items`() = runTest(testDispatcher) {
        val items = listOf(
            Item(id = "1", title = "Apple", subtitle = "Fruit"),
            Item(id = "2", title = "Bread", subtitle = "Baked"),
        )
        val viewModel = ListViewModel(FakeGetItemsUseCase(items))

        viewModel.uiState.test {
            // StateFlow emits current value first, then init triggers LoadItems
            skipItems(1) // skip initial default state
            val loading = awaitItem()
            assertTrue(loading.isLoading)

            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            assertEquals(2, loaded.items.size)
            assertNull(loaded.error)
        }
    }

    @Test
    fun `error state is set when use case fails`() = runTest(testDispatcher) {
        val viewModel = ListViewModel(
            FakeGetItemsUseCase(error = RuntimeException("Network error"))
        )

        viewModel.uiState.test {
            skipItems(1) // skip initial default state
            awaitItem() // loading state
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Network error", errorState.error)
        }
    }

    @Test
    fun `ItemClicked sends NavigateToDetail side effect`() = runTest(testDispatcher) {
        val viewModel = ListViewModel(FakeGetItemsUseCase(emptyList()))

        viewModel.sideEffects.test {
            viewModel.onAction(ListAction.ItemClicked("42"))
            val effect = awaitItem()
            assertTrue(effect is ListSideEffect.NavigateToDetail)
            assertEquals("42", (effect as ListSideEffect.NavigateToDetail).itemId)
        }
    }
}

private class FakeGetItemsUseCase(
    private val items: List<Item> = emptyList(),
    private val error: Throwable? = null,
) : GetItemsUseCase {
    override fun invoke(): Flow<List<Item>> = flowSingle {
        error?.let { throw it }
        items
    }
}
