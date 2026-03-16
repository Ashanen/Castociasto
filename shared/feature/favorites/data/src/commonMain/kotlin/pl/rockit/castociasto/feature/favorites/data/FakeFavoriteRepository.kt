package pl.rockit.castociasto.feature.favorites.data

import pl.rockit.castociasto.core.events.AppEvent
import pl.rockit.castociasto.core.events.AppEventBus
import pl.rockit.castociasto.core.favorites.repository.FavoriteRepository

internal class FakeFavoriteRepository(
    private val eventBus: AppEventBus,
) : FavoriteRepository {

    private val favoriteIds = mutableSetOf("1", "3")

    override suspend fun getFavoriteIds(): Set<String> = favoriteIds.toSet()

    override suspend fun isFavorite(itemId: String): Boolean = itemId in favoriteIds

    override suspend fun toggleFavorite(itemId: String) {
        if (itemId in favoriteIds) {
            favoriteIds.remove(itemId)
        } else {
            favoriteIds.add(itemId)
        }
        eventBus.emit(AppEvent.FavoriteToggled(itemId))
    }
}
