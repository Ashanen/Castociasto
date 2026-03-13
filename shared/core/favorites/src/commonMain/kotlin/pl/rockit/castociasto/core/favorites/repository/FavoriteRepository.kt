package pl.rockit.castociasto.core.favorites.repository

interface FavoriteRepository {
    suspend fun getFavoriteIds(): Set<String>
    suspend fun isFavorite(itemId: String): Boolean
    suspend fun toggleFavorite(itemId: String)
}
