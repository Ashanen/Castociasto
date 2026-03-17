package pl.rockit.castociasto.feature.favorites.domain

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.feature.favorites.domain.fake.FakeFavoriteRepository
import pl.rockit.castociasto.feature.favorites.domain.fake.FakeItemRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToggleFavoriteUseCaseImplTest {

    private val favoriteRepository = FakeFavoriteRepository()
    private val itemRepository = FakeItemRepository()
    private val useCase = ToggleFavoriteUseCaseImpl(favoriteRepository, itemRepository)

    @Test
    fun `toggles favorite and returns updated favorite items`() = runTest {
        favoriteRepository.favoriteIds = mutableSetOf("1")
        itemRepository.itemById = mapOf(
            "1" to Item(id = "1", title = "Bread", subtitle = "Fresh"),
            "2" to Item(id = "2", title = "Cake", subtitle = "Chocolate"),
        )

        useCase("2").test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.isFavorite })
            awaitComplete()
        }
    }

    @Test
    fun `toggle removes item and returns remaining favorites`() = runTest {
        favoriteRepository.favoriteIds = mutableSetOf("1", "2")
        itemRepository.itemById = mapOf(
            "1" to Item(id = "1", title = "Bread", subtitle = "Fresh"),
            "2" to Item(id = "2", title = "Cake", subtitle = "Chocolate"),
        )

        useCase("2").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("1", result[0].id)
            assertTrue(result[0].isFavorite)
            awaitComplete()
        }
    }

    @Test
    fun `filters out null items when item not found in repository`() = runTest {
        favoriteRepository.favoriteIds = mutableSetOf("1", "999")
        itemRepository.itemById = mapOf(
            "1" to Item(id = "1", title = "Bread", subtitle = "Fresh"),
        )

        // Toggle "999" removes it from favorites, leaving only "1"
        useCase("999").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("1", result[0].id)
            assertTrue(result[0].isFavorite)
            awaitComplete()
        }
    }

    @Test
    fun `returns empty list when toggling results in no favorites`() = runTest {
        favoriteRepository.favoriteIds = mutableSetOf("1")
        itemRepository.itemById = mapOf(
            "1" to Item(id = "1", title = "Bread", subtitle = "Fresh"),
        )

        useCase("1").test {
            val result = awaitItem()
            assertEquals(emptyList(), result)
            awaitComplete()
        }
    }
}
