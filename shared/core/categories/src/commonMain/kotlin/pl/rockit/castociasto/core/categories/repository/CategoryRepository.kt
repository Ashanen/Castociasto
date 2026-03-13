package pl.rockit.castociasto.core.categories.repository

import pl.rockit.castociasto.core.categories.model.Category

interface CategoryRepository {
    suspend fun getCategories(): List<Category>
}
