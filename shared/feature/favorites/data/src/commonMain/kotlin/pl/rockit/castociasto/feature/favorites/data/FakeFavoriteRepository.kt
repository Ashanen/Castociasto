package pl.rockit.castociasto.feature.favorites.data

import pl.rockit.castociasto.core.favorites.repository.FavoriteRepository

internal class FakeFavoriteRepository : FavoriteRepository {

    private val favoriteIds = mutableSetOf("1", "3")

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
