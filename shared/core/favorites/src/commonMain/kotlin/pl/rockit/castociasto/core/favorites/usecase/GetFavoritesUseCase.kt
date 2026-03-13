package pl.rockit.castociasto.core.favorites.usecase

import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.items.model.Item

fun interface GetFavoritesUseCase {
    operator fun invoke(): Flow<List<Item>>
}
