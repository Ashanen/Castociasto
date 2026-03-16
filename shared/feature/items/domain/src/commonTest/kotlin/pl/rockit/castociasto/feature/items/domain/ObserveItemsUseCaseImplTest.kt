package pl.rockit.castociasto.feature.items.domain

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.feature.items.domain.fake.FakeItemRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveItemsUseCaseImplTest {

    private val repository = FakeItemRepository()
    private val useCase = ObserveItemsUseCaseImpl(repository)

    @Test
    fun `emits items from repository`() = runTest {
        val items = listOf(
            Item(id = "1", title = "Apple", subtitle = "Red fruit"),
            Item(id = "2", title = "Banana", subtitle = "Yellow fruit"),
        )

        useCase().test {
            assertEquals(emptyList(), awaitItem())
            repository.emitItems(items)
            assertEquals(items, awaitItem())
        }
    }

    @Test
    fun `emits updates when repository changes`() = runTest {
        useCase().test {
            assertEquals(emptyList(), awaitItem())

            val first = listOf(Item(id = "1", title = "Apple", subtitle = "Fruit"))
            repository.emitItems(first)
            assertEquals(first, awaitItem())

            val second = first + Item(id = "2", title = "Banana", subtitle = "Fruit")
            repository.emitItems(second)
            assertEquals(second, awaitItem())
        }
    }
}
