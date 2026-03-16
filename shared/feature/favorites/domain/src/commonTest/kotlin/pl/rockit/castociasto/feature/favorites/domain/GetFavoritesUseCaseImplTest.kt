package pl.rockit.castociasto.feature.favorites.domain

import app.cash.turbine.test
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import pl.rockit.castociasto.core.favorites.repository.FavoriteRepository
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.core.items.repository.ItemRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetFavoritesUseCaseImplTest {

    private val favoriteRepository = FakeFavoriteRepository()
    private val itemRepository = FakeItemRepository()
    private val useCase = GetFavoritesUseCaseImpl(favoriteRepository, itemRepository)

    @Test
    fun `returns favorite items with isFavorite set to true`() = runTest {
        favoriteRepository.favoriteIds = setOf("1", "3")
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
        favoriteRepository.favoriteIds = emptySet()

        useCase().test {
            val result = awaitItem()
            assertEquals(emptyList(), result)
            awaitComplete()
        }
    }

    @Test
    fun `filters out null items when item is not found`() = runTest {
        favoriteRepository.favoriteIds = setOf("1", "999")
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

private class FakeFavoriteRepository : FavoriteRepository {
    var favoriteIds: Set<String> = emptySet()

    override suspend fun getFavoriteIds(): Set<String> = favoriteIds
    override suspend fun isFavorite(itemId: String): Boolean = itemId in favoriteIds
    override suspend fun toggleFavorite(itemId: String) {}
}

private class FakeItemRepository : ItemRepository {
    var itemById: Map<String, Item> = emptyMap()

    override suspend fun getItems(): List<Item> = itemById.values.toList()
    override suspend fun getItem(id: String): Item? = itemById[id]
    override fun observeItems(): Flow<List<Item>> = emptyFlow()
    override fun observeItem(id: String): Flow<Item?> = emptyFlow()
    override suspend fun refresh() {}
}
