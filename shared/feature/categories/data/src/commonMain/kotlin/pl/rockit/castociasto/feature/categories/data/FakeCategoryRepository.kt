package pl.rockit.castociasto.feature.categories.data

import pl.rockit.castociasto.core.categories.model.Category
import pl.rockit.castociasto.core.categories.repository.CategoryRepository

internal class FakeCategoryRepository : CategoryRepository {

    private val categories = listOf(
        Category(id = "1", name = "Bread", itemCount = 3),
        Category(id = "2", name = "Pastries", itemCount = 2),
        Category(id = "3", name = "Cakes", itemCount = 4),
        Category(id = "4", name = "Sourdough", itemCount = 1),
    )

    override suspend fun getCategories(): List<Category> = categories
}
