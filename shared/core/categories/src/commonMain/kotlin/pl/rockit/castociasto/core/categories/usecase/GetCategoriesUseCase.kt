package pl.rockit.castociasto.core.categories.usecase

import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.categories.model.Category

fun interface GetCategoriesUseCase {
    operator fun invoke(): Flow<List<Category>>
}
