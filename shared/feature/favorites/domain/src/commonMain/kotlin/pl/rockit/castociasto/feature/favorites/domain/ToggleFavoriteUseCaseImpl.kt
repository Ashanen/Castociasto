package pl.rockit.castociasto.feature.favorites.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.favorites.repository.FavoriteRepository
import pl.rockit.castociasto.core.favorites.usecase.ToggleFavoriteUseCase
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.core.items.repository.ItemRepository
import pl.rockit.castociasto.foundation.extensions.flowSingle

internal class ToggleFavoriteUseCaseImpl(
    private val favoriteRepository: FavoriteRepository,
    private val itemRepository: ItemRepository,
) : ToggleFavoriteUseCase {
    override fun invoke(itemId: String): Flow<List<Item>> = flowSingle {
        favoriteRepository.toggleFavorite(itemId)

        coroutineScope {
            val favoriteIds = favoriteRepository.getFavoriteIds()

            favoriteIds.map { id ->
                async { itemRepository.getItem(id) }
            }.awaitAll()
                .filterNotNull()
                .map { it.copy(isFavorite = true) }
        }
    }
}
