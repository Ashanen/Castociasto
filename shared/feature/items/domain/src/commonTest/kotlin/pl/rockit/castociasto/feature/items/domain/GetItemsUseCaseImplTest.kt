package pl.rockit.castociasto.feature.items.domain

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.feature.items.domain.fake.FakeItemRepository
import pl.rockit.castociasto.foundation.exception.CastociastoException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetItemsUseCaseImplTest {

    private val repository = FakeItemRepository()
    private val useCase = GetItemsUseCaseImpl(repository)

    @Test
    fun `returns items sorted by title`() = runTest {
        repository.items = listOf(
            Item(id = "2", title = "Banana", subtitle = "Yellow fruit"),
            Item(id = "1", title = "Apple", subtitle = "Red fruit"),
            Item(id = "3", title = "Cherry", subtitle = "Small fruit"),
        )

        useCase().test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("Apple", result[0].title)
            assertEquals("Banana", result[1].title)
            assertEquals("Cherry", result[2].title)
            awaitComplete()
        }
    }

    @Test
    fun `returns empty list when repository has no items`() = runTest {
        repository.items = emptyList()

        useCase().test {
            val result = awaitItem()
            assertEquals(emptyList(), result)
            awaitComplete()
        }
    }

    @Test
    fun `propagates repository exception`() = runTest {
        repository.shouldThrow = CastociastoException.NetworkException.NoConnection("No internet")

        useCase().test {
            val error = awaitError()
            assertIs<CastociastoException.NetworkException.NoConnection>(error)
        }
    }
}
