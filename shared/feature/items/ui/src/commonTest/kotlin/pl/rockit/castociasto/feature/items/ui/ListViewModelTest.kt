package pl.rockit.castociasto.feature.items.ui

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.feature.items.domain.ObserveItemsUseCase
import pl.rockit.castociasto.feature.items.domain.RefreshItemsUseCase
import pl.rockit.castociasto.foundation.extensions.flowSingle
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

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
    fun `initial state triggers refresh and emits observed items`() = runTest(testDispatcher) {
        val items = listOf(
            Item(id = "1", title = "Apple", subtitle = "Fruit"),
            Item(id = "2", title = "Bread", subtitle = "Baked"),
        )
        val observeUseCase = FakeObserveItemsUseCase()
        val viewModel = ListViewModel(observeUseCase, FakeRefreshItemsUseCase())

        advanceUntilIdle()

        viewModel.uiState.test {
            val initial = awaitItem()
            assertFalse(initial.isLoading)

            observeUseCase.emit(items)
            val loaded = awaitItem()
            assertEquals(2, loaded.items.size)
            assertNull(loaded.error)
        }
    }

    @Test
    fun `error state is set when refresh fails`() = runTest(testDispatcher) {
        val viewModel = ListViewModel(
            FakeObserveItemsUseCase(),
            FakeRefreshItemsUseCase(error = RuntimeException("Network error")),
        )

        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals("Network error", state.error)
        }
    }

    @Test
    fun `ItemClicked sends NavigateToDetail side effect`() = runTest(testDispatcher) {
        val viewModel = ListViewModel(
            FakeObserveItemsUseCase(),
            FakeRefreshItemsUseCase(),
        )

        viewModel.sideEffects.test {
            viewModel.onAction(ListAction.ItemClicked("42"))
            val effect = awaitItem()
            val navigateEffect = effect as ListSideEffect.NavigateToDetail
            assertEquals("42", navigateEffect.itemId)
        }
    }

    @Test
    fun `observed items update UI continuously`() = runTest(testDispatcher) {
        val observeUseCase = FakeObserveItemsUseCase()
        val viewModel = ListViewModel(observeUseCase, FakeRefreshItemsUseCase())

        advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // current settled state

            val first = listOf(Item(id = "1", title = "Apple", subtitle = "Fruit"))
            observeUseCase.emit(first)
            assertEquals(first, awaitItem().items)

            val second = first + Item(id = "2", title = "Banana", subtitle = "Fruit")
            observeUseCase.emit(second)
            assertEquals(second, awaitItem().items)
        }
    }
}

private class FakeObserveItemsUseCase : ObserveItemsUseCase {
    private val flow = MutableStateFlow<List<Item>>(emptyList())
    override fun invoke(): Flow<List<Item>> = flow
    fun emit(items: List<Item>) { flow.value = items }
}

private class FakeRefreshItemsUseCase(
    private val error: Throwable? = null,
) : RefreshItemsUseCase {
    override fun invoke(): Flow<Unit> = flowSingle {
        error?.let { throw it }
    }
}
