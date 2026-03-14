package pl.rockit.castociasto.feature.items.domain.fake

import pl.rockit.castociasto.core.favorites.repository.FavoriteRepository

class FakeFavoriteRepository : FavoriteRepository {

    var favoriteIds: MutableSet<String> = mutableSetOf()
    var shouldThrow: Throwable? = null

    override suspend fun getFavoriteIds(): Set<String> {
        shouldThrow?.let { throw it }
        return favoriteIds.toSet()
    }

    override suspend fun isFavorite(itemId: String): Boolean {
        shouldThrow?.let { throw it }
        return itemId in favoriteIds
    }

    override suspend fun toggleFavorite(itemId: String) {
        shouldThrow?.let { throw it }
        if (itemId in favoriteIds) favoriteIds.remove(itemId) else favoriteIds.add(itemId)
    }
}
