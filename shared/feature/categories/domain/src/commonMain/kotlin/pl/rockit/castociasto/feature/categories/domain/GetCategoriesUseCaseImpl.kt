package pl.rockit.castociasto.feature.categories.domain

import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.categories.model.Category
import pl.rockit.castociasto.core.categories.repository.CategoryRepository
import pl.rockit.castociasto.core.categories.usecase.GetCategoriesUseCase
import pl.rockit.castociasto.foundation.extensions.flowSingle

internal class GetCategoriesUseCaseImpl(
    private val repository: CategoryRepository,
) : GetCategoriesUseCase {
    override fun invoke(): Flow<List<Category>> = flowSingle {
        repository.getCategories().sortedByDescending { it.itemCount }
    }
}
