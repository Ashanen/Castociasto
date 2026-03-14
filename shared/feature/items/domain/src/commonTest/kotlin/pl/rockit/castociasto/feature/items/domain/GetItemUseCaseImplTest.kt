package pl.rockit.castociasto.feature.items.domain

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.feature.items.domain.fake.FakeFavoriteRepository
import pl.rockit.castociasto.feature.items.domain.fake.FakeItemRepository
import pl.rockit.castociasto.foundation.exception.CastociastoException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class GetItemUseCaseImplTest {

    private val itemRepository = FakeItemRepository()
    private val favoriteRepository = FakeFavoriteRepository()
    private val useCase = GetItemUseCaseImpl(itemRepository, favoriteRepository)

    @Test
    fun `returns item with favorite status true when favorited`() = runTest {
        val item = Item(id = "1", title = "Bread", subtitle = "Fresh bread")
        itemRepository.itemById = mapOf("1" to item)
        favoriteRepository.favoriteIds = mutableSetOf("1")

        useCase("1").test {
            val result = awaitItem()
            assertEquals("1", result.id)
            assertEquals("Bread", result.title)
            assertTrue(result.isFavorite)
            awaitComplete()
        }
    }

    @Test
    fun `returns item with favorite status false when not favorited`() = runTest {
        val item = Item(id = "2", title = "Cake", subtitle = "Chocolate cake")
        itemRepository.itemById = mapOf("2" to item)
        favoriteRepository.favoriteIds = mutableSetOf()

        useCase("2").test {
            val result = awaitItem()
            assertEquals("2", result.id)
            assertEquals(false, result.isFavorite)
            awaitComplete()
        }
    }

    @Test
    fun `throws DataException NotFound when item does not exist`() = runTest {
        itemRepository.itemById = emptyMap()

        useCase("999").test {
            val error = awaitError()
            assertIs<CastociastoException.DataException.NotFound>(error)
        }
    }

    @Test
    fun `propagates repository exception`() = runTest {
        itemRepository.shouldThrow = CastociastoException.NetworkException.Unauthorized

        useCase("1").test {
            val error = awaitError()
            assertIs<CastociastoException.NetworkException.Unauthorized>(error)
        }
    }
}
