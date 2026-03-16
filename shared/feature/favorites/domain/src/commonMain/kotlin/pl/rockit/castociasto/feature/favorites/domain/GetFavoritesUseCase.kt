package pl.rockit.castociasto.feature.favorites.domain

import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.items.model.Item

fun interface GetFavoritesUseCase {
    operator fun invoke(): Flow<List<Item>>
}
