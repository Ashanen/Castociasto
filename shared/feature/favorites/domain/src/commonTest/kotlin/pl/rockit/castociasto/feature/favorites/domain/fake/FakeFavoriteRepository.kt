package pl.rockit.castociasto.feature.favorites.domain.fake

import pl.rockit.castociasto.core.favorites.repository.FavoriteRepository

class FakeFavoriteRepository : FavoriteRepository {
    var favoriteIds: MutableSet<String> = mutableSetOf()

    override suspend fun getFavoriteIds(): Set<String> = favoriteIds.toSet()
    override suspend fun isFavorite(itemId: String): Boolean = itemId in favoriteIds
    override suspend fun toggleFavorite(itemId: String) {
        if (itemId in favoriteIds) {
            favoriteIds.remove(itemId)
        } else {
            favoriteIds.add(itemId)
        }
    }
}
