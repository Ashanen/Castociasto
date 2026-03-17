package pl.rockit.castociasto.feature.favorites.domain

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.feature.favorites.domain.fake.FakeFavoriteRepository
import pl.rockit.castociasto.feature.favorites.domain.fake.FakeItemRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetFavoritesUseCaseImplTest {

    private val favoriteRepository = FakeFavoriteRepository()
    private val itemRepository = FakeItemRepository()
    private val useCase = GetFavoritesUseCaseImpl(favoriteRepository, itemRepository)

    @Test
    fun `returns favorite items with isFavorite set to true`() = runTest {
        favoriteRepository.favoriteIds = mutableSetOf("1", "3")
        itemRepository.itemById = mapOf(
            "1" to Item(id = "1", title = "Bread", subtitle = "Fresh"),
            "3" to Item(id = "3", title = "Cake", subtitle = "Chocolate"),
        )

        useCase().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.isFavorite })
            awaitComplete()
        }
    }

    @Test
    fun `returns empty list when no favorites exist`() = runTest {
        favoriteRepository.favoriteIds = mutableSetOf()

        useCase().test {
            val result = awaitItem()
            assertEquals(emptyList(), result)
            awaitComplete()
        }
    }

    @Test
    fun `filters out null items when item is not found`() = runTest {
        favoriteRepository.favoriteIds = mutableSetOf("1", "999")
        itemRepository.itemById = mapOf(
            "1" to Item(id = "1", title = "Bread", subtitle = "Fresh"),
        )

        useCase().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("1", result[0].id)
            awaitComplete()
        }
    }
}
