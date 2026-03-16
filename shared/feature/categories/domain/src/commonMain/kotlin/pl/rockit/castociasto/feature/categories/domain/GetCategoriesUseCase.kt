package pl.rockit.castociasto.feature.categories.domain

import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.categories.model.Category

fun interface GetCategoriesUseCase {
    operator fun invoke(): Flow<List<Category>>
}
