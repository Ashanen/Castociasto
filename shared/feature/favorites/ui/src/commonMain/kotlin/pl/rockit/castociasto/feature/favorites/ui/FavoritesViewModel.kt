package pl.rockit.castociasto.feature.favorites.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import pl.rockit.castociasto.feature.favorites.domain.GetFavoritesUseCase
import pl.rockit.castociasto.feature.favorites.domain.ToggleFavoriteUseCase
import pl.rockit.castociasto.foundation.BaseViewModel
import pl.rockit.castociasto.foundation.extensions.launchWith

class FavoritesViewModel(
    private val getFavorites: GetFavoritesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : BaseViewModel<FavoritesState, FavoritesAction, FavoritesSideEffect>() {

    override val _uiState = MutableStateFlow(FavoritesState())

    init {
        onAction(FavoritesAction.LoadFavorites)
    }

    override fun onAction(action: FavoritesAction) {
        when (action) {
            is FavoritesAction.LoadFavorites -> loadFavorites()
            is FavoritesAction.ToggleFavorite -> toggle(action.itemId)
            is FavoritesAction.ItemClicked -> sendEffect(
                FavoritesSideEffect.NavigateToDetail(action.itemId)
            )
        }
    }

    private fun loadFavorites() {
        getFavorites()
            .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
            .onEach { items ->
                _uiState.update { it.copy(favorites = items, isLoading = false) }
            }
            .launchWith(viewModelScope) { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
    }

    private fun toggle(itemId: String) {
        toggleFavorite(itemId)
            .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
            .onEach { items ->
                _uiState.update { it.copy(favorites = items, isLoading = false) }
            }
            .launchWith(viewModelScope) { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
    }
}
