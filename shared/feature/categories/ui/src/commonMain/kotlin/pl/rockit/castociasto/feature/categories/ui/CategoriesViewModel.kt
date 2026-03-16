package pl.rockit.castociasto.feature.categories.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import pl.rockit.castociasto.feature.categories.domain.GetCategoriesUseCase
import pl.rockit.castociasto.foundation.BaseViewModel
import pl.rockit.castociasto.foundation.extensions.launchWith

class CategoriesViewModel(
    private val getCategories: GetCategoriesUseCase,
) : BaseViewModel<CategoriesState, CategoriesAction, CategoriesSideEffect>() {

    override val _uiState = MutableStateFlow(CategoriesState())

    init {
        onAction(CategoriesAction.LoadCategories)
    }

    override fun onAction(action: CategoriesAction) {
        when (action) {
            is CategoriesAction.LoadCategories -> loadCategories()
            is CategoriesAction.CategoryClicked -> sendEffect(
                CategoriesSideEffect.NavigateToCategory(action.categoryId)
            )
        }
    }

    private fun loadCategories() {
        getCategories()
            .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
            .onEach { categories ->
                _uiState.update { it.copy(categories = categories, isLoading = false) }
            }
            .launchWith(viewModelScope) { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
    }
}
