package pl.rockit.castociasto.feature.items.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.core.items.repository.ItemRepository
import pl.rockit.castociasto.feature.items.domain.GetItemUseCase
import pl.rockit.castociasto.core.favorites.repository.FavoriteRepository
import pl.rockit.castociasto.foundation.exception.CastociastoException
import pl.rockit.castociasto.foundation.extensions.flowSingle

internal class GetItemUseCaseImpl(
    private val itemRepository: ItemRepository,
    private val favoriteRepository: FavoriteRepository,
) : GetItemUseCase {
    override fun invoke(id: String): Flow<Item> = flowSingle {
        coroutineScope {
            val itemDeferred = async { itemRepository.getItem(id) }
            val isFavoriteDeferred = async { favoriteRepository.isFavorite(id) }

            val item = itemDeferred.await()
                ?: throw CastociastoException.DataException.NotFound("Item $id not found")

            item.copy(isFavorite = isFavoriteDeferred.await())
        }
    }
}
