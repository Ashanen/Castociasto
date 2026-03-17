package pl.rockit.castociasto.feature.favorites.ui

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.feature.favorites.domain.GetFavoritesUseCase
import pl.rockit.castociasto.feature.favorites.domain.ToggleFavoriteUseCase
import pl.rockit.castociasto.foundation.extensions.flowSingle
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

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
    fun `initial state loads favorites`() = runTest(testDispatcher) {
        val items = listOf(
            Item(id = "1", title = "Bread", subtitle = "Fresh", isFavorite = true),
            Item(id = "3", title = "Cake", subtitle = "Chocolate", isFavorite = true),
        )
        val viewModel = FavoritesViewModel(
            FakeGetFavoritesUseCase(items),
            FakeToggleFavoriteUseCase(),
        )

        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.favorites.size)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `error state is set when loading fails`() = runTest(testDispatcher) {
        val viewModel = FavoritesViewModel(
            FakeGetFavoritesUseCase(error = RuntimeException("Network error")),
            FakeToggleFavoriteUseCase(),
        )

        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals("Network error", state.error)
        }
    }

    @Test
    fun `ToggleFavorite updates favorites list`() = runTest(testDispatcher) {
        val initialItems = listOf(
            Item(id = "1", title = "Bread", subtitle = "Fresh", isFavorite = true),
        )
        val toggledItems = listOf(
            Item(id = "1", title = "Bread", subtitle = "Fresh", isFavorite = true),
            Item(id = "2", title = "Cake", subtitle = "Chocolate", isFavorite = true),
        )
        val viewModel = FavoritesViewModel(
            FakeGetFavoritesUseCase(initialItems),
            FakeToggleFavoriteUseCase(toggledItems),
        )

        advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // settled after init

            viewModel.onAction(FavoritesAction.ToggleFavorite("2"))
            val loading = awaitItem()
            assertTrue(loading.isLoading)

            val toggled = awaitItem()
            assertEquals(2, toggled.favorites.size)
            assertFalse(toggled.isLoading)
        }
    }

    @Test
    fun `ToggleFavorite sets error when use case fails`() = runTest(testDispatcher) {
        val viewModel = FavoritesViewModel(
            FakeGetFavoritesUseCase(emptyList()),
            FakeToggleFavoriteUseCase(error = RuntimeException("Toggle failed")),
        )

        advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // settled

            viewModel.onAction(FavoritesAction.ToggleFavorite("1"))
            awaitItem() // loading
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Toggle failed", errorState.error)
        }
    }

    @Test
    fun `ItemClicked sends NavigateToDetail side effect`() = runTest(testDispatcher) {
        val viewModel = FavoritesViewModel(
            FakeGetFavoritesUseCase(emptyList()),
            FakeToggleFavoriteUseCase(),
        )

        viewModel.sideEffects.test {
            viewModel.onAction(FavoritesAction.ItemClicked("42"))
            val effect = awaitItem()
            assertIs<FavoritesSideEffect.NavigateToDetail>(effect)
            assertEquals("42", effect.itemId)
        }
    }
}

private class FakeGetFavoritesUseCase(
    private val items: List<Item> = emptyList(),
    private val error: Throwable? = null,
) : GetFavoritesUseCase {
    override fun invoke(): Flow<List<Item>> = flowSingle {
        error?.let { throw it }
        items
    }
}

private class FakeToggleFavoriteUseCase(
    private val result: List<Item> = emptyList(),
    private val error: Throwable? = null,
) : ToggleFavoriteUseCase {
    override fun invoke(itemId: String): Flow<List<Item>> = flowSingle {
        error?.let { throw it }
        result
    }
}
