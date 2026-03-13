package pl.rockit.castociasto.feature.favorites.ui

import pl.rockit.castociasto.core.items.model.Item

data class FavoritesState(
    val favorites: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface FavoritesAction {
    data object LoadFavorites : FavoritesAction
    data class ToggleFavorite(val itemId: String) : FavoritesAction
    data class ItemClicked(val itemId: String) : FavoritesAction
}

sealed interface FavoritesSideEffect {
    data class NavigateToDetail(val itemId: String) : FavoritesSideEffect
}
