package pl.rockit.castociasto.feature.categories.ui

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import pl.rockit.castociasto.core.categories.model.Category
import pl.rockit.castociasto.feature.categories.domain.GetCategoriesUseCase
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
class CategoriesViewModelTest {

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
    fun `initial state loads categories`() = runTest(testDispatcher) {
        val categories = listOf(
            Category(id = "1", name = "Bread", itemCount = 5),
            Category(id = "2", name = "Pastries", itemCount = 10),
        )
        val viewModel = CategoriesViewModel(FakeGetCategoriesUseCase(categories))

        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.categories.size)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `error state is set when loading fails`() = runTest(testDispatcher) {
        val viewModel = CategoriesViewModel(
            FakeGetCategoriesUseCase(error = RuntimeException("Failed to load")),
        )

        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals("Failed to load", state.error)
        }
    }

    @Test
    fun `loading state is set while fetching categories`() = runTest(testDispatcher) {
        val viewModel = CategoriesViewModel(FakeGetCategoriesUseCase(emptyList()))

        viewModel.uiState.test {
            val initial = awaitItem()
            assertFalse(initial.isLoading)

            // loading state emitted by onStart
            val loading = awaitItem()
            assertTrue(loading.isLoading)

            // loaded state
            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
        }
    }

    @Test
    fun `CategoryClicked sends NavigateToCategory side effect`() = runTest(testDispatcher) {
        val viewModel = CategoriesViewModel(FakeGetCategoriesUseCase(emptyList()))

        viewModel.sideEffects.test {
            viewModel.onAction(CategoriesAction.CategoryClicked("42"))
            val effect = awaitItem()
            assertIs<CategoriesSideEffect.NavigateToCategory>(effect)
            assertEquals("42", effect.categoryId)
        }
    }

    @Test
    fun `returns empty list when no categories exist`() = runTest(testDispatcher) {
        val viewModel = CategoriesViewModel(FakeGetCategoriesUseCase(emptyList()))

        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(emptyList(), state.categories)
            assertFalse(state.isLoading)
        }
    }
}

private class FakeGetCategoriesUseCase(
    private val categories: List<Category> = emptyList(),
    private val error: Throwable? = null,
) : GetCategoriesUseCase {
    override fun invoke(): Flow<List<Category>> = flowSingle {
        error?.let { throw it }
        categories
    }
}
