package pl.rockit.castociasto.feature.favorites.domain

import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.items.model.Item

fun interface ToggleFavoriteUseCase {
    operator fun invoke(itemId: String): Flow<List<Item>>
}
