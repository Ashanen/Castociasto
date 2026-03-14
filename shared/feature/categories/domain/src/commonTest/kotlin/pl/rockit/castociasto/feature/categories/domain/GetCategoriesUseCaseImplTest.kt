package pl.rockit.castociasto.feature.categories.domain

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import pl.rockit.castociasto.core.categories.model.Category
import pl.rockit.castociasto.core.categories.repository.CategoryRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class GetCategoriesUseCaseImplTest {

    private val repository = FakeCategoryRepository()
    private val useCase = GetCategoriesUseCaseImpl(repository)

    @Test
    fun `returns categories sorted by item count descending`() = runTest {
        repository.categories = listOf(
            Category(id = "1", name = "Bread", itemCount = 5),
            Category(id = "2", name = "Pastries", itemCount = 15),
            Category(id = "3", name = "Cakes", itemCount = 10),
        )

        useCase().test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("Pastries", result[0].name)
            assertEquals(15, result[0].itemCount)
            assertEquals("Cakes", result[1].name)
            assertEquals(10, result[1].itemCount)
            assertEquals("Bread", result[2].name)
            assertEquals(5, result[2].itemCount)
            awaitComplete()
        }
    }

    @Test
    fun `returns empty list when no categories exist`() = runTest {
        repository.categories = emptyList()

        useCase().test {
            val result = awaitItem()
            assertEquals(emptyList(), result)
            awaitComplete()
        }
    }

    @Test
    fun `preserves order for equal item counts`() = runTest {
        repository.categories = listOf(
            Category(id = "1", name = "A", itemCount = 5),
            Category(id = "2", name = "B", itemCount = 5),
        )

        useCase().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            // sortedByDescending is stable, so original order is preserved for equal keys
            assertEquals("A", result[0].name)
            assertEquals("B", result[1].name)
            awaitComplete()
        }
    }
}

private class FakeCategoryRepository : CategoryRepository {
    var categories: List<Category> = emptyList()

    override suspend fun getCategories(): List<Category> = categories
}
